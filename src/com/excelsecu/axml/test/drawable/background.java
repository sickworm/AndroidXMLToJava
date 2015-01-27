package com.excelsecu.axml.test.drawable;

import android.graphics.drawable.Drawable;
import android.content.Context;
import java.io.InputStream;

public final class background {

	public static final Drawable get(Context context) {
		InputStream inStream = background.class.getResourceAsStream("/assets/drawable/background.jpg"); 
		Drawable drawable = Drawable.createFromStream(inStream, "background");
		return drawable;
	}
}