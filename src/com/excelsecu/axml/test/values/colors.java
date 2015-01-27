package com.excelsecu.axml.test.values;

import android.graphics.Color;
import com.excelsecu.axml.test.R;
import java.util.HashMap;

public final class colors {
	public static final int testcolor1 = Color.parseColor("#FF9900");
	public static final int testcolor2 = Color.parseColor("#33CC33");
	public static final int testcolor3 = Color.parseColor("#FF9900");
	public static final int testcolor5 = Color.parseColor("#33CC33");
	
	public static final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>() {
		{
			put(R.color.testcolor1, testcolor1);
			put(R.color.testcolor2, testcolor2);
			put(R.color.testcolor3, testcolor3);
			put(R.color.testcolor5, testcolor5);
		}
	};
}