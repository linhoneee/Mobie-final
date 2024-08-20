package com.example.brandtests.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.brandtests.R;
import com.example.brandtests.databinding.BrandItemBinding;
import com.example.brandtests.model.Brand;

import java.util.List;

public class BrandAdapter extends ArrayAdapter<Brand> {

    private final OnBrandClickListener onBrandClickListener;

    public interface OnBrandClickListener {
        void onDeleteClick(Brand brand);
        void onUpdateClick(Brand brand);
    }

    public BrandAdapter(@NonNull Context context, @NonNull List<Brand> brands, OnBrandClickListener listener) {
        super(context, 0, brands);
        this.onBrandClickListener = listener;

        if (listener == null) {
            Log.e("BrandAdapter", "OnBrandClickListener is null!");
        }
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BrandItemBinding binding;

        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.brand_item, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (BrandItemBinding) convertView.getTag();
        }

        Brand brand = getItem(position);
        binding.setBrand(brand);
        binding.setListener(onBrandClickListener);
        binding.executePendingBindings();

        return convertView;
    }
}
