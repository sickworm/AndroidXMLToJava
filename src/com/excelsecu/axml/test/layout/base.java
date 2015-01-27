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
        relativeLayout2.setId(R.id.title);
        relativeLayout2.setBackgroundColor(0xFF338CD9);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 / scale + 0.5f));
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        relativeLayout2.setLayoutParams(layoutParams2);
        relativeLayout1.addView(relativeLayout2);
        
        return relativeLayout1;
    }
}