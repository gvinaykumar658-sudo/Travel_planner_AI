from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.contrib.auth.decorators import login_required
from agents.system import run_agents
from .models import Trip
from django.shortcuts import get_object_or_404
from django.views.decorators.http import require_POST
from django.db import DataError
from reportlab.platypus import SimpleDocTemplate, Paragraph
from reportlab.lib.styles import getSampleStyleSheet
from html import escape
import urllib.parse
import re


@login_required
def dashboard(request):
    trips = Trip.objects.filter(user=request.user)
    return render(request, 'dashboard.html', {"trips": trips})


@login_required
def generate_trip(request):
    city = request.GET.get("city")
    budget = request.GET.get("budget")
    people = request.GET.get("people")
    days = request.GET.get("days")

    result = run_agents(city, budget, people, days)

    return JsonResponse(result)




@login_required
def save_trip(request):
    # Accept POST JSON body to save trip (used by client-side JS)
    if request.method == 'POST':
        try:
            import json
            payload = json.loads(request.body.decode('utf-8'))
            city = payload.get('city')
            budget = payload.get('budget')
            itinerary = payload.get('itinerary')
        except Exception:
            return JsonResponse({'error': 'invalid payload'}, status=400)

        if not city:
            return JsonResponse({'error': 'missing city'}, status=400)
        # Normalize itinerary and budget: unescape unicode escapes (e.g. \u000A) and ensure strings
        try:
            if isinstance(itinerary, str):
                try:
                    # convert escaped unicode sequences to actual characters
                    itinerary = itinerary.encode('utf-8').decode('unicode_escape')
                except Exception:
                    # fallback: use raw string
                    itinerary = itinerary
            if isinstance(budget, str):
                try:
                    budget = budget.encode('utf-8').decode('unicode_escape')
                except Exception:
                    budget = budget

            Trip.objects.create(
                user=request.user,
                city=city,
                budget=budget or '',
                itinerary=itinerary or ''
            )
        except DataError as e:
            return JsonResponse({'error': 'data too long for a field', 'details': str(e)}, status=400)

        return JsonResponse({"status": "saved"})

    # fallback to previous GET behavior for backwards compatibility
    city = request.GET.get("city")
    budget = request.GET.get("budget")
    itinerary = request.GET.get("itinerary")
    if city:
        try:
            Trip.objects.create(user=request.user, city=city, budget=budget or '', itinerary=itinerary or '')
        except DataError as e:
            return JsonResponse({'error': 'data too long for a field', 'details': str(e)}, status=400)
        return JsonResponse({"status": "saved"})

    return JsonResponse({'error': 'method not allowed'}, status=405)


@login_required
@require_POST
def delete_trip(request, id):
    # allow user to delete their own trip
    trip = get_object_or_404(Trip, id=id, user=request.user)
    trip.delete()
    return JsonResponse({'status': 'deleted'})


@login_required
def download_pdf(request):
    city = request.GET.get("city", "Trip")
    itinerary = request.GET.get("itinerary", "")
    
    # Handle empty itinerary
    if not itinerary or not itinerary.strip():
        return JsonResponse({'error': 'No itinerary data to download'}, status=400)

    # Decode URL-encoded data
    city = urllib.parse.unquote(city)
    itinerary = urllib.parse.unquote(itinerary)
    
    # Helper function to fix mojibake and unicode escapes
    def clean_text(text):
        if not text:
            return text
        
        # Handle escaped unicode sequences like \u000A, \u002D
        def unescape_unicode(s):
            def replacer(match):
                hex_str = match.group(1)
                return chr(int(hex_str, 16))
            return re.sub(r'\\u([0-9a-fA-F]{4})', replacer, s)
        
        text = unescape_unicode(text)
        
        # Fix mojibake: UTF-8 bytes interpreted as latin1
        # Common issue: Ã© (wrong) should be é (correct)
        try:
            # Try to detect and fix mojibake
            text = text.encode('latin1').decode('utf-8', errors='ignore')
        except (UnicodeDecodeError, UnicodeEncodeError):
            pass
        
        return text
    
    city = clean_text(city)
    itinerary = clean_text(itinerary)

    response = HttpResponse(content_type='application/pdf')
    response['Content-Disposition'] = f'attachment; filename="trip_{city.lower().replace(" ", "_")}.pdf"'

    doc = SimpleDocTemplate(response)
    styles = getSampleStyleSheet()

    content = []
    content.append(Paragraph(f"<b>Trip Plan: {city}</b>", styles['Title']))
    # Properly escape HTML special characters in the itinerary
    escaped_itinerary = escape(itinerary).replace("\n", "<br/>")
    content.append(Paragraph(escaped_itinerary, styles['Normal']))

    doc.build(content)

    return response