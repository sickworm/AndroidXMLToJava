package com.excelsecu.axml.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Get the resources with int id by using inflect.
 * This file is used in output directory to manager the resources. No meaning to the AXML transform project.
 * @author ch
 *
 */
public class AXMLResources {
    public Context context = null;
    
    public static void main(String[] argv) {
        System.out.println(new AXMLResources(null).getLayout(R.layout.base));
    }
    
    public AXMLResources(Context context) {
        this.context = context;
    }
    
    public String getString(int id) {
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
    
    public Drawable getDrawable(int id) {
        return null;
    }
    
    public View getLayout(int id) {
        Field[] fieldList = R.layout.class.getFields();
        for (Field f : fieldList) {
            try {
                if (f.getInt(null) == id) {
                    String name = f.getName();
                        String packageName = new Object() {  
                            public String getClassName() {  
                                String packageName = this.getClass().getTypeName();
                                return packageName.substring(0, packageName.lastIndexOf('.'));  
                            }  
                        }.getClassName();
                        try {
                            Class<?> clazz = Thread.currentThread().getContextClassLoader().
                            loadClass(packageName + ".layout." + name);
                            try {
                                Method method = clazz.getMethod("get", Context.class);
                                try {
                                    return (View)method.invoke(null, context);
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
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
}