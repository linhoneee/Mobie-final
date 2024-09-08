package com.example.brandtests;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
public class NotificationHelper {

    private static final String CHANNEL_ID = "my_channel_id";

    public static void showNotification(Context context, String title, String message) {
        // Kiểm tra nếu API là Android 13 (TIRAMISU) trở lên thì cần quyền POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            // Tạo Notification Manager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Xóa kênh thông báo cũ nếu tồn tại để tạo kênh mới
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
                if (existingChannel != null) {
                    notificationManager.deleteNotificationChannel(CHANNEL_ID);
                }

                // Tạo lại kênh thông báo với mức độ ưu tiên cao
                CharSequence name = "My Channel";
                String description = "Channel for app notifications";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }

            // Thêm PendingIntent khi nhấn vào thông báo
            Intent intent = new Intent(context, MainActivity.class); // Thay đổi thành activity bạn muốn mở khi nhấn vào thông báo
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE // Thêm FLAG_IMMUTABLE để tránh lỗi trên Android 12+
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.message)  // Icon nhỏ hiện trên status bar
                    .setContentTitle(title)  // Tiêu đề của thông báo
                    .setContentText(message)  // Nội dung ngắn của thông báo
                    .setPriority(NotificationCompat.PRIORITY_HIGH)  // Đặt mức độ ưu tiên cao để kích hoạt Heads-up notification
                    .setDefaults(Notification.DEFAULT_ALL)  // Sử dụng âm thanh, rung và đèn LED mặc định
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI) // Đảm bảo có âm thanh thông báo
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})  // Thiết lập rung cho thông báo
                    .setContentIntent(pendingIntent)  // Thêm hành động khi người dùng nhấn vào thông báo
                    .setAutoCancel(true);  // Tự động hủy khi người dùng nhấn vào thông báo

            // Hiển thị thông báo với ID duy nhất
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        } else {
            // Thông báo người dùng nếu quyền không được cấp
            Toast.makeText(context, "Notification permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}
