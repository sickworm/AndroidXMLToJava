package com.excelsecu.axml.dbbuilder;

public class AndroidDocConfig {
    @SuppressWarnings("deprecation")
	public static final Class<?>[] CLASSES_LIST = {
    	/** Super **/
        android.view.View.class,
        android.view.ViewGroup.class,
        
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
        //android.support.v7.widget.GridLayout.class,   handle as GridLayout
        android.widget.GridLayout.class,
        android.widget.RelativeLayout.class,
        android.widget.LinearLayout.class,
        android.widget.FrameLayout.class,
        android.widget.TableLayout.class,
        android.widget.TableRow.class,
        //android.support.v7.widget.Space.class,        handle as Space
        android.widget.Space.class,
    	android.app.Fragment.class,
    	
    	/** Composite **/
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
    };
    
    /**the local Android doc path from SDK Manager**/
    public static final String BASE_PATH = "C:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/";
    
    /**for test**/
    public static final String VIEW_PATH = "C:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/android/view/View.html";
    
    public static String ENCODE = "UTF8";
}