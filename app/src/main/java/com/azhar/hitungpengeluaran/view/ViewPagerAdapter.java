package com.azhar.hitungpengeluaran.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.azhar.hitungpengeluaran.view.fragment.home.HomeFragment;
import com.azhar.hitungpengeluaran.view.fragment.pemasukan.PemasukanFragment;
import com.azhar.hitungpengeluaran.view.fragment.pengaturan.PengaturanFragment;
import com.azhar.hitungpengeluaran.view.fragment.pengeluaran.PengeluaranFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

   public ViewPagerAdapter(FragmentManager manager) {
      super(manager);
   }

   @Override
   public Fragment getItem(int position) {
      Fragment fragment = null;

      switch (position) {
         case 0:
            fragment = new HomeFragment();
            break;
         case 1:
            fragment = new PengeluaranFragment();
            break;
         case 2:
            fragment = new PemasukanFragment();
            break;
         case 3:
            fragment = new PengaturanFragment();
            break;
      }
      return fragment;
   }

   @Override
   public int getCount() {
      return 4;
   }

   @Override
   public CharSequence getPageTitle(int position) {
      String strTitle = "";
      switch (position) {
         case 0:
            strTitle = "Home";
            break;
         case 1:
            strTitle = "Pengeluaran";
            break;
         case 2:
            strTitle = "Pemasukan";
            break;
         case 3:
            strTitle = "Pengaturan";
            break;
      }
      return strTitle;
   }
}
