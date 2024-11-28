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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {

    private Context context;
    private List<ImageMetadata> imageList; // Updated to use ImageMetadata
    private FirebaseFirestore db;
    boolean FromFacility;

    public AdminImagesAdapter(Context context, List<ImageMetadata> imageList, boolean FromFacility) {
        this.context = context;
        this.imageList = imageList;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
        this.FromFacility = FromFacility;
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
        ImageMetadata meta = imageList.get(position); // Get metadata

        // Use Glide to load the image
        Glide.with(context)
                .load(meta.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imageHolder);

        // Set OnClickListener to determine type
        holder.imageHolder.setOnClickListener(v -> {
            if (meta.isEventPoster()) {
                showImageOptionsDialog(context, db, meta.getImageUrl(), true);
            } else {
                showImageOptionsDialog(context, db, meta.getImageUrl(), false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageHolder = itemView.findViewById(R.id.imageHolder);
        }
    }

    /**
     * Listen for real-time changes in the "events" collection.
     */
    private void listenForPosterChanges() {
        // Listen for changes in the "events" collection
        db.collection("events").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for event updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                // Clear the list before repopulating to prevent duplicates
                imageList.clear();

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String imageURL = document.getString("eventPosterURL");

                    if (imageURL != null && !imageURL.trim().isEmpty()) {
                        imageList.add(new ImageMetadata(imageURL, true)); // true for event posters
                    }
                }

                notifyDataSetChanged(); // Refresh the UI
            }
        });

        // Listen for changes in the "profiles" collection
        db.collection("profiles").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening for profile updates: ", error);
                return;
            }

            if (querySnapshot != null) {
                // Clear the list for profile images too (if needed, depending on use case)
                imageList.removeIf(meta -> !meta.isEventPoster()); // Keep only event posters

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    boolean usingDefaultPicture = Boolean.TRUE.equals(document.getBoolean("usingDefaultPicture"));
                    String profilePicturePath = document.getString("profilePicturePath");

                    if (!usingDefaultPicture && profilePicturePath != null && !profilePicturePath.trim().isEmpty()) {
                        imageList.add(new ImageMetadata(profilePicturePath, false)); // false for profile pictures
                    }
                }

                notifyDataSetChanged(); // Refresh the UI
            }
        });
    }

    /**
     * Shows a dialog with event name and location, and options to Cancel or Delete the image.
     */
    public static void showImageOptionsDialog(Context context, FirebaseFirestore db, String ImageURL, boolean isPoster) {
        if (isPoster)
        {
            db.collection("events")
                    .whereEqualTo("eventPosterURL", ImageURL)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.e("Firestore", "Error listening for real-time updates: ", error);
                            Toast.makeText(context, "Error fetching event details.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Fetch the first matching document
                            String eventName = querySnapshot.getDocuments().get(0).getString("name");
                            String location = querySnapshot.getDocuments().get(0).getString("location");

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Event Details");

                            String message = "Event Name: " + (eventName != null ? eventName : "N/A") + "\n" +
                                    "Location: " + (location != null ? location : "N/A");

                            builder.setMessage(message);

                            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                            builder.setPositiveButton("Delete", (dialog, which) -> {
                                // Call method to delete image and Firestore data
                                deleteImageFromStorageAndFirestore(context, db, ImageURL, isPoster);
                            });

                            // Show the dialog
                            builder.show();
                        }else {
                            Log.e("Firestore", "No matching event found.");
                            Toast.makeText(context, "No matching event found.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            db.collection("profiles")
                    .whereEqualTo("profilePicturePath", ImageURL)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.e("Firestore", "Error listening for real-time updates: ", error);
                            Toast.makeText(context, "Error fetching event details.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Fetch the first matching document
                            String name = querySnapshot.getDocuments().get(0).getString("name");
                            String email = querySnapshot.getDocuments().get(0).getString("email");

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Profile Details");

                            String message = "Name: " + (name != null ? name : "N/A") + "\n" +
                                    "Email: " + (email != null ? email : "N/A");

                            builder.setMessage(message);

                            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                            builder.setPositiveButton("Delete", (dialog, which) -> {
                                // Call method to delete image and Firestore data
                                deleteImageFromStorageAndFirestore(context, db, ImageURL, false);
                            });

                            // Show the dialog
                            builder.show();
                        } else {
                            Log.e("Firestore", "No matching event found.");
                            Toast.makeText(context, "No matching event found.", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public static void deleteImageFromStorageAndFirestore(Context context, FirebaseFirestore db, String ImageURL, boolean isPoster) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(ImageURL);

        // Delete the image from Firebase Storage
        storageRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If deletion from storage is successful, delete from Firestore
                deleteImageFromFirestore(context, db, ImageURL, isPoster);
            } else {
                Log.e("Storage", "Error deleting image from storage", task.getException());
                Toast.makeText(context, "Failed to delete image from storage.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteImageFromFirestore(Context context, FirebaseFirestore db, String ImageURL, boolean isPoster) {
        if (isPoster)
        {
            db.collection("events")
                    .whereEqualTo("eventPosterURL", ImageURL)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.e("Firestore", "Error listening for real-time updates: ", error);
                            Toast.makeText(context, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            String documentId = querySnapshot.getDocuments().get(0).getId();

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
                        }
                        else {
                            Log.e("Firestore", "No matching document found for real-time update.");
                            Toast.makeText(context, "No matching event found for the given image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            db.collection("profiles")
                    .whereEqualTo("profilePicturePath", ImageURL)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.e("Firestore", "Error listening for real-time updates: ", error);
                            Toast.makeText(context, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            String documentId = querySnapshot.getDocuments().get(0).getId();

                            // Update Firestore to remove the eventPosterURL
                            db.collection("profiles").document(documentId).update("usingDefaultPicture", "false")
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(context, "usingDefaultPicture updated successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("Firestore", "Error updating usingDefaultPicture", updateTask.getException());
                                            Toast.makeText(context, "Failed to update usingDefaultPicture from database.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            db.collection("profiles").document(documentId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // Convert the document snapshot into a Profile object
                                            Profile profile = documentSnapshot.toObject(Profile.class);

                                            if (profile != null) {
                                                // Modify the profile object
                                                profile.setProfilePicturePath(profile.generateProfilePicture(profile.getName(), context));

                                                // Update the profile object in Firestore
                                                db.collection("profiles").document(documentId)
                                                        .set(profile)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Log.d("Firestore", "Profile updated successfully.");
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("Firestore", "Error updating profile: ", e);
                                                        });

                                                // Successfully retrieved and updated the Profile object
                                                Log.d("Firestore", "Profile Name: " + profile.getName());
                                            } else {
                                                Log.e("Firestore", "Failed to parse Profile object.");
                                            }
                                        } else {
                                            Log.e("Firestore", "No such document.");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error fetching profile: ", e);
                                    });
                        }
                        else {
                            Log.e("Firestore", "No matching document found for real-time update.");
                            Toast.makeText(context, "No matching profile found for the given image.", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
}
