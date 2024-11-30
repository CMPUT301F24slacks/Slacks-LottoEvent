package com.example.slacks_lottoevent.Utility;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.slacks_lottoevent.view.SignUpActivity;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarUtils {

    /**
     * Display a snackbar prompting the user to sign up.
     *
     * @param view         The view to anchor the snackbar.
     * @param context      The context to start the activity.
     * @param anchorViewId The ID of the view to anchor the snackbar.
     */
    public static void promptSignUp(View view, Context context, int anchorViewId) {
        Snackbar.make(view, "Please create an account first.", Snackbar.LENGTH_LONG)
                .setAction("SIGN UP", v -> {
                    // Navigate to SignUpActivity
                    Intent intent = new Intent(context, SignUpActivity.class);
                    context.startActivity(intent);
                })
                .setAnchorView(anchorViewId) // Anchor to the specified view
                .show();
    }

    public static void promptCreateFacility(View view, Context context, int anchorViewId) {
        Snackbar.make(view, "Please create a facility first.", Snackbar.LENGTH_LONG)
                .setAnchorView(anchorViewId) // Anchor to the specified view
                .show();
    }
}
