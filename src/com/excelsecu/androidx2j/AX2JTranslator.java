package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
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
        add(string2QName(qNameString), methodString, 0);
    }
    
    public void add(String qNameString, String methodString, int methodType) {
        add(string2QName(qNameString), methodString, methodType);
    }
    
    public void add(QName name, String methodString, int methodType) {
        AX2JMethod method = new AX2JMethod(name, methodString);
        addAttribute(name, method, methodType);
    }
    
    public AX2JAttribute get(QName name) {
        for (AX2JAttribute attribute : attributeList) {
            if (attribute.getName().equals(name)) {
                return attribute.clone();
            }
        }
        return null;
    }
    
    public AX2JAttribute findAttribute(QName name) {
        for (AX2JAttribute attribute : attributeList) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }
    
    
    public AX2JMethod findMethod(AX2JMethod oldMethod) {
        for (AX2JMethod method : methodList) {
            if (method.equals(oldMethod)) {
                return method;
            }
        }
        return null;
    }
    
    public void addAttribute(QName name, AX2JMethod method, int methodType) {
        AX2JAttribute attribute = findAttribute(name);
        if (attribute == null) {
            attribute = new AX2JAttribute(name, methodType);
            attributeList.add(attribute);
        } else {
            attribute.setMethodType(methodType);
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
        String nameString = qNameString.substring(qNameString.indexOf(':') + 1);
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
    
    public void translate(AX2JCodeBlock codeBlock, Attribute attr) {
        AX2JAttribute attribute = findAttribute(attr.getQName());
        if (attribute == null) {
            throw new AX2JException(AX2JException.ATTRIBUTE_NOT_FOUND, attr.asXML());
        }
        attribute.setValue(attr.getValue());
        
        //currently not support multi relative method, default choose the first one
        AX2JMethod method = attribute.getRelativeMethodList().get(0);
        if (method == null || method.getName().equals("")) {
            throw new AX2JException(AX2JException.METHOD_NOT_FOUND, attr.asXML());
        }
        
        String methodString = method.getName() + "(" + translateValue(attribute, method) + ");\n";
        codeBlock.add(methodString, attribute.getTypeValue(AX2JAttribute.TYPE_PRIORITY));
    }
    
    /**
     * Translate a XML attribute's value to a Java method's value.
     * @param attr the attribute to be translated
     * @return the value after translating
     */
    protected String translateValue(AX2JAttribute attribute, AX2JMethod method) {
        int argOrder = attribute.getTypeValue(AX2JAttribute.TYPE_ARGUMENTS);
        System.out.println(attribute);
        Class<?> argType = method.getArgTypes()[argOrder];
        String value = attribute.getValue();
        String attrName = attribute.getName().getQualifiedName();
        
        if (argType.equals(Integer.class)) {
            //dp, px, sp
            if (value.matches("[0-9.]+dp")) {
                value = value.substring(0, value.length() - 2);
                value = "(int) (" + value + " * scale + 0.5f)";
            } else if (value.matches("[0-9.]+sp")) {
                value = value.substring(0, value.length() - 2);
            } else if (value.matches("[0-9]+px")) {
                value = value.substring(0, value.length() - 2);
            } else if (value.equals("fill_parent") || value.equals("match_parent")) {
                value = "ViewGroup.LayoutParams.MATCH_PARENT";
            } else if (value.equals("wrap_content")) {
                value = "ViewGroup.LayoutParams.WRAP_CONTENT";
            }
            
            //id
            else if (value.startsWith("@+id/") || value.startsWith("@id/")) {
                value = value.substring(value.indexOf('/') + 1);
                value = Config.R_CLASS + ".id." + value;
            }
            
            //string
            else if (value.contains("@string/")) {
                value = value.substring(value.indexOf('/') + 1);
                value = Config.R_CLASS + ".string." + value;
                value = Config.RESOURCES_NAME + ".getString(" + value + ")";
            } else if (attrName.equals("android:text") ||
                    attrName.equals("android:hint")) {
                value = "\"" + value + "\"";
            }
            
            //color
            else if (value.matches("#[0-9a-fA-F]+")) {
                if (value.length() == 4) {
                    value = "#" + value.charAt(1) + '0' + value.charAt(2) + '0' +
                            value.charAt(3) + '0';
                } else if (value.length() == 5) {
                    value = "#" + value.charAt(1) + '0' + value.charAt(2) + '0' +
                            value.charAt(3) + '0' + value.charAt(4) + '0';
                }
                value = "Color.parseColor(\"" + value + "\")";
            } else if (value.matches("@android:color/.+")) {
                value = value.substring(value.indexOf('/') + 1);
                value = value.toUpperCase();
                value = "Color." + value;
            } else if (value.matches("@color/.+")) {
                value = value.substring(value.indexOf('/') + 1);
                value = Config.R_CLASS + ".color." + value;
                value = Config.RESOURCES_NAME + ".getColor(" + value + ")";
            }
            
            //visibility
            else if (value.equals("gone") || value.equals("visibile") ||
                    value.equals("invisibile")) {
                value = "View." + value.toUpperCase();
            }
            
            //orientation
            else if (value.equals("vertical")) {
                value = "LinearLayout.VERTICAL";
            } else if (value.equals("horizontal")) {
                value = "LinearLayout.HORIZONTAL";
            }
            
            //gravity
            else if (attrName.equals("android:gravity") ||
                    attrName.equals("android:layout_gravity")) {
                value = Utils.prefixParams(value, "Gravity");
            }
        }
        
        else if (argType.equals(Float.class)) {
            //float
            value = value + "f";
        }
        
        else if (argType.equals(Drawable.class) || argType.equals(ColorStateList.class)) {
            //drawable
            if (value.startsWith("@drawable/")) {
                value = value.substring(value.indexOf('/') + 1);
                value = Config.R_CLASS + ".drawable." + value;
                if (attrName.contains("Color") ||
                        attrName.contains("TintList")) {
                    value = "resources.getColorStateList(" + value + ")";
                } else {
                    value = "resources.getDrawable(" + value + ")";
                }
            }
        }
        
        else if (argType.equals(TransformationMethod.class)) {
            //text
            if (attrName.equals("android:password")) {
                value = "new PasswordTransformationMethod()";
            } else if (attrName.equals("android:singleLine")) {
                value = "new SingleLineTransformationMethod()";
            } else if (attrName.equals("android:inputType")) {
                String error = value; 
                value = Config.INPUT_TYPE_MAP.get(value);
                if (value == null) {
                    throw new AX2JException(AX2JException.ATTRIBUTE_VALUE_ERROR, error);
                }
                value = Utils.prefixParams(value, "InputType");
            } else if (attrName.equals("android:ellipsize")) {
                value = value.toUpperCase();
                value = "TextUtils.TruncateAt." + value;
            }
        }
        
        return value;
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
    
    public String toString() {
        StringBuffer content = new StringBuffer();
        for (AX2JAttribute attribute : attributeList) {
            content.append(attribute.toString());
        }
        return content.toString();
    }
    
    public final class AX2JAttribute implements Cloneable {
        /** normal attribute **/
        public static final int TYPE_NORMAL = 0x00000000;
        /** method has api limit. Range 0x00000000 - 0x000000ff **/
        public static final int TYPE_API_LIMIT = 0x00000ff;
        /** method has priority in Java method list. Range 0x00000100 - 0x00000f00 **/
        public static final int TYPE_PRIORITY = 0x00000f00;
        /** method has multi-arguments. Range 0x00001000 - 0x0000f000 **/
        public static final int TYPE_ARGUMENTS = 0x0000f000;
        /** attribute for LayoutParams. 0x00100000 **/
        public static final int TYPE_LAYOUT_PARAMETER = 0x00010000;
        /** use style resource **/
        public static final int TYPE_STYLE = 0x00020000;
        /** assign variable directly **/
        public static final int TYPE_VARIABLE_ASSIGNMENT = 0x00040000;
        /** use reflect to assign variable **/
        public static final int TYPE_VARIABLE_REFLECTION = 0x00080000;
        /** use reflect to invoke method **/
        public static final int TYPE_METHOD_REFLECTION = 0x00100000;
        
        private QName name;
        private List<AX2JMethod> relativeMethodList;
        private int methodType;
        private String value;
        
        public AX2JAttribute(QName name) {
            this.name = name;
            this.methodType = TYPE_NORMAL;
            relativeMethodList = new ArrayList<AX2JMethod>();
            value = null;
        }
        
        public AX2JAttribute(QName name, int methodType) {
            this.name = name;
            this.methodType = methodType;
            relativeMethodList = new ArrayList<AX2JMethod>();
            value = null;
        }
        
        public AX2JAttribute(QName name, AX2JMethod method) {
            this.name = name;
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
            int value = methodType & mask;
            switch(mask) {
                case TYPE_NORMAL:
                    break;
                case TYPE_API_LIMIT:
                    if (value == 0) {
                        value = 1;
                    }
                    break;
                case TYPE_PRIORITY:
                    value = value >> 8;
                    if (value == 0) {
                        value = AX2JCodeBlock.PRIORITY_DEFAULT;
                    }
                    break;
                case TYPE_ARGUMENTS:
                    value = value >> 12;
                    break;
                case TYPE_LAYOUT_PARAMETER:
                    value = value >> 20;
                    break;
                case TYPE_STYLE:
                    value = value >> 21;
                    break;
                case TYPE_VARIABLE_ASSIGNMENT:
                    value = value >> 22;
                    break;
                case TYPE_VARIABLE_REFLECTION:
                    value = value >> 23;
                    break;
                case TYPE_METHOD_REFLECTION:
                    value = value >> 24;
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
    
    public class AX2JMethod implements Cloneable {
        private String methodName;
        private Class<?>[] argTypes;
        private List<AX2JAttribute> relativeAttributeList;
        private String[] args;
        
        public AX2JMethod(QName attributeName, String methodString) {
            setMethod(methodString);
            relativeAttributeList = new ArrayList<AX2JAttribute>();
            args = null;
        }
        
        public void setMethod(String methodString) {
            //no relative method
            if (methodString.indexOf('(') == -1) {
                methodName = "";
                argTypes = new Class<?>[0];
            } else {
                methodName = methodString.substring(0, methodString.indexOf('('));
                methodName = methodName.replace("\n", "");
                
                String[] args = methodString.substring(methodString.indexOf('(') + 1,
                        methodString.indexOf(')')).split(",");
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
        
        public void setMethod(AX2JMethod newMethod) {
            methodName = newMethod.getMethodName();
            argTypes = newMethod.getArgTypes();
            relativeAttributeList = newMethod.getRelativeAttributeList();
        }
        
        public void setArgs(String[] args) {
            this.args = args;
        }
        
        public AX2JMethod clone() {
            AX2JMethod method = null;
            try {
                method = (AX2JMethod) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            
            return method;
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
            StringBuffer argsBuffer = new StringBuffer();
            if (args != null) {
                for (String arg : args) {
                    argsBuffer.append(arg + ", ");
                }
                if (argsBuffer.length() > 1) {
                    argsBuffer.deleteCharAt(argsBuffer.length() - 2);
                }
            } else {
                for (Class<?> clazz : argTypes) {
                    argsBuffer.append(clazz.getSimpleName() + ",");
                }
                if (argsBuffer.length() > 1) {
                    argsBuffer.deleteCharAt(argsBuffer.length() - 1);
                }
            }
            String methodString = this.getMethodName() == ""? "" :
                this.getMethodName() + "(" + argsBuffer +")";
            
            return methodString;
        }
        
        public boolean equals(AX2JMethod method2) {
            if (methodName.equals(method2.getName())) {
                Class<?>[] argTypes2 = method2.getArgTypes();
                int i = 0;
                for (; i < argTypes.length; i++) {
                    if (!argTypes[i].equals(argTypes2[i])) {
                        break;
                    }
                }
                if (i == argTypes.length) {
                    return true;
                }
            }
            return false;
        }
    }
}
