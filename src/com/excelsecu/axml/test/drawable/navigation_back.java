package com.excelsecu.axml.test.drawable;

import android.graphics.drawable.Drawable;
import android.content.Context;
import java.io.InputStream;

public final class navigation_back {

	public static final Drawable get(Context context) {
		InputStream inStream = navigation_back.class.getResourceAsStream("/assets/drawable/navigation_back.9.png"); 
		Drawable drawable = Drawable.createFromStream(inStream, "navigation_back");
		return drawable;
	}
}