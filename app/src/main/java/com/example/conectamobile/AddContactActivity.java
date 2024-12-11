package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

public class AddContactActivity extends AppCompatActivity {

    private ListView usersListView;
    private UsersAdapter usersAdapter;
    private EditText searchEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private List<User> userList;
    private List<User> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(AddContactActivity.this, LoginActivity.class));
            finish();
        }

        // Referencias a vistas
        usersListView = findViewById(R.id.usersListView);  // Usamos ListView
        searchEditText = findViewById(R.id.searchEditText);

        // Inicializar ListView
        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        usersAdapter = new UsersAdapter(filteredList);
        usersListView.setAdapter(usersAdapter);

        // Obtener referencia a los usuarios de Firebase
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Buscar usuarios por nombre
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Obtener todos los usuarios registrados
        getAllUsers();
    }

    // Obtener todos los usuarios registrados
    private void getAllUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Limpiar la lista antes de agregar nuevos usuarios
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getUserId() != null && !user.getUserId().equals(mAuth.getCurrentUser().getUid())) {
                        // Excluir al usuario actual de la lista
                        userList.add(user);
                    }
                }
                filterUsers("");  // Inicializa la lista con todos los usuarios
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddContactActivity.this, "Error al obtener usuarios.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filtrar usuarios según la búsqueda
    private void filterUsers(String query) {
        filteredList.clear();
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        usersAdapter.notifyDataSetChanged();
    }

    // Adaptador para mostrar usuarios
    class UsersAdapter extends android.widget.BaseAdapter {

        private List<User> users;

        public UsersAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.item_user, parent, false);
            }

            User user = users.get(position);

            TextView nameTextView = convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(user.getName());

            // Al hacer clic en un usuario, agregarlo a los contactos
            convertView.setOnClickListener(v -> addContact(user));

            return convertView;
        }
    }

    private void addContact(User user) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("contacts");

            String contactId = contactsRef.push().getKey();
            Contact contact = new Contact(contactId, user.getName(), user.getEmail());
            contactsRef.child(contactId).setValue(contact)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddContactActivity.this, "Contacto agregado", Toast.LENGTH_SHORT).show();
                            // Regresar a la pantalla principal
                            Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AddContactActivity.this, "Error al agregar contacto", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
