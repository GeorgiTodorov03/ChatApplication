package com.example.chatapplication.Controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chatapplication.Model.Users;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class Registration extends AppCompatActivity {
    TextView loginbut;
    EditText rg_username, rg_email , rg_password, rg_repassword;
    Button rg_signup;
    CircleImageView rg_profileImg;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing The Account");
        progressDialog.setCancelable(false);
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        loginbut = findViewById(R.id.loginbut);
        rg_username = findViewById(R.id.rgusername);
        rg_email = findViewById(R.id.rgemail);
        rg_password = findViewById(R.id.rgpassword);
        rg_repassword = findViewById(R.id.rgrepassword);
        rg_profileImg = findViewById(R.id.profilerg0);
        rg_signup = findViewById(R.id.signupbutton);


        loginbut.setOnClickListener(v -> {
            Intent intent = new Intent(Registration.this, Login.class);
            startActivity(intent);
            finish();
        });

        rg_signup.setOnClickListener(v -> {
            String namee = rg_username.getText().toString();
            String emaill = rg_email.getText().toString();
            String Password = rg_password.getText().toString();
            String cPassword = rg_repassword.getText().toString();
            String status = "Hey I'm Using This Application";

            if (TextUtils.isEmpty(namee) || TextUtils.isEmpty(emaill) ||
                    TextUtils.isEmpty(Password) || TextUtils.isEmpty(cPassword)){
                progressDialog.dismiss();
                Toast.makeText(Registration.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
            }else  if (!emaill.matches(emailPattern)){
                progressDialog.dismiss();
                rg_email.setError("Type A Valid Email Here");
            }else if (Password.length()<6){
                progressDialog.dismiss();
                rg_password.setError("Password Must Be 6 Characters Or More");
            }else if (!Password.equals(cPassword)){
                progressDialog.dismiss();
                rg_password.setError("The Password Doesn't Match");
            }else {
                auth.createUserWithEmailAndPassword(emaill,Password).addOnCompleteListener(task -> {
                     if (task.isSuccessful()){
                         String id = task.getResult().getUser().getUid();
                         DatabaseReference reference = database.getReference().child("user").child(id);
                         StorageReference storageReference = storage.getReference().child("Upload").child(id);

                         if (imageURI!=null){
                             storageReference.putFile(imageURI).addOnCompleteListener(task1 -> {
                                 if (task1.isSuccessful()){
                                     storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                         imageuri = uri.toString();
                                         Users users = new Users(id,namee,emaill,Password,imageuri,status);
                                         reference.setValue(users).addOnCompleteListener(task2 -> {
                                             if (task2.isSuccessful()){
                                                 progressDialog.show();
                                                 Intent intent = new Intent(Registration.this, MainActivity.class);
                                                 startActivity(intent);
                                                 finish();
                                             }else {
                                                 Toast.makeText(Registration.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                             }
                                         });
                                     });
                                 }
                             });
                         }else {
                             String status1 = "Hey I'm Using This Application";
                             imageuri = "https://firebasestorage.googleapis.com/v0/b/av-messenger-dc8f3.appspot.com/o/man.png?alt=media&token=880f431d-9344-45e7-afe4-c2cafe8a5257";
                             Users users = new Users(id,namee,emaill,Password,imageuri, status1);
                             reference.setValue(users).addOnCompleteListener(task3 -> {
                                 if (task3.isSuccessful()){
                                     progressDialog.show();
                                     Intent intent = new Intent(Registration.this,MainActivity.class);
                                     startActivity(intent);
                                     finish();
                                 }else {
                                     Toast.makeText(Registration.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
                     }else {
                         Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                     }
                });
            }

        });


        rg_profileImg.setOnClickListener(v -> {
           Intent intent = new Intent();
           intent.setType("image/*");
           intent.setAction(Intent.ACTION_GET_CONTENT);
           startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10){
            if (data!=null){
                imageURI = data.getData();
                rg_profileImg.setImageURI(imageURI);
            }
        }
    }
}