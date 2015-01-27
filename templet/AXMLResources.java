package <!PACKAGE_NAME>;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import <!PACKAGE_NAME>.values.colors;
import <!PACKAGE_NAME>.values.strings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Get the resources with int id.
 * This file is used in output directory to manager the resources. No meaning to the AXML transform project.
 * @author ch
 *
 */
public class AXMLResources {
    private Context context = null;
    
    public static void main(String[] argv) {
        System.out.println(new AXMLResources(null).getDrawable(R.drawable.background));
    }
    
    public AXMLResources(Context context) {
        this.context = context;
    }
    
    public String getString(int id) {
        return strings.map.get(id);
    }
    
    public Drawable getDrawable(int id) {
        return drawables.get(context, id);
    }
    
    public ColorStateList getColorStateList(int id) {
        return null;
    }
    
    public int getColor(int id) {
        return colors.map.get(id);
    }
    
    public View getLayout(int id) {
        Field[] fieldList = R.layout.class.getFields();
        for (Field f : fieldList) {
            try {
                if (f.getInt(null) == id) {
                    String name = f.getName();
                        String packageName = new Object() {  
                            public String getClassName() {  
                                String packageName = this.getClass().getName();
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