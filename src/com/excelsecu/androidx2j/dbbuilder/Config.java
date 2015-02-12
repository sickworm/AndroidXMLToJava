package com.excelsecu.androidx2j.dbbuilder;

import java.util.HashMap;

import org.dom4j.Namespace;

public class Config {
    public static final String ANDROID_URI = "http://schemas.android.com/apk/res/android";
    public static final String ANDROID_PRIFIX = "http://schemas.android.com/apk/res/android";
    public static final Namespace ANDROID_NAMESPACE = new Namespace(ANDROID_PRIFIX, ANDROID_URI);
    
    public static final String DAT_PATH = "data.dat";
    public static final String DAT_COMMENT = "/** TRANSLATION TABLE. DO NOT EDIT **/";
    public static final String DAT_BLOCK = "<DAT_BLOCK>";
    public static final String STYLE_BLOCK = "<STYLE_BLOCK>";
    public static final String THEME_BLOCK = "<THEME_BLOCK>";
    public static final String NOTHING = " ";
    
    @SuppressWarnings("deprecation")
	public static final Class<?>[] CLASSES_LIST = {
    	/** Super **/
        android.view.View.class,
        android.view.ViewGroup.class,
        android.widget.AdapterView.class,
        android.widget.ListAdapter.class,
        android.widget.AbsListView.class,
        android.widget.CompoundButton.class,
        
    	/** Form Widgets **/
        android.widget.Button.class,
        android.widget.ToggleButton.class,
        android.widget.CheckBox.class,
        android.widget.RadioButton.class,
        android.widget.Spinner.class,
        android.widget.SeekBar.class,
        android.widget.QuickContactBadge.class,
        android.widget.RadioGroup.class,
        android.widget.RadioButton.class,
        android.widget.RatingBar.class,
        android.widget.Switch.class,
        android.widget.ProgressBar.class,
        android.widget.TextView.class,
        
        /** Text Fields**/
        android.widget.EditText.class,
        
        /** Layouts **/
        //<include> not a class
        android.support.v7.widget.GridLayout.class,
        android.widget.GridLayout.class,
        android.widget.RelativeLayout.class,
        android.widget.LinearLayout.class,
        android.widget.FrameLayout.class,
        android.widget.TableLayout.class,
        android.widget.TableRow.class,
        android.support.v7.widget.Space.class,
        android.widget.Space.class,
    	android.app.Fragment.class,
    	
    	/** Composite **/
        android.widget.ListView.class,
        android.widget.ExpandableListView.class,
        android.widget.GridView.class,
        android.widget.ScrollView.class,
        android.widget.HorizontalScrollView.class,
        android.widget.TabHost.class,
        android.widget.TabWidget.class,
        android.webkit.WebView.class,
        
        /** Images & Media **/
        android.widget.ImageView.class,
        android.widget.ImageButton.class,
        android.widget.Gallery.class,
        android.widget.MediaController.class,
        android.widget.VideoView.class,
        
        /** Time & Date **/
        android.widget.TimePicker.class,
        android.widget.DatePicker.class,
        android.widget.CalendarView.class,
        android.widget.Chronometer.class,
        android.widget.DigitalClock.class,
        
        /** Transitions **/
        android.widget.ImageSwitcher.class,
        android.widget.AdapterViewFlipper.class,
        android.widget.StackView.class,
        android.widget.TextSwitcher.class,
        android.widget.ViewAnimator.class,
        android.widget.ViewFlipper.class,
        android.widget.ViewSwitcher.class,
        
        /** Advanced **/
        //<requestFocus> not a class
        android.view.ViewStub.class,
        android.view.TextureView.class,
        android.view.SurfaceView.class,
        android.gesture.GestureOverlayView.class,
        android.widget.NumberPicker.class,
        android.widget.ZoomButton.class,
        android.widget.ZoomControls.class,
        android.widget.DialerFilter.class,
        android.widget.TwoLineListItem.class,
        android.widget.AbsoluteLayout.class,
        
        /** Other **/
        android.widget.ActionMenuView.class,
        android.widget.TextClock.class,
        android.widget.Toolbar.class,
        
        /** Drawable **/
        android.graphics.drawable.Drawable.class,
        android.graphics.drawable.DrawableContainer.class,
        android.graphics.drawable.GradientDrawable.class,
        android.content.res.ColorStateList.class,
        android.graphics.drawable.StateListDrawable.class,
    };

    /**
     * some attributes don't shown in Android doc, add them in here
     */
    public static final HashMap<String, String> ADDITION_MAP = new HashMap<String, String>() {
		private static final long serialVersionUID = 6525815328033908940L;
		
		{
	        put("View$android:enabled", "setEnabled(boolean)");
            put("View$android:duplicateParentState", "setDuplicateParentStateEnabled(boolean)");
            
	        put("RadioButton$android:checked", "setChecked(boolean)");
	        put("LinearLayout$android:weightSum", "setWeightSum(float)");
	        put("View$android:background", "setBackground(Drawable)");     //replace setBackgroundResource(int) to setBackground(Drawable)
	        
	        put("GradientDrawable$android:shape", "setType(int)");
	        put("GradientDrawable$android:type", "setGradientType(int)");
            put("GradientDrawable$android:gradientRadius", "setGradientRadius(float)");
	        put("GradientDrawable$android:useLevel", "setUseLevel(boolean)");
	        //put("GradientDrawable$android:angle", "setOrientation(int)");    //use in API 16 or higher
	        put("GradientDrawable$android:radius", "setCornerRadius(float)");
	        
            put("ListView$android:headerDividersEnabled", "setHeaderDividersEnabled(boolean)");
            put("ListView$android:divider", "setDivider(Drawable)");
            put("ListView$android:dividerHeight", "setDividerHeight(float)");
    	}
    };

    /**
     * some attributes can't translate directly (like padding), remove them in here
     */
    public static final String[] REMOVAL_LIST = {
        "View$android:padding",
        "View$android:paddingLeft",
        "View$android:paddingRight",
        "View$android:paddingStart",
        "View$android:paddingEnd",
        "View$android:paddingTop",
        "View$android:paddingBottom",
        "TextView$android:drawableLeft",
        "TextView$android:drawableRight",
        "TextView$android:drawableStart",
        "TextView$android:drawableEnd",
        "TextView$android:drawableTop",
        "TextView$android:drawableBottom",
    };
    
    /**the local Android doc path from SDK Manager**/
    public static final String ANDROID_DOCS_PATH = "C:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/";
    /**the local style.xml path from AOSP code**/
    public static final String SYSTEM_STYLES_PATH = "F:/SDK4.4.4/frameworks/base/core/res/res/values/styles.xml";
    /**the local themes.xml path from AOSP code**/
    public static final String SYSTEM_THEMES_PATH = "F:/SDK4.4.4/frameworks/base/core/res/res/values/themes.xml";
    
    public static String ENCODE = "UTF8";
}
