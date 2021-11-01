package com.example.minicapstone390.Views;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.Models.User;
import com.example.minicapstone390.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Device Fragment
public class UpdateInfoFragment extends DialogFragment {

    private final FirebaseDatabase dB = FirebaseDatabase.getInstance();;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    protected String userId;
    protected Button cancelButton, saveButton;
    protected EditText userNameInput, userEmailInput, userPhoneInput, userFirstNameInput, userLastNameInput;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_info, container, false);

        // Input Fields for Student Profile Data
        userNameInput = (EditText) view.findViewById(R.id.updateUsername);
        userEmailInput = (EditText) view.findViewById(R.id.updateEmail);
        userPhoneInput = (EditText) view.findViewById(R.id.updatePhone);
        userFirstNameInput = (EditText) view.findViewById(R.id.updateFirstName);
        userLastNameInput = (EditText) view.findViewById(R.id.updateLastName);

        //Create Object and Listener for Cancel and Save Buttons
        cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
        saveButton = (Button) view.findViewById(R.id.save_add_device_button);

        cancelButton.setOnClickListener(v -> dismiss());

        // Not working right now
        saveButton.setOnClickListener(view1 -> {
            // Get Users DB Reference
            FirebaseUser currentUser = auth.getCurrentUser();
            assert currentUser != null;
            userId = currentUser.getUid();

            DatabaseReference userRef = dB.getReference("Users").child(userId);

            // Get Input Responses
            String userName = userNameInput.getText().toString() == null ? userRef.child("userName").toString() : userNameInput.getText().toString();
            String userEmail = userEmailInput.getText().toString() == null ? userRef.child("userEmail").toString() : userEmailInput.getText().toString();
            String userPhone = userPhoneInput.getText().toString() == null ? userRef.child("userPhone").toString() : userPhoneInput.getText().toString();
            String userFirstName = userFirstNameInput.getText().toString() == null ? userRef.child("userFirstName").toString() : userFirstNameInput.getText().toString();
            String userLastName = userLastNameInput.getText().toString() == null ? userRef.child("userLastName").toString() : userLastNameInput.getText().toString();


            // Can add validation after
            User user = new User(userName, userEmail, userPhone, userFirstName, userLastName);
            userRef.setValue(user);
            ((ProfileActivity)getActivity()).updateAllInfo(userRef);
            // Close Fragment
            dismiss();
        });
        return view;
    }
}