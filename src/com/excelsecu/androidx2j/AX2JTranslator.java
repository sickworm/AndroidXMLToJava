package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.QName;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.method.KeyListener;
import android.transition.Transition;

public class AX2JTranslator {
    public static HashMap<String, Class<?>> typeMap = new HashMap<String, Class<?>>() {
        private static final long serialVersionUID = -4934808097054114253L;

        {
            put("int", Integer.class);
            put("float", Float.class);
            put("boolean", Boolean.class);
            put("long", Long.class);
            put("PorterDuff.Mode", PorterDuff.Mode.class);
            put(String.class.getSimpleName(), String.class);
            put(Drawable.class.getSimpleName(), Drawable.class);
            put(ColorStateList.class.getSimpleName(), ColorStateList.class);
            put(Transition.class.getSimpleName(), Transition.class);
            put(CharSequence.class.getSimpleName(), CharSequence.class);
            put(KeyListener.class.getSimpleName(), KeyListener.class);
        }
        
    };
    
    private Class<?> type;
    private List<AX2JAttribute> attributeList;
    private List<AX2JMethod> methodList;
    
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
                Class<?>[] argsType = method.getArgsType();
                Class<?>[] argsType2 = method2.getArgsType();
                int i = 0;
                for (; i < argsType.length; i++) {
                    if (!argsType[i].equals(argsType2[i])) {
                        break;
                    }
                }
                if (i == argsType.length) {
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
            oldMethod.addRelativeAttribute(attribute);
        } else {
            methodList.add(method);
        }
        
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
    }
    
    public class AX2JMethod {
        private String methodName;
        private Class<?>[] argsType;
        private List<AX2JAttribute> relativeAttributeList;
        
        public AX2JMethod(QName attributeName, String method) {
            relativeAttributeList = new ArrayList<AX2JAttribute>();
            methodName = method.substring(0, method.indexOf('('));
            
            String[] args = method.substring(method.indexOf('(') + 1, method.indexOf(')')).split(",");
            argsType = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argsType[i] = typeMap.get(args[i]);
                if (argsType[i] == null) {
                    throw new AX2JException(AX2JException.CLASS_NOT_FOUND, argsType[i].toString());
                }
            }
        }
        
        public String getName() {
            return methodName;
        }
        
        public Class<?>[] getArgsType() {
            return argsType;
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
    }

}
