package com.excelsecu.axml.test.values;

import com.excelsecu.axml.test.R;
import java.util.HashMap;

public final class strings {
	public static final String title = "titleString";
	public static final String ok = "okString";
	public static final String login_no_device = "暂无连接记录";
	
	public static final HashMap<Integer, String> map = new HashMap<Integer, String>() {
		{
			put(R.string.title, title);
			put(R.string.ok, ok);
		}
	};
}