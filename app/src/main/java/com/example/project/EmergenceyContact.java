package com.example.project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EmergenceyContact extends AppCompatActivity {
    private ArrayList<Contact> contacts;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.emergencycontacts);

        contacts = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contacts);

        ListView listView = findViewById(R.id.contactListView);
        listView.setAdapter(contactAdapter);

        Button addContactButton = findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(v -> showAddContactPopup());
    }

    private void showAddContactPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_add_contact, null);
        builder.setView(dialogView);

        EditText contactName = dialogView.findViewById(R.id.contactName);
        EditText contactNumber = dialogView.findViewById(R.id.contactNumber);
        Button saveContactButton = dialogView.findViewById(R.id.saveContactButton);

        AlertDialog alertDialog = builder.create();

        saveContactButton.setOnClickListener(v -> {
            String name = contactName.getText().toString();
            String number = contactNumber.getText().toString();
            if (!name.isEmpty() && !number.isEmpty()) {
                contacts.add(new Contact(name, number));
                contactAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}