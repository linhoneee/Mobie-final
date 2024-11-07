package com.example.brandtests.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brandtests.MainActivity;
import com.example.brandtests.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaymentSuccessActivity extends AppCompatActivity {

    private static final String TAG = "PaymentSuccessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success); // Đảm bảo bạn đang sử dụng đúng layout

        // Nhận Intent và xử lý Deep Link khi Activity được khởi tạo
        Intent intent = getIntent();
        handleDeepLink(intent);

        // Xử lý sự kiện click của nút Back to Home
        Button backToHomeButton = findViewById(R.id.backToHomeButton);
        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại MainActivity
                Intent homeIntent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish(); // Đóng PaymentSuccessActivity sau khi điều hướng
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Xử lý Deep Link nếu Activity đã mở và có Intent mới
        handleDeepLink(intent);
    }

    // Phương thức xử lý Deep Link và gửi dữ liệu thanh toán lên backend
    private void handleDeepLink(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals("yourapp")) {
            if (uri.getHost().equals("payment") && uri.getPath().equals("/success")) {
                // Lấy paymentId và payerId từ URI
                String paymentId = uri.getQueryParameter("paymentId");
                String payerId = uri.getQueryParameter("PayerID");

                // Hiển thị thông tin thanh toán qua Toast
                Toast.makeText(this, "Payment Success! Payment ID: " + paymentId, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Payment Success! Payment ID: " + paymentId + ", PayerID: " + payerId);

                // Thực hiện yêu cầu HTTP đến backend để xác nhận thanh toán và gửi dữ liệu lên Kafka
                sendPaymentDataToBackend(paymentId, payerId);
            }
        }
    }

    // Phương thức gửi dữ liệu thanh toán lên backend
    private void sendPaymentDataToBackend(String paymentId, String payerId) {
        OkHttpClient client = new OkHttpClient();

        // Tạo yêu cầu HTTP đến endpoint /payment/success của backend
        String url = "http://10.0.2.2:8009/payment/success?paymentId=" + paymentId + "&PayerID=" + payerId;
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Gửi yêu cầu bất đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to send payment data to backend");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Payment data sent to backend successfully");
                } else {
                    Log.e(TAG, "Backend response: " + response.message());
                }
            }
        });
    }
}
