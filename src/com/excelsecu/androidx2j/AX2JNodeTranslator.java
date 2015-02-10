package com.excelsecu.androidx2j;

import java.util.HashMap;
import java.util.List;

import org.dom4j.QName;

public class AX2JNodeTranslator {
    private Class<?> type;
    private List<AX2JAttribute> attributes;
    
    public AX2JNodeTranslator(Class<?> type, HashMap<QName, AX2JMethod> attrList) {
    }
    
    protected class AX2JAttribute {
        private QName name;
        private List<QName> relativeAttributes;
        private AX2JMethod relativeMethod;
    }
    
    protected class AX2JMethod {
        private String methodName;
        private List<String> args;
        private List<QName> relativeAttributes;
    }
}
