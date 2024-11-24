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

    private RecyclerView recyclerViewEventPosters;
    private AdminImagesAdapter adapter;
    private List<String> posterURLs = new ArrayList<>();

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
        recyclerViewEventPosters = view.findViewById(R.id.recyclerViewEventPosters);
        recyclerViewEventPosters.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter
        adapter = new AdminImagesAdapter(getContext(), posterURLs);
        recyclerViewEventPosters.setAdapter(adapter);

        // Fetch Event Posters
        fetchEventPosters();

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
                posterURLs.clear(); // Clear the list to avoid duplicates or stale data

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String posterURL = document.getString("eventPosterURL");
                    if (posterURL != null && !posterURL.isEmpty()) {
                        posterURLs.add(posterURL);
                    }
                }

                // Notify Adapter of Changes
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Helper method to find the index of a poster URL by event ID
    private int findPosterIndex(String eventId) {
        for (int i = 0; i < posterURLs.size(); i++) {
            // Assuming `posterURLs` can be mapped to event IDs if needed
            if (posterURLs.get(i).contains(eventId)) {
                return i;
            }
        }
        return -1;
    }

}
