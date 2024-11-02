package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
    private static final String KEY_GEOFENCES = "geofences";
    private static final String KEY_SELECTED_GEOFENCE = "selectedGeofence";
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
        if (json == null || json.equals("null")) {
            return new ArrayList<>(); // Return empty list if there are no contacts
        }
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
        if (json == null || json.equals("null")) {
            return new ArrayList<>(); // Return empty list if there are no medicines
        }
        Type type = new TypeToken<ArrayList<Medicine>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public void saveGeofences(ArrayList<Geofence> geofences) {
        Gson gson = new Gson();
        String json = gson.toJson(geofences);
        editor.putString(KEY_GEOFENCES, json);
        editor.commit();
    }

    public ArrayList<Geofence> getGeofences() {
        Gson gson = new Gson();
        String json = pref.getString(KEY_GEOFENCES, null);
        if (json == null || json.equals("null")) {
            return new ArrayList<>(); // Return empty list if there are no geofences
        }
        try {
            Type type = new TypeToken<ArrayList<Geofence>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            // Handle the case where the JSON is not in the expected format
            return new ArrayList<>(); // Return empty list if JSON is malformed
        }
    }

    public void saveSelectedGeofence(int position) {
        editor.putInt(KEY_SELECTED_GEOFENCE, position);
        editor.commit();
    }

    public Geofence getSelectedGeofence() {
        int position = getSelectedGeofencePosition();
        ArrayList<Geofence> geofences = getGeofences();
        if (position != -1 && geofences != null && position < geofences.size()) {
            return geofences.get(position); // Return the selected geofence
        }
        return null; // Return null if no geofence is selected
    }

    public int getSelectedGeofencePosition() {
        return pref.getInt(KEY_SELECTED_GEOFENCE, -1);
    }

    public void saveDataToFirebase(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY_EMAIL, getUserEmail());
        data.put(KEY_SOS_MESSAGE, getSOSMessage());
        data.put(KEY_CONTACTS, getContacts());
        data.put(KEY_MEDICINES, getMedicines());
        data.put(KEY_GEOFENCES, getGeofences());

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
                        editor.putString(KEY_CONTACTS, contactsJson != null ? contactsJson : "[]");
                        String medicinesJson = gson.toJson(dataSnapshot.child(KEY_MEDICINES).getValue());
                        editor.putString(KEY_MEDICINES, medicinesJson != null ? medicinesJson : "[]");
                        String geofencesJson = gson.toJson(dataSnapshot.child(KEY_GEOFENCES).getValue());
                        editor.putString(KEY_GEOFENCES, geofencesJson != null ? geofencesJson : "[]");
                        editor.commit();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error getting document
                });
    }
}