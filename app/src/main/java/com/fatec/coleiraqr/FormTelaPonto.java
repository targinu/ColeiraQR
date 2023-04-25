package com.fatec.coleiraqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

public class FormTelaPonto extends AppCompatActivity {

    private TextView textRazaoSocial,textEmail,textEndereco,textTelefone,textEditPonto;
    private Button bt_deslogar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String PontoID;

    ImageView fotoPonto;

    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_tela_user);

        getSupportActionBar().hide();
        IniciarComponentes();

        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(FormTelaPonto.this,FormLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        PontoID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("usuarios").document(PontoID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    textRazaoSocial.setText(documentSnapshot.getString("razao"));
                    textEmail.setText(email);
                    textEndereco.setText(documentSnapshot.getString("endereco"));
                    textTelefone.setText(documentSnapshot.getString("telefone"));
                }
            }
        });
    }

    private void IniciarComponentes(){
        textRazaoSocial = findViewById(R.id.textRazaoSocial);
        textEmail = findViewById(R.id.textEmail);
        textEndereco = findViewById(R.id.textEndereco);
        textTelefone = findViewById(R.id.textTelefone);
        bt_deslogar = findViewById(R.id.bt_deslogar);
    }

}