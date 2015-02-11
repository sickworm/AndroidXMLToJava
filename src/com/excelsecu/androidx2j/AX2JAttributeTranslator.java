package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.QName;

public class AX2JAttributeTranslator {
    public static HashMap<String, Class<?>> typeMap = new HashMap<String, Class<?>>() {
        private static final long serialVersionUID = -4934808097054114253L;

        {
            put(Integer.class.getCanonicalName(), Integer.class);
            put("String", String.class);
        }
        
    };
    
    private Class<?> type;
    private List<AX2JAttribute> attributeList;
    private List<AX2JMethod> methodList;
    
    public AX2JAttributeTranslator(Class<?> type) {
        this.type = type;
        attributeList = new ArrayList<AX2JAttribute>();
    }
    
    public AX2JAttribute findAttribute(QName name) {
        for (AX2JAttribute attribute : attributeList) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }
    
    public AX2JMethod findMethod(String name) {
        for (AX2JMethod method : methodList) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
    
    public void addAttributes(HashMap<QName, AX2JMethod> map) {
        
    }
    
    public void addAttribute(QName name, AX2JMethod method) {
        AX2JAttribute attribute = findAttribute(name);
        if (attribute != null) {
            attribute.addRelativeMethod(method);
        } else {
            attribute = new AX2JAttribute(name, method);
            attributeList.add(attribute);
        }
        
        AX2JMethod oldMethod = findMethod(method.getName());
        if (oldMethod != null) {
            oldMethod.addRelativeAttribute(attribute);
        } else {
            methodList.add(method);
        }
    }
    
    public String translate(AX2JNode node) {
        return "";
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public final class AX2JAttribute {
        private QName name;
        private List<AX2JMethod> relativeMethods;
        
        public AX2JAttribute(QName name, AX2JMethod method) {
            this.name = name;
            relativeMethods = new ArrayList<AX2JMethod>();
            relativeMethods.add(method);
        }
        
        public void addRelativeMethod(AX2JMethod method) {
            relativeMethods.add(method);
        }
        
        public QName getName() {
            return name;
        }
    }
    
    protected class AX2JMethod {
        
        private String methodName;
        private List<Class<?>> argsType;
        private List<String> args;
        private List<AX2JAttribute> relativeAttributes;
        
        public AX2JMethod(QName attributeName, String method) {
            relativeAttributes = new ArrayList<AX2JAttribute>();
        }
        
        public String getName() {
            return methodName;
        }
        
        public void addRelativeAttribute(AX2JAttribute attribute) {
            relativeAttributes.add(attribute);
        }
    }
}
