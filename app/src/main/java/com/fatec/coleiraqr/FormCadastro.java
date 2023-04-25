package com.fatec.coleiraqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FormCadastro extends AppCompatActivity {

    private TextView bt_voltar_cadastro;
    private TextView bt_cadastro_tutor;
    private TextView bt_cadastro_ponto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();

        bt_voltar_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCadastro.this,FormLogin.class);
                startActivity(intent);
            }
        });

        bt_cadastro_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCadastro.this,FormCadastroTutor.class);
                startActivity(intent);
            }
        });

        bt_cadastro_ponto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCadastro.this,FormCadastroPonto.class);
                startActivity(intent);
            }
        });

    }

    private void IniciarComponentes(){
        bt_voltar_cadastro = findViewById(R.id.bt_voltar_cadastro);
        bt_cadastro_tutor = findViewById(R.id.bt_cadastro_tutor);
        bt_cadastro_ponto = findViewById(R.id.bt_cadastro_ponto);
    }

}