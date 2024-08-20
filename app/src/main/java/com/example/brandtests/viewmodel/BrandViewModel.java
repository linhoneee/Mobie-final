package com.example.brandtests.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.brandtests.model.Brand;
import com.example.brandtests.service.BrandService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class BrandViewModel extends ViewModel {
    private MutableLiveData<List<Brand>> brands;
    private MutableLiveData<Brand> selectedBrand;
    private BrandService brandService;

    // Inject BrandService thông qua constructor
    public BrandViewModel(BrandService brandService) {
        this.brandService = brandService;
        brands = new MutableLiveData<>();
        selectedBrand = new MutableLiveData<>();
        loadBrands();
    }

    public LiveData<List<Brand>> getBrands() {
        return brands;
    }

    public LiveData<Brand> getSelectedBrand() {
        return selectedBrand;
    }

    private void loadBrands() {
        Log.d("BrandViewModel", "loadBrands: Đang tải thương hiệu");
        brandService.getBrands().enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> call, Response<List<Brand>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("BrandViewModel", "loadBrands: Đã nhận " + response.body().size() + " thương hiệu");
                    brands.setValue(response.body());
                } else {
                    Log.e("BrandViewModel", "loadBrands: Không tải được thương hiệu");
                }
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                Log.e("BrandViewModel", "loadBrands: Lỗi", t);
            }
        });
    }

    public void getBrandById(Long brandId) {
        brandService.getBrandById(brandId).enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedBrand.setValue(response.body());
                    Log.d("BrandViewModel", "getBrandById: Đã nhận thông tin thương hiệu");
                } else {
                    Log.e("BrandViewModel", "getBrandById: Không nhận được thông tin thương hiệu");
                }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                Log.e("BrandViewModel", "getBrandById: Lỗi", t);
            }
        });
    }

    public void addBrand(Brand brand) {
        brandService.createBrand(brand).enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadBrands(); // Tải lại danh sách sau khi thêm mới
                    Log.d("BrandViewModel", "addBrand: Thêm thương hiệu thành công");
                } else {
                    Log.e("BrandViewModel", "addBrand: Thất bại khi thêm thương hiệu");
                }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                Log.e("BrandViewModel", "addBrand: Lỗi", t);
            }
        });
    }

    public void updateBrand(Brand brand) {
        brandService.updateBrand(brand.getId(), brand).enqueue(new Callback<Brand>() {
            @Override
            public void onResponse(Call<Brand> call, Response<Brand> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadBrands(); // Tải lại danh sách sau khi cập nhật
                    Log.d("BrandViewModel", "updateBrand: Cập nhật thương hiệu thành công");
                } else {
                    Log.e("BrandViewModel", "updateBrand: Thất bại khi cập nhật thương hiệu");
                }
            }

            @Override
            public void onFailure(Call<Brand> call, Throwable t) {
                Log.e("BrandViewModel", "updateBrand: Lỗi", t);
            }
        });
    }

    public void deleteBrand(Long brandId) {
        brandService.deleteBrand(brandId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadBrands(); // Tải lại danh sách sau khi xóa
                    Log.d("BrandViewModel", "deleteBrand: Xóa thương hiệu thành công");
                } else {
                    Log.e("BrandViewModel", "deleteBrand: Thất bại khi xóa thương hiệu");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("BrandViewModel", "deleteBrand: Lỗi", t);
            }
        });
    }
}
