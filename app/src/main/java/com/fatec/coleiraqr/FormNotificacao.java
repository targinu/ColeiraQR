package com.fatec.coleiraqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

public class FormNotificacao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_notificacao);

        getSupportActionBar().hide();

    }

    public void buscarInformacoesGPS(View v){
        //CHECA PERMISSÕES DE LOCALIZAÇÃO
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(FormNotificacao.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(FormNotificacao.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(FormNotificacao.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }

        LocationManager mLocManager = (LocationManager) getSystemService(FormNotificacao.this.LOCATION_SERVICE); //PEDE A LOCALIZAÇÃO PARA O SISTEMA
        LocationListener mLocListener = new MinhaLocalizacaoListener(); //INSTANCIA A CLASSE CRIADA PARA ARMAZENAR LATITUDE E LONGITUDE

        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener); //ATUALIZA AS INFORMAÇÕES

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //VERIFICA SE AS INFORMAÇÕES FORAM PROCESSADAS E FAZENDO UM TEXTO
            String texto = "Latitude: " + MinhaLocalizacaoListener.Latitude + "\n" +
                            "Longitude: " + MinhaLocalizacaoListener.Longitude + "\n";
            Toast.makeText(FormNotificacao.this, texto, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(FormNotificacao.this, "GPS DESABILITADO", Toast.LENGTH_LONG).show();
        }

        this.mostrarGoogleMaps(MinhaLocalizacaoListener.Latitude, MinhaLocalizacaoListener.Longitude);
    }

    public void mostrarGoogleMaps(double latitude, double longitude){
        WebView wv = findViewById(R.id.webv); //LOCALIZA O WEBVIEW NA INTERFACE
        wv.getSettings().setJavaScriptEnabled(true);//HABILITA O USO DE JAVASCRIPT
        wv.loadUrl("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude); //CARREGA A PAGINA DA API
    }

}