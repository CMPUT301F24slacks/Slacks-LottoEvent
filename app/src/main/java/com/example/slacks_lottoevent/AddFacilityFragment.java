package com.example.slacks_lottoevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
*
* AddFacilityFragment is a DialogFragment that allows the user to create or edit a Facility.
*
* Relevant Documentation:
* https://developers.google.com/maps/documentation/places/android-sdk/autocomplete-tutorial
*
* */
public class AddFacilityFragment extends DialogFragment {

    public Facility facility;
    private Boolean isEdit;
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private AddFacilityDialogListener listener;
    private Map<AutoCompleteTextView, String> validSelections = new HashMap<>();
    private boolean userSelectedFromDropdown = false;
    /**
     * AddFacilityDialogListener is an interface that must be implemented by the parent Fragment or Activity
     * to handle the user's input when adding or editing a Facility.
     */
    interface AddFacilityDialogListener {
        void addFacility(Facility facility);
        void updateFacility();
    }
    /**
     * Constructor for AddFacilityFragment
     * Default constructor for creating a new Facility
     */
    public AddFacilityFragment() {
        this.facility = new Facility("facilityName", "StreetAddress1", "StreetAddress2", "PostalCode", "OrganizerId", "DeviceId");
        this.isEdit = false;
    }

    /**
     * Constructor for AddFacilityFragment
     * @param facility The Facility object to edit
     * @param isEdit Boolean flag to determine if the dialog is for editing an existing Facility
     */
    public AddFacilityFragment(Facility facility, Boolean isEdit) {
        this.facility = facility;
        this.isEdit = isEdit;
    }
    /**
     * onAttach is called when the fragment is associated with an activity.
     * @param context Context object
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof AddFacilityDialogListener) {
            listener = (AddFacilityDialogListener) getParentFragment();
        } else if (context instanceof AddFacilityDialogListener) {
            listener = (AddFacilityDialogListener) context;
        } else {
            throw new ClassCastException("Parent fragment or activity must implement AddFacilityDialogListener");
        }
    }
    /**
     * onCreateDialog creates the AlertDialog for the AddFacilityFragment
     * @param savedInstanceState Bundle object containing the saved state
     * @return Dialog object
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add_facility, null);


        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(requireContext());
        sessionToken = AutocompleteSessionToken.newInstance();


        EditText editFacilityName = view.findViewById(R.id.facility_name_input);
        AutoCompleteTextView editStreetAddress1 = view.findViewById(R.id.street_address_1);


        if (isEdit && facility != null) {
            editFacilityName.setText(facility.getFacilityName());
            editStreetAddress1.setText(facility.getStreetAddress1());
        }

        setupAutocomplete(editStreetAddress1);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle(isEdit ? "Edit Facility" : "Create Facility")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String facilityName = editFacilityName.getText().toString().trim();
                String streetAddress1 = editStreetAddress1.getText().toString().trim();

                if (facilityName.isEmpty()) {
                    editFacilityName.setError("Facility Name is required");
                    return;
                }
                if (streetAddress1.isEmpty() || !isUserSelectedFromDropdown(editStreetAddress1)) {
                    editStreetAddress1.setError("Street Address 1 is required. Please choose an option from the dropdown suggestions.");
                    return;
                }

                String deviceId = Settings.Secure.getString(requireActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                if (isEdit) {
                    Log.d("msg", "updating facility");
                    facility.setFacilityName(facilityName);
                    facility.setStreetAddress1(streetAddress1);
                    listener.updateFacility();

                } else {
                    listener.addFacility(new Facility(facilityName, streetAddress1, deviceId, deviceId));
                }

                dialog.dismiss();
            });
        });

        return dialog;
    }

    /**
    * Sets up Google Places Autocomplete functionality for the AutoCompleteTextView's
    * This method adds a text change listener that triggers autocomplete suggestons when the user types.
    * Uses the google places API to fetch the location suggestions based on the current input.
    *
    * @param autoCompleteTextView The AutoCompleteTextView to attach autocomplete suggestions to.
    * Relevant Documentation
    * https://developers.google.com/maps/documentation/places/android-sdk/autocomplete#maps_places_autocomplete_support_fragment-java
    * https://developer.android.com/reference/android/widget/AutoCompleteTextView
    * https://developer.android.com/reference/android/text/TextWatcher
    * */
    private void setupAutocomplete(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(sessionToken)
                            .setQuery(s.toString())
                            .build();

                    placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                        List<String> suggestions = new ArrayList<>();
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            suggestions.add(prediction.getFullText(null).toString());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, suggestions);
                        autoCompleteTextView.setAdapter(adapter);
                        autoCompleteTextView.showDropDown();
                    }).addOnFailureListener(e -> e.printStackTrace());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add OnItemClickListener for the dropdown in order to verify when the user presses confirm that they have selected a
        // valid location from the dropdown.
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            validSelections.put(autoCompleteTextView, selectedItem); // Store the selected item
        });
    }

    /**
     * Checks if the current text in the specified AutoCompleteTextView matches a valid selection from the dropdown suggestions.
     * This method compares the current text with a stored valid selection. Ensures the input is a valid option from the dropdown.
     *
     * @param autoCompleteTextView The AutoCompleteTextView to check for a valid selection.
     * */
    private boolean isUserSelectedFromDropdown(AutoCompleteTextView autoCompleteTextView){
        String currentText = autoCompleteTextView.getText().toString().trim();
        String validSelection = validSelections.get(autoCompleteTextView);
        // if the user doesn't select something from the dropdown validSelection will be null and thus return false.
        return validSelection != null && validSelection.equals(currentText);
    }


}
