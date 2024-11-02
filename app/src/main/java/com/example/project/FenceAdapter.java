package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class FenceAdapter extends ArrayAdapter<Geofence> {
    private final List<Geofence> fences;
    private final Context context;
    private int selectedPosition = -1;
    private SessionManager sessionManager;

    public FenceAdapter(Context context, List<Geofence> fences) {
        super(context, R.layout.fence_item, fences);
        this.context = context;
        this.fences = fences;
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fence_item, parent, false);
        }

        Geofence fence = fences.get(position);

        TextView fenceTextView = convertView.findViewById(R.id.fenceTextView);
        RadioButton fenceRadioButton = convertView.findViewById(R.id.fenceRadioButton);

        fenceTextView.setText(fence.toString());
        fenceRadioButton.setChecked(position == selectedPosition);

        fenceRadioButton.setOnClickListener(v -> {
            setSelectedPosition(position);
        });

        return convertView;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        Geofence selectedGeofence = fences.get(position);
        sessionManager.saveSelectedGeofence(position);

        saveGeofenceDetails(selectedGeofence);
        notifyDataSetChanged();
        notifyLocationService();
    }

    private void saveGeofenceDetails(Geofence geofence) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("latitude", (float) geofence.getLatitude());
        editor.putFloat("longitude", (float) geofence.getLongitude());
        editor.putInt("radius", geofence.getRadius());
        editor.apply();
    }

    private void notifyLocationService() {
        Geofence selectedGeofence = fences.get(selectedPosition);
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction("UPDATE_GEOFENCE");
        intent.putExtra("latitude", selectedGeofence.getLatitude());
        intent.putExtra("longitude", selectedGeofence.getLongitude());
        intent.putExtra("radius", selectedGeofence.getRadius());
        context.stopService(intent);
        context.startService(intent);
    }


}
