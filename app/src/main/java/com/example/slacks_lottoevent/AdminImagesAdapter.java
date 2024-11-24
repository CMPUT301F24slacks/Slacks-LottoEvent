package com.example.slacks_lottoevent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {

    private Context context;
    private List<String> posterURLs;
    private FirebaseFirestore db;

    public AdminImagesAdapter(Context context, List<String> posterURLs) {
        this.context = context;
        this.posterURLs = posterURLs;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
        listenForPosterChanges(); // Start listening for changes
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_poster, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String posterURL = posterURLs.get(position);

        // Use Glide to load the image from the URL
        Glide.with(context)
                .load(posterURL)
                .placeholder(R.drawable.placeholder_image) // Replace with your placeholder image
                .into(holder.imageEventPoster);

        // Handle click on the image
        holder.imageEventPoster.setOnClickListener(v -> {
            AdminImagesAdapter.showImageOptionsDialog(context, db, posterURL); // Call the static method
        });
    }

    @Override
    public int getItemCount() {
        return posterURLs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEventPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEventPoster = itemView.findViewById(R.id.imageEventPoster);
        }
    }

    /**
     * Listen for real-time changes in the "events" collection.
     */
    private void listenForPosterChanges() {
        db.collection("events").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for event updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                posterURLs.clear(); // Clear the list to avoid duplicates

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String posterURL = document.getString("eventPosterURL");
                    if (posterURL != null && !posterURL.isEmpty()) {
                        posterURLs.add(posterURL);
                    }
                }

                notifyDataSetChanged(); // Refresh the RecyclerView
            }
        });
    }

    /**
     * Shows a dialog with event name and location, and options to Cancel or Delete the image.
     */
    public static void showImageOptionsDialog(Context context, FirebaseFirestore db, String posterURL) {
        db.collection("events")
                .whereEqualTo("eventPosterURL", posterURL)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        String eventName = task.getResult().getDocuments().get(0).getString("name");
                        String location = task.getResult().getDocuments().get(0).getString("location");

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Event Details");

                        String message = "Event Name: " + (eventName != null ? eventName : "N/A") + "\n" +
                                "Location: " + (location != null ? location : "N/A");

                        builder.setMessage(message);

                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                        builder.setPositiveButton("Delete", (dialog, which) -> {
                            deleteImageFromStorageAndFirestore(context, db, posterURL);
                        });

                        builder.show();
                    } else {
                        Log.e("Firestore", "Error fetching event details or no matching event found.");
                        Toast.makeText(context, "Error fetching event details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void deleteImageFromStorageAndFirestore(Context context, FirebaseFirestore db, String posterURL) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(posterURL);

        // Delete the image from Firebase Storage
        storageRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If deletion from storage is successful, delete from Firestore
                deleteImageFromFirestore(context, db, posterURL);
            } else {
                Log.e("Storage", "Error deleting poster from storage", task.getException());
                Toast.makeText(context, "Failed to delete image from storage.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteImageFromFirestore(Context context, FirebaseFirestore db, String posterURL) {
        db.collection("events")
                .whereEqualTo("eventPosterURL", posterURL)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        String documentId = task.getResult().getDocuments().get(0).getId();

                        // Update Firestore to remove the eventPosterURL
                        db.collection("events").document(documentId).update("eventPosterURL", "")
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(context, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("Firestore", "Error updating database", updateTask.getException());
                                        Toast.makeText(context, "Failed to delete image from database.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e("Firestore", "Error finding event for poster deletion");
                        Toast.makeText(context, "Failed to delete image from database.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
