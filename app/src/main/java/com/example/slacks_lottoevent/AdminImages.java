package com.example.slacks_lottoevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot document : task.getResult()) {
                    if (document.contains("eventPosterURL")) {
                        String posterURL = document.getString("eventPosterURL");
                        if (posterURL != null && !posterURL.isEmpty()) {
                            posterURLs.add(posterURL);
                        }
                    }
                }
                // Notify Adapter of Changes
                adapter.notifyDataSetChanged();
            }
        });
    }
}
