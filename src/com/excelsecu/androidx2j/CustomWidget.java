package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.List;

/**
 * Use for add custom widget
 * @author ch
 *
 */
public class CustomWidget {
	public String widgetName;
	public String className;
	public Class<?> parent;
    public String constructorParam;
    
    /**this is a map that declare the custom widget**/
    private static List<CustomWidget> customMap = new ArrayList<CustomWidget>();
    
	public CustomWidget(String widgetName, String className, Class<?> parent, String constructorParam) {
		this.widgetName = widgetName;
		this.className = className;
		this.parent = parent;
	    this.constructorParam = constructorParam;
	}
    
    public static void addCustomWidget(String widgetName, String className, Class<?> parent, String constructorParam) {
    	addCustomWidget(new CustomWidget(widgetName, className, parent, constructorParam));
    }
    
    public static void addCustomWidget(CustomWidget customWidget) {
    	customMap.add(customWidget);
    }
    
    public static List<CustomWidget> getCustomWidget() {
    	return customMap;
    }
    
    public static CustomWidget findCustomWidget(String labelName) {
    	for (CustomWidget w : customMap) {
    		if (w.widgetName.equals(labelName)) {
    			return w;
    		}
    	}
    	return null;
    }
    
    public static Class<?> findParentByLabelName(String labelName) {
    	CustomWidget customWidget = findCustomWidget(labelName);
    	if (customWidget != null) {
    		return customWidget.parent;
    	}
    	return Void.class;
    }
}
