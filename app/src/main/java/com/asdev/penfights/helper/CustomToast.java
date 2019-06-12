package com.asdev.penfights.helper;

import android.app.Activity;
import android.graphics.Color;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asdev.penfights.R;

public class CustomToast {

    public Toast TOAST;
    public LinearLayout TOAST_PARENT;
    public TextView messageView;
    public Activity activity;

    public CustomToast(Activity callback )
    {
        activity = callback;
      //  APP_CONTEXT = context;
        LayoutInflater inflate = activity.getLayoutInflater();
        View layout = inflate.inflate(R.layout.toast_primary, (ViewGroup) activity.findViewById(R.id.toast_parent));
        TOAST = new Toast(activity.getApplicationContext());
        TOAST.setView(layout);
        messageView = layout.findViewById(R.id.toast_message);
        TOAST_PARENT = layout.findViewById(R.id.toast_parent);

    }

    public CustomToast()
    {
        Log.e("customToast", "Pass the activity in the customToast object");
        System.exit(0);
    }

    public void show() {
       TOAST.show();
    }

    public void setMessage(String input) {
        messageView.setText(input);
    }

    public void setLongDuration() {
        TOAST.setDuration(Toast.LENGTH_LONG);
    }

    public void setShortDuration() {
        TOAST.setDuration(Toast.LENGTH_LONG);
    }

    public void setBackGroundColor(String color) {
        TOAST_PARENT.setBackgroundColor(Color.parseColor(color));
    }
    public void setBackGroundColor(int color) {
        TOAST_PARENT.setBackgroundColor(color);
    }

    public void setBackGroundDrawable(int drawable)
    {
        TOAST_PARENT.setBackground(activity.getDrawable(drawable));
    }

    public void setGravity(int gravity, int xOffset , int yOffset )
    {
        TOAST.setGravity(gravity, xOffset, yOffset);
    }
    public void setAlpha(float alpha)
    {
        if(alpha>0f && alpha<1f)
            TOAST_PARENT.setAlpha(alpha);
    }
}
