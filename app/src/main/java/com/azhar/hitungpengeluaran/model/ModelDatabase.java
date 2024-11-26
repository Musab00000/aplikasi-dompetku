package com.azhar.hitungpengeluaran.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelDatabase implements Parcelable {
    public String uid;
    public String userId; // ID pengguna yang terkait dengan transaksi
    public String tipe; // "pemasukan" atau "pengeluaran"
    public String keterangan;
    public String tanggal;
    public int jmlUang;

    // Default constructor for Firestore
    public ModelDatabase() {
    }

    public ModelDatabase(String uid, String userId, String tipe, String keterangan, String tanggal, int jmlUang) {
        this.uid = uid;
        this.userId = userId;
        this.tipe = tipe;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.jmlUang = jmlUang;
    }

    protected ModelDatabase(Parcel in) {
        uid = in.readString();
        userId = in.readString();
        tipe = in.readString();
        keterangan = in.readString();
        tanggal = in.readString();
        jmlUang = in.readInt();
    }

    public static final Creator<ModelDatabase> CREATOR = new Creator<ModelDatabase>() {
        @Override
        public ModelDatabase createFromParcel(Parcel in) {
            return new ModelDatabase(in);
        }

        @Override
        public ModelDatabase[] newArray(int size) {
            return new ModelDatabase[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(userId);
        dest.writeString(tipe);
        dest.writeString(keterangan);
        dest.writeString(tanggal);
        dest.writeInt(jmlUang);
    }
}
