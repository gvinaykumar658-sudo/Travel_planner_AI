from django.db import models
from django.contrib.auth.models import User

class Trip(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    city = models.CharField(max_length=100)
    budget = models.TextField()
    itinerary = models.TextField()
    people = models.IntegerField(default=1)
    days = models.IntegerField(default=1)
    created = models.DateTimeField(auto_now_add=True)