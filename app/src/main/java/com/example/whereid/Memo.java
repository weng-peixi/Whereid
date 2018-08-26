package com.example.whereid;

import org.litepal.crud.DataSupport;

/**
 * Created by sf Zhang on 2016/12/20.
 */
public class Memo extends DataSupport {
    private int num;
    private int tag;
    private String textDate;
    private String textTime;
    private String alarm;
    private String mainText;
   // public boolean ifDone;
    private int id;
//    private  boolean isShow; // 是否显示CheckBox
//    private boolean isChecked;


    //getter
    public int getNum(){
        return num;
    }
    public int getTag(){
        return tag;
    }
    public String getTextDate(){
        return textDate;
    }
    public String getTextTime(){
        return textTime;
    }
    public String getAlarm(){
        return alarm;
    }
    public String getMainText(){
        return mainText;
    }
    public int getId() { return id; }
    /*public boolean getIfDone(){
        return ifDone;
    }*/

    //setter
    public void setNum(int num) {
        this.num=num;
    }
    public void setTag(int tag){
        this.tag=tag;
    }
    public void setTextDate(String textDate){
        this.textDate=textDate;
    }
    public void setTextTime(String textTime){
        this.textTime=textTime;
    }
    public void setAlarm(String alarm){
        this.alarm=alarm;
    }
    public void setMainText(String mainText){
        this.mainText=mainText;
    }
    public void setId(int id){ this.id=id; }
   /* public void setIfDone (boolean ifDone ){this.ifDone =ifDone ;}*/

//    public  boolean isShow() {
//        return isShow;
//    }
//    public void setShow(boolean isShow) {
//        this.isShow = isShow;
//    }
//    public boolean isChecked() {
//        return isChecked;
//    }
//    public void setChecked(boolean isChecked) {
//        this.isChecked = isChecked;
//    }
}
