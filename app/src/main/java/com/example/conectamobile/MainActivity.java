package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView contactsListView;
    private ContactAdapter contactAdapter;
    private EditText searchBar;
    private FirebaseAuth mAuth;
    private DatabaseReference contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Verificar si el usuario está logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Si no está logueado, redirigir al login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Inicializar las vistas
        contactsListView = findViewById(R.id.contactsListView);
        searchBar = findViewById(R.id.searchBar);
        Button profileButton = findViewById(R.id.profileButton);
        Button addContactButton = findViewById(R.id.addContactButton);

        // Inicializar adaptador
        contactAdapter = new ContactAdapter(new ArrayList<>());
        contactsListView.setAdapter(contactAdapter);

        // Obtener la referencia a la base de datos de los contactos del usuario
        String currentUserId = currentUser.getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("contacts");

        // Obtener los contactos del usuario
        getContacts();

        // Lógica para buscar contactos
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filtrar contactos basados en la búsqueda
                contactAdapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Redirigir al perfil
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Redirigir a la pantalla de agregar contacto
        addContactButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        });
    }

    private void getContacts() {
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Contact> contactList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null) {
                        contactList.add(contact);
                    }
                }
                contactAdapter.updateContacts(contactList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error al obtener los contactos.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
