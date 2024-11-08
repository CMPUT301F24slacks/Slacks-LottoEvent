package com.example.slacks_lottoevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 * AddFacilityFragment is a DialogFragment that allows the user to create or edit a Facility.
 */
public class AddFacilityFragment extends DialogFragment {
    public Facility facility;
    private Boolean isEdit;

    /**
     * AddFacilityDialogListener is an interface that must be implemented by the parent Fragment or Activity
     * to handle the user's input when adding or editing a Facility.
     */
    interface AddFacilityDialogListener {
        void addFacility(Facility facility);
        void updateFacility();
    }
    private AddFacilityDialogListener listener;

    /**
     * onAttach is called when the fragment is associated with an activity.
     * @param context Context object
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof AddFacilityDialogListener) {
            listener = (AddFacilityDialogListener) parentFragment; // Set listener from ParentFragment
        } else if (context instanceof AddFacilityDialogListener) {
            listener = (AddFacilityDialogListener) context; // Fallback to Activity if ParentFragment is not the listener
        } else {
            throw new ClassCastException("Parent fragment or activity must implement AddFacilityDialogListener");
        }
    }

    /**
     * Constructor for AddFacilityFragment
     * @param facility The Facility object to edit
     * @param isEdit Boolean flag to determine if the dialog is for editing an existing Facility
     */
    public AddFacilityFragment(Facility facility, Boolean isEdit){
        this.facility = facility;
        this.isEdit = isEdit;
    }

    /**
     * Constructor for AddFacilityFragment
     * Default constructor for creating a new Facility
     */
    public AddFacilityFragment(){
        this.facility = new Facility("FacilityName", "StreetAddress1", "StreetAddress2", "City", "Province", "Country", "PostalCode", "OrganizerId", "DeviceId");
        this.isEdit = false;
    }

    /**
     * onCreateDialog creates the AlertDialog for the AddFacilityFragment
     * @param savedInstanceState Bundle object containing the saved state
     * @return Dialog object
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_facility, null);
        EditText editFacilityName = view.findViewById(R.id.facility_name_input);
        EditText editStreetAddress1 = view.findViewById(R.id.street_address_1);
        EditText editStreetAddress2 = view.findViewById(R.id.street_address_2);
        EditText editCity = view.findViewById(R.id.city);
        EditText editProvince = view.findViewById(R.id.province);
        EditText editCountry = view.findViewById(R.id.country);
        EditText editPostalCode = view.findViewById(R.id.postal_code);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (isEdit && facility != null) {
            editFacilityName.setText(facility.getFacilityName());
            editStreetAddress1.setText(facility.getStreetAddress1());
            editStreetAddress2.setText(facility.getStreetAddress2());
            editCity.setText(facility.getCity());
            editProvince.setText(facility.getProvince());
            editCountry.setText(facility.getCountry());
            editPostalCode.setText(facility.getPostalCode());
        }

        builder.setView(view)
                .setTitle("Create/Edit Facility")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", null);

        AlertDialog dialog = builder.create();

        // Override the positive button click listener to perform validation
        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String facilityName = editFacilityName.getText().toString().trim();
                String streetAddress1 = editStreetAddress1.getText().toString().trim();
                String streetAddress2 = editStreetAddress2.getText().toString().trim();
                String city = editCity.getText().toString().trim();
                String province = editProvince.getText().toString().trim();
                String country = editCountry.getText().toString().trim();
                String postalCode = editPostalCode.getText().toString().trim();


                if (facilityName.isEmpty()) {
                    editFacilityName.setError("Facility Name is required");
                    return;
                }
                if (streetAddress1.isEmpty()) {
                    editStreetAddress1.setError("Street Address 1 is required");
                    return;
                }
                if (city.isEmpty()) {
                    editCity.setError("City is required");
                    return;
                }
                if (province.isEmpty()) {
                    editProvince.setError("Province is required");
                    return;
                }
                if (country.isEmpty()) {
                    editCountry.setError("Country is required");
                    return;
                }
                if (postalCode.isEmpty()) {
                    editPostalCode.setError("Postal Code is required");
                    return;
                }


                String deviceId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                if (isEdit) {
                    facility.setFacilityName(facilityName);
                    facility.setStreetAddress1(streetAddress1);
                    facility.setStreetAddress2(streetAddress2);
                    facility.setCity(city);
                    facility.setProvince(province);
                    facility.setCountry(country);
                    facility.setPostalCode(postalCode);
                    listener.updateFacility();
                } else {
                    if (listener != null) {
                        listener.addFacility(new Facility(facilityName, streetAddress1, streetAddress2, city, province, country, postalCode, deviceId, deviceId));
                    }
                }

                dialog.dismiss(); // Close dialog if validation passes
            });
        });

        return dialog;
    }

}
