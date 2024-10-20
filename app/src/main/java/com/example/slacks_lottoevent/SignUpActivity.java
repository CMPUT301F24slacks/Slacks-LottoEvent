package com.example.slacks_lottoevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slacks_lottoevent.databinding.SignUpActivityBinding;

import com.google.firebase.database.DatabaseReference;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.UUID;

/*
*
*
* Relevant Documentation
* https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
* https://developer.android.com/reference/android/util/Patterns#PHONE
* https://developer.android.com/reference/java/util/regex/Pattern
* https://stackoverflow.com/questions/48705124/how-can-i-create-a-unique-key-and-use-it-to-send-data-in-firebase
* https://github.com/google/gson
* */

public class SignUpActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference usersRef;

    private SignUpActivityBinding binding;
    private EditText nameInput;
    private EditText emailInput;
    private EditText phoneInput;

    private  String name;
    private String email;
    private String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = SignUpActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");

        nameInput = binding.nameInput;

        emailInput = binding.emailInput;

        phoneInput = binding.phoneInput;

        Button signUpBtn = binding.signUpButton;

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){
                    Toast.makeText(SignUpActivity.this,"Sign-Up Successful",Toast.LENGTH_SHORT).show();

                    // Inserting the info Device and DB
                    saveUserInfoToDevice();
                    saveUserInfoToFirebase();
//                    Intent homeIntent = new Intent(SignUpActivity.this, EventHomeActivity.class);
//                    startActivity(homeIntent);
                    finish(); // Closing the SignUpActivity to prevent any possible other Activity navigating back to it.
                }

            }
        });


    }
    private boolean validateInputs(){
        name = binding.nameInput.getText().toString().trim();
        email = binding.emailInput.getText().toString().trim();
        phoneNumber = binding.phoneInput.getText().toString().trim();
        System.out.println(phoneNumber);
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            nameInput.requestFocus();
            return false;
        }

        // Validating the email to ensure it is not empty and matches email pattern
        // Patterns.EMAIL_ADDRESS is a regex pattern and we are using matcher() to apply the regex to the field.
        // matches() checks if the pattern is fully matched.
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            emailInput.requestFocus();
            return false;
        }

        // Validating phone number similarly to email
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                phoneInput.setError("Phone number should only contain numbers");
                phoneInput.requestFocus();
                return false;
            }
        }
        return true;

    }
    private void saveUserInfoToDevice() {
        SharedPreferences sharedPreferences = getSharedPreferences("SlacksLottoEventUserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("userName", nameInput.getText().toString().trim());
        editor.putString("userEmail", emailInput.getText().toString().trim());


        editor.putString("userPhone", phoneInput.getText().toString().trim());


        editor.putBoolean("isSignedUp", true); // Mark the user as signed up so MainActivity can check this.
        editor.apply(); // Saving changes to sharedPreferences
    }
    private void saveUserInfoToFirebase(){
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();


        String phone = phoneInput.getText().toString().trim();


        UUID uuid = UUID.fromString(UUID.randomUUID().toString());
        // Generating a Unique key so we can then push a User Object to the DB.

        User user = new User(name,phone,email);


        String userId = UUID.randomUUID().toString();
        usersRef.document(userId).set(user)
                .addOnSuccessListener(nothing -> {
                    System.out.println("Added to DB");
                })
                .addOnFailureListener(nothing -> {
                    System.out.println("failed");
                });


    }

}
