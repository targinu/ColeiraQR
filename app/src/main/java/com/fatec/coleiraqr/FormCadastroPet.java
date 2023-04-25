package com.fatec.coleiraqr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.EncodeHintType;

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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FormCadastroPet extends AppCompatActivity {

    private TextView textPetFoto;
    private EditText edit_nome, edit_idade, edit_pelo, edit_raca, edit_temperamento;
    private Button bt_cadastrar_pet, bt_voltar;
    private ImageView ivQRCode, fotoPet;
    String usuarioID;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    private StorageReference mStorageRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_pet);

        getSupportActionBar().hide();
        IniciarComponentes();
        clickButton();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();



        bt_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                Intent intent = new Intent(FormCadastroPet.this,FormTelaUser.class);
                startActivity(intent);
            }
        });

        //EDITAR O USUARIO E TROCAR A FOTO DE PERFIL
        textPetFoto.setOnClickListener(new View.OnClickListener() {
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
                fotoPet.setImageURI(imageUri);
                enviarFoto(imageUri);
            }
        }
    }

    //CRIAR METODO DE SUBIR A FOTO DO PET
    private void enviarFoto(Uri imageUri) {
        StorageReference imgRef = storageReference.child("pets/"+firebaseAuth.getCurrentUser().getUid()+"/foto.jpg"); //REFERENCIANDO AO BANCO DE DADOS
        imgRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(fotoPet);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormCadastroPet.this,"Falha ao carregar imagem",Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void clickButton(){
        bt_cadastrar_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalvarDadosPet();
                gerarQRCode();
            }
        });
    }

    private void gerarQRCode(){
        //PASSANDO AS VARIAVEIS A SEREM LIDAS PELO GERADOR DE QR CODE
        String nome = edit_nome.getText().toString();
        String idade = edit_idade.getText().toString();
        String pelo = edit_pelo.getText().toString();
        String raca = edit_raca.getText().toString();
        String temperamento = edit_temperamento.getText().toString();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter(); //CRIANDO UM NOVO GERADOR DE QR CODE

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(nome + idade + pelo + raca + temperamento, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void SalvarDadosPet(){
        String nome = edit_nome.getText().toString();
        String idade = edit_idade.getText().toString();
        String pelo = edit_pelo.getText().toString();
        String raca = edit_raca.getText().toString();
        String temperamento = edit_temperamento.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> pets = new HashMap<>();
        pets.put("nome",nome);
        pets.put("idade",idade);
        pets.put("pelo",pelo);
        pets.put("raca",raca);
        pets.put("temperamento",temperamento);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("pets").document(usuarioID);
        documentReference.set(pets).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        edit_nome = findViewById(R.id.edit_nome);
        edit_idade = findViewById(R.id.edit_idade);
        edit_pelo = findViewById(R.id.edit_pelo);
        edit_raca = findViewById(R.id.edit_raca);
        edit_temperamento = findViewById(R.id.edit_temperamento);
        bt_cadastrar_pet = findViewById(R.id.bt_cadastrar_pet);
        ivQRCode = findViewById(R.id.ivQRCode);
        bt_voltar = findViewById(R.id.bt_voltar);
        textPetFoto = findViewById(R.id.textPetFoto);
    }
}