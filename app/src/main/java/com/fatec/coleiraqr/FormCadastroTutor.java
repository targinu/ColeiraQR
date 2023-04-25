package com.fatec.coleiraqr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FormCadastroTutor extends AppCompatActivity {

    private TextView bt_voltar_cadastro, editFoto;
    private EditText edit_nome,edit_sobrenome,edit_email,edit_telefone,edit_endereco,edit_senha;
    private Button bt_cadastrartutor;

    ImageView fotoTutor;

    String[] mensagens = {"Todos os campos devem ser preenchidos.", "Cadastro realizado com sucesso.", "As senhas devem coincidir"};
    String usuarioID;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    private StorageReference mStorageRef;
    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_tutor);

        getSupportActionBar().hide();
        IniciarComponentes();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        bt_cadastrartutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //PEGA OS VALORES DAS VARIAVEIS
                String nome = edit_nome.getText().toString();
                String sobrenome = edit_sobrenome.getText().toString();
                String email = edit_email.getText().toString();
                String telefone = edit_telefone.getText().toString();
                String endereco = edit_endereco.getText().toString();
                String senha = edit_senha.getText().toString();

                //CHECA A CONDIÇÃO DE NÃO VAZIO E ENVIA UMA MENSAGEM CASO ESTEJA VAZIA
                if(nome.isEmpty() || sobrenome.isEmpty() || email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v,mensagens[0],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    //DEIXA OS CAMPOS EM BRANCO
                    edit_nome.setText("");
                    edit_sobrenome.setText("");
                    edit_email.setText("");
                    edit_telefone.setText("");
                    edit_endereco.setText("");
                    edit_senha.setText("");

                }else{ //SE NÃO OCCORRER ERROS EXECUTA A FUNÇÃO DE CADASTRAR TUTOR
                    CadastrarTutor(v);
                }

            }
        });

        //NAVEGAÇÃO DA PARA A PAGINA ANTERIOR
        bt_voltar_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                Intent intent = new Intent(FormCadastroTutor.this,FormCadastro.class);
                startActivity(intent);
            }
        });

    }


    private void CadastrarTutor(View v){

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

            //CONEXÃO COM O BANCO DE DADOS FIREBASE, CRIANDO USUARIO COM EMAIL E SENHA
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){ //SE A CONEXÃO FOR SUCEDIDA

                        SalvarDadosTutor(); //SALVA DADOS NO BANCO

                        //IMPRIME UMA MENSAGEM DE SUCESSO
                        Snackbar snackbar = Snackbar.make(v,mensagens[1],Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();

                        //ZERA OS CAMPOS
                        edit_nome.setText("");
                        edit_sobrenome.setText("");
                        edit_email.setText("");
                        edit_telefone.setText("");
                        edit_endereco.setText("");
                        edit_senha.setText("");

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
                        edit_sobrenome.setText("");
                        edit_email.setText("");
                        edit_telefone.setText("");
                        edit_endereco.setText("");
                        edit_senha.setText("");

                    }
                }
            });

    }

    private void SalvarDadosTutor(){
        String nome = edit_nome.getText().toString();
        String sobrenome = edit_sobrenome.getText().toString();
        String telefone = edit_telefone.getText().toString();
        String endereco = edit_endereco.getText().toString();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome",nome);
        usuarios.put("sobrenome",sobrenome);
        usuarios.put("telefone",telefone);
        usuarios.put("endereco",endereco);
        usuarios.put("tipo","tutor");

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

    private void IniciarComponentes(){
        bt_voltar_cadastro = findViewById(R.id.bt_voltar_cadastro);
        edit_nome = findViewById(R.id.edit_nome);
        edit_sobrenome = findViewById(R.id.edit_sobrenome);
        edit_email = findViewById(R.id.edit_email);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_endereco = findViewById(R.id.edit_endereco);
        edit_senha = findViewById(R.id.edit_senha);
        bt_cadastrartutor = findViewById(R.id.bt_cadastrartutor);
        fotoTutor = findViewById(R.id.fotoTutor);
    }

}