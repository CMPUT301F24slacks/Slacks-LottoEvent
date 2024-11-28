package com.example.slacks_lottoevent;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DialogHelper {

    // This method can be used to show the message crafting dialog
    public static void showMessageDialog(Context context, Notifications notifications, String eventId, String listToUse) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_custom_message, null);

        // Get references to the input fields from the dialog view
        EditText inputTitle = dialogView.findViewById(R.id.dialogMessageTitle);
        EditText inputMessage = dialogView.findViewById(R.id.dialogMessageInput);

        // Create and show the dialog
        new AlertDialog.Builder(context)
                .setTitle("Craft Message")
                .setView(dialogView)  // Set the custom dialog layout here
                .setPositiveButton("Send", (dialog, which) -> {
                    // Get the input values
                    String message = inputMessage.getText().toString();
                    String title = inputTitle.getText().toString();

                    // Check if the message and title are not empty
                    if (!message.isEmpty() && !title.isEmpty()) {
                        // Call the method from Notifications class to send the notification
                        notifications.sendNotifications(title, message, listToUse, eventId);
                    }
                    else {
                        // Show a Toast if either title or message is empty
                        Toast.makeText(context, "Message and title cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
