package com.azhar.hitungpengeluaran.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.azhar.hitungpengeluaran.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin; // Tambahkan deklarasi untuk tvLogin

    // Firestore reference
    private FirebaseFirestore firestore;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        usersRef = firestore.collection("users");

        // Find Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin); // Temukan TextView Login

        // Set up register button listener
        btnRegister.setOnClickListener(v -> registerUser());

        // Set up login text click listener
        tvLogin.setOnClickListener(v -> {
            // Pindah ke LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Opsional: Tutup RegisterActivity jika tidak diperlukan
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique user_id
        String userId = usersRef.document().getId();

        // Prepare user data
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", userId); // Primary key
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("password", password);

        // Save to Firestore with user_id as document ID
        usersRef.document(userId).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                // Start LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close RegisterActivity after navigating to LoginActivity
            } else {
                Toast.makeText(RegisterActivity.this, "Failed to save user data: " +
                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
