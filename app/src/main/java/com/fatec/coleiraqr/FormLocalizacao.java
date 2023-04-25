package com.fatec.coleiraqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class FormLocalizacao extends AppCompatActivity {

    private TextView bt_voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_localizacao);

        getSupportActionBar().hide();
        IniciarComponentes();
        buscarInformacoesGPS();

        bt_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLocalizacao.this,FormEncontro.class);
                startActivity(intent);
            }
        });

    }

    public void buscarInformacoesGPS(){
        //CHECA PERMISSÕES DE LOCALIZAÇÃO
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(FormLocalizacao.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(FormLocalizacao.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(FormLocalizacao.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }

        LocationManager mLocManager = (LocationManager) getSystemService(FormLocalizacao.this.LOCATION_SERVICE); //PEDE A LOCALIZAÇÃO PARA O SISTEMA
        LocationListener mLocListener = new MinhaLocalizacaoListener(); //INSTANCIA A CLASSE CRIADA PARA ARMAZENAR LATITUDE E LONGITUDE

        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2000, mLocListener); //ATUALIZA AS INFORMAÇÕES

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //VERIFICA SE AS INFORMAÇÕES FORAM PROCESSADAS E FAZENDO UM TEXTO
            String texto = "Latitude: " + MinhaLocalizacaoListener.Latitude + "\n" +
                    "Longitude: " + MinhaLocalizacaoListener.Longitude + "\n";
            Toast.makeText(FormLocalizacao.this, texto, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(FormLocalizacao.this, "GPS DESABILITADO", Toast.LENGTH_LONG).show();
        }

        this.mostrarGoogleMaps(MinhaLocalizacaoListener.Latitude, MinhaLocalizacaoListener.Longitude);
    }

    public void mostrarGoogleMaps(double latitude, double longitude){
        WebView wv = findViewById(R.id.webv); //LOCALIZA O WEBVIEW NA INTERFACE
        wv.getSettings().setJavaScriptEnabled(true);//HABILITA O USO DE JAVASCRIPT
        wv.loadUrl("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude); //CARREGA A PAGINA DA API
    }

    private void IniciarComponentes(){
        bt_voltar = findViewById(R.id.bt_voltar);
    }

}