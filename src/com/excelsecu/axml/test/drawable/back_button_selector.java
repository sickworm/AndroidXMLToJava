package com.excelsecu.axml.test.drawable;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import com.excelsecu.axml.test.AXMLResources;
import com.excelsecu.axml.test.R;

public final class back_button_selector {

	public static final StateListDrawable get(Context context) {
		AXMLResources resources = new AXMLResources(context);
		
		StateListDrawable stateListDrawable = new StateListDrawable();
		int[] stateSet0 = new int[] {-android.R.attr.state_pressed, -android.R.attr.state_selected};
		stateListDrawable.addState(stateSet0, resources.getDrawable(R.drawable.navigation_back_on));
		int[] stateSet1 = new int[] {android.R.attr.state_pressed, -android.R.attr.state_selected};
		stateListDrawable.addState(stateSet1, resources.getDrawable(R.drawable.navigation_back));
		int[] stateSet2 = new int[] {-android.R.attr.state_pressed, android.R.attr.state_selected};
		stateListDrawable.addState(stateSet2, resources.getDrawable(R.drawable.navigation_back));
		return stateListDrawable;
	}
}