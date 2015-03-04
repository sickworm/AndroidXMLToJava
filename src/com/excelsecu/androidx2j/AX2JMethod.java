package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.QName;

import android.content.Context;

public class AX2JMethod implements Cloneable {
    private String methodName;
    private Class<?>[] argTypes;
    private List<AX2JAttribute> relativeAttributeList;
    private String[] args;
    private int argsNum;
    
    public AX2JMethod(QName attributeName, String methodString) {
        setMethod(methodString);
        relativeAttributeList = new ArrayList<AX2JAttribute>();
    }
    
    public void setMethod(String methodString) {
        methodString = methodString.replace("\n", "");
        //no relative method
        if (methodString.indexOf('(') == -1) {
            methodName = methodString;
            argTypes = new Class<?>[0];
            args = new String[0];
            argsNum = 0;
        } else {
            methodName = methodString.substring(0, methodString.indexOf('('));
            
            String[] argTypesString = methodString.substring(methodString.indexOf('(') + 1,
                    methodString.indexOf(')')).split(",");
            if (!argTypesString[0].equals("")) {
                argTypes = new Class<?>[argTypesString.length];
                args = new String[argTypesString.length];
                argsNum = argTypesString.length;
                for (int i = 0; i < args.length; i++) {
                    argTypes[i] = AX2JClassTranslator.getType(argTypesString[i]);
                    if (argTypes[i] == null) {		//this means it already has a constant value in this position.
                    								//e.g. addRule(RelativeLayout.START_OF,Integer)
                    	argTypes[i] = Void.class;
                    	this.args[i] = argTypesString[i];
                    }
                }
            } else {
                argTypes = new Class<?>[0];
            }
        }
    }
    
    public void setMethod(AX2JMethod newMethod) {
        methodName = newMethod.getMethodName();
        argTypes = newMethod.getArgTypes();
        relativeAttributeList = newMethod.getRelativeAttributeList();
    }
    
    public void setArg(int order, String value) {
    	if (order > args.length || order < 0) {
    		throw new AX2JException(AX2JException.ARRAY_OUT_OF_RANGE, this + ", order: " + order);
    	}
        args[order - 1] = value;
    }
    
    public String getArg(int order) {
    	if (order > args.length || order < 0) {
    		throw new AX2JException(AX2JException.ARRAY_OUT_OF_RANGE, this + ", order: " + order);
    	}
    	String value = args[order - 1];
        if (value == null) {
        	value = getDefaultValue(order);
        }
        
        return value;
    }
    
    public String[] getArgs() {
        return args;
    }
    
    public int getArgsNum() {
    	return argsNum;
    }
    
    public String getDefaultValue(int order) {
        if (order < 1 || order > argTypes.length) {
            throw new AX2JException(AX2JException.ARRAY_OUT_OF_RANGE, this.toString() + ", order: " + order);
        }
        
        String value = "";
        Class<?> type = argTypes[order - 1];
        if (type.equals(Context.class)) {
            value = "context";
        } else {
            value = "0";
        }
        
        return value;
    }
    
    public String getName() {
        return methodName;
    }
    
    public Class<?> getArgType(int order) {
        return argTypes[order - 1];
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
    
    @Override
    public AX2JMethod clone() {
        AX2JMethod method = null;
        try {
            method = (AX2JMethod) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        
        return method;
    }
    
    @Override
    public String toString() {
        String argsString = "";
        
        for (int i = 0; i < argTypes.length; i++) {
        	Class<?> type = argTypes[i];
        	if (type == Void.class) {		//this means it already has a constant value in this position.
        									//e.g. addRule(RelativeLayout.START_OF,Integer)
                argsString += args[i];
        	} else {
                argsString += type.getSimpleName();
        	}
        	
        	if (i < argTypes.length - 1) {
        		argsString += ",";
        	}
        }
        
        String methodString = this.getMethodName() + (argsString.equals("")? "" : ("(" + argsString +")"));
        
        return methodString;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof AX2JMethod) {
            AX2JMethod method2 = (AX2JMethod) object;
            if (methodName.equals(method2.getName())) {
                Class<?>[] argTypes2 = method2.getArgTypes();
                String[] args2 = method2.getArgs();
                for (int i = 0; i < argTypes.length; i++) {
                	if (argTypes[i].equals(Void.class)) {
                        if (!args[i].equals(args2[i])) {
                        	return false;
                        }
                	} else {
                        if (!argTypes[i].equals(argTypes2[i])) {
                        	return false;
                        }
                	}
                }
                return true;
            }
        }
        return false;
    }
}