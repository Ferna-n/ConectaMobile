package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button registerButton;
    private TextView signInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar las vistas
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        signInLink = findViewById(R.id.signInLink);

        // Lógica para registrar al usuario
        registerButton.setOnClickListener(v -> registerUser());

        // Lógica para redirigir a la pantalla de inicio de sesión
        signInLink.setOnClickListener(v -> {
            // Redirigir a la pantalla de inicio de sesión (LoginActivity)
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        // Obtener los datos de los campos
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validación de los campos
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Por favor ingresa tu nombre.");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Por favor ingresa tu correo.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Por favor ingresa tu contraseña.");
            return;
        }

        // Crear el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si el registro es exitoso, obtener el usuario actual
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Guardar la información del usuario en Firebase Realtime Database
                        if (user != null) {
                            String userId = user.getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                            // Crear un objeto para almacenar la información del usuario
                            User newUser = new User(userId, name, email);
                            userRef.setValue(newUser)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Si la información del usuario se guarda correctamente, redirigir a la pantalla principal
                                            Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                            finish(); // Cerrar la actividad de registro y volver al login
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Error al guardar los datos. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Si el registro falla, mostrar un mensaje de error
                        Toast.makeText(RegisterActivity.this, "Registro fallido: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
