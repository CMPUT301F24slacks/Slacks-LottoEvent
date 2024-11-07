package com.example.slacks_lottoevent.refactor;

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

import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.data.Facility;

public class AddFacilityFragment extends DialogFragment {
    public Facility facility;
    private Boolean isEdit;

    interface AddFacilityDialogListener {
        void addFacility(Facility facility);
        void updateFacility();
    }
    private AddFacilityDialogListener listener;

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

    public AddFacilityFragment(Facility facility, Boolean isEdit){
        this.facility = facility;
        this.isEdit = isEdit;
    }
    public AddFacilityFragment(){
        this.facility = new Facility("FacilityName", "StreetAddress1", "StreetAddress2", "City", "Province", "Country", "PostalCode", "OrganizerId", "DeviceId");
        this.isEdit = false;
    }

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

        return builder
                .setView(view)
                .setTitle("Create/Edit Facility")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String facilityName = editFacilityName.getText().toString();
                    String streetAddress1 = editStreetAddress1.getText().toString();
                    String streetAddress2 = editStreetAddress2.getText().toString();
                    String city = editCity.getText().toString();
                    String province = editProvince.getText().toString();
                    String country = editCountry.getText().toString();
                    String postalCode = editPostalCode.getText().toString();
                    String deviceId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

                    if (isEdit){
                        facility.setFacilityName(facilityName);
                        facility.setStreetAddress1(streetAddress1);
                        facility.setStreetAddress2(streetAddress2);
                        facility.setCity(city);
                        facility.setProvince(province);
                        facility.setCountry(country);
                        facility.setPostalCode(postalCode);
                        listener.updateFacility();
                    } else {
                        // Ensure listener is set before calling addFacility()
                        if (listener != null) {
                            listener.addFacility(new Facility(facilityName, streetAddress1, streetAddress2, city, province, country, postalCode, deviceId, deviceId));
                        }
                    }
                })
                .create();

    }
}
