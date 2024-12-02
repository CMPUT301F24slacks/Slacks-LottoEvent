package com.example.slacks_lottoevent.view;

import static com.example.slacks_lottoevent.view.AdminActivity.showAdminAlertDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.slacks_lottoevent.Utility.Callback;
import com.example.slacks_lottoevent.Utility.Notifications;
import com.example.slacks_lottoevent.model.Event;
import com.example.slacks_lottoevent.R;
import com.example.slacks_lottoevent.databinding.ActivityOrganizerEventDetailsBinding;
import com.example.slacks_lottoevent.viewmodel.adapter.AdminImagesAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * EntrantEventDetailsActivity is the activity that displays the details of an event for an entrant.
 * The entrant can leave the event from this activity.
 */
public class OrganizerEventDetailsActivity extends BaseActivity {
    Boolean entrantsChosen;
    Uri selectedImageUri;
    FirebaseFirestore db;
    String qrCodeValue;
    Integer spotsRemaining;
    boolean isAdmin;
    String spotsRemainingText;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date currentDate = new Date();
    Date signup = null;
    @SuppressLint("HardwareIds")
    String deviceId;
    private ActivityOrganizerEventDetailsBinding binding;
    private DocumentSnapshot document;
    private String location;
    private String date;
    private String eventName;
    private String signupDate;
    private String time;
    private Boolean usesGeolocation;
    private String description;
    private Event event;
    private String eventPosterURL;
    private String qrData;
    private String eventID;
    private CollectionReference eventsRef;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private static Notifications notifications = new Notifications();

    /**
     * DeletingQRCode method deletes the QR code for the event from Firestore.
     * @param context the context of the activity
     * @param db the Firestore instance
     * @param event the event object
     */
    public static void DeletingQRCode(Context context, FirebaseFirestore db, Event event) {
        // Validate inputs
        if (event == null || db == null || event.getEventID() == null ||
            event.getEventID().isEmpty()) {
            throw new IllegalArgumentException(
                    "Event, Firestore instance, or Event ID cannot be null or empty.");
        }

        // Reference the event document
        String eventID = event.getEventID();

        // Update the attributes in Firestore
        db.collection("events")
          .document(eventID) // Use the event's unique ID to identify the document
          .update(
                  "qrdata", "", // Set qrdata to an empty string
                  "qrhash", "", // Set qrhash to an empty string
                  "disabled", true
          )
          .addOnSuccessListener(aVoid -> {
              Log.d("Firestore", "QR code attributes successfully updated for event: " + eventID);
              Toast.makeText(context, "QR code deleted successfully.", Toast.LENGTH_SHORT).show();
          })
          .addOnFailureListener(e -> {
              Log.e("Firestore", "Error updating QR code attributes for event: " + eventID, e);
              Toast.makeText(context, "Failed to delete QR code.", Toast.LENGTH_SHORT).show();
          });
    }

    /**
     * DeletingEventPoster method deletes the event poster from Firestore.
     * @param context the context of the activity
     * @param db the Firestore instance
     * @param posterURL the URL of the event poster
     * @param FromFacility a boolean indicating if the deletion is from the facility
     */
    public static void DeletingEventPoster(Context context, FirebaseFirestore db, String posterURL,
                                           boolean FromFacility) {
        if (posterURL != null && !posterURL.isEmpty() && !FromFacility) {
            // Call the method if the URL is not empty
            AdminImagesAdapter.showImageOptionsDialog(context, db, posterURL, true);
        } else if (posterURL != null && !posterURL.isEmpty() && FromFacility) {
            AdminImagesAdapter.deleteImageFromFirestore(context, db, posterURL, true);
        } else {
            if (!FromFacility) {
                AdminActivity.showAdminAlertDialog(context, null, "No Poster Available",
                                                   "There is no event poster to delete.",
                                                   "TIP: ADD AN EVENT POSTER TO MAKE YOUR EVENT MORE ENTICING!",
                                                   null, "OK", null);
            }
        }
    }

    /**
     * DeletingEvent method deletes the event from Firestore.
     * @param context the context of the activity
     * @param eventID the ID of the event
     * @param db the Firestore instance
     * @param onSuccess the success callback
     * @param onFailure the failure callback
     * @param FromFacility a boolean indicating if the deletion is from the facility
     */
    public static void DeletingEvent(Context context, String eventID, FirebaseFirestore db,
                                     Runnable onSuccess, Runnable onFailure, boolean FromFacility) {
        // Fetch the event document from Firestore
        db.collection("events").document(eventID).get()
          .addOnSuccessListener(documentSnapshot -> {
              Event current_event = documentSnapshot.toObject(Event.class);
              if (current_event == null || db == null || current_event.getEventID() == null ||
                  current_event.getEventID().isEmpty()) {
                  throw new IllegalArgumentException(
                          "Event, Firestore instance, or Event ID cannot be null or empty.");
              }
              if (!FromFacility) {
                  AdminActivity.showAdminAlertDialog(
                          context,
                          () -> DeleteEvent(context, db, current_event, eventID, onSuccess,
                                            onFailure), // Pass the confirmation action
                          "Delete this event?",
                          null,
                          "WARNING: DELETION CANNOT BE UNDONE",
                          "Cancel",
                          "Confirm",
                          null
                  );

//                  notifications.addNotifications( "Event Deleted", "This event was deleted by admin, sorry for the inconvienience", null, eventID, true);

              } else {
                  // Directly delete the event if not from facility
                  DeleteEvent(context, db, current_event, eventID, onSuccess, onFailure);
              }
          })
          .addOnFailureListener(e -> {
              // Handle failure to fetch event document
              if (onFailure != null) {
                  onFailure.run();
              }
              throw new RuntimeException("Failed to fetch event document: " + e.getMessage(), e);
          });
    }

    /**
     * DeleteEvent method deletes the event from Firestore.
     * @param context the context of the activity
     * @param db the Firestore instance
     * @param current_event the current event object
     * @param eventID the ID of the event
     * @param onSuccess the success callback
     * @param onFailure the failure callback
     */
    public static void DeleteEvent(Context context, FirebaseFirestore db, Event current_event,
                                   String eventID, Runnable onSuccess, Runnable onFailure) {
        if (current_event == null || db == null || eventID == null || eventID.isEmpty()) {
            throw new IllegalArgumentException(
                    "Event, Firestore instance, or Event ID cannot be null or empty.");
        }

        ArrayList<String> WaitlistedEntrants = current_event.getWaitlisted();
        ArrayList<String> SelectedEntrants = current_event.getSelected();
        ArrayList<String> FinalistEntrants = current_event.getFinalists();
        ArrayList<String> CancelledEntrants = current_event.getCancelled();

        // Delete the event poster and QR code
        DeletingEventPoster(context, db, current_event.getEventPosterURL(), true);
        DeletingQRCode(context, db, current_event);

        // Remove the event ID from related collections
        DeletingAsWaitlisted(current_event.getEventID(), db, WaitlistedEntrants);
        DeletingAsSelected(current_event.getEventID(), db, SelectedEntrants);
        DeletingAsFinalist(current_event.getEventID(), db, FinalistEntrants);
        DeletingAsCancelled(current_event.getEventID(), db, CancelledEntrants);

        // Remove the event ID from the organizers collection
        DeletingEventIDinOrganizers(context, current_event.getDeviceId(),
                                    current_event.getEventID(), db);

        // Delete the event document itself
        db.collection("events").document(eventID)
          .delete()
          .addOnSuccessListener(aVoid -> {
              // Call success callback when all operations are complete
              if (onSuccess != null) {
                  onSuccess.run();
              }
          })
          .addOnFailureListener(e -> {
              // Handle failure to delete the event document
              if (onFailure != null) {
                  onFailure.run();
              }
          });
    }

    /**
     * DeletingAsWaitlisted method removes the event ID from the waitlistedEvents array for each entrant.
     * @param eventID the ID of the event
     * @param db the Firestore instance
     * @param WaitlistedEntrants the list of waitlisted entrants
     */
    public static void DeletingAsWaitlisted(String eventID, FirebaseFirestore db,
                                            ArrayList<String> WaitlistedEntrants) {
        if (WaitlistedEntrants == null || WaitlistedEntrants.isEmpty()) {
            Log.e("Firestore", "WaitlistedEntrants is null or empty");
            return;
        }
        for (String entrant : WaitlistedEntrants) {
            // Access each entrant's document
            db.collection("entrants")
              .document(entrant)
              .update("waitlistedEvents",
                      FieldValue.arrayRemove(eventID)) // Remove eventID from the array
              .addOnSuccessListener(aVoid -> {
                  Log.d("Firestore",
                        "Successfully removed eventID from waitlistedEvents for entrant: " +
                        entrant);
              })
              .addOnFailureListener(e -> {
                  Log.e("Firestore",
                        "Failed to remove eventID from waitlistedEvents for entrant: " + entrant,
                        e);
              });
        }
    }

    /**
     * DeletingAsSelected method removes the event ID from the invitedEvents array for each entrant.
     * @param eventID the ID of the event
     * @param db the Firestore instance
     * @param SelectedEntrants the list of selected entrants
     */
    public static void DeletingAsSelected(String eventID, FirebaseFirestore db,
                                          ArrayList<String> SelectedEntrants) {
        if (SelectedEntrants == null || SelectedEntrants.isEmpty()) {
            Log.e("Firestore", "WaitlistedEntrants is null or empty");
            return;
        }
        for (String entrant : SelectedEntrants) {
            // Access each entrant's document
            db.collection("entrants")
              .document(entrant)
              .update("invitedEvents",
                      FieldValue.arrayRemove(eventID)) // Remove eventID from the array
              .addOnSuccessListener(aVoid -> {
                  Log.d("Firestore",
                        "Successfully removed eventID from waitlistedEvents for entrant: " +
                        entrant);
              })
              .addOnFailureListener(e -> {
                  Log.e("Firestore",
                        "Failed to remove eventID from waitlistedEvents for entrant: " + entrant,
                        e);
              });
        }
    }

    /**
     * DeletingAsFinalist method removes the event ID from the finalistEvents array for each entrant.
     * @param eventID the ID of the event
     * @param db the Firestore instance
     * @param FinalistEntrants the list of finalist entrants
     */
    public static void DeletingAsFinalist(String eventID, FirebaseFirestore db,
                                          ArrayList<String> FinalistEntrants) {
        if (FinalistEntrants == null || FinalistEntrants.isEmpty()) {
            Log.e("Firestore", "FinalistEntrants is null or empty");
            return;
        }
        for (String entrant : FinalistEntrants) {
            // Access each entrant's document
            db.collection("entrants")
              .document(entrant)
              .update("finalistEvents",
                      FieldValue.arrayRemove(eventID)) // Remove eventID from the array
              .addOnSuccessListener(aVoid -> {
                  Log.d("Firestore",
                        "Successfully removed eventID from waitlistedEvents for entrant: " +
                        entrant);
              })
              .addOnFailureListener(e -> {
                  Log.e("Firestore",
                        "Failed to remove eventID from waitlistedEvents for entrant: " + entrant,
                        e);
              });
        }
    }

    /**
     * DeletingAsCancelled method removes the event ID from the uninvitedEvents array for each entrant.
     * @param eventID the ID of the event
     * @param db the Firestore instance
     * @param uninvitedEvents the list of uninvited events
     */
    public static void DeletingAsCancelled(String eventID, FirebaseFirestore db,
                                           ArrayList<String> uninvitedEvents) {
        if (uninvitedEvents == null || uninvitedEvents.isEmpty()) {
            Log.e("Firestore", "uninvitedEvents is null or empty");
            return;
        }
        for (String entrant : uninvitedEvents) {
            // Access each entrant's document
            db.collection("entrants")
              .document(entrant)
              .update("uninvitedEvents",
                      FieldValue.arrayRemove(eventID)) // Remove eventID from the array
              .addOnSuccessListener(aVoid -> {
                  Log.d("Firestore",
                        "Successfully removed eventID from waitlistedEvents for entrant: " +
                        entrant);
              })
              .addOnFailureListener(e -> {
                  Log.e("Firestore",
                        "Failed to remove eventID from waitlistedEvents for entrant: " + entrant,
                        e);
              });
        }
    }

    /**
     * DeletingEventIDinOrganizers method removes the event ID from the events array for the organizer.
     * @param context the context of the activity
     * @param deviceId the device ID of the organizer
     * @param eventID the ID of the event
     * @param db the Firestore instance
     */
    public static void DeletingEventIDinOrganizers(Context context, String deviceId, String eventID,
                                                   FirebaseFirestore db) {
        db.collection("organizers")
          .document(deviceId)
          .update("events", FieldValue.arrayRemove(eventID))
          .addOnSuccessListener(aVoid -> {
              // Log success or notify the user if needed
              Toast.makeText(context, "Event ID removed successfully.", Toast.LENGTH_SHORT).show();
          })
          .addOnFailureListener(e -> {
              // Handle failure (e.g., log the error or show a message to the user)
              Toast.makeText(context, "Failed to remove Event ID: " + e.getMessage(),
                             Toast.LENGTH_SHORT).show();
          });
    }

    /**
     * showQRCodePopup method displays the QR code for the event in a popup dialog.
     * @param context the context of the activity
     * @param db the Firestore instance
     * @param event the event object
     * @param qrData the QR data for the event
     * @param isAdmin a boolean indicating if the user is an admin
     */
    public static void showQRCodePopup(Context context, FirebaseFirestore db, Event event,
                                       String qrData, boolean isAdmin) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.dialog_qr_code, null);

        // Find the ImageView in the popup layout
        ImageView qrCode = popupView.findViewById(R.id.qr_code_image);

        // Set the QR code image
        if (event != null) {
            if (qrData != null && !qrData.isEmpty()) {
                try {
                    BitMatrix bitMatrix = deserializeBitMatrix(qrData); // Convert back to BitMatrix
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(bitMatrix); // Create Bitmap from BitMatrix
                    qrCode.setImageBitmap(bitmap); // Set the QR code image
                } catch (WriterException e) {
                    Log.e("QRCodeError", "Error converting QR code string to BitMatrix", e);
                }
                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(popupView);

                // Add the Close button
                builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

                // Add the Delete button if the user is an admin
                if (isAdmin) {
                    builder.setNegativeButton("Delete", (dialog, which) -> {
                        DeletingQRCode(context, db, event); // Call the delete method
                    });
                }

                // Show the dialog
                builder.create().show();
            } else {
                AdminActivity.showAdminAlertDialog(context, null, "QR Code is non-existent",
                                                   "This QR Code has most likely been deleted by an Admin",
                                                   null, null, "OK", null);
//                qrCode.setImageBitmap(null); // Clear the image if QR data is null or empty
            }
        }

    }

    /**
     * deserializeBitMatrix method converts a string representation of a BitMatrix back to a BitMatrix object.
     * @param data the string representation of the BitMatrix
     * @return the BitMatrix object
     * @throws WriterException if there is an error converting the string to a BitMatrix
     */
    public static BitMatrix deserializeBitMatrix(String data) throws WriterException {
        String[] lines = data.split("\n");
        int width = lines[0].length();
        int height = lines.length;
        BitMatrix bitMatrix = new BitMatrix(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Set pixel based on whether the character is '1' (black) or '0' (white)
                if (lines[y].charAt(x) == '1') {
                    bitMatrix.set(x, y); // Set pixel to black
                }
            }
        }
        return bitMatrix;
    }

    /**
     * onCreate method for EntrantEventDetailsActivity
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerEventDetailsBinding.inflate(getLayoutInflater());
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        contentFrame.addView(binding.getRoot());

        // Set up the app bar for back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
            getSupportActionBar().setTitle("Event Details"); // Set a custom title if needed
        }

        qrCodeValue = getIntent().getStringExtra("qrCodeValue");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false); // Default value: false

        db = FirebaseFirestore.getInstance();

        // Use addSnapshotListener for real-time updates
        db.collection("events").whereEqualTo("eventID", qrCodeValue)
          .addSnapshotListener((querySnapshot, error) -> {
              if (error != null) {
                  Log.e("Firestore", "Error listening to event updates", error);
                  return;
              }

              if (querySnapshot != null && !querySnapshot.isEmpty()) {
                  // Get the document snapshot
                  document = querySnapshot.getDocuments().get(0);
                  event = document.toObject(Event.class);

                  // Extract and update fields
                  if (event != null) {
                      date = event.getEventDate();  // Get the date from the Event object
                      signupDate = event.getSignupDeadline();  // Get the signup date
                      time = event.getTime();  // Get the time
                      eventName = event.getName();  // Get the name
                      location = event.getLocation();  // Get the location
                      description = event.getDescription();  // Get the description
                      eventPosterURL = event.getEventPosterURL();  // Get the event poster URL
                      qrData = event.getQRData();  // Get the QR data
                      eventID = event.getEventID();  // Get the event ID
                      entrantsChosen = event.getEntrantsChosen();
                  }

                  try {
                      signup = sdf.parse(signupDate);
                      currentDate = sdf.parse(sdf.format(new Date())); // Truncate current date
                  } catch (ParseException e) {
                      Log.e("ParseError", "Error parsing dates", e);
                  }

                  List<Object> finalists = (List<Object>) document.get("finalists");

                  ImageView eventPoster = binding.eventImage;

                  // Load event poster or default image
                  String posterURL = event.getEventPosterURL();
                  if (posterURL != null && !posterURL.trim().isEmpty()) {
                      // Load the image from the posterURL using Glide
                      Glide.with(this)
                           .load(posterURL)
                           .placeholder(R.drawable.event_image) // Set the default placeholder image
                           .error(R.drawable.error_image) // Set the error image for invalid URLs
                           .into(eventPoster);
                  } else {
                      // No posterURL available, use the default placeholder image
                      eventPoster.setImageResource(R.drawable.event_image);
                  }

                  binding.eventTitle.setText(eventName);
                  binding.eventDate.setText(date);
                  List<Object> waitlisted = (List<Object>) document.get("waitlisted");
                  List<Object> finalist = (List<Object>) document.get("finalists");
                  Long capacity = (Long) document.get("eventSlots");
                  Long waitListCapacity = (Long) document.get("waitListCapacity");
                  assert capacity != null;
                  Integer eventSlots = capacity.intValue() - finalist.size();
                  String capacityAsString = eventSlots.toString();

                  if (waitListCapacity == 0) {
//                          Does not show badges if there is no waitlistCapacity section
                      binding.eventWaitlistCapacitySection.setVisibility(View.GONE);
                      binding.spotsAvailableSection.setVisibility(View.GONE);
                  } else if (waitListCapacity > 0) {
                      spotsRemaining = (waitListCapacity.intValue() - waitlisted.size()) > 0 ? (
                              waitListCapacity.intValue() - waitlisted.size()) : 0;
                      spotsRemainingText = "Only " + spotsRemaining +
                                           " spot(s) available on waitlist";
                      binding.spotsAvailable.setText(spotsRemainingText);

                      if (entrantsChosen) {
                          spotsRemainingText = "Only 0 spots available on waitlist";
                          binding.spotsAvailable.setText(spotsRemainingText);
                      }
                  }

                  binding.eventTitle.setText(eventName);
                  binding.eventDate.setText("Event Date: " + date);
                  binding.signupDate.setText("Signup Deadline: " + signupDate);
                  binding.eventTime.setText("Event Time: " + time);
                  binding.eventWaitlistCapacity.setText(
                          "Waitlist Capacity: " + waitListCapacity);
                  binding.eventLocation.setText(location);
                  binding.eventSlotsCapacity.setText("Event Slots: " + capacityAsString);
                  binding.eventDescription.setText(description);
                  usesGeolocation = (Boolean) document.get("geoLocation");

                  event.setFinalists((ArrayList<String>) document.get("finalists"));
                  event.setWaitlisted((ArrayList<String>) document.get("waitlisted"));
                  event.setSelected((ArrayList<String>) document.get("selected"));
                  event.setCancelled((ArrayList<String>) document.get("cancelled"));
                  event.setReselected((ArrayList<String>) document.get("reselected"));

                  event.setWaitlistedNotificationsList(
                          (ArrayList<String>) document.get("waitlistedNotificationsList"));
                  event.setWaitlistedNotificationsList(
                          (ArrayList<String>) document.get("joinedNotificationsList"));
                  event.setWaitlistedNotificationsList(
                          (ArrayList<String>) document.get("cancelledNotificationsList"));
                  event.setWaitlistedNotificationsList(
                          (ArrayList<String>) document.get("selectedNotificationsList"));

                  updateLotteryButtonVisibility(isAdmin);

              }
          });
        eventsRef = db.collection("events");
        binding.editEventButton.setVisibility(View.VISIBLE);

        binding.editEventButton.setOnClickListener(view -> {
            selectImage();
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                            }
                            if (selectedImageUri != null) {
                                editImage(selectedImageUri);
                            }
                        }
                    }

                });

        binding.lotterySystemButton.setOnClickListener(view -> {
            if (event == null) {

                // Show a message that the event data is not available yet
                showAdminAlertDialog(this, null, "Error",
                                     "Event data is not loaded yet. Please try again later.", null,
                                     null, "OK", null);
                return;
            }

            if (event.getWaitlisted().size() == 0) {
                showAdminAlertDialog(this, null, "Cannot Select",
                                     "There is no one in the waitlist.", null, null, "OK", null);
                return;

            }

            if (!currentDate.after(signup)) {
                showAdminAlertDialog(this, null, "Cannot Select",
                                     "Cannot select entrants until after the signup date", null,
                                     null, "OK", null);

            }

            if (event.getEntrantsChosen()) {
                showAdminAlertDialog(this, null, "Cannot Sample Again",
                                     "Cannot sample entrants again. You can reselect them in the List of Entrants tab.",
                                     null, null, "OK", null);
            }

            if (binding.lotterySystemButton.isEnabled() && !event.getEntrantsChosen() &&
                currentDate.after(signup)) {
                event.lotterySystem();
                updateUnSelectedEntrants(event);
                updateSelectedEntrants(event);
                updateInvitedEntrants(event);
                updateUninvitedEntrants(event);

                showAdminAlertDialog(this, null, "Entrants Selected",
                                     "Entrants were selected for the event.", null, null, "OK",
                                     null);
            }

            updateLotteryButtonVisibility(isAdmin);
        });

        binding.entrantListButton.setVisibility(View.VISIBLE);
        binding.entrantListButton.setOnClickListener(view -> {
            // get the event object corresponding to the qr code - huh is this not the wrong value?
            Intent intent = new Intent(this, OrganizerNotificationsActivity.class);
            intent.putExtra("eventID", qrCodeValue);
            startActivity(intent);
        });

        binding.showQrCodeButton.setOnClickListener(view -> {
            showQRCodePopup(this, db, event, qrData, isAdmin);
        });

        if (isAdmin) {
            binding.lotterySystemButton.setVisibility(View.GONE);

            binding.editEventButton.setText("Delete Event Poster");
            binding.editEventButton.setOnClickListener(view -> {
                showAdminAlertDialog(
                        this,
                        () -> {
                            // This is the action for the first dialog's confirm button
                            showAdminAlertDialog(
                                    this,
                                    () -> {
                                        // Action for the second dialog's confirm button
                                        DeletingEventPoster(this, db, event.getEventPosterURL(), true);
                                    },
                                    "Confirm Deletion?",
                                    null,
                                    "WARNING: DELETION CANNOT BE UNDONE",
                                    "Cancel",
                                    "Confirm",
                                    null
                            );
                        },
                        "Delete Event Poster?",
                        "Continue to Confirm",
                        "WARNING: DELETION CANNOT BE UNDONE",
                        "Cancel",
                        "Delete",
                        event.getEventPosterURL()
                );
            });


            binding.entrantListButton.setVisibility(View.VISIBLE);
            binding.entrantListButton.setText("Delete Event");
            binding.entrantListButton.setOnClickListener(view ->
                                                         {
                                                             DeletingEvent(this, event.getEventID(),
                                                                           db,
                                                                           () -> {
                                                                               Toast.makeText(this,
                                                                                              "Event deleted successfully.",
                                                                                              Toast.LENGTH_SHORT)
                                                                                    .show();
                                                                               finish(); // Close the activity or return to the previous screen
                                                                           },
                                                                           () -> Toast.makeText(
                                                                                              this,
                                                                                              "Failed to delete event.",
                                                                                              Toast.LENGTH_SHORT)
                                                                                      .show(),
                                                                           false);

                                                             //Go back to the previous page to simulate exiting the event
//                    finish();
//                    onBackPressed();
                                                         });

            binding.showQrCodeButton.setText("Delete QR Code");
            if (event != null && event.getQRData() != null && !event.getQRData().isEmpty()) {
                binding.showQrCodeButton.setOnClickListener(view -> {
                    showQRCodePopup(this, db, event, event.getQRData(), isAdmin);
                });
            } else {
//                Toast.makeText(this, "QR code has already been deleted.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * updateLotteryButtonVisibility method updates the visibility of the lottery button based on the entrants chosen.
     * @param newImageUri the new image URI
     */
    private void editImage(Uri newImageUri) {
        // Step 1: Delete the old image from Google Cloud Storage
        deleteOldImage(eventPosterURL, () -> {
            // Step 2: Upload the new image
            uploadNewImage(newImageUri, newImageUrl -> {
                // Step 3: Update Firestore with the new image URL
                updateFirestoreWithNewImage(newImageUrl, success -> {
                    if (success) {
                        refreshUI(newImageUrl); // Update the UI
                    } else {
                        Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT)
                             .show();
                    }
                });
            });
        });
    }

    /**
     *  refreshUI method refreshes the UI with the new image URL.
     * @param newImageUrl the new image URL
     */
    private void refreshUI(String newImageUrl) {
        Glide.with(this).load(newImageUrl).into(binding.eventImage); // Update your ImageView
        Toast.makeText(this, "Image updated successfully", Toast.LENGTH_SHORT).show();
    }

    /**
     * updateFirestoreWithNewImage method updates Firestore with the new image URL.
     * @param newImageUrl the new image URL
     * @param callback the callback function
     */
    private void updateFirestoreWithNewImage(String newImageUrl, Callback<Boolean> callback) {
        eventsRef.document(eventID).update("eventPosterURL", newImageUrl)
                 .addOnSuccessListener(aVoid -> {
                     callback.onComplete(true); // Notify success
                 })
                 .addOnFailureListener(e -> {
                     callback.onComplete(false); // Notify failure
                 });
    }

    /**
     * uploadNewImage method uploads a new image to Google Cloud Storage.
     * @param newImageUri the new image URI
     * @param callback the callback function
     */
    private void uploadNewImage(Uri newImageUri, Callback<String> callback) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        StorageReference storageRef = FirebaseStorage.getInstance()
                                                     .getReference("event_posters/" + fileName);

        storageRef.putFile(newImageUri)
                  .addOnSuccessListener(taskSnapshot -> {
                      Log.d("Storage", "Successfully uploaded");

                      // Retrieve the download URL
                      storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String eventPosterURL = uri.toString();
                                    callback.onComplete(
                                            eventPosterURL); // Pass the URL to the callback
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Error", "Failed to get download URL", e);
                                    callback.onComplete(null); // Return null to indicate failure
                                });
                  })
                  .addOnFailureListener(e -> {
                      Log.d("Storage", "Failed to upload");
                      callback.onComplete(null); // Return null to indicate failure
                  });
    }

    /**
     * deleteOldImage method deletes the old image from Google Cloud Storage.
     * @param oldImageUrl the old image URL
     * @param onSuccess the success callback
     */
    private void deleteOldImage(String oldImageUrl, Runnable onSuccess) {
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                                                               .getReferenceFromUrl(oldImageUrl);
            storageReference.delete()
                            .addOnSuccessListener(aVoid -> onSuccess.run())
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to delete old image",
                                               Toast.LENGTH_SHORT).show();
                            });
        } else {
            onSuccess.run(); // No old image to delete, continue
        }
    }

    /**
     * selectImage method allows the user to select an image from the device's gallery.
     */
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    /**
     * updateSelectedEntrants method updates the selected entrants in Firestore.
     * @param event the event object
     */
    private void updateSelectedEntrants(Event event) {
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful() && !task.getResult().isEmpty()) {
                  DocumentSnapshot document = task.getResult().getDocuments().get(0);
                  DocumentReference eventRef = db.collection("events").document(document.getId());

                  for (String entrant : event.getSelected()) {
                      eventRef.update("selected", FieldValue.arrayUnion(entrant),
                                      "selectedNotificationsList", FieldValue.arrayUnion(entrant),
                                      "waitlistedNotificationsList",
                                      FieldValue.arrayRemove(entrant));
                  }

                  eventRef.update("waitlisted", event.getReselected()); // DB update
                  eventRef.update("reselected", event.getReselected());
                  eventRef.update("entrantsChosen", event.getEntrantsChosen());
              }
          });
    }

    /**
     * updateUnSelectedEntrants method updates the unselected entrants in Firestore.
     * @param event the event object
     */
    private void updateUnSelectedEntrants(Event event) {
        db.collection("events").whereEqualTo("eventID", event.getEventID()).get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful() && !task.getResult().isEmpty()) {
                  DocumentSnapshot document = task.getResult().getDocuments().get(0);
                  DocumentReference eventRef = db.collection("events").document(document.getId());

                  for (String entrant : event.getCancelled()) {
                      eventRef.update("cancelled", FieldValue.arrayUnion(entrant),
                                      "cancelledNotificationsList", FieldValue.arrayUnion(entrant),
                                      "waitlistedNotificationsList",
                                      FieldValue.arrayRemove(entrant));
                  }
              }
          });
    }

    /**
     * updateInvitedEntrants method updates the invited entrants in Firestore.
     * @param event the event object
     */
    private void updateInvitedEntrants(Event event) {
        for (String entrant : event.getSelected()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    entrantsRef.update("invitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                       "waitlistedEvents",
                                       FieldValue.arrayRemove(event.getEventID()))
                               .addOnSuccessListener(
                                       aVoid -> Log.d("Firestore", "Event added for entrant"))
                               .addOnFailureListener(e -> Log.e("Firestore",
                                                                "Error updating invitedEvents for entrant"));
                }
            });
        }
    }

    /**
     * updateUninvitedEntrants method updates the uninvited entrants in Firestore.
     * @param event the event object
     */
    private void updateUninvitedEntrants(Event event) {
        for (String entrant : event.getWaitlisted()) {
            DocumentReference entrantsRef = db.collection("entrants").document(entrant);
            entrantsRef.get().addOnSuccessListener(entrantDoc -> {
                if (entrantDoc.exists()) {
                    // Assuming `notifications` is an array field in the entrant document
                    entrantsRef.update("uninvitedEvents", FieldValue.arrayUnion(event.getEventID()),
                                       "waitlistedEvents",
                                       FieldValue.arrayRemove(event.getEventID()))
                               .addOnSuccessListener(aVoid -> Log.d("Firestore",
                                                                    "Notification added for entrant"))
                               .addOnFailureListener(e -> Log.e("Firestore",
                                                                "Error updating uninvitedEvents for entrant"));

                }

            });
        }
        event.setWaitlisted(event.getReselected());
    }

    /**
     * updateLotteryButtonVisibility method updates the visibility of the lottery button based on the entrants chosen.
     * @param isAdmin a boolean indicating if the user is an admin
     */
    private void updateLotteryButtonVisibility(boolean isAdmin) {
        if (event == null) {
            binding.lotterySystemButton.setVisibility(View.GONE);
            return;
        }

        if (!isAdmin) {
            boolean showLotteryButton = !event.getEntrantsChosen() && currentDate.after(signup);
            binding.lotterySystemButton.setVisibility(showLotteryButton ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * onBackPressed method overrides the default back button behavior.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button click
            onBackPressed(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}