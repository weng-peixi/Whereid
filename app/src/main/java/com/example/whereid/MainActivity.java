package com.example.whereid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MemoAdapter .OnShowItemClickListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
private DrawerLayout mDrawerLayout;
    private Intent serviceIntent;

    //list to store all the memo
    private List<OneMemo> memolist=new ArrayList<>();
    private List<OneMemo> selectList =new ArrayList<>();
    private List<OneMemo> donelist=new ArrayList<>();
    //adapter
    MemoAdapter adapter;

    //main ListView
    ListView lv;

    //alarm clock
    int BIG_NUM_FOR_ALARM=100;
    private static boolean isShow;
    private LinearLayout lay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout =(DrawerLayout )findViewById(R.id.drawer_layout);
        lay= (LinearLayout) findViewById(R.id.lay);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar =getSupportActionBar() ;
        if(actionBar !=null){
            actionBar .setDisplayHomeAsUpEnabled(true) ;
            //actionBar .setHomeAsUpIndicator(R.drawable.ic_menu_save  );
        }

        Connector.getDatabase();

        //addDataLitepPal();
        loadHistoryData();

        adapter=new MemoAdapter(MainActivity.this, R.layout.memo_list, memolist);
        lv=(ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        adapter.setOnShowItemClickListener(MainActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                onAdd();
                break;
            case android.R.id.home:

                mDrawerLayout.openDrawer(GravityCompat.START) ;
                break;

            default:
        }
        return true;
    }

    private void loadHistoryData() {
        List<Memo> memoes= DataSupport.findAll(Memo.class);

        if(memoes.size()==0) {
            initializeLitePal();
            memoes = DataSupport.findAll(Memo.class);
        }

        for(Memo record:memoes) {
            Log.d("MainActivity", "current num: " + record.getNum());
            Log.d("MainActivity", "id: " + record.getId());
            Log.d("MainActivity", "getAlarm: " + record.getAlarm());
            int tag = record.getTag();
            String textDate = record.getTextDate();
            String textTime = record.getTextTime();
            boolean alarm = record.getAlarm().length() > 1 ? true : false;
            String mainText = record.getMainText();
            OneMemo temp = new OneMemo(tag, textDate, textTime, alarm, mainText,false);
            memolist.add(temp);
        }

    }

    //test
    public void testAdd() {

        Memo record=new Memo();
        record.setNum(1);
        record.setTag(1);
        record.setTextDate("1212");
        record.setTextTime("23:00");
        record.setAlarm("123");
        record.setMainText("hahaha");
//        record.setShow(false );
//        record.setChecked(false);
        record.save();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int n=memolist.size();
        if (isShow) {
            OneMemo oneMemo = memolist.get(position);
            boolean isChecked = oneMemo.isChecked();
            if (isChecked) {
                oneMemo.setChecked(false);
            } else {
                oneMemo.setChecked(true);
            }
            adapter.notifyDataSetChanged();
        } else {
            Intent it = new Intent(this, Edit.class);

            Memo record = getMemoWithNum(position);

            //add information into intent
            transportInformationToEdit(it, record);

            startActivityForResult(it, position);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (isShow) {
            return false;
        } else {
            isShow = true;
            for (OneMemo oneMemo : memolist) {
                oneMemo.setShow(true);
            }
            adapter.notifyDataSetChanged();
            showOpervate();
            //lv.setLongClickable(false);
        }
        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent it) {
        Log.d("cc",""+requestCode );
        if(resultCode==RESULT_OK) {
            updateLitePalAndList(requestCode, it);
        }

    }

    //update the database and memolist acccording to the "num" memo that Edit.class return
    private void updateLitePalAndList(int requestCode, Intent it) {

        int num=requestCode;
        int tag=it.getIntExtra("tag",0);

        Calendar c=Calendar.getInstance();
        String current_date=getCurrentDate(c);
        String current_time=getCurrentTime(c);

        String alarm=it.getStringExtra("alarm");
        String mainText=it.getStringExtra("mainText");

        boolean gotAlarm = alarm.length() > 1 ? true : false;
        OneMemo new_memo = new OneMemo(tag, current_date, current_time, gotAlarm, mainText,false);

        if((requestCode+1)>memolist.size()) {
            // add a new memo record into database
            addRecordToLitePal(num, tag, current_date, current_time, alarm, mainText );

            // add a new OneMemo object into memolist and show
            memolist.add(new_memo);
//            Memo record=getMemoWithNum(num);
//            Memo record1=getMemoWithNum(1);
//            //标识
//            String main=record1.getMainText() ;
//            Log.d("xixi", main);
        }
        else {
            //if the previous has got an alarm clock
            //cancel it first
            if(memolist.get(num).getAlarm()) {
                cancelAlarm(num);
            }

            //update the previous "num" memo
            ContentValues temp = new ContentValues();
            temp.put("tag", tag);
            temp.put("textDate", current_date);
            temp.put("textTime", current_time);
            temp.put("alarm", alarm);
            temp.put("mainText", mainText);
            String where = String.valueOf(num);
            DataSupport.updateAll(Memo.class, temp, "num = ?", where);

            memolist.set(num, new_memo);
        }
        //if user has set up an alarm
        if(gotAlarm) {
            serviceIntent = new Intent(MainActivity.this, MyService.class);
            serviceIntent .putExtra("num",requestCode);
            serviceIntent .putExtra("alarm",alarm);
            startService(serviceIntent);
            //loadAlarm(alarm, requestCode, 0);
        }
        for(OneMemo oneMemo :memolist ){
            oneMemo.setShow(false);}
        adapter.notifyDataSetChanged();
    }

    //when there's no memo in the app
    private void initializeLitePal() {
        Calendar c=Calendar.getInstance();
        String textDate=getCurrentDate(c);
        String textTime=getCurrentTime(c);

        //insert two records into the database
        addRecordToLitePal(0,0,textDate,textTime,"","click to edit" );
        addRecordToLitePal(1,1,textDate,textTime,"","long click to delete");
    }

    //get current date in XX/XX format
    private String getCurrentDate(Calendar c){
        return c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH);
    }

    //get current time in XX:XX format
    private String getCurrentTime(Calendar c){
        String current_time="";
        if(c.get(Calendar.HOUR_OF_DAY)<10) current_time=current_time+"0"+c.get(Calendar.HOUR_OF_DAY);
        else current_time=current_time+c.get(Calendar.HOUR_OF_DAY);

        current_time=current_time+":";

        if(c.get(Calendar.MINUTE)<10) current_time=current_time+"0"+c.get(Calendar.MINUTE);
        else current_time=current_time+c.get(Calendar.MINUTE);

        return current_time;
    }

    private void addRecordToLitePal(int num, int tag, String textDate, String textTime, String alarm, String mainText) {
        Memo record=new Memo();
        record.setNum(num);
        record.setTag(tag);
        record.setTextDate(textDate);
        record.setTextTime(textTime);
        record.setAlarm(alarm);

        record.setMainText(mainText);
//        record.setIfDone(ifDone ) ;
        record.save();
Log.d("wo", "add") ;
        String main=record.getMainText() ;
        Log.d("wo",main);

    }

    private void transportInformationToEdit(Intent it, Memo record) {
        it.putExtra("num",record.getNum());
        it.putExtra("tag",record.getTag());
        it.putExtra("textDate",record.getTextDate());
        it.putExtra("textTime",record.getTextTime());
        it.putExtra("alarm",record.getAlarm());
        it.putExtra("mainText",record.getMainText());
    }

    //press the add button
    public void onAdd() {
        Intent it=new Intent(this,Edit.class);

        int position = memolist.size();

        Calendar c=Calendar.getInstance();
        String current_date=getCurrentDate(c);
        String current_time=getCurrentTime(c);

        it.putExtra("num",position);
        it.putExtra("tag",0);
        it.putExtra("textDate",current_date);
        it.putExtra("textTime",current_time);
        it.putExtra("alarm","");
        it.putExtra("mainText","");

        startActivityForResult(it,position);
    }

    private Memo getMemoWithNum(int num) {
        String whereArgs = String.valueOf(num);
        Memo record= DataSupport.where("num = ?", whereArgs).findFirst(Memo.class);
        return record;
    }

    //***********************************load or cancel alarm************************************************************************************
    //*****************BUG  SOLVED*************************
    //still have a bug as I know:
    //after deleting a memo, the "num" changes, then the cancelAlarm may have some trouble (it do not cancel actually)
    //establishing a hash table may solve this problem
    //SOLVED through adding id
    //******************************************

    //set an alarm clock according to the "alarm"
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
        Intent intent = new Intent(MainActivity.this, OneShotAlarm.class);
        intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
        intent.putExtra("alarmId",record.getId() +BIG_NUM_FOR_ALARM);
        PendingIntent sender = PendingIntent.getBroadcast(
                MainActivity.this, record.getId()+BIG_NUM_FOR_ALARM, intent, 0);

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
    Toast.makeText(MainActivity.this, "所设置时间大于当前时间，请从新设置！", Toast.LENGTH_LONG ).show();
}
        else {
        //int interval = 1000 * 60 * 60 * 24 *days;

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        //if(interval==0)
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarm_time.getTimeInMillis(), sender);}
    }

    //cancel the alarm
    private void cancelAlarm(int num) {
        Memo record=getMemoWithNum(num);

        Intent intent = new Intent(MainActivity.this,
                OneShotAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(
                MainActivity.this,record.getId()+BIG_NUM_FOR_ALARM, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }
    //显示操作界面
    private void showOpervate() {
        lay.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.operate_in);
        lay.setAnimation(anim);
        // 返回、删除、全选和反选按钮初始化及点击监听
        TextView tvBack =(TextView) findViewById(R.id.operate_back);
        TextView tvDelete = (TextView) findViewById(R.id.operate_delete);
        TextView tvSelect = (TextView) findViewById(R.id.operate_select);
        TextView tvInvertSelect = (TextView) findViewById(R.id.invert_select);


        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShow) {
                    selectList.clear();
                    for (OneMemo  bean : memolist ) {
                        bean.setChecked(false);
                        bean.setShow(false);
                    }
                    adapter.notifyDataSetChanged();
                    isShow = false;
                    lv.setLongClickable(true);
                    dismissOperate();
                }
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (OneMemo  bean : memolist ) {
                    if (!bean.isChecked()) {
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        tvInvertSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (OneMemo bean : memolist){
                    if (!bean.isChecked()){
                        bean.setChecked(true);
                        if (!selectList.contains(bean)) {
                            selectList.add(bean);
                        }
                    }else {
                        bean.setChecked(false);
                        if (selectList.contains(bean)) {
                            selectList.remove(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int n = memolist.size();


                if (selectList != null && selectList.size() > 0) {
//                    memolist.removeAll(selectList);
//                    adapter.notifyDataSetChanged();

                    for (OneMemo oneMemo : selectList) {
                       Log.d("xixi",""+oneMemo.getPosition() )  ;
                        if (memolist.get(oneMemo.getPosition()).getAlarm()) {
                            cancelAlarm(oneMemo.getPosition());
                        }
                        memolist.remove(oneMemo);
                        adapter.notifyDataSetChanged();
                        String whereArgs = String.valueOf(oneMemo.getPosition()); //why not position ?
                        DataSupport.deleteAll(Memo.class, "num = ?", whereArgs);

                        for (int i = oneMemo.getPosition() + 1; i < n; i++) {
                            ContentValues temp = new ContentValues();
                            temp.put("num", i - 1);
                            String where = String.valueOf(i);
                            DataSupport.updateAll(Memo.class, temp, "num = ?", where);
                        }
                    }
                    selectList.clear();

                    if (memolist.isEmpty()) {
                        dismissOperate();
                        isShow = false;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "请选择条目", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    // 隐藏操作界面
    private void dismissOperate() {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.operate_out);
        lay.setVisibility(View.GONE);
        lay.setAnimation(anim);
    }
    //checkbox有没有被勾到，有就加到删除列表
    public void onShowItemClick(OneMemo  oneMemo,int position ) {
        if (oneMemo.isChecked() && !selectList.contains(oneMemo)) {
            selectList.add(oneMemo);
            oneMemo.setPosition(position ) ;
        } else if (!oneMemo.isChecked() && selectList.contains(oneMemo)) {
            selectList.remove(oneMemo);
        }
    }

    //********************************************************************************************************************************
}

