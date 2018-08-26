package com.example.whereid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.Calendar;

/**
 * Created by 翁沛希 on 2018/8/23.
 */
public class MyService extends Service {
    int BIG_NUM_FOR_ALARM=100;
    public static final String TAG = "MyService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "in onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "in onStartCommand");
        Log.w(TAG, "MyService:" + this);
        String name = intent.getStringExtra("name");
        String alarm= intent.getStringExtra("alarm");
        int num=intent.getIntExtra("num",0);
        loadAlarm(alarm ,num,0);
        Log.w(TAG, "name:" + name);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "in onDestroy");
    }
    private void loadAlarm(String alarm, int num, int days) {
        int alarm_hour=0;
        int alarm_minute=0;
        int alarm_year=0;
        int alarm_month=0;
        int alarm_day=0;

        int i=0, k=0;
        while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
        alarm_year=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
        alarm_month=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!=' ') i++;
        alarm_day=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!=':') i++;
        alarm_hour=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        alarm_minute=Integer.parseInt(alarm.substring(k));

        Memo record=getMemoWithNum(num);
        String main=record.getMainText() ;
        Log.d("wo",main);
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        Intent intent = new Intent(MyService.this, OneShotAlarm.class);
        intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
        intent.putExtra("alarmId",record.getId() +BIG_NUM_FOR_ALARM);
        PendingIntent sender = PendingIntent.getBroadcast(
                MyService.this, record.getId()+BIG_NUM_FOR_ALARM, intent, 0);

        // We want the alarm to go off 10 seconds from now.
        Calendar calendar = Calendar.getInstance();
        int cyear= calendar.get(Calendar.YEAR);
        int cmonth= calendar.get(Calendar.MONTH );
        int cday= calendar.get(Calendar.DAY_OF_MONTH  );
        int chour= calendar.get(Calendar.HOUR_OF_DAY  );
        int cmintue= calendar.get(Calendar.MINUTE  );
        //calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(cyear ,cmonth,cday ,chour ,cmintue  );
        //calendar.add(Calendar.SECOND, 5);

        Calendar alarm_time = Calendar.getInstance();
        alarm_time.set(alarm_year,alarm_month-1,alarm_day,alarm_hour,alarm_minute);
        if(calendar.getTimeInMillis() >alarm_time.getTimeInMillis()){
            Toast.makeText(MyService .this, "所设置时间大于当前时间，请从新设置！", Toast.LENGTH_LONG).show();
        }
        else {
            //int interval = 1000 * 60 * 60 * 24 *days;

            // Schedule the alarm!
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            //if(interval==0)
            am.set(AlarmManager.RTC_WAKEUP, alarm_time.getTimeInMillis(), sender);}
    }
    private Memo getMemoWithNum(int num) {
        String whereArgs = String.valueOf(num);
        Memo record= DataSupport.where("num = ?", whereArgs).findFirst(Memo.class);
        return record;
    }
}
