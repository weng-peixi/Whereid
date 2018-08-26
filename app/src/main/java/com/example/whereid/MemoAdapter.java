package com.example.whereid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sf Zhang on 2016/12/20.
 */
public class MemoAdapter extends ArrayAdapter<OneMemo>{
    private OnShowItemClickListener onShowItemClickListener;
    private int resourceId;
    int[] color={Color.parseColor("#F5EFA0"), Color.parseColor("#8296D5"),Color.parseColor("#95C77E"),Color.parseColor("#F49393"),Color.parseColor("#FFFFFF")};

    public MemoAdapter(Context context, int resource, List<OneMemo> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OneMemo memo=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        ImageView tag=(ImageView)view.findViewById(R.id.tag);
        TextView textDate=(TextView)view.findViewById(R.id.textDate);
        TextView textTime=(TextView)view.findViewById(R.id.textTime);
        ImageView alarm=(ImageView) view.findViewById(R.id.alarm);
        TextView mainText=(TextView)view.findViewById(R.id.mainText);
        CheckBox cb = view.findViewById(R.id.checkBox);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    memo.setChecked(true);

                } else {
                    memo.setChecked(false);
                }
                // 回调方法，将Item加入已选
                onShowItemClickListener.onShowItemClick(memo,position);
            }
        });

        cb.setChecked(memo.isChecked());
//要不要显示checkbox
        if (OneMemo.isShow()) {
            cb.setVisibility(View.VISIBLE);
        } else {
            cb.setVisibility(View.GONE);
        }

        if(memo.getTag()<color.length)
            tag.setBackgroundColor(color[memo.getTag()]);
        textDate.setText(memo.getTextDate());
        textTime.setText(memo.getTextTime());
        if(memo.getAlarm()) {
            alarm.setVisibility(View.VISIBLE);
        }
        else {
            alarm.setVisibility(View.GONE);
        }
        mainText.setText(memo.getMainText());

        return view;
    }
    public interface OnShowItemClickListener {
        void onShowItemClick(OneMemo bean, int position);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}
