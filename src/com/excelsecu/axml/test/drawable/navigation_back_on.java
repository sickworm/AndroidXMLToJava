package com.excelsecu.axml.test.drawable;

import android.graphics.drawable.Drawable;
import android.content.Context;
import java.io.InputStream;

public final class navigation_back_on {

	public static final Drawable get(Context context) {
		InputStream inStream = navigation_back_on.class.getResourceAsStream("/assets/drawable/navigation_back_on.9.png"); 
		Drawable drawable = Drawable.createFromStream(inStream, "navigation_back_on");
		return drawable;
	}
}