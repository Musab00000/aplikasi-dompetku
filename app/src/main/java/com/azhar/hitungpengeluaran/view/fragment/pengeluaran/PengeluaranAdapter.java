package com.azhar.hitungpengeluaran.view.fragment.pengeluaran;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azhar.hitungpengeluaran.R;
import com.azhar.hitungpengeluaran.model.ModelDatabase;

import java.util.ArrayList;

public class PengeluaranAdapter extends RecyclerView.Adapter<PengeluaranAdapter.ViewHolder> {

    private final ArrayList<ModelDatabase> pengeluaranList;
    private final PengeluaranAdapterCallback callback;

    public PengeluaranAdapter(ArrayList<ModelDatabase> pengeluaranList, PengeluaranAdapterCallback callback) {
        this.pengeluaranList = pengeluaranList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelDatabase data = pengeluaranList.get(position);
        holder.tvPrice.setText("Rp " + data.jmlUang);
        holder.tvNote.setText(data.keterangan);
        holder.tvDate.setText(data.tanggal);

        holder.ivDelete.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDeleteClicked(data);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onEditClicked(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pengeluaranList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice, tvNote, tvDate;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public interface PengeluaranAdapterCallback {
        void onDeleteClicked(ModelDatabase modelDatabase);
        void onEditClicked(ModelDatabase modelDatabase);
    }
}
