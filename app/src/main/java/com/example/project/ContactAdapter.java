package com.example.project;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Contact> contacts;
    private SessionManager sessionManager;

    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        }

        TextView contactName = convertView.findViewById(R.id.contactName);
        TextView contactNumber = convertView.findViewById(R.id.contactNumber);
        Button editButton = convertView.findViewById(R.id.editButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        Contact contact = contacts.get(position);
        contactName.setText(contact.getName());
        contactNumber.setText(contact.getNumber());

        editButton.setOnClickListener(v -> showEditContactPopup(position));
        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete this contact?")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        contacts.remove(position);
                        notifyDataSetChanged();
                        sessionManager.saveContacts(contacts); // Save updated contacts list to SessionManager
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        return convertView;
    }

    private void showEditContactPopup(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.popup_add_contact, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.contactTitle);
        title.setText("Edit Contact");

        EditText contactName = dialogView.findViewById(R.id.contactName);
        EditText contactNumber = dialogView.findViewById(R.id.contactNumber);
        Button saveContactButton = dialogView.findViewById(R.id.saveContactButton);

        Contact contact = contacts.get(position);
        contactName.setText(contact.getName());
        contactNumber.setText(contact.getNumber());

        AlertDialog alertDialog = builder.create();

        saveContactButton.setOnClickListener(v -> {
            String name = contactName.getText().toString();
            String number = contactNumber.getText().toString();
            if (!name.isEmpty() && !number.isEmpty()) {
                contact.setName(name);
                contact.setNumber(number);
                notifyDataSetChanged();
                sessionManager.saveContacts(contacts); // Save updated contacts list to SessionManager
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}