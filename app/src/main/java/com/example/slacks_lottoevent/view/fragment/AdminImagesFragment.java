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

public class AdminImagesFragment extends Fragment {

    private RecyclerView RecyclerViewImages;
    private AdminImagesAdapter adapter;
    private final List<ImageMetadata> imageList = new ArrayList<>(); // Updated to use ImageMetadata

    public AdminImagesFragment() {
        // Required empty public constructor
    }

    public static AdminImagesFragment newInstance() {
        return new AdminImagesFragment();
    }

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

    private void updateImageList(List<ImageMetadata> tempEventImages,
                                 List<ImageMetadata> tempProfileImages) {
        imageList.clear();
        imageList.addAll(tempEventImages);
        imageList.addAll(tempProfileImages);
        adapter.notifyDataSetChanged();
    }

}
