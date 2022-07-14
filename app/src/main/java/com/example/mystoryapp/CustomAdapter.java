package com.example.mystoryapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

// Adapter class to be used in RecyclerView
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    // Message strings
    public static final String EXTRA_MESSAGE = "com.example.mystoryapp.MESSAGE";
    private static final String TAG = "MessageLogs";

    // List where I stored my entries from Firebase
    private List<EntryModel> entryList;

    // Firebase declarations (for Delete icon onClick)
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Initialize the array list of the Adapter (contains the data to populate views to be used by RecyclerView)
    public CustomAdapter(List<EntryModel> entries){
        entryList = entries;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        // Initialize Firebase Auth and get current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Code onClick methods for edit and delete icons, and for tapping on entry
        ViewHolder holder = new ViewHolder(view, new MyClickListener() {
            // EDIT ICON ONCLICK
            @Override
            public void onEdit(int p) {
                // Open NewEntryScreen activity with selected entry's details
                Context context = view.getContext();
                Intent intent = new Intent(context, NewEntryScreen.class);
                intent.putExtra(EXTRA_MESSAGE, entryList.get(p).getDate());
                context.startActivity(intent);
            }
            // DELETE ICON ONCLICK
            @Override
            public void onDelete(int p) {
                // Get the corresponding entry and delete from list
                Context context = view.getContext();
                db.collection("users").document(user.getUid()).collection("entries").document(entryList.get(p).getDate())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Let the user know it was deleted successfully
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                GeneralFunctions.showAlert(context, context.getResources().getString(R.string.successEntryDelete), Constants.AlertType.ALERT_FINISH.getInt());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }

            // WHEN CLICKING ON ENTRY
            @Override
            public void onEntryClick(int p) {
                // Open ReadEntryScreen activity with selected entry details
                Context context = view.getContext();
                Intent intent = new Intent(context ,ReadEntryScreen.class);
                intent.putExtra(EXTRA_MESSAGE, entryList.get(p).getDate());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get entry from entryList at this position and replace the contents of the view with that element
        EntryModel entry = entryList.get(position);
        viewHolder.getTextViewRow1().setText(entry.getDate() + " at " + entry.getLocation());
        viewHolder.getTextViewRow2().setText(entry.getEntry());
    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return entryList.size();
    }

    // ViewHolder is the view that will contain each individual data
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Declare a listener
        MyClickListener listener;

        // Access the views from text_row_item.xml
        private final TextView textViewRow1, textViewRow2;
        private final ImageView imageViewEdit, imageViewDelete;
        private final TableLayout tableLayoutView;

        // Constructor that accepts the entire item row and does the view lookups to find each subview
        public ViewHolder(View view, MyClickListener listener) {
            super(view);

            // Access the views
            textViewRow1 = view.findViewById(R.id.textViewRow1);
            textViewRow2 = view.findViewById(R.id.textViewRow2);
            imageViewEdit = view.findViewById(R.id.imageViewEdit);
            imageViewDelete = view.findViewById(R.id.imageViewDelete);
            tableLayoutView = view.findViewById(R.id.tableLayoutView);

            // Define click listener for the ViewHolder's View
            this.listener = listener;
            imageViewEdit.setOnClickListener(this);
            imageViewDelete.setOnClickListener(this);
            tableLayoutView.setOnClickListener(this);
        }

        // onClick methods here
        @Override
        public void onClick(View view) {

            // Get index of entry selected
            int position = getAdapterPosition();

            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                // If not, go to the corresponding onClick method
                switch (view.getId()) {
                    case R.id.imageViewEdit:
                        listener.onEdit(position);
                        break;
                    case R.id.imageViewDelete:
                        listener.onDelete(position);
                        break;
                    case R.id.tableLayoutView:
                        listener.onEntryClick(position);
                        break;
                    default:
                        break;
                }
            }
        }

        // Textview getters
        public TextView getTextViewRow1() {
            return textViewRow1;
        }
        public TextView getTextViewRow2() {
            return textViewRow2;
        }
    }

    // onClick listener interface for ViewHolder
    public interface MyClickListener {
        void onEdit(int p);
        void onDelete(int p);
        void onEntryClick(int p);
    }
}
