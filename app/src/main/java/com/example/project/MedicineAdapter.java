package com.example.project;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicineAdapter extends ArrayAdapter<Medicine> {
    private ArrayList<Medicine> medicines;
    private SessionManager sessionManager;

    public MedicineAdapter(Context context, ArrayList<Medicine> medicines) {
        super(context, 0, medicines);
        this.medicines = medicines;
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Medicine medicine = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_medicine, parent, false);
        }

        TextView medicineName = convertView.findViewById(R.id.medicineName);
        TextView timings = convertView.findViewById(R.id.timings);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        medicineName.setText(medicine.getName());
        timings.setText(medicine.getTimings().toString());

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Medicine")
                    .setMessage("Are you sure you want to delete this medicine?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        medicines.remove(position);
                        sessionManager.saveMedicines(medicines);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }
}