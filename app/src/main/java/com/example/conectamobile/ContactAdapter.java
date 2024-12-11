package com.example.conectamobile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseAdapter {

    private List<Contact> contactList;
    private List<Contact> contactListFull;

    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
        this.contactListFull = new ArrayList<>(contactList);  // Guardamos una copia para el filtro
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        }

        Contact contact = contactList.get(position);
        TextView nameTextView = convertView.findViewById(R.id.contactName);
        TextView emailTextView = convertView.findViewById(R.id.contactEmail);
        Button chatButton = convertView.findViewById(R.id.chatButton);

        nameTextView.setText(contact.getName());
        emailTextView.setText(contact.getEmail());

        // Al hacer clic en el botón de chat, redirigir al ChatActivity
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(parent.getContext(), ChatActivity.class);
            intent.putExtra("contactId", contact.getContactId());
            intent.putExtra("contactName", contact.getName());
            parent.getContext().startActivity(intent);
        });

        return convertView;
    }

    public void updateContacts(List<Contact> contactList) {
        this.contactList = contactList;
        this.contactListFull = new ArrayList<>(contactList);  // Guardamos una copia de los datos
        notifyDataSetChanged();
    }

    public void filter(String query) {
        contactList.clear();
        if (query.isEmpty()) {
            contactList.addAll(contactListFull);
        } else {
            query = query.toLowerCase();
            for (Contact contact : contactListFull) {
                if (contact.getName().toLowerCase().contains(query)) {
                    contactList.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }
}
