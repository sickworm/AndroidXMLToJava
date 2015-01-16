package com.excelsecu.axml.test;

import java.lang.reflect.Field;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Get the resources with int id by using inflect.
 * This file is used in output directory to manager the resources. No meaning to the AXML transform project.
 * @author ch
 *
 */
public class AXMLResources {
    public static void main(String[] argv) {
        System.out.println(getString(R.string.bluetooth));
    }
    
    public static String getString(int id) {
            Field[] fieldList = R.string.class.getFields();
            for (Field f : fieldList) {
                try {
                    if (f.getInt(null) == id) {
                        String name = f.getName();
                        Field[] fieldList1 = com.excelsecu.axml.test.values.strings.class.getFields();
                        for (Field f1 : fieldList1) {
                            if (f1.getName() == name) {
                                return (String)f1.get(null);
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        return null;
    }
    
    public static Drawable getDrawable(int id) {
        return null;
    }
    
    public static View getView(int id) {
        return null;
    }
}