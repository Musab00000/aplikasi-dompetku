package com.azhar.hitungpengeluaran.view.fragment.pemasukan.add;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.azhar.hitungpengeluaran.R;
import com.azhar.hitungpengeluaran.model.ModelDatabase;
import com.azhar.hitungpengeluaran.view.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddPemasukanActivity extends AppCompatActivity {

    private static String KEY_IS_EDIT = "key_is_edit";
    private static String KEY_DATA = "key_data";

    public static void startActivity(Context context, boolean isEdit, ModelDatabase pemasukan) {
        Intent intent = new Intent(new Intent(context, AddPemasukanActivity.class));
        intent.putExtra(KEY_IS_EDIT, isEdit);
        intent.putExtra(KEY_DATA, pemasukan);
        context.startActivity(intent);
    }

    private boolean mIsEdit = false;
    private String strId;
    private String userId;

    Toolbar toolbar;
    TextInputEditText etKeterangan, etTanggal, etJmlUang;
    Button btnSimpan;

    private FirebaseFirestore firestore;
    private CollectionReference transaksiRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);

        firestore = FirebaseFirestore.getInstance();
        transaksiRef = firestore.collection("transaksi");

        toolbar = findViewById(R.id.toolbar);
        etKeterangan = findViewById(R.id.etKeterangan);
        etTanggal = findViewById(R.id.etTanggal);
        etJmlUang = findViewById(R.id.etJmlUang);
        btnSimpan = findViewById(R.id.btnSimpan);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userId = getSharedPreferences("user_session", MODE_PRIVATE).getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddPemasukanActivity.this, MainActivity.class));
            finish();
        }

        loadData();
        initAction();
    }

    private void loadData() {
        mIsEdit = getIntent().getBooleanExtra(KEY_IS_EDIT, false);
        if (mIsEdit) {
            ModelDatabase pemasukan = getIntent().getParcelableExtra(KEY_DATA);
            if (pemasukan != null) {
                strId = pemasukan.uid;
                String keterangan = pemasukan.keterangan;
                String tanggal = pemasukan.tanggal;
                int uang = pemasukan.jmlUang;

                etKeterangan.setText(keterangan);
                etTanggal.setText(tanggal);
                etJmlUang.setText(String.valueOf(uang));
            }
        }
    }

    private void initAction() {
        etTanggal.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String strFormatDefault = "d MMMM yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormatDefault, Locale.getDefault());
                etTanggal.setText(simpleDateFormat.format(calendar.getTime()));
            };

            new DatePickerDialog(AddPemasukanActivity.this, date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSimpan.setOnClickListener(v -> {
            String strTipe = "pemasukan";
            String strKeterangan = etKeterangan.getText().toString();
            String strTanggal = etTanggal.getText().toString();
            String strJmlUang = etJmlUang.getText().toString();

            if (strKeterangan.isEmpty() || strTanggal.isEmpty() || strJmlUang.isEmpty()) {
                Toast.makeText(AddPemasukanActivity.this, "Ups, form tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            } else {
                ModelDatabase transaksi = new ModelDatabase(
                        mIsEdit ? strId : transaksiRef.document().getId(),
                        userId,
                        strTipe,
                        strKeterangan,
                        strTanggal,
                        Integer.parseInt(strJmlUang)
                );

                transaksiRef.document(transaksi.uid).set(transaksi).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddPemasukanActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddPemasukanActivity.this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(AddPemasukanActivity.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
