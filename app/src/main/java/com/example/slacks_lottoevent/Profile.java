package com.example.slacks_lottoevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Represents a user profile with basic information.
 */
public class Profile {
    private String name;
    private String email;
    private String phone;
    private String deviceId;
    private String profilePicturePath; // Path to the profile picture image
    private boolean usingDefaultPicture;
    private boolean adminNotifications;
    private boolean isAdmin;

    /**
     * Default constructor for Firestore serialization.
     */
    public Profile() {}

    /**
     * Constructor for creating a new profile.
     *
     * @param name The name of the user.
     * @param phone The phone number of the user.
     * @param email The email address of the user.
     * @param context The application context for file access.
     */
    public Profile(String name, String phone, String email, String deviceId, Context context) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.deviceId = deviceId;

        // Skip profile picture generation if Context is null (for testing)
        if (context != null) {
            this.profilePicturePath = generateProfilePicture(name, context);
        } else {
            this.profilePicturePath = null;
        }

        this.usingDefaultPicture = context != null;
        this.adminNotifications = true;
    }


    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user and updates the profile picture if using the default picture.
     *
     * @param name The name of the user.
     * @param context The application context for file access.
     */
    public void setName(String name, Context context) {
        this.name = name;

        // Skip profile picture update if Context is null
        if (usingDefaultPicture && context != null) {
            this.profilePicturePath = generateProfilePicture(name, context);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDeviceId() {return deviceId;}

    public boolean getAdminNotifications() {
        return adminNotifications;
    }

    public void setAdminNotifications(boolean adminNotifications) {
        this.adminNotifications = adminNotifications;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean getUsingDefaultPicture() {
        return usingDefaultPicture;
    }

    public void setUsingDefaultPicture(boolean usingDefaultPicture) {
        this.usingDefaultPicture = usingDefaultPicture;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    /**
     * Generates and saves a profile picture image with the initials of the name.
     *
     * @param name The name to extract initials from.
     * @param context The application context for accessing file storage.
     * @return The file path of the saved profile picture.
     */
    public String generateProfilePicture(String name, Context context) {
        // Set dimensions for the image
        int width = 200;
        int height = 200;

        // Create a blank bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw on the bitmap
        Canvas canvas = new Canvas(bitmap);

        // Set background color
        canvas.drawColor(Color.LTGRAY);

        // Prepare paint for text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(80);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        // Extract initials
        String initials = getInitials(name);

        // Draw initials on the canvas
        canvas.drawText(initials, width / 2f, height / 2f + paint.getTextSize() / 3, paint);

        // Save the bitmap as an image file
        return saveBitmapAsImage(bitmap, context, name);
    }

    /**
     * Extracts initials from a given name.
     *
     * @param name The full name.
     * @return Initials as a string.
     */
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "N/A";

        String[] parts = name.split(" ");
        StringBuilder initials = new StringBuilder();

        for (int i = 0 ; i < 2 && i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty() && Character.isLetter(part.charAt(0))) {
                initials.append(part.charAt(0));
            }
        }

        return initials.length() > 0 ? initials.toString().toUpperCase() : "N/A";
    }

    /**
     * Saves a Bitmap as an image file in the app's files directory.
     *
     * @param bitmap The bitmap to save.
     * @param context The application context for file access.
     * @param name The name used to generate a unique file name.
     * @return The file path of the saved image.
     */
    private String saveBitmapAsImage(Bitmap bitmap, Context context, String name) {
        String fileName = "profile_" + name.replaceAll("\\s+", "_") + ".png";
        File directory = context.getFilesDir();
        File imageFile = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return imageFile.getAbsolutePath();
    }
}
