package com.example.slacks_lottoevent.Utility;

import java.util.Objects;

/**
 * ImageMetadata class to store image metadata
 */
public class ImageMetadata {
    private String imageUrl;
    private boolean isEventPoster;
    private String documentId; // Add document ID for uniqueness

    /**
     * Constructor for ImageMetadata
     * @param imageUrl Image URL
     * @param isEventPoster Is event poster
     * @param documentId Document ID
     */
    public ImageMetadata(String imageUrl, boolean isEventPoster, String documentId) {
        this.imageUrl = imageUrl;
        this.isEventPoster = isEventPoster;
        this.documentId = documentId;
    }

    /**
     * Get image URL
     * @return Image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Check if image is event poster
     * @return True if image is event poster, false otherwise
     */
    public boolean isEventPoster() {
        return isEventPoster;
    }

    /**
     * Get document ID
     * @return Document ID
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Set image URL
     * @param o Image URL
     * @return ImageMetadata object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageMetadata that = (ImageMetadata) o;
        return Objects.equals(documentId, that.documentId); // Use documentId for equality
    }

    /**
     * Get hash code
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(documentId); // Use documentId for hashcode
    }
}


