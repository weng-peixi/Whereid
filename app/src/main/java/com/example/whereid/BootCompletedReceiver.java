package com.example.whereid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by 翁沛希 on 2018/8/24.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent it) {
        Intent intent = new Intent(context,MyService.class);
        context.startService(intent);
    }
}
