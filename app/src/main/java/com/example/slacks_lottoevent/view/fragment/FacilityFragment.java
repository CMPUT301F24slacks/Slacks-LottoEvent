package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.slacks_lottoevent.BuildConfig;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.Utility.SnackbarUtils;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.viewmodel.EventViewModel;
import com.example.slacks_lottoevent.viewmodel.FacilityViewModel;
import com.example.slacks_lottoevent.viewmodel.OrganizerViewModel;
import com.example.slacks_lottoevent.viewmodel.ProfileViewModel;
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
    private final Map<AutoCompleteTextView, String> validSelections = new HashMap<>();
    private FacilityViewModel facilityViewModel;
    private OrganizerViewModel organizerViewModel;
    private ProfileViewModel profileViewModel;
    private EventViewModel eventViewModel;
    // UI elements
    private TextView title;
    private Button create_button;
    private Button edit_button;
    private Button cancel_button;
    private Button confirm_button;
    private EditText name;
    private AutoCompleteTextView street_address;
    // Places API
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;

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

        // UI elements
        title = view.findViewById(R.id.title);
        create_button = view.findViewById(R.id.create_facility_button);
        edit_button = view.findViewById(R.id.edit_facility_button);
        cancel_button = view.findViewById(R.id.cancel_button);
        confirm_button = view.findViewById(R.id.confirm_button);
        name = view.findViewById(R.id.facility_name);
        street_address = view.findViewById(R.id.street_address);
        name.setHint("");
        street_address.setHint("");
        name.setEnabled(false);
        street_address.setEnabled(false);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(requireContext());
        sessionToken = AutocompleteSessionToken.newInstance();

        // Initialize facilityViewModel
        facilityViewModel = new ViewModelProvider(requireActivity()).get(FacilityViewModel.class);
        facilityViewModel.getCurrentFacilityLiveData()
                         .observe(getViewLifecycleOwner(), facility -> {
                             if (facility != null) {
                                 // Facility object is set
                                 title.setText("Facility Information");
                                 create_button.setVisibility(View.GONE);
                                 edit_button.setVisibility(View.VISIBLE);
                                 name.setText(facility.getFacilityName());
                                 street_address.setText(facility.getStreetAddress());
                             } else {
                                 // Facility object is not set
                                 title.setText("Create Facility");
                                 create_button.setVisibility(View.VISIBLE);
                                 edit_button.setVisibility(View.GONE);
                                 name.setText("");
                                 street_address.setText("");
                                 name.setHint("");
                                 street_address.setHint("");
                             }
                         });

        // Initialize eventViewModel
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        // Initialize organizerViewModel
        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);

        // Initialize profileViewModel
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileObserver();

        setupAutocomplete(street_address); // Setup autocomplete for street address

        // click listener for create facility button
        create_button.setOnClickListener(v -> {
            if (!isUserSignedUp()) return;
            name.setEnabled(true);
            street_address.setEnabled(true);
            name.setHint("Enter Facility Name");
            street_address.setHint("Enter Street Address");
            create_button.setVisibility(View.GONE);
            confirm_button.setVisibility(View.VISIBLE);
            confirm_button.setEnabled(true);
            cancel_button.setVisibility(View.VISIBLE);
            cancel_button.setEnabled(true);
        });

        // click listener for cancel button
        cancel_button.setOnClickListener(v -> {
            name.setEnabled(false);
            street_address.setEnabled(false);
            name.setHint("");
            street_address.setHint("");
            confirm_button.setVisibility(View.GONE);
            cancel_button.setVisibility(View.GONE);
            if (facilityViewModel.getCurrentFacilityLiveData().getValue() == null) {
                title.setText("Create Facility");
                name.setText("");
                street_address.setText("");
                create_button.setVisibility(View.VISIBLE);
            } else {
                title.setText("Facility Information");
                name.setText(facilityViewModel.getCurrentFacilityLiveData().getValue()
                                              .getFacilityName());
                street_address.setText(facilityViewModel.getCurrentFacilityLiveData().getValue()
                                                        .getStreetAddress());
                edit_button.setVisibility(View.VISIBLE);
            }
        });

        // click listener for confirm button
        confirm_button.setOnClickListener(v -> {
            // Confirm the new/updated facility
            if (facilityInputCheck()) {
                updateFacility();
                if (organizerViewModel.getCurrentOrganizerLiveData().getValue() == null) {
                    organizerViewModel.updateOrganizer(new ArrayList<String>());
                }
                title.setText("Facility Information");
                name.setEnabled(false);
                street_address.setEnabled(false);
                confirm_button.setVisibility(View.GONE);
                cancel_button.setVisibility(View.GONE);
                if (facilityViewModel.getCurrentFacilityLiveData().getValue() == null) {
                    create_button.setVisibility(View.VISIBLE);
                } else {
                    edit_button.setVisibility(View.VISIBLE);
                }
            }
        });

        // click listener for edit facility button
        edit_button.setOnClickListener(v -> {
            // Edit the existing facility
            title.setText("Edit Facility");
            name.setEnabled(true);
            street_address.setEnabled(true);
            name.setHint("Enter Facility Name");
            street_address.setHint("Enter Street Address");
            edit_button.setVisibility(View.GONE);
            confirm_button.setVisibility(View.VISIBLE);
            confirm_button.setEnabled(true);
            cancel_button.setVisibility(View.VISIBLE);
            cancel_button.setEnabled(true);
        });
    }

    /**
     * Sets up Google Places Autocomplete functionality for the AutoCompleteTextView's
     * This method adds a text change listener that triggers autocomplete suggestons when the user types.
     * Uses the google places API to fetch the location suggestions based on the current input.
     *
     * @param autoCompleteTextView The AutoCompleteTextView to attach autocomplete suggestions to.
     *                             Relevant Documentation
     *                             https://developers.google.com/maps/documentation/places/android-sdk/autocomplete#maps_places_autocomplete_support_fragment-java
     *                             https://developer.android.com/reference/android/widget/AutoCompleteTextView
     *                             https://developer.android.com/reference/android/text/TextWatcher
     */
    private void setupAutocomplete(AutoCompleteTextView autoCompleteTextView) {
        // Add focus change listener to trigger autocomplete when the view is focused
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() >= 3) {
                            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                                                                                                           .setSessionToken(
                                                                                                                   sessionToken)
                                                                                                           .setQuery(
                                                                                                                   s.toString())
                                                                                                           .build();

                            placesClient.findAutocompletePredictions(request)
                                        .addOnSuccessListener(response -> {
                                            List<String> suggestions = new ArrayList<>();
                                            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                                                suggestions.add(
                                                        prediction.getFullText(null).toString());
                                            }
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                                    getContext(),
                                                    android.R.layout.simple_dropdown_item_1line,
                                                    suggestions);
                                            autoCompleteTextView.setAdapter(adapter);
                                            autoCompleteTextView.showDropDown();
                                        }).addOnFailureListener(e -> e.printStackTrace());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            } else {
                // Remove the TextWatcher when focus is lost
                autoCompleteTextView.clearListSelection();
                autoCompleteTextView.dismissDropDown();
            }
        });

        // Add OnItemClickListener for the dropdown in order to verify when the user presses confirm that they have selected a
        // valid location from the dropdown.
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            validSelections.put(autoCompleteTextView, selectedItem); // Store the selected item
            autoCompleteTextView.dismissDropDown(); // To dismiss the dropdown
        });
        autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                autoCompleteTextView.dismissDropDown();
                return true;
            }
            return false;
        });
    }

    /**
     * Checks if the current text in the specified AutoCompleteTextView matches a valid selection from the dropdown suggestions.
     * This method compares the current text with a stored valid selection. Ensures the input is a valid option from the dropdown.
     *
     * @param autoCompleteTextView The AutoCompleteTextView to check for a valid selection.
     */
    private boolean isUserSelectedFromDropdown(AutoCompleteTextView autoCompleteTextView) {
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
            street_address.setError(
                    "Street Address is required. Please choose an option from the dropdown suggestions.");
            name.requestFocus();
            return false;
        }
        return true;
    }

    private void updateFacility() {
        String facilityName = name.getText().toString().trim();
        String facilityAddress = street_address.getText().toString().trim();
        facilityViewModel.updateFacility(facilityName, facilityAddress);
        List<Event> events = eventViewModel.getHostingEventsLiveData().getValue();
        for (Event event : events) {
            event.setLocation(facilityAddress);
            eventViewModel.updateEvent(event);
        }
    }

    private void profileObserver() {
        profileViewModel.getCurrentProfileLiveData().observe(getViewLifecycleOwner(), profile -> {
            if (profile == null) {
                SnackbarUtils.promptSignUp(requireView(), requireContext(),
                                           R.id.bottom_app_bar); // Prompt user to sign up
            }
        });
    }

    private boolean isUserSignedUp() {
        if (profileViewModel.getCurrentProfileLiveData().getValue() == null) {
            SnackbarUtils.promptSignUp(requireView(), requireContext(), R.id.bottom_app_bar);
            return false;
        }
        return true;
    }

}