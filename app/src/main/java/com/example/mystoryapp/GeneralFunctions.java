package com.example.mystoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

// Class for methods used in multiple activities
public abstract class GeneralFunctions extends Context {

    // Method to show pop-up alert messages (dialogs)
    public static void showAlert(final Context context, String alertMessage, int alertType){

        // Instantiate builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set up alert messages
        builder.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    //  Actions for 'Ok' Button
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss alert but remain in the same activity
                        if(alertType == Constants.AlertType.ALERT_CANCEL.getInt()){
                            dialog.cancel();
                            // Dismiss alert and end activity
                        } else if(alertType == Constants.AlertType.ALERT_FINISH.getInt()){
                            ((Activity)context).finish();
                        } else {
                            System.out.print("Error in calling AlertDialog.");
                        }
                    }
                });

        // Create dialog box and show it
        AlertDialog alert = builder.create();
        alert.show();
    }
}
