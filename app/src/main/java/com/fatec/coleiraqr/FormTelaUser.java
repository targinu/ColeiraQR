package com.fatec.coleiraqr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class FormTelaUser extends AppCompatActivity {

    private TextView textNomeUser,textSobrenomeUser,textEmailUser,textEnderecoUser,textTelefoneUser,edit_foto;
    private Button bt_deslogar, bt_cadastrar_pet;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    ImageView fotoTutor;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    StorageReference mStorageRef;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_tela_user);

        getSupportActionBar().hide();
        IniciarComponentes();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference imgRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(fotoTutor);
            }
        });

        bt_cadastrar_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                Intent intent = new Intent(FormTelaUser.this, FormCadastroPet.class);
                startActivity(intent);
            }
        });

        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(FormTelaUser.this,FormLogin.class);
                startActivity(intent);
                finish();
            }
        });

        //EDITAR O USUARIO E TROCAR A FOTO DE PERFIL
        edit_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v3) {
                //ABRE A GALERIA PARA SELECIONAR UMA IMAGEM
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

    }

    @Override //SOLICITA PARA A GALERIA
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                fotoTutor.setImageURI(imageUri);
                enviarFoto(imageUri);
            }
        }
    }

    private void enviarFoto(Uri imageUri) {
        StorageReference imgRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg"); //REFERENCIANDO AO BANCO DE DADOS
        imgRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(fotoTutor);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormTelaUser.this,"Falha ao carregar imagem",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    textNomeUser.setText(documentSnapshot.getString("nome"));
                    textSobrenomeUser.setText(documentSnapshot.getString("sobrenome"));
                    textEmailUser.setText(email);
                    textEnderecoUser.setText(documentSnapshot.getString("endereco"));
                    textTelefoneUser.setText(documentSnapshot.getString("telefone"));
                }
            }
        });
    }

    private void IniciarComponentes(){
        textNomeUser = findViewById(R.id.textNomeUser);
        textEmailUser = findViewById(R.id.textEmailUser);
        textSobrenomeUser = findViewById(R.id.textSobrenomeUser);
        textEnderecoUser = findViewById(R.id.textEnderecoUser);
        textTelefoneUser = findViewById(R.id.textTelefoneUser);
        bt_deslogar = findViewById(R.id.bt_deslogar);
        fotoTutor = findViewById(R.id.fotoTutor);
        bt_cadastrar_pet = findViewById(R.id.bt_cadastrar_pet);
        edit_foto = findViewById(R.id.edit_foto);
    }

}