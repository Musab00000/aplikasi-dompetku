package com.azhar.hitungpengeluaran.view.fragment.pengeluaran;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azhar.hitungpengeluaran.R;
import com.azhar.hitungpengeluaran.model.ModelDatabase;
import com.azhar.hitungpengeluaran.view.fragment.pengeluaran.add.AddPengeluaranActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class PengeluaranFragment extends Fragment implements PengeluaranAdapter.PengeluaranAdapterCallback {

    private RecyclerView rvListData;
    private FloatingActionButton fabAdd;
    private TextView tvTotal, tvNotFound;
    private PengeluaranAdapter pengeluaranAdapter;
    private ArrayList<ModelDatabase> pengeluaranList = new ArrayList<>();

    private FirebaseFirestore firestore;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pengeluaran, container, false);

        rvListData = view.findViewById(R.id.rvListData);
        fabAdd = view.findViewById(R.id.fabAdd);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvNotFound = view.findViewById(R.id.tvNotFound);

        if (FirebaseApp.getApps(getContext()).isEmpty()) {
            FirebaseApp.initializeApp(getContext());
        }
        firestore = FirebaseFirestore.getInstance();

        userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("user_id", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return view;
        }

        setupRecyclerView();
        loadPengeluaranData();

        fabAdd.setOnClickListener(v -> AddPengeluaranActivity.startActivity(getContext(), false, null));
        view.findViewById(R.id.btnHapus).setOnClickListener(v -> confirmDeleteAllPengeluaran());

        return view;
    }

    private void setupRecyclerView() {
        pengeluaranAdapter = new PengeluaranAdapter(pengeluaranList, this);
        rvListData.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListData.setAdapter(pengeluaranAdapter);
    }

    private void loadPengeluaranData() {
        firestore.collection("transaksi")
                .whereEqualTo("tipe", "pengeluaran")
                .whereEqualTo("userId", userId)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("PengeluaranFragment", "Error memuat data: ", e);
                        Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    pengeluaranList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            ModelDatabase model = snapshot.toObject(ModelDatabase.class);
                            if (model != null) {
                                model.uid = snapshot.getId();
                                pengeluaranList.add(model);
                            }
                        }
                        pengeluaranAdapter.notifyDataSetChanged();

                        int total = 0;
                        for (ModelDatabase data : pengeluaranList) {
                            total += data.jmlUang;
                        }
                        tvTotal.setText("Rp " + total);
                        tvNotFound.setVisibility(View.GONE);
                        rvListData.setVisibility(View.VISIBLE);
                    } else {
                        tvNotFound.setVisibility(View.VISIBLE);
                        rvListData.setVisibility(View.GONE);
                        tvTotal.setText("Rp -");
                    }
                });
    }

    private void confirmDeleteAllPengeluaran() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus semua data pengeluaran?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteAllPengeluaran())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteAllPengeluaran() {
        firestore.collection("transaksi")
                .whereEqualTo("tipe", "pengeluaran")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            firestore.collection("transaksi").document(document.getId()).delete();
                        }
                        Toast.makeText(getContext(), "Semua data pengeluaran berhasil dihapus", Toast.LENGTH_SHORT).show();
                        pengeluaranList.clear();
                        pengeluaranAdapter.notifyDataSetChanged();
                        tvTotal.setText("Rp -");
                        tvNotFound.setVisibility(View.VISIBLE);
                        rvListData.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), "Tidak ada data pengeluaran untuk dihapus", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PengeluaranFragment", "Error menghapus data pengeluaran: ", e);
                    Toast.makeText(getContext(), "Gagal menghapus data pengeluaran", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteClicked(ModelDatabase modelDatabase) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deletePengeluaran(modelDatabase))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deletePengeluaran(ModelDatabase modelDatabase) {
        firestore.collection("transaksi").document(modelDatabase.uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    pengeluaranList.remove(modelDatabase);
                    pengeluaranAdapter.notifyDataSetChanged();

                    int total = 0;
                    for (ModelDatabase data : pengeluaranList) {
                        total += data.jmlUang;
                    }
                    tvTotal.setText("Rp " + total);

                    if (pengeluaranList.isEmpty()) {
                        tvNotFound.setVisibility(View.VISIBLE);
                        rvListData.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PengeluaranFragment", "Error menghapus data: ", e);
                    Toast.makeText(getContext(), "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onEditClicked(ModelDatabase modelDatabase) {
        AddPengeluaranActivity.startActivity(getContext(), true, modelDatabase);
    }
}
