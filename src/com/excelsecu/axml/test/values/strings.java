package com.excelsecu.axml.test.values;

import com.excelsecu.axml.test.R;
import android.util.SparseArray;

public final class strings {
	public static final String title = "titleString";
	public static final String ok = "okString";
	
	public static final SparseArray<String> map = new SparseArray<String>() {
		{
			put(R.string.title, title);
			put(R.string.ok, ok);
		}
	};
}