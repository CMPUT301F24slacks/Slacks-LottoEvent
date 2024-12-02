package com.example.slacks_lottoevent.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slacks_lottoevent.viewmodel.adapter.AdminImagesAdapter;
import com.example.slacks_lottoevent.Utility.ImageMetadata;
import com.example.slacks_lottoevent.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
/**
 * A fragment that displays and manages a list of images (event posters and profile pictures)
 * in the admin panel. The images are fetched from Firestore and displayed in a RecyclerView.
 */
public class AdminImagesFragment extends Fragment {

    private RecyclerView RecyclerViewImages;
    private AdminImagesAdapter adapter;
    private final List<ImageMetadata> imageList = new ArrayList<>(); // Updated to use ImageMetadata
    /**
     * Default constructor required for a fragment.
     */
    public AdminImagesFragment() {
        // Required empty public constructor
    }
    /**
     * Static factory method to create a new instance of {@link AdminImagesFragment}.
     *
     * @return A new instance of {@link AdminImagesFragment}.
     */
    public static AdminImagesFragment newInstance() {
        return new AdminImagesFragment();
    }
    /**
     * Called to create the view hierarchy associated with the fragment.
     * Initializes the RecyclerView and fetches images from Firestore.
     * @param inflater           The LayoutInflater object used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_images, container, false);

        // Initialize RecyclerView
        RecyclerViewImages = view.findViewById(R.id.recyclerViewImages);
        RecyclerViewImages.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter
        adapter = new AdminImagesAdapter(getContext(), imageList);
        RecyclerViewImages.setAdapter(adapter);

        imageList.clear(); // Clear the list to avoid duplicates or stale data

        // Fetch Event Posters
        // Fetch Profile Pictures
        fetchImages();
        return view;
    }
    /**
     * Fetches images from Firestore, including event posters and profile pictures.
     * Updates the UI with real-time changes using Firestore listeners.
     */
    private void fetchImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<ImageMetadata> tempEventImages = new ArrayList<>();
        List<ImageMetadata> tempProfileImages = new ArrayList<>();

        db.collection("events").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for event updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                tempEventImages.clear();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String posterURL = document.getString("eventPosterURL");
                    String eventId = document.getId();
                    if (posterURL != null && !posterURL.trim().isEmpty()) {
                        tempEventImages.add(new ImageMetadata(posterURL, true, eventId));
                    }
                }
                updateImageList(tempEventImages, tempProfileImages);
            }
        });

        db.collection("profiles").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for profile updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                tempProfileImages.clear();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    boolean usingDefaultPicture = Boolean.TRUE.equals(
                            document.getBoolean("usingDefaultPicture"));
                    String profilePicturePath = document.getString("profilePicturePath");
                    String deviceId = document.getId();
                    if (!usingDefaultPicture && profilePicturePath != null &&
                        !profilePicturePath.trim().isEmpty()) {
                        tempProfileImages.add(
                                new ImageMetadata(profilePicturePath, false, deviceId));
                    }
                }
                updateImageList(tempEventImages, tempProfileImages);
            }
        });
    }
    /**
     * Updates the RecyclerView with the latest images fetched from Firestore.
     * Combines event images and profile images into a single list.
     * @param tempEventImages  A list of event poster images.
     * @param tempProfileImages A list of profile picture images.
     */
    private void updateImageList(List<ImageMetadata> tempEventImages,
                                 List<ImageMetadata> tempProfileImages) {
        imageList.clear();
        imageList.addAll(tempEventImages);
        imageList.addAll(tempProfileImages);
        adapter.notifyDataSetChanged();
    }

}
