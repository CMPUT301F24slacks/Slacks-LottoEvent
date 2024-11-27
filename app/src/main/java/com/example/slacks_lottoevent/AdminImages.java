package com.example.slacks_lottoevent;

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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminImages extends Fragment {

    private RecyclerView RecyclerViewImages;
    private AdminImagesAdapter adapter;
    private List<ImageMetadata> imageList = new ArrayList<>(); // Updated to use ImageMetadata

    public AdminImages() {
        // Required empty public constructor
    }

    public static AdminImages newInstance() {
        return new AdminImages();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        fetchEventPosters();

        // Fetch Profile Pictures
        fetchProfilePictures();

        return view;
    }

    private void fetchEventPosters() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for event updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    DocumentSnapshot document = change.getDocument();
                    String posterURL = document.getString("eventPosterURL");

                    if (posterURL != null && !posterURL.trim().isEmpty()) {
                        switch (change.getType()) {
                            case ADDED:
                                imageList.add(new ImageMetadata(posterURL, true)); // true for event poster
                                break;
                            case REMOVED:
                                imageList.removeIf(meta -> meta.getImageUrl().equals(posterURL));
                                break;
                            case MODIFIED:
                                // Handle modifications if needed
                                if (!imageList.stream().anyMatch(meta -> meta.getImageUrl().equals(posterURL))) {
                                    imageList.add(new ImageMetadata(posterURL, true));
                                }
                                break;
                        }
                    }
                }

                // Notify Adapter of Changes
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchProfilePictures() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("profiles").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for profile updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    DocumentSnapshot document = change.getDocument();
                    Boolean usingDefaultPictureObj = document.getBoolean("usingDefaultPicture");
                    boolean UsingDefaultPicture = usingDefaultPictureObj == null || usingDefaultPictureObj; // Defaults to true if null

                    if (!UsingDefaultPicture) {
                        String profilePicturePath = document.getString("profilePicturePath");
                        if (profilePicturePath != null && !profilePicturePath.trim().isEmpty()) {
                            switch (change.getType()) {
                                case ADDED:
                                    imageList.add(new ImageMetadata(profilePicturePath, false)); // false for profile picture
                                    break;
                                case REMOVED:
                                    imageList.removeIf(meta -> meta.getImageUrl().equals(profilePicturePath));
                                    break;
                                case MODIFIED:
                                    if (!imageList.stream().anyMatch(meta -> meta.getImageUrl().equals(profilePicturePath))) {
                                        imageList.add(new ImageMetadata(profilePicturePath, false));
                                    }
                                    break;
                            }
                        }
                    }
                }

                // Notify Adapter of Changes
                adapter.notifyDataSetChanged();
            }
        });
    }
}
