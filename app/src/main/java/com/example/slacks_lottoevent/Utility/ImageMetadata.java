package com.example.slacks_lottoevent.Utility;

import java.util.Objects;

public class ImageMetadata {
    private String imageUrl;
    private boolean isEventPoster;
    private String documentId; // Add document ID for uniqueness

    public ImageMetadata(String imageUrl, boolean isEventPoster, String documentId) {
        this.imageUrl = imageUrl;
        this.isEventPoster = isEventPoster;
        this.documentId = documentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isEventPoster() {
        return isEventPoster;
    }

    public String getDocumentId() {
        return documentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageMetadata that = (ImageMetadata) o;
        return Objects.equals(documentId, that.documentId); // Use documentId for equality
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId); // Use documentId for hashcode
    }
}


