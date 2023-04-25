package com.fatec.coleiraqr;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class MinhaLocalizacaoListener implements LocationListener{
    public static double Latitude;
    public static double Longitude;

    @Override
    public void onLocationChanged(Location location){
        //CAPTURA E ARMAZENA VARIAVEIS DE LATITUDE E LONGITUDE
        this.Latitude = location.getLatitude();
        this.Longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider){
    }

    @Override
    public void onProviderEnabled(@NonNull String provider){
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
    }

}
