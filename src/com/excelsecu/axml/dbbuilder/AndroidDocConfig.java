package com.excelsecu.axml.dbbuilder;

public class AndroidDocConfig {
    public static final Class<?>[] CLASSES_LIST = {
        android.view.View.class,
        android.view.ViewGroup.class,
        android.widget.RelativeLayout.class,
        android.widget.LinearLayout.class,
        android.widget.FrameLayout.class,
        android.widget.RelativeLayout.class,
        android.widget.Button.class,
        android.widget.TextView.class,
        android.widget.EditText.class,
    };
    /**the local Android doc path from SDK Manager**/
    public static final String BASE_PATH = "C:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/";
    /**for test**/
    public static final String VIEW_PATH = "C:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/android/view/View.html";
    public static String ENCODE = "UTF8";
}
