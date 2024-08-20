package com.example.brandtests.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.R;
import com.example.brandtests.adapter.BrandAdapter;
import com.example.brandtests.model.Brand;
import com.example.brandtests.viewmodel.BrandViewModel;
import com.example.brandtests.viewmodel.BrandViewModelFactory;
import com.example.brandtests.service.RetrofitClient;

public class BrandActivity extends AppCompatActivity {

    private BrandViewModel brandViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        ListView listView = findViewById(R.id.listViewBrands);
        Button addButton = findViewById(R.id.buttonAddBrand);

        brandViewModel = new ViewModelProvider(this, new BrandViewModelFactory(RetrofitClient.getBrandService())).get(BrandViewModel.class);

        brandViewModel.getBrands().observe(this, brands -> {
            if (brands != null) {
                listView.setAdapter(new BrandAdapter(this, brands, new BrandAdapter.OnBrandClickListener() {
                    @Override
                    public void onDeleteClick(Brand brand) {
                        brandViewModel.deleteBrand(brand.getId());
                    }

                    @Override
                    public void onUpdateClick(Brand brand) {
                        showBrandDialog(brand);
                    }
                }));
            }
        });

        addButton.setOnClickListener(v -> showBrandDialog(null));
    }

    private void showBrandDialog(Brand brand) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_brand, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(dialogView).create();

        EditText nameField = dialogView.findViewById(R.id.editTextBrandName);
        EditText descriptionField = dialogView.findViewById(R.id.editTextBrandDescription);
        Button confirmButton = dialogView.findViewById(R.id.buttonConfirmAddBrand);

        if (brand != null) {
            nameField.setText(brand.getName());
            descriptionField.setText(brand.getDescription());
            confirmButton.setText("Cập nhật");
        }

        confirmButton.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String description = descriptionField.getText().toString();

            if (!name.isEmpty() && !description.isEmpty()) {
                if (brand == null) {
                    Brand newBrand = new Brand();
                    newBrand.setName(name);
                    newBrand.setDescription(description);
                    brandViewModel.addBrand(newBrand);
                } else {
                    brand.setName(name);
                    brand.setDescription(description);
                    brandViewModel.updateBrand(brand);
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
