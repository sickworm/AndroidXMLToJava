package com.excelsecu.androidx2j.dbbuilder;

import org.dom4j.Namespace;

import static com.excelsecu.androidx2j.AX2JAttribute.*;
import static com.excelsecu.androidx2j.AX2JCodeBlock.AX2JCode.*;

public class Config {
    public static final String ANDROID_URI = "http://schemas.android.com/apk/res/android";
    public static final String ANDROID_PRIFIX = "android";
    public static final Namespace ANDROID_NAMESPACE = new Namespace(ANDROID_PRIFIX, ANDROID_URI);
    
    public static final String DAT_PATH = "data.dat";
    public static final String DAT_COMMENT = "/** TRANSLATION TABLE. DO NOT EDIT **/";
    public static final String DAT_BLOCK = "<DAT_BLOCK>";
    public static final String STYLE_BLOCK = "<STYLE_BLOCK>";
    public static final String THEME_BLOCK = "<THEME_BLOCK>";
    public static final String MAP_OBJECT_NAME = "<AX2J_OBJECT_NAME>";
    
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
        //android.support.v7.widget.GridLayout.class,	//the same as android.widget.GridLayout.class
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
     * some attributes don't shown in Android doc, or the type value should be updated, add them in here
     */
    public static final String[] ADDITION_LIST = new String[] {
        "View,android:enabled,setEnabled(boolean),",
        "View,android:duplicateParentState,setDuplicateParentStateEnabled(boolean),",
        
        "RadioButton,android:checked,setChecked(boolean),",
        "LinearLayout,android:weightSum,setWeightSum(float),",
        "View,android:background,setBackground(Drawable),",
        
        "ListView,android:headerDividersEnabled,setHeaderDividersEnabled(boolean),",
        "ListView,android:divider,setDivider(Drawable)," + (PRIORITY_THIRD << TYPE_PRIORITY_INDEX),
        "ListView,android:dividerHeight,setDividerHeight(float),",
        
        //LayoutParams
        "View,android:layout_width,width(int)," + (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        "View,android:layout_height,height(int),"+ (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        "View,android:layout_weight,weight(float)," + (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        "View,android:layout_gravity,gravity(int)," + (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        
        "View,android:layout_marginTop,topMargin(int)," + (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        "View,android:layout_marginBottom,bottomMargin(int),"+ (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        "View,android:layout_marginLeft,leftMargin(int)," + (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        "View,android:layout_marginRight,rightMargin(int)," + (TYPE_LAYOUT_PARAMETER + TYPE_VARIABLE_ASSIGNMENT),
        "View,android:layout_marginStart,setMarginStart(int)," + (TYPE_LAYOUT_PARAMETER),
        "View,android:layout_marginEnd,setMarginEnd(int),"+ (TYPE_LAYOUT_PARAMETER),
        "View,android:layout_margin,setMargins(int,int,int,int)," + (TYPE_LAYOUT_PARAMETER + (TYPE_ARGUMENTS_ALL_THE_SAME << TYPE_ARGUMENTS_ORDER_INDEX)),
        
        "View,android:padding,setPaddingRelative(int,int,int,int)," + ((TYPE_ARGUMENTS_ALL_THE_SAME << TYPE_ARGUMENTS_ORDER_INDEX) + (PRIORITY_THIRD << TYPE_PRIORITY_INDEX)),
        "View,android:paddingStart,setPaddingRelative(int,int,int,int)," + (1 << TYPE_ARGUMENTS_ORDER_INDEX),
        "View,android:paddingTop,setPaddingRelative(int,int,int,int)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        "View,android:paddingBottom,setPaddingRelative(int,int,int,int)," + (4 << TYPE_ARGUMENTS_ORDER_INDEX),
        "View,android:paddingEnd,setPaddingRelative(int,int,int,int)," + (3 << TYPE_ARGUMENTS_ORDER_INDEX),
        "View,android:paddingLeft,setPadding(int,int,int,int)," + (1 << TYPE_ARGUMENTS_ORDER_INDEX),
        "View,android:paddingTop,setPadding(int,int,int,int)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        "View,android:paddingRight,setPadding(int,int,int,int)," + (3 << TYPE_ARGUMENTS_ORDER_INDEX),
        "View,android:paddingBottom,setPadding(int,int,int,int)," + (4 << TYPE_ARGUMENTS_ORDER_INDEX),
        
        "TextView,android:drawablePadding,setCompoundDrawablePadding(int)," + (PRIORITY_THIRD << TYPE_PRIORITY_INDEX),
        "TextView,android:drawableStart,setCompoundDrawablesRelativeWithIntrinsicBounds(int,int,int,int)," + (1 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:drawableTop,setCompoundDrawablesRelativeWithIntrinsicBounds(int,int,int,int)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:drawableEnd,setCompoundDrawablesRelativeWithIntrinsicBounds(int,int,int,int)," + (3 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:drawableBottom,setCompoundDrawablesRelativeWithIntrinsicBounds(int,int,int,int)," + (4 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:drawableLeft,setCompoundDrawablesWithIntrinsicBounds(int,int,int,int)," + (1 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:drawableTop,setCompoundDrawablesWithIntrinsicBounds(int,int,int,int)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:drawableRight,setCompoundDrawablesWithIntrinsicBounds(int,int,int,int)," + (3 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:drawableBottom,setCompoundDrawablesWithIntrinsicBounds(int,int,int,int)," + (4 << TYPE_ARGUMENTS_ORDER_INDEX),
        
        "TextView,android:textAppearance,setTextAppearance(context,int)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        "TextView,android:text,setText(int),",
        "TextView,android:text,setText(CharSequence),",
        "TextView,android:bufferType,setText(<AX2J_OBJECT_NAME>.getText(),BufferType)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        
        //RelativeLayout rule
        "View,android:layout_above,addRule(RelativeLayout.ABOVE,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignBaseline,addRule(RelativeLayout.ALIGN_BASELINE,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignBottom,addRule(RelativeLayout.ALIGN_BOTTOM,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignEnd,addRule(RelativeLayout.ALIGN_END,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignLeft,addRule(RelativeLayout.ALIGN_LEFT,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignParentBottom,addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignParentEnd,addRule(RelativeLayout.ALIGN_PARENT_END,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignParentLeft,addRule(RelativeLayout.ALIGN_PARENT_LEFT,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignParentRight,addRule(RelativeLayout.ALIGN_PARENT_RIGHT,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignParentStart,addRule(RelativeLayout.ALIGN_PARENT_START,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignParentTop,addRule(RelativeLayout.ALIGN_PARENT_TOP,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignRight,addRule(RelativeLayout.ALIGN_RIGHT,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignStart,addRule(RelativeLayout.ALIGN_START,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_alignTop,addRule(RelativeLayout.ALIGN_TOP,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        //"View,android:layout_alignWithParentIfMissing,addRule(RelativeLayout.,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_below,addRule(RelativeLayout.BELOW,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_centerHorizontal,addRule(RelativeLayout.CENTER_HORIZONTAL,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        //"View,android:layout_centerInParent,addRule(RelativeLayout.,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_centerVertical,addRule(RelativeLayout.CENTER_VERTICAL,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_toEndOf,addRule(RelativeLayout.END_OF,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_toLeftOf,addRule(RelativeLayout.LEFT_OF,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_toRightOf,addRule(RelativeLayout.RIGHT_OF,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "View,android:layout_toStartOf,addRule(RelativeLayout.START_OF,int)," + (TYPE_LAYOUT_PARAMETER + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        
        //shape
        "GradientDrawable,android:shape,setType(int),",
        //gradient
        "GradientDrawable,android:type,setGradientType(int),",
        "GradientDrawable,android:gradientRadius,setGradientRadius(float),",
        "GradientDrawable,android:useLevel,setUseLevel(boolean),",
        "GradientDrawable,android:centerX,setGradientCenter(float,float)," + (1 << TYPE_ARGUMENTS_ORDER_INDEX),
        "GradientDrawable,android:centerY,setGradientCenter(float,float)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        //"GradientDrawable,android:angle,setOrientation(int)",    //use in API 16 or higher
        //stroke
        "GradientDrawable,android:startColor,setColors(int,int,int)," + (TYPE_ARGUMENTS_ARRAY + (1 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:centerColor,setColors(int,int,int)," + (TYPE_ARGUMENTS_ARRAY + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:endColor,setColors(int,int,int)," + (TYPE_ARGUMENTS_ARRAY + (3 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:startColor,setColors(int,int)," + (TYPE_ARGUMENTS_ARRAY + (1 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:endColor,setColors(int,int)," + (TYPE_ARGUMENTS_ARRAY + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:width,setStroke(int,int,int,int)," + (1 << TYPE_ARGUMENTS_ORDER_INDEX),
        "GradientDrawable,android:color,setStroke(int,int,int,int)," + (2 << TYPE_ARGUMENTS_ORDER_INDEX),
        "GradientDrawable,android:dashWidth,setStroke(int,int,int,int)," + (3 << TYPE_ARGUMENTS_ORDER_INDEX),
        "GradientDrawable,android:dashGap,setStroke(int,int,int,int)," + (4 << TYPE_ARGUMENTS_ORDER_INDEX),
        //solid
        "GradientDrawable,android:color,setColor(int),",
        //corners
        "GradientDrawable,android:radius,setCornerRadius(int),",
        "GradientDrawable,android:topLeftRadius,setCornerRadii(int,int,int,int)," + (TYPE_ARGUMENTS_ARRAY + (1 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:topRightRadius,setCornerRadii(int,int,int,int)," + (TYPE_ARGUMENTS_ARRAY + (2 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:bottomRightRadius,setCornerRadii(int,int,int,int)," + (TYPE_ARGUMENTS_ARRAY + (3 << TYPE_ARGUMENTS_ORDER_INDEX)),
        "GradientDrawable,android:bottomLeftRadius,setCornerRadii(int,int,int,int)," + (TYPE_ARGUMENTS_ARRAY + (4 << TYPE_ARGUMENTS_ORDER_INDEX)),
    };
    
    public static String[] REMOVAL_LIST = new String[] {
        "TextView,android:text,setText(CharSequence,TextView.BufferType),",
        "TextView,android:bufferType,setText(CharSequence,BufferType),",
    };
    
    /**the local Android doc path from SDK Manager**/
    public static final String ANDROID_DOCS_PATH = "C:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/";
    /**the local style.xml path from AOSP code**/
    public static final String SYSTEM_STYLES_PATH = "F:/SDK4.4.4/frameworks/base/core/res/res/values/styles.xml";
    /**the local themes.xml path from AOSP code**/
    public static final String SYSTEM_THEMES_PATH = "F:/SDK4.4.4/frameworks/base/core/res/res/values/themes.xml";
    
    public static String ENCODE = "UTF8";
}
