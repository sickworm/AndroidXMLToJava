package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.QName;

import com.excelsecu.androidx2j.AX2JCodeBlock.AX2JCode;

public class AX2JAttribute implements Cloneable {
    /** normal attribute, use default value**/
    public static final int TYPE_NORMAL = 0x00000000;
    
    /** method has api limit. Range 0x00000000 - 0x000000ff **/
    public static final int TYPE_API_LIMIT = 0x00000ff;
    public static final int TYPE_API_LIMIT_INDEX = 0;
    
    /** method has priority in Java method list. Range 0x00000100 - 0x00000f00 **/
    public static final int TYPE_PRIORITY = 0x00000f00;
    public static final int TYPE_PRIORITY_INDEX = 8;
    
    /** the number of arguments. Range 0x00001000 - 0x0000f000 **/
    
    /** the order of this arguments. Range 0x00010000 - 0x000e0000. 0x000f0000 for all the value is the same**/
    public static final int TYPE_ARGUMENTS_ORDER = 0x0000f000;
    public static final int TYPE_ARGUMENTS_ORDER_INDEX = 12;
    public static final int TYPE_ARGUMENTS_ALL_THE_SAME = 0xf;
    
    /** attribute for LayoutParams. 0x00100000 **/
    public static final int TYPE_LAYOUT_PARAMETER = 0x00010000;
    public static final int TYPE_LAYOUT_PARAMETER_INDEX = 16;
    
    /** use style resource **/
    public static final int TYPE_STYLE = 0x00200000;
    public static final int TYPE_STYLE_INDEX = 21;
    
    /** assign variable directly **/
    public static final int TYPE_VARIABLE_ASSIGNMENT = 0x00400000;
    public static final int TYPE_VARIABLE_ASSIGNMENT_INDEX = 22;
    
    /** use reflect to assign variable **/
    public static final int TYPE_VARIABLE_REFLECTION = 0x00800000;
    public static final int TYPE_VARIABLE_REFLECTION_INDEX = 23;
    
    /** use reflect to invoke method **/
    public static final int TYPE_METHOD_REFLECTION = 0x01000000;
    public static final int TYPE_METHOD_REFLECTION_INDEX = 24;

    private Class<?> type;
    private QName name;
    private List<AX2JMethod> relativeMethodList;
    private int methodType;
    private String value;
    
    public AX2JAttribute(QName name, Class<?> type) {
        this.name = name;
        this.methodType = TYPE_NORMAL;
        this.type = type;
        relativeMethodList = new ArrayList<AX2JMethod>();
        value = null;
    }
    
    public AX2JAttribute(QName name, int methodType, Class<?> type) {
        this.name = name;
        this.methodType = methodType;
        this.type = type;
        relativeMethodList = new ArrayList<AX2JMethod>();
        value = null;
    }
    
    public AX2JAttribute(QName name, AX2JMethod method, Class<?> type) {
        this.name = name;
        this.type = type;
        relativeMethodList = new ArrayList<AX2JMethod>();
        relativeMethodList.add(method);
        value = null;
    }
    
    public void addRelativeMethod(AX2JMethod method) {
        if (relativeMethodList.size() == 1) {
            if (relativeMethodList.get(0).getName().equals("")
                && !method.getName().equals("")) {
                relativeMethodList = new ArrayList<AX2JMethod>();
                relativeMethodList.add(method);
            } else if (!method.getName().equals("")) {
                relativeMethodList.add(method);
            }
        } else {
            relativeMethodList.add(method);
        }
    }
    
    public AX2JMethod findMethod(AX2JMethod oldMethod) {
        for (AX2JMethod method : relativeMethodList) {
            if (method.equals(oldMethod)) {
                return method;
            }
        }
        return null;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public void setMethodType(int methodType) {
        this.methodType = methodType;
    }
    
    public QName getName() {
        return name;
    }
    
    public int getType() {
        return methodType;
    }
    
    public String getValue() {
        return value;
    }
    
    public List<AX2JMethod> getRelativeMethodList() {
        return relativeMethodList;
    }
    
    public AX2JAttribute clone() {
        AX2JAttribute attribute = null;
        try {
            attribute = (AX2JAttribute) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        
        return attribute;
    }
    
    public int getTypeValue(int mask) {
        return getTypeValue(methodType, mask);
    }
    
    public static int getTypeValue(int methodType, int mask) {
        int value = methodType & mask;
        switch(mask) {
            case TYPE_NORMAL:
                break;
            case TYPE_API_LIMIT:
            	value = value >> TYPE_API_LIMIT_INDEX;
                if (value == 0) {
                    value = 1;
                }
                break;
            case TYPE_PRIORITY:
                value = value >> TYPE_PRIORITY_INDEX;
                if (value == 0) {
                    value = AX2JCode.PRIORITY_DEFAULT;
                }
                break;
            case TYPE_ARGUMENTS_ORDER:
                value = value >> TYPE_ARGUMENTS_ORDER_INDEX;
                if (value == 0) {
                    value = 1;
                }
                break;
            case TYPE_LAYOUT_PARAMETER:
                value = value >> TYPE_LAYOUT_PARAMETER_INDEX;
                break;
            case TYPE_STYLE:
                value = value >> TYPE_STYLE_INDEX;
                break;
            case TYPE_VARIABLE_ASSIGNMENT:
                value = value >> TYPE_VARIABLE_ASSIGNMENT_INDEX;
                break;
            case TYPE_VARIABLE_REFLECTION:
                value = value >> TYPE_VARIABLE_REFLECTION_INDEX;
                break;
            case TYPE_METHOD_REFLECTION:
                value = value >> TYPE_METHOD_REFLECTION_INDEX;
                break;
        }
        return value;
    }
    
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (AX2JMethod method : relativeMethodList) {
            String methodString = method.toString();
            String methodTypeString = "";
            if (!methodString.equals("") && methodType != 0) {
                methodTypeString = String.format("0x%08x", methodType);
            }
            stringBuffer.append(type.getSimpleName() + "," + name.getQualifiedName() +
                    "," + methodString + "," + methodTypeString + "\n");
        }
        return stringBuffer.toString();
    }
}