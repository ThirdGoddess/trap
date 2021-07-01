package com.cyn.traps;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author ThirdGoddess
 * @email ofmyhub@gmail.com
 * @Github https://github.com/ThirdGoddess
 * @date :2019-12-29 01:24
 */
public class BoxDialog extends Dialog {

    //Dialog View
    private View view;

    //Dialog弹出位置
    private LocationView locationView = LocationView.CENTER;

    /**
     * @param context 上下文
     * @param view    Dialog View
     */
    public BoxDialog(Context context, View view) {
        super(context, R.style.BoxDialog);
        this.view = view;
    }

    /**
     * @param context      上下文
     * @param view         Dialog View
     * @param locationView Dialog弹出位置
     */
    public BoxDialog(Context context, View view, LocationView locationView) {
        super(context, R.style.BoxDialog);
        this.view = view;
        this.locationView = locationView;
    }


    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != view) {
            setContentView(view);
            setCancelable(false);//点击外部是否可以关闭Dialog
            setCanceledOnTouchOutside(false);//返回键是否可以关闭Dialog
            Window window = this.getWindow();
            assert window != null;
            switch (locationView) {
                case TOP:
                    window.setGravity(Gravity.TOP);
                    break;
                case BOTTOM:
                    window.setGravity(Gravity.BOTTOM);
                    break;
                case CENTER:
                    window.setGravity(Gravity.CENTER);
                    break;
            }
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    public enum LocationView {
        CENTER, TOP, BOTTOM
    }
}

