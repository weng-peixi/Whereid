package com.example.whereid;

/**
 * Created by sf Zhang on 2016/12/20.
 */
public class OneMemo {
    private int tag;
    private String textDate;
    private String textTime;
    private boolean alarm;
    private String mainText;
    private int position;
    private static boolean isShow; // 是否显示CheckBox
    private boolean isChecked;
    public OneMemo(int tag, String textDate, String textTime,boolean alarm, String mainText,boolean isShow ) {
        this.tag=tag;
        this.textDate=textDate;
        this.textTime=textTime;
        this.alarm=alarm;
        this.mainText=mainText;
        this.position =position;
        this.isShow = isShow;
        this.isChecked = isChecked;
    }

    //getter
    public int getTag(){
        return tag;
    }
    public String getTextDate(){
        return textDate;
    }
    public String getTextTime(){
        return textTime;
    }
    public boolean getAlarm(){ return alarm; }
    public String getMainText(){
        return mainText;
    }
    public int getPosition (){return position ;}

    //setter
    public void setTag(int tag){
        this.tag=tag;
    }
    public void setTextDate(String textDate){
        this.textDate=textDate;
    }
    public void setTextTime(String textTime){
        this.textTime=textTime;
    }
    public void setAlarm(boolean alarm){
        this.alarm=alarm;
    }
    public void setMainText(String mainText){
        this.mainText=mainText;
    }
    public void setPosition(int position ){this.position =position ;}

    public static boolean isShow() {
        return isShow;
    }
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
