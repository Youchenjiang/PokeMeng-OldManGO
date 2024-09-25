package com.PokeMeng.OldManGO.Personal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.PokeMeng.OldManGO.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class SetPersonalData extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    // Inside SetPersonalData class
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Calendar c = Calendar.getInstance();
    TextView txDate;
    EditText editTextAge, editTextText, editTextText3, editTextNumber2, editTextText2;
    ImageView img01, img02, img03;
    RadioButton rbt01, rbt02;
    ImageButton save;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_personal_data);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Find views
        editTextText = findViewById(R.id.editTextText);
        txDate = findViewById(R.id.year);
        editTextAge = findViewById(R.id.editTextNumber);
        txDate.setOnClickListener(this);

        img01 = findViewById(R.id.father);
        img02 = findViewById(R.id.mother);
        img03 = findViewById(R.id.imageView9);

        // Initialize RadioButtons
        rbt01 = findViewById(R.id.men);
        rbt02 = findViewById(R.id.girl);

        editTextText3 = findViewById(R.id.editTextText3);
        editTextNumber2 = findViewById(R.id.editTextNumber2);
        editTextText2 = findViewById(R.id.editTextText2);
        save = findViewById(R.id.save);

        // Set listeners for RadioButtons
        rbt01.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                img01.setVisibility(View.VISIBLE);
                img02.setVisibility(View.INVISIBLE);
                img03.setVisibility(View.INVISIBLE);
            }
        });

        rbt02.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                img01.setVisibility(View.INVISIBLE);
                img02.setVisibility(View.VISIBLE);
                img03.setVisibility(View.INVISIBLE);
            }
        });

        // Set listener for save button
        save.setOnClickListener(v -> saveData());

        // Set listener for close button
        ImageView closeButton = findViewById(R.id.close);
        closeButton.setOnClickListener(v -> showConfirmationDialog());

        // Add Firebase ValueEventListener to monitor the 'users' node
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Personal personal = snapshot.getValue(Personal.class);
                    if (personal != null) {
                        // Log or display the data for debugging
                        Log.d("FirebaseData", "User Name: " + personal.getName());
                        // Additional logging or UI updates can be added here
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("FirebaseData", "Error: " + databaseError.getMessage());
            }
        });
    }

    // Date picker click event
    public void onClick(View v) {
        if (v == txDate) {
            new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this,
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        txDate.setText(year + "/" + (month + 1) + "/" + dayOfMonth);

        // Calculate age
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, dayOfMonth);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        // Set age to EditText
        editTextAge.setText(String.valueOf(age));
    }

    private void saveData() {
        String date = txDate.getText().toString();
        String age = editTextAge.getText().toString();
        String text1 = editTextText.getText().toString();
        String text2 = editTextText3.getText().toString();
        String number2 = editTextNumber2.getText().toString();
        String text3 = editTextText2.getText().toString();

        String selectedGender = rbt01.isChecked() ? "男" : rbt02.isChecked() ? "女" : "Not specified";

        Personal personal = new Personal(text1, date, selectedGender, age, text2, number2, text3);

        // Save to Firestore under the current user
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("Users")
                .document(uid) // Store data under user's UID
                .set(personal)
                .addOnSuccessListener(aVoid -> Toast.makeText(SetPersonalData.this, "儲存成功", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SetPersonalData.this, "Data save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Show confirmation dialog
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你確定要離開這個頁面嗎")
                .setPositiveButton("是", (dialog, id) -> finish())
                .setNegativeButton("否", (dialog, id) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
