package com.example.slacks_lottoevent;

public class ImageMetadata {
    private String imageUrl;         // The actual URL of the image
    private boolean isEventPoster;   // True for event posters, false for profile pictures

    public ImageMetadata(String imageUrl, boolean isEventPoster) {
        this.imageUrl = imageUrl;
        this.isEventPoster = isEventPoster;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isEventPoster() {
        return isEventPoster;
    }
}

