from django.shortcuts import render, redirect
from django.contrib.auth.models import User
from django.contrib.auth import authenticate, login, logout
from django.http import JsonResponse



def register(request):
    if request.method == "POST":
        username = request.POST['username']
        password = request.POST['password']
        confirm_password = request.POST['confirm_password']

        # ✅ Check password match
        if password != confirm_password:
            if request.headers.get('X-Requested-With') == 'XMLHttpRequest':
                return JsonResponse({'error': 'Passwords do not match'}, status=400)
            else:
                return render(request, 'register.html', {'error': 'Passwords do not match'})

        # Check if user exists
        if User.objects.filter(username=username).exists():
            if request.headers.get('X-Requested-With') == 'XMLHttpRequest':
                return JsonResponse({'error': 'Username already exists. Please choose a different username.'}, status=400)
            else:
                return render(request, 'register.html', {'error': 'Username already exists. Please choose a different username.'})

        user = User.objects.create_user(username=username, password=password)
        login(request, user)
        return redirect('/')

    return render(request, 'register.html')


def login_view(request):
    error = None
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(request, username=username, password=password)
        if user is not None:
            login(request, user)
            return redirect('/')
        else:
            error = 'Invalid credentials'
    return render(request, 'login.html', {'error': error})


def logout_view(request):
    logout(request)
    return redirect('/')