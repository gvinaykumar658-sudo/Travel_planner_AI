from django.urls import path
from . import views

app_name = 'dashboard'

urlpatterns = [
    path('', views.dashboard, name='index'),
    path('generate/', views.generate_trip, name='generate'),
    path('save/', views.save_trip, name='save'),
    path('delete/<int:id>/', views.delete_trip, name='delete'),
    path('download/', views.download_pdf, name='download'),
]