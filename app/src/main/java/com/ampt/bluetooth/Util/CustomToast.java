package com.ampt.bluetooth.Util;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ampt.bluetooth.R;

/**
 * Created by malith on 8/7/15.
 */
public class CustomToast {

    public static void showToast(Activity context, String message, int layoutid) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(layoutid,
                (ViewGroup) context.findViewById(R.id.error_toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.error_toast_text);
        text.setText("           " + message + "           ");
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
