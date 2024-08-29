package com.example.brandtests.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brandtests.R;

public class PaymentSuccessActivity extends AppCompatActivity {

    private static final String TAG = "PaymentSuccessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        Intent intent = getIntent();
        handleDeepLink(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals("yourapp")) {
            if (uri.getHost().equals("payment") && uri.getPath().equals("/success")) {
                String paymentId = uri.getQueryParameter("paymentId");
                String payerId = uri.getQueryParameter("PayerID");
                // Hiển thị thông tin thanh toán hoặc thực hiện các hành động cần thiết
                Toast.makeText(this, "Payment Success! Payment ID: " + paymentId, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Payment Success! Payment ID: " + paymentId + ", PayerID: " + payerId);
            }
        }
    }
}
