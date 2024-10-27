//package com.example.slacks_lottoevent;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.DialogFragment;
//
//public class AddFacilityFragment extends DialogFragment {
//    public Facility facility;
//
//    interface AddFacilityDialogListener {
//        void addFacility(Facility facility);
//    }
//    private AddFacilityDialogListener listener;
//
//    public AddFacilityFragment(){
//        this.facility = facility;
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.facility_add_fragment, null);
//        EditText editFacilityName = view.findViewById(R.id.facility_name_input);
//        EditText editStreetAddress1 = view.findViewById(R.id.street_address_1);
//        EditText editStreetAddress2 = view.findViewById(R.id.street_address_2);
//        EditText editCity = view.findViewById(R.id.city);
//        EditText editProvince = view.findViewById(R.id.province);
//        EditText editCountry = view.findViewById(R.id.country);
//        EditText editPostalCode = view.findViewById(R.id.postal_code);
//        // Organizer facilityOrganizer = facility.getOrganizer();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//
//        editFacilityName.setText("");
////        editStreetAddress1.setText(facility.getStreetAddress1());
//        editStreetAddress1.setText("");
//        editStreetAddress2.setText("");
//        editCity.setText("");
//        editProvince.setText("");
//        editCountry.setText("");
//        editPostalCode.setText("");
//
//        return builder
//                .setView(view)
//                .setTitle("Create Facility")
//                .setNegativeButton("Cancel", null)
//                .setPositiveButton("Confirm", (dialog, which) -> {
//                    String facilityName = editFacilityName.getText().toString();
//                    String streetAddress1 = editStreetAddress1.getText().toString();
//                    String streetAddress2 = editStreetAddress2.getText().toString();
//                    String city = editCity.getText().toString();
//                    String province = editProvince.getText().toString();
//                    String country = editCountry.getText().toString();
//                    String postalCode = editPostalCode.getText().toString();
//
//                    listener.addFacility(new Facility(facilityName, streetAddress1, streetAddress2, city, province, country, postalCode));
//                    }
//                )
//                .create();
//    }
//
//}
package com.example.slacks_lottoevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddFacilityFragment extends DialogFragment {
    public Facility facility;

    interface AddFacilityDialogListener {
        void addFacility(Facility facility);
    }
    private AddFacilityDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Set listener from the hosting Activity or Fragment
            listener = (AddFacilityDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddFacilityDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.facility_add_fragment, null);
        EditText editFacilityName = view.findViewById(R.id.facility_name_input);
        EditText editStreetAddress1 = view.findViewById(R.id.street_address_1);
        EditText editStreetAddress2 = view.findViewById(R.id.street_address_2);
        EditText editCity = view.findViewById(R.id.city);
        EditText editProvince = view.findViewById(R.id.province);
        EditText editCountry = view.findViewById(R.id.country);
        EditText editPostalCode = view.findViewById(R.id.postal_code);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        editFacilityName.setText("");
        editStreetAddress1.setText("");
        editStreetAddress2.setText("");
        editCity.setText("");
        editProvince.setText("");
        editCountry.setText("");
        editPostalCode.setText("");

        return builder
                .setView(view)
                .setTitle("Create Facility")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String facilityName = editFacilityName.getText().toString();
                    String streetAddress1 = editStreetAddress1.getText().toString();
                    String streetAddress2 = editStreetAddress2.getText().toString();
                    String city = editCity.getText().toString();
                    String province = editProvince.getText().toString();
                    String country = editCountry.getText().toString();
                    String postalCode = editPostalCode.getText().toString();

                    // Ensure listener is set before calling addFacility()
                    if (listener != null) {
                        listener.addFacility(new Facility(facilityName, streetAddress1, streetAddress2, city, province, country, postalCode));
                    }
                })
                .create();

    }
}
