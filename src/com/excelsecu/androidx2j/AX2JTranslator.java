package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.QName;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.text.method.TransformationMethod;
import android.transition.Transition;
import android.widget.ImageView;
import android.widget.TextView;

public class AX2JTranslator {
    public static HashMap<String, Class<?>> typeMap = new HashMap<String, Class<?>>() {
        private static final long serialVersionUID = -4934808097054114253L;

        {
            put("int", Integer.class);
            put("float", Float.class);
            put("boolean", Boolean.class);
            put("long", Long.class);
            put("PorterDuff.Mode", PorterDuff.Mode.class);
            put("TextView.BufferType", TextView.BufferType.class);
            put("TextUtils.TruncateAt", TextUtils.TruncateAt.class);
            put("ImageView.ScaleType", TextUtils.TruncateAt.class);
            
            put(Integer.class);
            put(Float.class);
            put(Boolean.class);
            put(Long.class);
            put(PorterDuff.Mode.class);
            put(TextView.BufferType.class);
            put(TextUtils.TruncateAt.class);
            put(ImageView.ScaleType.class);
            
            put(String.class);
            put(Drawable.class);
            put(ColorStateList.class);
            put(Transition.class);
            put(CharSequence.class);
            put(KeyListener.class);
            put(Paint.class);
            put(LayoutTransition.class);
            put(Context.class);
            put(Typeface.class);
            put(InputFilter.class);
            put(TransformationMethod.class);
        }
        
        private void put(Class<?> type) {
            put(type.getSimpleName(), type);
        }
        
    };
    
    private Class<?> type;
    private List<AX2JAttribute> attributeList = new ArrayList<AX2JAttribute>();
    private List<AX2JMethod> methodList = new ArrayList<AX2JMethod>();
    
    public AX2JTranslator(Class<?> type) {
        this.type = type;
        attributeList = new ArrayList<AX2JAttribute>();
    }
    
    public void add(String qNameString, String methodString) {
        add(string2QName(qNameString), methodString);
    }
    
    public void add(QName name, String methodString) {
        AX2JMethod method = new AX2JMethod(name, methodString);
        addAttribute(name, method);
    }
    
    public AX2JAttribute findAttribute(QName name) {
        for (AX2JAttribute attribute : attributeList) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }
    
    public AX2JMethod findMethod(AX2JMethod method2) {
        for (AX2JMethod method : methodList) {
            if (method.getName().equals(method2.getName())) {
                Class<?>[] argTypes = method.getArgTypes();
                Class<?>[] argTypes2 = method2.getArgTypes();
                int i = 0;
                for (; i < argTypes.length; i++) {
                    if (!argTypes[i].equals(argTypes2[i])) {
                        break;
                    }
                }
                if (i == argTypes.length) {
                    return method;
                }
            }
        }
        return null;
    }
    
    public void addAttribute(QName name, AX2JMethod method) {
        AX2JAttribute attribute = findAttribute(name);
        if (attribute == null) {
            attribute = new AX2JAttribute(name);
            attributeList.add(attribute);
        }
        
        AX2JMethod oldMethod = findMethod(method);
        if (oldMethod != null) {
            method = oldMethod;
        } else {
            methodList.add(method);
        }
        method.addRelativeAttribute(attribute);
        
        attribute.addRelativeMethod(method);
    }
    
    public static QName string2QName(String qNameString) {
        QName name = null;
        String prefixString = qNameString.substring(0, qNameString.indexOf(':'));
        String nameString = qNameString.substring(0, qNameString.indexOf(':'));
        if (prefixString.equals("android")) {
            name = new QName(nameString, Config.ANDROID_NAMESPACE);
        } else {
            name = new QName(nameString);
        }
        return name;
    }
    
    public static Class<?> getType(String typeString) {
        Class<?> type = typeMap.get(typeString);
        if (type == null) {
            throw new AX2JException(AX2JException.CLASS_NOT_FOUND, typeString);
        }
        return type;
    }
    
    public String translate(AX2JNode node) {
        return "";
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public List<AX2JAttribute> getAttributeList() {
        return attributeList;
    }
    
    public List<AX2JMethod> getMethodList() {
        return methodList;
    }
    
    public final class AX2JAttribute {
        private QName name;
        private List<AX2JMethod> relativeMethodList;
        
        public AX2JAttribute(QName name) {
            this.name = name;
            relativeMethodList = new ArrayList<AX2JMethod>();
        }
        
        public AX2JAttribute(QName name, AX2JMethod method) {
            this.name = name;
            relativeMethodList = new ArrayList<AX2JMethod>();
            relativeMethodList.add(method);
        }
        
        public void addRelativeMethod(AX2JMethod method) {
            relativeMethodList.add(method);
        }
        
        public QName getName() {
            return name;
        }
        
        public List<AX2JMethod> getRelativeMethodList() {
            return relativeMethodList;
        }
        
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            for (AX2JMethod method : relativeMethodList) {
                stringBuffer.append(type.getSimpleName() + "," + name.getQualifiedName() +
                        "," + method.toString());
            }
            return stringBuffer.toString();
        }
    }
    
    public class AX2JMethod {
        private String methodName;
        private Class<?>[] argTypes;
        private List<AX2JAttribute> relativeAttributeList;
        
        public AX2JMethod(QName attributeName, String method) {
            relativeAttributeList = new ArrayList<AX2JAttribute>();
            
            //no relative method
            if (method.indexOf('(') == -1) {
                methodName = "";
                argTypes = new Class<?>[0];
            } else {
                methodName = method.substring(0, method.indexOf('('));
                methodName = methodName.replace("\n", "");
                
                String[] args = method.substring(method.indexOf('(') + 1, method.indexOf(')')).split(",");
                if (!args[0].equals("")) {
                    argTypes = new Class<?>[args.length];
                    for (int i = 0; i < args.length; i++) {
                        argTypes[i] = getType(args[i]);
                    }
                } else {
                    argTypes = new Class<?>[0];
                }
            }
        }
        
        public String getName() {
            return methodName;
        }
        
        public Class<?>[] getArgTypes() {
            return argTypes;
        }
        
        public void addRelativeAttribute(AX2JAttribute attribute) {
            relativeAttributeList.add(attribute);
        }
        
        public String getMethodName() {
            return methodName;
        }
        
        public List<AX2JAttribute> getRelativeAttributeList() {
            return relativeAttributeList;
        }
        
        public String toString() {
            StringBuffer clazzBuffer = new StringBuffer();
            for (Class<?> clazz : this.getArgTypes()) {
                clazzBuffer.append(clazz.getSimpleName() + ",");
            }
            if (clazzBuffer.length() > 1) {
                clazzBuffer.deleteCharAt(clazzBuffer.length() - 1);
            }
            String methodString = this.getMethodName() == ""? "" :
                this.getMethodName() + "(" + clazzBuffer +")";
            
            return methodString;
        }
    }

}
