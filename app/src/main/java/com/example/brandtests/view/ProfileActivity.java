package com.example.brandtests.view;


//hiện tại do bên xampp bị lỗi nên k vô php đổi đường dẫn ảnh đúng được nên k biết nó hiển thị ảnh
//trong order history đúng chưa vì data ni mình nói gpt viết sql để nhập vô nên link ảnh k đúng

//có viết log để kiểm tra nếu không tải được ảnh á với log ra api mà được gọi là gì
// xem lại 2 cái ni

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.brandtests.R;
import com.example.brandtests.model.Address;
import com.example.brandtests.model.Order;
import com.example.brandtests.model.OrderItem;
import com.example.brandtests.model.Shipping;
import com.example.brandtests.viewmodel.ProfileViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView tvName, tvEmail, tvBirthday, tvGender;
    private TableLayout addressTable;
    private LinearLayout orderHistoryContainer;
    private ProfileViewModel profileViewModel;
    private Long userId;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("UserID", -1);

        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các thành phần giao diện
        profileImage = findViewById(R.id.profileImage);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvGender = findViewById(R.id.tvGender);
        addressTable = findViewById(R.id.addressTable);
        orderHistoryContainer = findViewById(R.id.orderHistoryContainer);

        // Khởi tạo ViewModel và quan sát dữ liệu
        profileViewModel = new ViewModelProvider(this, new ProfileViewModel.Factory(userId)).get(ProfileViewModel.class);

        // Hiển thị thông tin người dùng
        profileViewModel.getUserData().observe(this, user -> {
            tvName.setText(user.getFirstName() + " " + user.getLastName());
            tvEmail.setText("Email: " + user.getEmail());
            tvBirthday.setText("Birthday: " + user.getBirthday());
            tvGender.setText("Gender: " + user.getGender());
        });

        // Hiển thị danh sách địa chỉ trong TableLayout
        profileViewModel.getAddressData().observe(this, addresses -> {
            addressTable.removeAllViews();
            for (Address address : addresses) {
                addAddressRow("Receiver", address.getReceiverName());
                addAddressRow("Address", address.getStreet() + ", " + address.getWard() + ", " + address.getDistrict() + ", " + address.getProvinceCity());
                addAddressRow("Primary", address.getPrimary() ? "Yes" : "No");
                addAddressRow("Latitude", String.valueOf(address.getLatitude()));
                addAddressRow("Longitude", String.valueOf(address.getLongitude()));
            }
        });

        // Hiển thị lịch sử đơn hàng trong LinearLayout
        profileViewModel.getOrderHistoryData().observe(this, orders -> {
            orderHistoryContainer.removeAllViews();
            for (Order order : orders) {
                TableLayout orderTable = new TableLayout(this);
                orderTable.setPadding(8, 8, 8, 8);

                // Thêm thông tin đơn hàng
                addOrderRow(orderTable, "Order ID", String.valueOf(order.getId()));
                addOrderRow(orderTable, "Total", String.valueOf(order.getTotal()));
                addOrderRow(orderTable, "Date", order.getCreatedAt());

                // Thêm các mục trong đơn hàng
                List<OrderItem> items = order.decodeItems();
                if (items != null) {
                    for (OrderItem item : items) {
                        Log.d(TAG, "OrderItem Data: ID=" + item.getProductId() + ", Name=" + item.getName() +
                                ", Price=" + item.getPrice() + ", Quantity=" + item.getQuantity() +
                                ", ImageURL=" + item.getPrimaryImageUrl());

                        // Tạo một hàng mới để hiển thị ảnh và thông tin sản phẩm
                        TableRow itemRow = new TableRow(this);

                        // Thêm ảnh sản phẩm
                        ImageView productImageView = new ImageView(this);
                        String imageUrl = item.getPrimaryImageUrl();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            String fullImageUrl = "http://10.0.2.2:6001" + imageUrl;
                            Picasso.get().load(fullImageUrl)
                                    .into(productImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "Image loaded successfully for URL: " + fullImageUrl);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.e(TAG, "Failed to load image for URL: " + fullImageUrl, e);
                                        }
                                    });
                        } else {
                            // Nếu không có URL ảnh, dùng ảnh mặc định
                            Picasso.get().load("https://via.placeholder.com/150").into(productImageView);
                        }

                        // Thiết lập kích thước và thêm ảnh vào hàng
                        productImageView.setLayoutParams(new TableRow.LayoutParams(200, 200));
                        itemRow.addView(productImageView);

                        // Thêm thông tin sản phẩm
                        TextView itemInfoView = new TextView(this);
                        itemInfoView.setText(item.getName() + " (x" + item.getQuantity() + ") - Price: $" + item.getPrice());
                        itemInfoView.setPadding(16, 0, 0, 0);
                        itemRow.addView(itemInfoView);

                        orderTable.addView(itemRow);
                    }
                }

                // Thêm thông tin vận chuyển
                Shipping shipping = order.decodeSelectedShipping();
                if (shipping != null) {
                    addOrderRow(orderTable, "Shipping", shipping.getName() + "\nPrice per Km: " + shipping.getPricePerKm() + "\nPrice per Kg: " + shipping.getPricePerKg());
                }

                // Thêm bảng của đơn hàng vào container
                orderHistoryContainer.addView(orderTable);
            }
        });
    }

    // Phương thức để thêm một hàng vào bảng địa chỉ
    private void addAddressRow(String label, String value) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(4, 4, 4, 4);

        TextView labelView = new TextView(this);
        labelView.setText(label + ": ");
        labelView.setTextColor(getResources().getColor(R.color.green_dark));
        labelView.setTextSize(14);
        labelView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(getResources().getColor(R.color.green_dark));
        valueView.setTextSize(14);

        tableRow.addView(labelView);
        tableRow.addView(valueView);

        addressTable.addView(tableRow);
    }

    // Phương thức để thêm một hàng vào bảng đơn hàng
    private void addOrderRow(TableLayout tableLayout, String label, String value) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(4, 4, 4, 4);

        TextView labelView = new TextView(this);
        labelView.setText(label + ": ");
        labelView.setTextColor(getResources().getColor(R.color.green_dark));
        labelView.setTextSize(14);
        labelView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(getResources().getColor(R.color.green_dark));
        valueView.setTextSize(14);

        tableRow.addView(labelView);
        tableRow.addView(valueView);

        tableLayout.addView(tableRow);
    }
}
