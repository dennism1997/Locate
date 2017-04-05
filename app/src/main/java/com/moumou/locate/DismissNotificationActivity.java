package com.moumou.locate;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DismissNotificationActivity extends AppCompatActivity {

    public static PendingIntent getDismissIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, DelayNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int id = getIntent().getIntExtra(Constants.NOTIFICATION_ID, -1);
        manager.cancel(id);
        //MainActivity.removeReminder(id);
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately
    }
}
