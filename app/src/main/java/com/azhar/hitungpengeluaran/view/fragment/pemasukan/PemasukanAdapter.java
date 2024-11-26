package com.azhar.hitungpengeluaran.view.fragment.pemasukan;

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

public class PemasukanAdapter extends RecyclerView.Adapter<PemasukanAdapter.PemasukanViewHolder> {

    private final ArrayList<ModelDatabase> pemasukanList;
    private final PemasukanAdapterCallback callback;

    public interface PemasukanAdapterCallback {
        void onDeleteClicked(ModelDatabase modelDatabase);
        void onEditClicked(ModelDatabase modelDatabase);
    }

    public PemasukanAdapter(ArrayList<ModelDatabase> pemasukanList, PemasukanAdapterCallback callback) {
        this.pemasukanList = pemasukanList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public PemasukanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_data, parent, false);
        return new PemasukanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PemasukanViewHolder holder, int position) {
        ModelDatabase model = pemasukanList.get(position);

        holder.tvPrice.setText("Rp " + model.jmlUang);
        holder.tvNote.setText(model.keterangan);
        holder.tvDate.setText(model.tanggal);

        holder.ivDelete.setOnClickListener(v -> callback.onDeleteClicked(model));
    }

    @Override
    public int getItemCount() {
        return pemasukanList.size();
    }

    static class PemasukanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice, tvNote, tvDate;
        ImageView ivDelete;

        public PemasukanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
