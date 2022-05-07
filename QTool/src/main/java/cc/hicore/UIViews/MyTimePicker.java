package cc.hicore.UIViews;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.hicore.Utils.Utils;

public class MyTimePicker extends LinearLayout
{
    public int day =0;
    public int hour =1;
    public int minute =0;
    public int second =0;
    public MyTimePicker(Context context)
    {
        this(context,0,1,0,0);
    }
    public MyTimePicker(Context context, int _hour, int _minute, int _second)
    {
        super(context);
        hour=_hour;
        minute=_minute;
        second=_second;
        List<String> Hours = new ArrayList<>();
        List<String> Minute = new ArrayList<>();
        List<String> Second = new ArrayList<>();

        for (int i = 0; i < 24; i++)
        {
            Hours.add("" + i);
        }
        for (int i = 0; i < 60; i++)
        {
            Minute.add(i < 10 ? "" + i : "" + i);
        }
        for (int i = 0; i < 60; i++)
        {
            Second.add(i < 10 ? "" + i : "" + i);
        }
        PickerView mView1 = new PickerView(context);
        TextView tView1 = new TextView(context);

        mView1 = new PickerView(context);
        mView1.setData(Hours);
        mView1.setSelected(_hour);
        mView1.setOnSelectListener(text -> hour = Integer.parseInt(text));
        addView(mView1,new LayoutParams(0, Utils.dip2px(context,200),1));

        tView1 = new TextView(context);
        tView1.setTextColor(Color.GRAY);
        tView1.setText("时");
        tView1.setGravity(Gravity.CENTER);
        tView1.setTextSize(40);
        addView(tView1,new LayoutParams(0, Utils.dip2px(context,200),1));


        mView1 = new PickerView(context);
        mView1.setData(Minute);
        mView1.setSelected(_minute);
        mView1.setOnSelectListener(text -> minute = Integer.parseInt(text));
        addView(mView1,new LayoutParams(0, Utils.dip2px(context,200),1));


        tView1 = new TextView(context);
        tView1.setTextColor(Color.GRAY);
        tView1.setText("分");
        tView1.setGravity(Gravity.CENTER);
        tView1.setTextSize(40);
        addView(tView1,new LayoutParams(0, Utils.dip2px(context,200),1));

        mView1 = new PickerView(context);
        mView1.setData(Second);
        mView1.setSelected(_second);
        mView1.setOnSelectListener(text -> second = Integer.parseInt(text));
        addView(mView1,new LayoutParams(0, Utils.dip2px(context,200),1));

        tView1 = new TextView(context);
        tView1.setTextColor(Color.GRAY);
        tView1.setText("秒");
        tView1.setGravity(Gravity.CENTER);
        tView1.setTextSize(40);
        addView(tView1,new LayoutParams(0, Utils.dip2px(context,200),1));

    }

    public MyTimePicker(Context context, int _day, int _hour, int _minute, int _second)
    {
        super(context);
        day = _day;
        hour=_hour;
        minute=_minute;
        second=_second;
        List<String> Day = new ArrayList<>();
        List<String> Hours = new ArrayList<>();
        List<String> Minute = new ArrayList<>();
        List<String> Second = new ArrayList<>();

        for (int i = 0; i < 30; i++)
        {
            Day.add("" + i);
        }
        for (int i = 0; i < 24; i++)
        {
            Hours.add("" + i);
        }
        for (int i = 0; i < 60; i++)
        {
            Minute.add(i < 10 ? "" + i : "" + i);
        }
        for (int i = 0; i < 60; i++)
        {
            Second.add(i < 10 ? "" + i : "" + i);
        }

        setOrientation(HORIZONTAL);

        PickerView mView1 = new PickerView(context);
        mView1.setData(Day);
        mView1.setSelected(_day);
        mView1.setOnSelectListener(text -> day = Integer.parseInt(text));
        addView(mView1,new LayoutParams(0, Utils.dip2px(context,200),1));


        TextView tView1 = new TextView(context);
        tView1.setTextColor(Color.GRAY);
        tView1.setText("天");
        tView1.setGravity(Gravity.CENTER);
        tView1.setTextSize(40);
        addView(tView1,new LayoutParams(0, Utils.dip2px(context,200),1));


        mView1 = new PickerView(context);
        mView1.setData(Hours);
        mView1.setSelected(_hour);
        mView1.setOnSelectListener(text -> hour = Integer.parseInt(text));
        addView(mView1,new LayoutParams(0, Utils.dip2px(context,200),1));

        tView1 = new TextView(context);
        tView1.setTextColor(Color.GRAY);
        tView1.setText("时");
        tView1.setGravity(Gravity.CENTER);
        tView1.setTextSize(40);
        addView(tView1,new LayoutParams(0, Utils.dip2px(context,200),1));


        mView1 = new PickerView(context);
        mView1.setData(Minute);
        mView1.setSelected(_minute);
        mView1.setOnSelectListener(text -> minute = Integer.parseInt(text));
        addView(mView1,new LayoutParams(0, Utils.dip2px(context,200),1));


        tView1 = new TextView(context);
        tView1.setTextColor(Color.GRAY);
        tView1.setText("分");
        tView1.setGravity(Gravity.CENTER);
        tView1.setTextSize(40);
        addView(tView1,new LayoutParams(0, Utils.dip2px(context,200),1));

        mView1 = new PickerView(context);
        mView1.setData(Second);
        mView1.setSelected(_second);
        mView1.setOnSelectListener(text -> second = Integer.parseInt(text));
        addView(mView1,new LayoutParams(0, Utils.dip2px(context,200),1));

        tView1 = new TextView(context);
        tView1.setTextColor(Color.GRAY);
        tView1.setText("秒");
        tView1.setGravity(Gravity.CENTER);
        tView1.setTextSize(40);
        addView(tView1,new LayoutParams(0, Utils.dip2px(context,200),1));


    }
    public int GetSecond()
    {
        return day * 24 * 60 * 60 + hour * 60 * 60 + minute * 60+second;
     }

}
