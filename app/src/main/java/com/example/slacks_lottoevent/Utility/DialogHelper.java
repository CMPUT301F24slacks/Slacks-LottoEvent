package com.example.slacks_lottoevent.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.slacks_lottoevent.R;

/**
 * This class contains a helper method to show a custom dialog for crafting messages
 */
public class DialogHelper {

    /**
     * This method creates a custom dialog for crafting messages
     * @param context The context of the activity
     * @param notifications The Notifications object to send the message
     * @param eventId The event ID to send the message to
     * @param listToUse The list to send the message to
     */
    public static void showMessageDialog(Context context, Notifications notifications,
                                         String eventId, String listToUse) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_custom_message, null);

        // Get references to the input fields from the dialog view
        EditText inputTitle = dialogView.findViewById(R.id.dialogMessageTitle);
        EditText inputMessage = dialogView.findViewById(R.id.dialogMessageInput);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);

        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)  // Set the custom layout
                .create();

        // Set up Cancel button functionality
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Set up Confirm button functionality
        confirmButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString();
            String title = inputTitle.getText().toString();

            if (!message.isEmpty() && !title.isEmpty()) {
                // Call the method to send the notification
                notifications.addNotifications(title, message, listToUse, eventId);
                dialog.dismiss(); // Close the dialog after sending
                Toast.makeText(context, "Message Sent!", Toast.LENGTH_SHORT);
            } else {
                // Show a Toast if inputs are empty
                Toast.makeText(context, "Message and title cannot be empty", Toast.LENGTH_SHORT)
                     .show();
            }
        });

        // Show the dialog
        dialog.show();
    }
}
