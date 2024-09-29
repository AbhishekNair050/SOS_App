package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EmergencyServiceAdapter extends ArrayAdapter<EmergencyService> {
    private Context context;
    private List<EmergencyService> services;

    public EmergencyServiceAdapter(Context context, List<EmergencyService> services) {
        super(context, 0, services);
        this.context = context;
        this.services = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_emergency_service, parent, false);
        }

        EmergencyService service = services.get(position);

        TextView serviceName = convertView.findViewById(R.id.serviceName);
        serviceName.setText(service.getName());

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + service.getNumber()));
            context.startActivity(intent);
        });

        return convertView;
    }
}