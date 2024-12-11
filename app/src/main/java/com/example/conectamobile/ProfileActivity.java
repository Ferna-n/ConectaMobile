package com.example.conectamobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText nameEditText, emailEditText;
    private Button uploadPhotoButton, saveProfileButton, logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializar vistas
        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }

        // Inicializar referencia a Storage y Database
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        storageRef = FirebaseStorage.getInstance().getReference("profile_pictures");

        // Cargar los datos actuales del usuario
        loadUserProfile(currentUser);

        // Configurar botones
        uploadPhotoButton.setOnClickListener(v -> openFileChooser());
        saveProfileButton.setOnClickListener(v -> saveProfile());
        logoutButton.setOnClickListener(v -> logout());
    }

    // Función para cargar los datos del usuario
    private void loadUserProfile(FirebaseUser currentUser) {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String name = task.getResult().child("name").getValue(String.class);
                String email = task.getResult().child("email").getValue(String.class);
                String photoUrl = task.getResult().child("photoUrl").getValue(String.class);

                nameEditText.setText(name);
                emailEditText.setText(email);

                // Si hay una foto de perfil en Firebase, cargarla con Picasso
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Picasso.get().load(photoUrl).into(profileImageView);
                }
            }
        });
    }

    // Función para abrir el selector de imágenes
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Recibir la imagen seleccionada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri); // Mostrar la imagen seleccionada
        }
    }

    // Función para guardar los cambios de perfil
    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa todos los datos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa para actualizar los datos
        HashMap<String, Object> profileUpdates = new HashMap<>();
        profileUpdates.put("name", name);
        profileUpdates.put("email", email);

        if (selectedImageUri != null) {
            // Subir la foto seleccionada a Firebase Storage
            StorageReference fileRef = storageRef.child(currentUserId() + ".jpg");
            fileRef.putFile(selectedImageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Obtener la URL de la imagen subida
                    fileRef.getDownloadUrl().addOnCompleteListener(urlTask -> {
                        if (urlTask.isSuccessful()) {
                            String photoUrl = urlTask.getResult().toString();
                            profileUpdates.put("photoUrl", photoUrl);

                            // Actualizar los datos del usuario en la base de datos
                            updateUserProfile(profileUpdates);
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Si no se selecciona una nueva imagen, actualizar solo los demás datos
            updateUserProfile(profileUpdates);
        }
    }

    // Función para actualizar los datos en la base de datos
    private void updateUserProfile(HashMap<String, Object> profileUpdates) {
        userRef.updateChildren(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Función para cerrar sesión
    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }

    // Función para obtener el ID del usuario actual
    private String currentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : "";
    }
}