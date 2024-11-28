package com.example.slacks_lottoevent;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

public class AdminActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private TabLayout tabLayout;

    /**
     * This method is called when the activity is first created.
     * It sets up the tab layout for the organizer to view the waitlist, invited, cancelled, and enrolled users.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

//        onBackPressed();

        frameLayout = (FrameLayout) findViewById(R.id.FrameLayoutAdmin);
        tabLayout = (TabLayout) findViewById(R.id.tab_Layout_Admin);

        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayoutAdmin, AdminEvents.newInstance()) //AdminEvents()
                .addToBackStack(null)
                .commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected_fragment = null;
                switch (tab.getPosition()){
                    case 0:
                        selected_fragment = AdminEvents.newInstance();
                        break;
                    case 1:
                        selected_fragment = AdminImages.newInstance();;
                        break;
                    case 2:
                        selected_fragment = AdminFacilities.newInstance();;
                        break;
                    case 3:
                        selected_fragment = AdminProfiles.newInstance();;
                        break;
                }
                if (selected_fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.FrameLayoutAdmin, selected_fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                // Change the background color of the tab to white
//                tab.view.setBackgroundColor(Color.WHITE);
//
//                // Change the text color of the tab to pink
//                TextView tabTextView = (TextView) ((LinearLayout) tab.view).getChildAt(1);
//                if (tabTextView != null) {
//                    tabTextView.setTextColor(Color.parseColor("#FFC0CB")); // Pink color
//                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ImageView back = findViewById(R.id.back_button2);
        back.setOnClickListener(v -> {
            onBackPressed();
            onBackPressed();
        });

    }

    /**
     * Creates a Customized Dialog box
     * */
    public static void showAdminAlertDialog(Context context, Runnable onConfirmAction, String Title, String Message, String Terms,
                                            String CancelText, String ConfirmText) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_enable_geolocation, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView DialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView DialogMessage = dialogView.findViewById(R.id.dialog_message);
        TextView DialogTerms = dialogView.findViewById(R.id.dialog_terms);
        Button cancelBtn = dialogView.findViewById(R.id.cancel_button);
        Button confirmBtn = dialogView.findViewById(R.id.confirm_button);

        if (Title != null && !Title.isEmpty()) DialogTitle.setText(Title);
        if (Message != null && !Message.isEmpty()) DialogMessage.setText(Message);
        if (Terms != null && !Terms.isEmpty()) DialogTerms.setText(Terms);
        if (ConfirmText != null && !ConfirmText.isEmpty()) confirmBtn.setText(ConfirmText);

        if (CancelText == null || CancelText.isEmpty()) {
            cancelBtn.setVisibility(View.GONE);
        } else {
            cancelBtn.setText(CancelText); // Optionally set the text if not null or empty
            cancelBtn.setOnClickListener(view -> dialog.dismiss());
        }

        confirmBtn.setOnClickListener(view -> {
            // Execute the confirm action (e.g., request geolocation permission)
            if (onConfirmAction != null) {
                onConfirmAction.run();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

}
