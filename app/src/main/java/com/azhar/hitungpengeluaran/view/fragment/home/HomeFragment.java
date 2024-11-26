package com.azhar.hitungpengeluaran.view.fragment.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.core.ui.ChartCredits;
import com.azhar.hitungpengeluaran.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private AnyChartView anyChartView;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        anyChartView = view.findViewById(R.id.anyChartView);
        progressBar = view.findViewById(R.id.progressBar);

        try {
            if (FirebaseApp.getApps(getContext()).isEmpty()) {
                FirebaseApp.initializeApp(getContext());
            }
            firestore = FirebaseFirestore.getInstance();

            userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    .getString("user_id", null);

            if (userId == null) {
                Log.e("HomeFragment", "Session expired. user_id is null.");
                return view;
            }

            setupPieChart();
        } catch (Exception e) {
            Log.e("HomeFragment", "Fatal Error: Failed to initialize HomeFragment.", e);
        }

        return view;
    }

    private void setupPieChart() {
        try {
            Pie pie = AnyChart.pie();

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", 0);
            boolean isDarkMode = sharedPreferences.getBoolean("darkMode", false);

            if (isDarkMode) {
                pie.palette(new String[]{"#BB86FC", "#03DAC6"});
                pie.background().fill("#121212");
                pie.title().fontColor("#FFFFFF");
                pie.labels().fontColor("#E0E0E0");
                pie.legend().fontColor("#F5F5F5");
            } else {
                pie.palette(new String[]{"#6200EE", "#03DAC6"});
                pie.background().fill("#FFFFFF");
                pie.title().fontColor("#000000");
                pie.labels().fontColor("#212121");
                pie.legend().fontColor("#000000");
            }

            pie.title("Pengeluaran vs Pemasukan (30 Hari Terakhir)");
            pie.labels().position("outside");
            pie.legend().title().enabled(true).text("Kategori").padding(0d, 0d, 10d, 0d);

            ChartCredits credits = pie.credits();
            credits.enabled(false);

            anyChartView.setChart(pie);

            listenToRealtimeUpdates(pie);
        } catch (Exception e) {
            Log.e("HomeFragment", "Fatal Error: Failed to set up PieChart.", e);
        }
    }

    private void listenToRealtimeUpdates(Pie pie) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            anyChartView.setVisibility(View.GONE);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -30);
            Date startDate = calendar.getTime();

            listenerRegistration = firestore.collection("transaksi")
                    .whereEqualTo("userId", userId)
                    .orderBy("tanggal", Query.Direction.DESCENDING)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.e("HomeFragment", "Fatal Error: Failed to listen to Firestore updates.", e);
                            progressBar.setVisibility(View.GONE);
                            anyChartView.setVisibility(View.VISIBLE);
                            return;
                        }

                        int totalPemasukan = 0;
                        int totalPengeluaran = 0;

                        try {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id"));
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    String tipe = document.getString("tipe");
                                    String tanggalStr = document.getString("tanggal");
                                    long jumlah = document.getLong("jmlUang");

                                    try {
                                        Date tanggal = dateFormat.parse(tanggalStr);
                                        if (tanggal != null && tanggal.after(startDate)) {
                                            if ("pemasukan".equals(tipe)) {
                                                totalPemasukan += jumlah;
                                            } else if ("pengeluaran".equals(tipe)) {
                                                totalPengeluaran += jumlah;
                                            }
                                        }
                                    } catch (ParseException ex) {
                                        Log.e("HomeFragment", "Error parsing tanggal: " + tanggalStr, ex);
                                    }
                                }

                                List<DataEntry> data = new ArrayList<>();
                                data.add(new ValueDataEntry("Pemasukan", totalPemasukan));
                                data.add(new ValueDataEntry("Pengeluaran", totalPengeluaran));

                                pie.data(data);
                                anyChartView.invalidate();
                            } else {
                                List<DataEntry> emptyData = new ArrayList<>();
                                emptyData.add(new ValueDataEntry("Pemasukan", 0));
                                emptyData.add(new ValueDataEntry("Pengeluaran", 0));
                                pie.data(emptyData);
                                Log.d("HomeFragment", "Tidak ada data untuk 30 hari terakhir.");
                            }
                        } catch (Exception ex) {
                            Log.e("HomeFragment", "Fatal Error: Failed to process Firestore data.", ex);
                        }

                        progressBar.setVisibility(View.GONE);
                        anyChartView.setVisibility(View.VISIBLE);
                    });
        } catch (Exception e) {
            Log.e("HomeFragment", "Fatal Error: Failed to set up Firestore listener.", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
