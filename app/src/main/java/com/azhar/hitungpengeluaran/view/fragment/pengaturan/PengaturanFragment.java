package com.azhar.hitungpengeluaran.view.fragment.pengaturan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.azhar.hitungpengeluaran.R;
import com.azhar.hitungpengeluaran.view.LoginActivity;

public class PengaturanFragment extends Fragment {

    private Switch switchDarkMode;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pengaturan, container, false);

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnLogout = view.findViewById(R.id.btnLogout);

        setupDarkModeSwitch();
        setupLogoutButton();

        return view;
    }

    private void setupDarkModeSwitch() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", 0);
        boolean isDarkModeEnabled = sharedPreferences.getBoolean("darkMode", false);

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        switchDarkMode.setChecked(isDarkModeEnabled);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(requireContext(), "Dark Mode diaktifkan", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(requireContext(), "Dark Mode dimatikan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Hapus semua data dari session
            editor.apply();

            Toast.makeText(getContext(), "Logout berhasil!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent); // Arahkan ke LoginActivity
        });
    }
}
