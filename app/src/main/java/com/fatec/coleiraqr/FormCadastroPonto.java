package com.fatec.coleiraqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class FormCadastroPonto extends AppCompatActivity {

    private TextView bt_voltar_cadastro;
    private Button bt_cadastrarponto;
    private EditText edit_razao_social,edit_cnpj,edit_email,edit_telefone,edit_endereco,edit_senha;
    String[] mensagens = {"Todos os campos devem ser preenchidos.", "Cadastro realizado com sucesso.", "As senhas devem coincidir"};
    String pontoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_ponto);

        getSupportActionBar().hide();
        IniciarComponentes();

        bt_cadastrarponto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String razao = edit_razao_social.getText().toString();
                String cnpj = edit_cnpj.getText().toString();
                String email = edit_email.getText().toString();
                String telefone = edit_telefone.getText().toString();
                String endereco = edit_endereco.getText().toString();
                String senha = edit_senha.getText().toString();

                if(razao.isEmpty() || cnpj.isEmpty() || email.isEmpty() || telefone.isEmpty() || endereco.isEmpty() ||senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v,mensagens[0],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    edit_razao_social.setText("");
                    edit_cnpj.setText("");
                    edit_email.setText("");
                    edit_telefone.setText("");
                    edit_endereco.setText("");
                    edit_senha.setText("");

                }else{
                    CadastrarPonto(v);
                }

            }
        });

        bt_voltar_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCadastroPonto.this,FormCadastro.class);
                startActivity(intent);
            }
        });
    }

    private void CadastrarPonto(View v){

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    SalvarDadosPonto();

                    Snackbar snackbar = Snackbar.make(v,mensagens[1],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    edit_razao_social.setText("");
                    edit_cnpj.setText("");
                    edit_email.setText("");
                    edit_telefone.setText("");
                    edit_endereco.setText("");
                    edit_senha.setText("");

                }else{
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

                    Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    edit_razao_social.setText("");
                    edit_cnpj.setText("");
                    edit_email.setText("");
                    edit_telefone.setText("");
                    edit_endereco.setText("");
                    edit_senha.setText("");

                }
            }
        });

    }

    private void SalvarDadosPonto(){
        String razao = edit_razao_social.getText().toString();
        String cnpj = edit_cnpj.getText().toString();
        String telefone = edit_telefone.getText().toString();
        String endereco = edit_endereco.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("razao",razao);
        usuarios.put("cnpj",cnpj);
        usuarios.put("telefone",telefone);
        usuarios.put("endereco",endereco);
        usuarios.put("tipo","ponto");

        pontoID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("usuarios").document(pontoID);
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

    private void IniciarComponentes(){
        bt_voltar_cadastro = findViewById(R.id.bt_voltar_cadastro);
        edit_razao_social = findViewById(R.id.edit_razao_social);
        edit_cnpj = findViewById(R.id.edit_cnpj);
        edit_email = findViewById(R.id.edit_email);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_endereco = findViewById(R.id.edit_endereco);
        edit_senha = findViewById(R.id.edit_senha);
        bt_cadastrarponto = findViewById(R.id.bt_cadastrarponto);
    }
}
