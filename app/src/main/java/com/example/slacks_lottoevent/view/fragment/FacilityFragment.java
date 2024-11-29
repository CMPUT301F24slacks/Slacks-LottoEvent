package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.slacks_lottoevent.BuildConfig;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.model.User;
import com.example.slacks_lottoevent.viewmodel.FacilityViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FacilityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FacilityFragment extends Fragment {

    private User user;
    private FacilityViewModel facilityViewModel;

    // UI elements
    private Button create_button;
    private Button edit_button;
    private EditText name;
    private AutoCompleteTextView street_address;

    // Places API
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private Map<AutoCompleteTextView, String> validSelections = new HashMap<>();

    public FacilityFragment() {
        // Required empty public constructor
    }

    public static FacilityFragment newInstance(String param1, String param2) {
        FacilityFragment fragment = new FacilityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facility, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = User.getInstance();

        // UI elements
        create_button = view.findViewById(R.id.create_facility_button);
        edit_button = view.findViewById(R.id.edit_facility_button);
        name = view.findViewById(R.id.facility_name);
        street_address = view.findViewById(R.id.street_address);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(requireContext());
        sessionToken = AutocompleteSessionToken.newInstance();

        // Initialize ViewModel
        facilityViewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);
        facilityViewModel.getCurrentFacilityLiveData().observe(getViewLifecycleOwner(), facility -> {
            if (facility != null) {
                // Facility object is set
                create_button.setVisibility(View.GONE);
                edit_button.setVisibility(View.VISIBLE);
                name.setText(facility.getFacilityName());
                street_address.setText(facility.getStreetAddress());
            } else {
                // Facility object is not set
                create_button.setVisibility(View.VISIBLE);
                edit_button.setVisibility(View.GONE);
            }
        });

        setupAutocomplete(street_address); // Setup autocomplete for street address

        // click listener for create facility button
        create_button.setOnClickListener(v -> {
            // Create a new facility
            if (facilityInputCheck()) {
                String facilityName = name.getText().toString().trim();
                String facilityAddress = street_address.getText().toString().trim();
                facilityViewModel.updateFacility(facilityName, facilityAddress);
            }
        });

        // click listener for edit facility button
        edit_button.setOnClickListener(v -> {
            // Edit the existing facility
            if (facilityInputCheck()) {
                String facilityName = name.getText().toString().trim();
                String facilityAddress = street_address.getText().toString().trim();
                facilityViewModel.updateFacility(facilityName, facilityAddress);
            }
        });
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

    private boolean facilityInputCheck() {
        String facilityName = name.getText().toString().trim();
        String facilityAddress = street_address.getText().toString().trim();
        if (facilityName.isEmpty()) {
            name.setError("Facility name is required");
            name.requestFocus();
            return false;
        }
        if (facilityAddress.isEmpty() || !isUserSelectedFromDropdown(street_address)) {
            street_address.setError("Street Address is required. Please choose an option from the dropdown suggestions.");
            name.requestFocus();
            return false;
        }
        return true;
    }

}