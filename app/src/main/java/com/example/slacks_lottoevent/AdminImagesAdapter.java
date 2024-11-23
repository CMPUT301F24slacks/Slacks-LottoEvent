package com.example.slacks_lottoevent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ViewHolder> {

    private Context context;
    private List<String> posterURLs;

    public AdminImagesAdapter(Context context, List<String> posterURLs) {
        this.context = context;
        this.posterURLs = posterURLs;
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
            Toast.makeText(context, "Clicked on: " + posterURL, Toast.LENGTH_SHORT).show();
            // Define further action here (e.g., open a new screen)
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
}

