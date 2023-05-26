package com.example.fish;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        // 알림을 생성하는 코드
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.ome_icon) // 알림 아이콘 설정
                .setContentTitle("Fisherman") // 알림 제목 설정
                .setContentText("물고기를 잡았습니다.") // 알림 내용 설정
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // 알림 우선순위 설정

        // 알림을 보내는 코드
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Fisherman", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("물고기를 잡으면 알림을 보냅니다.");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}