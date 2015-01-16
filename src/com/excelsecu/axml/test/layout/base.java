package com.excelsecu.axml.test.layout;

import com.excelsecu.axml.test.R;

import android.widget.RelativeLayout;
import android.content.Context;
import android.view.ViewGroup;

public class base {
    public static RelativeLayout get(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        
        RelativeLayout relativeLayout1 = new RelativeLayout(context);
        ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout1.setLayoutParams(layoutParams1);
        
        RelativeLayout relativeLayout2 = new RelativeLayout(context);
        relativeLayout2.setId(R.id.main_g_title_bar);
        relativeLayout2.setBackgroundColor(0xFF338CD9);
        ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 / scale + 0.5f));
        relativeLayout2.setLayoutParams(layoutParams2);
        //android:layout_alignParentTop="true";
        //android:layout_centerHorizontal="true";
        relativeLayout1.addView(relativeLayout2);
        
        return relativeLayout1;
    }
}