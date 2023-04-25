package com.fatec.coleiraqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormEncontro extends AppCompatActivity {

    //DECLARAÇÃO DAS VARIAVEIS USADAS
    private EditText edit_nome, edit_telefone, edit_endereco, edit_email, edit_senha;
    private Button bt_contato_dono, bt_contato_ponto, bt_deslogar;
    String[] mensagens = {"Todos os campos devem ser preenchidos.","Cadastro realizado com sucesso."}; //VETOR DE STRINGS COM MENSAGENS
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_encontro);

        //AÇÕES EXECUTADAS QUANDO A TELA É CRIADA
        getSupportActionBar().hide(); //ESCONDE A BARRA SUPERIOR
        IniciarComponentes();   //INICIA AS VARIAVEIS DECLARADAS
        BuscarInformacoesGPS(); //BUSCA AS INFORMAÇÕES DO GPS

        bt_contato_dono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //PEGA OS VALORES DAS VARIAVEIS
                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String telefone = edit_telefone.getText().toString();
                String endereco = edit_endereco.getText().toString();

                //CHECA A CONDIÇÃO DE NÃO VAZIO E ENVIA UMA MENSAGEM CASO ESTEJA VAZIA
                if(nome.isEmpty() || email.isEmpty() ||telefone.isEmpty() || endereco.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v,mensagens[0],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    //DEIXA OS CAMPOS EM BRANCO
                    edit_nome.setText("");
                    edit_email.setText("");
                    edit_telefone.setText("");
                    edit_endereco.setText("");

                }else{ //SE NÃO OCCORRER ERROS EXECUTA A FUNÇÃO DE CADASTRAR TUTOR
                    ContatoDono(v);
                }
            }
        });
    }

    private void ContatoDono(View v){
        //CONEXÃO COM O BANCO DE DADOS FIREBASE, CRIANDO USUARIO COM EMAIL E SENHA

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){ //SE A CONEXÃO FOR SUCEDIDA

                    SalvarDadosAnjo(); //SALVA DADOS NO BANCO

                    //IMPRIME UMA MENSAGEM DE SUCESSO
                    Snackbar snackbar = Snackbar.make(v,mensagens[1],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    //ZERA OS CAMPOS
                    edit_nome.setText("");
                    edit_telefone.setText("");
                    edit_endereco.setText("");

                }else{ //SE NÃO EXIBE MENSAGENS DE ERRO PARA CADA PROBLEMA POSSIVEL
                    String erro;
                    try{
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no mínimo 6 caracteres";

                    }catch (FirebaseAuthUserCollisionException e) {
                        erro = "Este email ja possui cadastro";

                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Email inválido";

                    }catch (Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }

                    //IMPRIME A MENSAGEM DE ERRO
                    Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    //ZERA OS CAMPOS
                    edit_nome.setText("");
                    edit_telefone.setText("");
                    edit_endereco.setText("");
                }
            }
        });
    }

    private void SalvarDadosAnjo(){
        String nome = edit_nome.getText().toString(); //PEGA INFORMAÇÕES DOS CAMPOS E TRANSFORMA EM VARIAVEL PARA MANIPULAÇÃO
        String telefone = edit_telefone.getText().toString();
        String endereco = edit_endereco.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance(); //CONECTA COM O BANCO DE DADOS

        Map<String,Object> usuarios = new HashMap<>(); //INSERE OS DADOS NO BANCO DE DADOS
        usuarios.put("nome",nome);
        usuarios.put("telefone",telefone);
        usuarios.put("endereco",endereco);
        usuarios.put("tipo","anjo");

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("usuarios").document(usuarioID);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db","Sucesso ao cadastrar");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error","Erro ao cadastrar" + e.toString());
                    }
                });
    }

    public void BuscarInformacoesGPS(){
        //CHECA PERMISSÕES DE LOCALIZAÇÃO
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(FormEncontro.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(FormEncontro.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(FormEncontro.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        LocationManager mLocManager = (LocationManager) getSystemService(FormEncontro.this.LOCATION_SERVICE); //PEDE A LOCALIZAÇÃO PARA O SISTEMA
        LocationListener mLocListener = new MinhaLocalizacaoListener(); //INSTANCIA A CLASSE CRIADA PARA ARMAZENAR LATITUDE E LONGITUDE

                                                                            //SE NÃO IR TROCAR PRA 0
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, mLocListener); //ATUALIZA AS INFORMAÇÕES

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //VERIFICA SE AS INFORMAÇÕES FORAM PROCESSADAS E FAZENDO UM TEXTO
            String texto = "Latitude: " + MinhaLocalizacaoListener.Latitude + "\n" +
                    "Longitude: " + MinhaLocalizacaoListener.Longitude + "\n";
            Toast.makeText(FormEncontro.this, texto, Toast.LENGTH_LONG).show();

            edit_endereco.setText(texto);

        } else {
            Toast.makeText(FormEncontro.this, "GPS DESABILITADO", Toast.LENGTH_LONG).show();
        }
    }

    private void IniciarComponentes(){
        bt_contato_dono = findViewById(R.id.bt_contato_dono);
        bt_contato_ponto = findViewById(R.id.bt_contato_ponto);
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_endereco = findViewById(R.id.edit_endereco);
        edit_senha = findViewById(R.id.edit_senha);
        bt_deslogar = findViewById(R.id.bt_deslogar);
    }

}