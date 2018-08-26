package com.example.whereid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by 翁沛希 on 2018/8/21.
 */
public class AlarmHandlerActivity extends AppCompatActivity     {
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                |WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                |WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }
//        setContentView(R.layout.lockscreen );
        initDialog();
        alertDialog.show();
    }
    @Override
    protected void onNewIntent(Intent intent) {

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {

            //点亮屏幕
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }
        initDialog();
        alertDialog.show();
    }
    public void initDialog()
    {
        //创建AlertDialog的构造器的对象
        AlertDialog.Builder builder=new AlertDialog.Builder(AlarmHandlerActivity .this);
        //设置构造器标题
        builder.setTitle("备忘录");
        //构造器对应的图标
        builder.setIcon(R.mipmap.alarm );
        //构造器内容,为对话框设置文本项(之后还有列表项的例子)
        builder.setMessage("快到备忘录看一下今天要做什么吧！");
        //为构造器设置确定按钮,第一个参数为按钮显示的文本信息，第二个参数为点击后的监听事件，用匿名内部类实现
//        builder.setPositiveButton("是呀", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                //第一个参数dialog是点击的确定按钮所属的Dialog对象,第二个参数which是按钮的标示值
//                finish();//结束App
//            }
//        });
//        //为构造器设置取消按钮,若点击按钮后不需要做任何操作则直接为第二个参数赋值null
//        builder.setNegativeButton("不呀", null);
//        //为构造器设置一个比较中性的按钮，比如忽略、稍后提醒等
//        builder.setNeutralButton("稍后提醒", null);
        //利用构造器创建AlertDialog的对象,实现实例化
         alertDialog=builder.create();
    }
}
