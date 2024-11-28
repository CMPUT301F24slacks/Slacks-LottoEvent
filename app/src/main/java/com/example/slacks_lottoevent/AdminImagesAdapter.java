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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {
    private Context context;
    private List<ImageMetadata> imageList;

    public AdminImagesAdapter(Context context, List<ImageMetadata> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_poster, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageMetadata meta = imageList.get(position);
        Glide.with(context)
                .load(meta.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imageHolder);

        holder.imageHolder.setOnClickListener(v -> {
            if (meta.isEventPoster()) {
                showImageOptionsDialog(context, FirebaseFirestore.getInstance(), meta.getImageUrl(), true);
            } else {
                showImageOptionsDialog(context, FirebaseFirestore.getInstance(), meta.getImageUrl(), false);
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
                                deleteImageFromFirestore(context, db, ImageURL, isPoster);
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
                                deleteImageFromFirestore(context, db, ImageURL, false);
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
                                            deleteImageFromStorageAndFirestore(context, ImageURL);
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

//                            // Update Firestore to remove the profile picture
//                            db.collection("profiles").document(documentId).update("usingDefaultPicture", false)
//                                    .addOnCompleteListener(updateTask -> {
//                                        if (updateTask.isSuccessful()) {
//                                            Toast.makeText(context, "usingDefaultPicture updated successfully.", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Log.e("Firestore", "Error updating usingDefaultPicture", updateTask.getException());
//                                            Toast.makeText(context, "Failed to update usingDefaultPicture from database.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });

                            db.collection("profiles").document(documentId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // Convert the document snapshot into a Profile object
                                            Profile profile = documentSnapshot.toObject(Profile.class);

                                            if (profile != null) {
                                                // Modify the profile object
                                                deleteImageFromStorageAndFirestore(context, profile.getProfilePicturePath());
                                                profile.setUsingDefaultPicture(true);
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
//                            Toast.makeText(context, "No matching profile found for the given image.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void deleteImageFromStorageAndFirestore(Context context, String ImageURL) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(ImageURL);

        // Delete the image from Firebase Storage
        storageRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If deletion from storage is successful, delete from Firestore
                Toast.makeText(context, "Successfully deleted image from storage.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Storage", "Error deleting image from storage", task.getException());
                Toast.makeText(context, "Failed to delete image from storage.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
