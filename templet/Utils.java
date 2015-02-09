package <!PACKAGE_NAME>;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.util.Log;

public class Utils {
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            Log.e("Utils", "use Reflection failed, class:" + object.getClass() + ", field: " + fieldName, e);
        }
    }
    
    protected static Field getDeclaredField(final Object object, final String fieldName) {
        return getDeclaredField(object.getClass(), fieldName);
    }
    
    protected static Field getDeclaredField(final Class<?> clazz, final String fieldName) {
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // the field doesn't declare in this class, search its super class in next loop
            }
        }
        return null;
    }
    
    protected static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }
    
    public static Object getFieldValue(final Object object, final String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }
        makeAccessible(field);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            Log.e("Utils", "use Reflection failed, class:" + object.getClass() + ", field: " + fieldName, e);
        }
        return null;
    }
    
    public static Object getFieldValue(final Class<?> clazz, final String fieldName) {
        Field field = getDeclaredField(clazz, fieldName);
        if (field == null) {
            Log.e("Utils", "Could not find field [" + fieldName + "] on target [" + clazz + "]");
        }
        makeAccessible(field);
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            Log.e("Utils", "use Reflection failed, class:" + clazz + ", field: " + fieldName, e);
        }
        return null;
    }
}