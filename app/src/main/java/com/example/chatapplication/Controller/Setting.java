package com.example.chatapplication.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapplication.Model.Users;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Setting extends AppCompatActivity {
    ImageView setprofile;
    EditText setname, setstatus;
    Button donebut;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri setImageUri;
    String email,password;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        donebut = findViewById(R.id.donebutt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);

        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = Objects.requireNonNull(snapshot.child("mail").getValue()).toString();
                password = Objects.requireNonNull(snapshot.child("password").getValue()).toString();
                String name = Objects.requireNonNull(snapshot.child("userName").getValue()).toString();
                String profile = Objects.requireNonNull(snapshot.child("profilePic").getValue()).toString();
                String status = Objects.requireNonNull(snapshot.child("status").getValue()).toString();
                setname.setText(name);
                setstatus.setText(status);
                Picasso.get().load(profile).into(setprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setprofile.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });

        donebut.setOnClickListener(view -> {
            progressDialog.show();

            String name = setname.getText().toString();
            String status = setstatus.getText().toString();
            if (setImageUri != null) {
                // Upload new image
                storageReference.putFile(setImageUri)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return storageReference.getDownloadUrl();
                        })
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String finalImageUri = task.getResult().toString();
                                saveUserData(reference, name, status, finalImageUri);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Use existing image
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String finalImageUri = uri.toString();
                            saveUserData(reference, name, status, finalImageUri);
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Failed to load profile image URL", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                setImageUri = data.getData();
                setprofile.setImageURI(setImageUri);
            }
        }
    }

    private void saveUserData(DatabaseReference reference, String name, String status, String profileUri) {
        Users users = new Users(auth.getUid(), name, email, password, profileUri, status);
        reference.setValue(users).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(Setting.this, "Data is saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Setting.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(Setting.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}