from django.contrib import admin
from .models import Trip


@admin.register(Trip)
class TripAdmin(admin.ModelAdmin):
    list_display = ('user', 'city', 'budget', 'created')
    search_fields = ('city', 'user__username')
    list_filter = ('created',)
    ordering = ('-created',)