// SessionManager.java
package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final String PREF_NAME = "LoginSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SOS_MESSAGE = "sosMessage";
    private static final String KEY_CONTACTS = "contacts";
    private static final String KEY_MEDICINES = "medicines";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private DatabaseReference databaseReference;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public void createLoginSession(String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void saveSOSMessage(String message) {
        editor.putString(KEY_SOS_MESSAGE, message);
        editor.commit();
    }

    public String getSOSMessage() {
        String sosMessage = pref.getString(KEY_SOS_MESSAGE, null);
        if (sosMessage == null || sosMessage.isEmpty()) {
            return "EMERGENCY";
        }
        return sosMessage;
    }

    public void saveContacts(ArrayList<Contact> contacts) {
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        editor.putString(KEY_CONTACTS, json);
        editor.commit();
    }

    public ArrayList<Contact> getContacts() {
        Gson gson = new Gson();
        String json = pref.getString(KEY_CONTACTS, null);
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveMedicines(ArrayList<Medicine> medicines) {
        Gson gson = new Gson();
        String json = gson.toJson(medicines);
        editor.putString(KEY_MEDICINES, json);
        editor.commit();
    }

    public ArrayList<Medicine> getMedicines() {
        Gson gson = new Gson();
        String json = pref.getString(KEY_MEDICINES, null);
        Type type = new TypeToken<ArrayList<Medicine>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public void saveDataToFirebase(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY_EMAIL, getUserEmail());
        data.put(KEY_SOS_MESSAGE, getSOSMessage());
        data.put(KEY_CONTACTS, getContacts());
        data.put(KEY_MEDICINES, getMedicines());

        databaseReference.child(userId).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error writing document: " + e);
                });
    }

    public void loadDataFromFirebase(String userId) {
        databaseReference.child(userId).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        editor.putString(KEY_EMAIL, dataSnapshot.child(KEY_EMAIL).getValue(String.class));
                        editor.putString(KEY_SOS_MESSAGE, dataSnapshot.child(KEY_SOS_MESSAGE).getValue(String.class));
                        Gson gson = new Gson();
                        String contactsJson = gson.toJson(dataSnapshot.child(KEY_CONTACTS).getValue());
                        editor.putString(KEY_CONTACTS, contactsJson);
                        String medicinesJson = gson.toJson(dataSnapshot.child(KEY_MEDICINES).getValue());
                        editor.putString(KEY_MEDICINES, medicinesJson);
                        editor.commit();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error getting document
                });
    }
}