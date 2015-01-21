package com.excelsecu.axml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.excelsecu.axml.dbbuilder.AndroidDocConverter;

/**
 * Translate a Android XML Node to a Java method block.
 * @author ch
 *
 */
public class LayoutTranslater {
    private HashMap<String, String> map = null;
    private String extraMethod = "";
    private static List<String> idList = new ArrayList<String>();
    private List<String> importList = new ArrayList<String>();
    private int num = 1;
    /** record of {@link LayoutTranslater#extraHandle(String attrName , String attrValue)} **/
    private boolean scale = false;
    
	public LayoutTranslater() {
        map = AndroidDocConverter.getMap();
	}
	
	/**
	 * Translate a XML attritube to a Java method.
	 * @param attrName the name of attribute
	 * @param attrValue the value of attribute
	 * @return
	 */
	public String translate(AXMLNode node) {
        String javaBlock = "";
        Class<?> type = null;
        String nodeName = Utils.classToObject(node.getLabelName()) + num;
        node.setObjectName(nodeName);
        
        String newMethod = "";
	    try {
	        type = node.getType();
	        newMethod = node.getLabelName() + " " + nodeName + " = new " + 
                    node.getLabelName() + "(context);\n";
	    } catch (AXMLException e) {
	        if (node.getLabelName().equals("include")) {
	            String layout = node.attributeValue("layout");
	            layout = layout.substring(layout.indexOf('/') + 1);
	            if (layout != null) {
	                newMethod = "View " + nodeName + " = " +
	                        layout + ".get(context);\n";
	            }
	            node.setType(android.view.View.class);
	            type = node.getType();
	            addImport(Config.PACKAGE_NAME + ".layout." + layout);
	        } else {
    	        if (e.getErrorCode() == AXMLException.CLASS_NOT_FOUND) {
    	            System.out.println("<" + node.getLabelName() + "/>" + " label not support");
    	            javaBlock = "//<" + node.getLabelName() + "/>\t//not support\n\n";
    	            return javaBlock;
    	        }
    	        e.printStackTrace();
	        }
	    }
	    
        javaBlock += newMethod;
        AXMLSpecialTranslater specialTranslater = new AXMLSpecialTranslater(node);
        javaBlock += specialTranslater.buildLayoutParams();
        addImport(Context.class.getName());
        for (Attribute a : node.getAttributes()) {
            String attrMethod = "";
            String attrName = a.getQualifiedName();
            String attrValue = a.getValue();
            try {
                String methodName = transAttrToMethod(a, type);
                String methodValue = translateValue(a);
                attrMethod = methodName + "(" + methodValue + ")";
                attrMethod = nodeName + "." + attrMethod + ";\n";
            } catch (AXMLException e) {
                try {
                    //deal with the attributes that doesn't match the XML attributes table
                    attrMethod = specialTranslater.translate(a);
                } catch (AXMLException e1) {
                    //translater can not translate this attribute
                    attrMethod = "//" + attrName + "=\"" + attrValue + "\";\t//not support\n";
                }
            }
            if (!attrMethod.startsWith("//"))
                extraHandle(node, a);
            javaBlock += attrMethod;
        }
        
        javaBlock += specialTranslater.setLayoutParams();
        AXMLNode parent = node.getParent();
        if (parent != null) {
            String addViewMethod = parent.getObjectName() + ".addView(" + nodeName + ");\n";
            javaBlock += addViewMethod;
        }
        javaBlock += "\n";
        num++;
        
        return javaBlock;
	}
	
	/**
	 * Translate XML element's attribute to Android method without parameters.
	 * @param attrName	The name of attribute.
	 * @return Android method matches the attribute without parameters.
	 */
	private String transAttrToMethod(Attribute a, Class<?> type) {
	    //find the conversion between XML attribute and Java method in the match map.
	    String attrName = a.getQualifiedName();
	    String attrValue = a.getValue();
	    String key = type.getSimpleName() + "$" + attrName;
        if (!map.containsKey(key)) {
            //find the conversion from its super class
            while (Utils.isSupportClass(type.getSuperclass())) {
                type = type.getSuperclass();
                key = type.getSimpleName() + "$" + attrName;
                if (map.containsKey(key))
                    break;
            }
            if (!map.containsKey(key)) {
                throw new AXMLException(AXMLException.METHOD_NOT_FOUND, key);
            }
        }
        String methodName = map.get(key);
        if (methodName.equals(null) || methodName.equals("")) {
            throw new AXMLException(AXMLException.METHOD_NOT_FOUND, key);
        }
        
        //when attribute has several types of value (like android:background),
        //change the method if nessary.
        if (methodName.equals("setBackground(Drawable)")) {
            if (attrValue.matches("#[0-9a-fA-F]+") ||
                    attrValue.matches("@android:color/.+") ||
                    attrValue.matches("@color/.+")) {
                methodName = "setBackgroundColor(int)";
            }
        }

        methodName = methodName.substring(0, methodName.indexOf("("));
        return methodName;
	}
	
	private String translateValue(Attribute attr) {
	    String value = attr.getValue();
        String attrName = attr.getQualifiedName();
	    
        //not strict enough, should check attrName both
	    //dp, px, sp, float
	    if (value.matches("[0-9.]+dp")) {
            value = value.substring(0, value.length() - 2);
            value = "(int) (" + value + " / scale + 0.5f)";
	    } else if (value.matches("[0-9.]+sp")) {
	        value = value.substring(0, value.length() - 2);
	    } else if (value.matches("[0-9]+px")) {
            value = value.substring(0, value.length() - 2);
        } else if (value.equals("fill_parent") || value.equals("match_parent")) {
            value = "ViewGroup.LayoutParams.MATCH_PARENT";
        } else if (value.equals("wrap_content")) {
            value = "ViewGroup.LayoutParams.WRAP_CONTENT";
        } else if (value.matches("[0-9]+.[0-9]+")) {
            value = value + "f";
        }
	    
	    //id
        else if (value.startsWith("@+id/") || value.startsWith("@id/")) {
	        value = value.substring(value.indexOf('/') + 1);
	        value = "R.id." + value;
	    }
        
	    //string
        else if (value.contains("@string/")) {
	        value = value.substring(value.indexOf('/') + 1);
            value = "strings." + value;
        } else if (attr.getQualifiedName().equals("android:text")) {
            value = "\"" + value + "\"";
        }
	    
	    //color
	    else if (value.matches("#[0-9a-fA-F]+")) {
	        value = "Color.parseColor(\"" + value + "\")";
	    } else if (value.matches("@android:color/.+")) {
	        value = value.substring(value.indexOf('/') + 1);
            value = "android.R.color." + value;
        } else if (value.matches("@color/.+")) {
            value = value.substring(value.indexOf('/') + 1);
            value = "color." + value;
        }
	    
	    //visibility
	    else if (value.equals("gone") || value.equals("visibile") ||
	            value.equals("invisibile")) {
	        value = "View." + value.toUpperCase();
	    }
	    
	    //drawable
        else if (value.startsWith("@drawable/")) {
            value = value.substring(value.indexOf('/') + 1);
            value = "" + value + ".get(context)";
        }
	    
        //orientation
        else if (value.equals("vertical")) {
            value = "LinearLayout.VERTICAL";
        } else if (value.equals("horizontal")) {
            value = "LinearLayout.HORIZONTAL";
        }
	    
	    //gravity
        else if (attr.getQualifiedName().equals("android:gravity") ||
                attr.getQualifiedName().equals("android:layout_gravity")) {
            value = Utils.devideParams(value, "Gravity");
        }
	    
	    //text
        else if (attrName.equals("android:password")) {
            value = "new PasswordTransformationMethod()";
        } else if (attrName.equals("android:singleLine")) {
            value = "new SingleLineTransformationMethod()";
        } else if (attrName.equals("android:inputType")) {
            String error = value; 
            value = Config.INPUT_TYPE_MAP.get(value);
            if (value == null) {
                throw new AXMLException(AXMLException.ATTRIBUTE_VALUE_ERROR, error);
            }
            value = Utils.devideParams(value, "InputType");
        }
	    
        return value;
	}
	
	/**
	 * Find out what extra constant, id or import need to be added.
	 * @param attrName
	 * @param attrValue
	 */
	private void extraHandle(AXMLNode node, Attribute attr) {
        addImport(node.getType().getName());
        
        String attrValue = attr.getValue();
        String attrName = attr.getQualifiedName();
        if (attrValue.matches("[0-9]+dp")) {
            if (!scale) {
                scale = true;
                extraMethod += "final float scale = context.getResources().getDisplayMetrics().density;\n";
            }
        } else if (attrValue.equals("fill_parent") || attrValue.equals("match_parent")
                || attrValue.equals("wrap_content")) {
            addImport(ViewGroup.class.getName());
        } else if (attrName.matches("android:layout_margin(Left)|(Top)|(Right)|(Bottom)")) {
            addImport(ViewGroup.class.getName());
        } else if (attrValue.equals("gone") || attrValue.equals("visibile") ||
                attrValue.equals("invisibile")) {
            addImport(View.class.getName());
        } else if (attrName.equals("android:id") &&
                    attrValue.startsWith("@+id/")) {
            addImport(Config.PACKAGE_NAME + ".R");
            String id = attrValue.substring(attrValue.indexOf('/') + 1);
            if (!Utils.hasString(idList, id)) {
                idList.add(id);
            }
        } else if (attrValue.matches("#[0-9a-fA-F]+")) {
            addImport(Color.class.getName());
        } else if (attrValue.startsWith("@string/")) {
            addImport(Config.PACKAGE_NAME + ".values.strings");
        } else if (attrValue.startsWith("@drawable/")) {
            String value = attrValue.substring(attrValue.indexOf('/') + 1);
            addImport(Config.PACKAGE_NAME + ".drawable." + value);
        } else if (attrName.equals("android:gravity") ||
                attr.getQualifiedName().equals("android:layout_gravity")) {
            addImport(Gravity.class.getName());
        } else if (attrName.equals("android:password")) {
            addImport(PasswordTransformationMethod.class.getName());
        } else if (attrName.equals("android:singleLine")) {
            addImport(SingleLineTransformationMethod.class.getName());
        } else if (attrName.equals("android:inputType")) {
            addImport(InputType.class.getName());
        } else if (attrValue.matches("@color/.+")) {
            addImport(Config.PACKAGE_NAME + ".values.color");
        }
	}
    
	/**
	 *  Add the class to the import list. If already exists, ignore. 
	 *  @param className the class try to be added in import list
	 */
    protected void addImport(String className) {
        if (!Utils.hasString(importList, className)) {
            importList.add(className);
        }
    }
    
    public String getExtraMethod() {
        return extraMethod;
    }
    
    public static List<String> getIdList() {
        return idList;
    }
    
    public List<String> getImportList() {
        return importList;
    }
    
    /**
     * Handle the method not exists in the attr-to-method map.
     * @author ch
     *
     */
    public class AXMLSpecialTranslater {
        private AXMLNode node;
        private String parentName;
        private String layoutParamName;
        private List<Attribute> attrList;
        private String width;
        private String height;
        
        /** set up margins just need one setting **/
        private boolean margin = false;
        /** set up padding just need one setting **/
        private boolean padding = false;
        
        public AXMLSpecialTranslater(AXMLNode node) {
            this.node = node;
            attrList = node.getAttributes();
            parentName = Utils.getParentName(node);
            addImport(Utils.matchClass(parentName).getName());
            layoutParamName = Utils.classToObject(ViewGroup.LayoutParams.class.getSimpleName()) + num;
        }
        
        public String translate(Attribute attr) throws AXMLException {
            String attrName = attr.getQualifiedName();
            String javaBlock = "";
            
            //LayoutParams, handled in buildLayoutParams
            if (attrName.equals("android:layout_width") || attrName.equals("android:layout_height")) {
                return "";
            }
            //weight
            if (attrName.equals("android:layout_weight")) {
                return layoutParamName + ".weight = " + attr.getValue() + ";\n";
            }
            //layout_gravity
            if (attrName.equals("android:layout_gravity")) {
                return layoutParamName + ".gravity = " + translateValue(attr) + ";\n";
            }
            
            //MarginLayoutParams
            if (attrName.equals("android:layout_marginTop") || attrName.equals("android:layout_marginBottom") ||
                    attrName.equals("android:layout_marginLeft") || attrName.equals("android:layout_marginRight") ||
                    attrName.equals("android:layout_margin")) {
                if (!margin) {
                    String left, top, right, bottom;
                    if (attrName.equals("android:layout_margin")) {
                        left = top = right = bottom = translateValue(attr);
                    } else {
                        Attribute attrLeft = findAttrByName("android:layout_marginLeft");
                        Attribute attrTop = findAttrByName("android:layout_marginTop");
                        Attribute attrRight = findAttrByName("android:layout_marginRight");
                        Attribute attrBottom = findAttrByName("android:layout_marginBottom");
                        left = (attrLeft == null)? "0" : translateValue(attrLeft);
                        top = (attrTop == null)? "0" : translateValue(attrTop);
                        right = (attrRight == null)? "0" : translateValue(attrRight);
                        bottom = (attrBottom == null)? "0" : translateValue(attrBottom);
                    }
                    String paramValue = left + ", " + top + ", " + right + ", " + bottom;
                    javaBlock += layoutParamName + ".setMargins(" + paramValue + ");\n";
                    
                    margin = true;
                    return javaBlock;
                }
                return "";
            }
            
            //panding
            if (attrName.equals("android:paddingBottom") || attrName.equals("android:paddingTop") ||
                    attrName.equals("android:paddingLeft") || attrName.equals("android:paddingRight") ||
                    attrName.equals("android:paddingStart") || attrName.equals("android:paddingEnd") ||
                    attrName.equals("android:padding")) {
                if (!padding) {
                    if (attrName.equals("android:padding")) {
                        String attrValue = translateValue(attr);
                        javaBlock = node.getObjectName() + ".setPadding(" +
                                attrValue + ", " + attrValue + ", " +
                                attrValue + ", " + attrValue + ");\n";
                    } else {
                        Attribute attrTop = findAttrByName("android:paddingTop");
                        Attribute attrBottom = findAttrByName("android:paddingBottom");
                        Attribute attrStart = findAttrByName("android:paddingStart");
                        Attribute attrEnd = findAttrByName("android:paddingEnd");
                        Attribute attrLeft = findAttrByName("android:paddingLeft");
                        Attribute attrRight = findAttrByName("android:paddingRight");
                        String top = (attrTop == null)? "0" : translateValue(attrTop);
                        String bottom = (attrBottom == null)? "0" : translateValue(attrBottom);
                        String start = (attrStart == null)? "0" : translateValue(attrRight);
                        String end = (attrEnd == null)? "0" : translateValue(attrBottom);
                        String left = (attrLeft == null)? "0" : translateValue(attrLeft);
                        String right = (attrRight == null)? "0" : translateValue(attrRight);
                        if (left != null || attrBottom != null) {
                            javaBlock += node.getObjectName() + ".setPadding(" +
                                    left + ", " + top + ", " +
                                    right + ", " + bottom + ");\n";
                        }
                        //padding should not be set in two ways, It may have translate problem
                        //here because of the order
                        //or I should add some warning
                        if (attrStart != null || attrEnd != null) {
                            javaBlock += node.getObjectName() + ".setPaddingRelative(" +
                                    start + ", " + top + ", " +
                                    end + ", " + bottom + ");\n";
                        }
                    }
                    
                    padding = true;
                    return javaBlock;
                }
                return "";
            }
            
            
            //RelativeLayout rules
            String rule = Utils.findRule(attrName);
            if (rule != null) {
                rule = "RelativeLayout." + rule;
                String ruleValue = attr.getValue();
                String className = Utils.getParentName(node);
                //false means nothing
                if (ruleValue.equals("false")) {
                    return "";
                }
                if (ruleValue.equals("true")) {
                    ruleValue = className + ".TRUE";
                } else if (ruleValue.startsWith("@id/.*") ||
                        ruleValue.startsWith("@+id/")){
                    ruleValue = ruleValue.substring(ruleValue.indexOf('/') + 1);
                    ruleValue = "R.id." + ruleValue;
                } else {
                    throw new AXMLException(AXMLException.ATTRIBUTE_VALUE_ERROR, ruleValue);
                }
                javaBlock += layoutParamName + ".addRule(" + rule + ", " + ruleValue + ");\n";
                
                return javaBlock;
            }
            
            //include
            if (attrName.equals("layout")) {
                //handled in newMethod
                return "";
            }
            
            throw new AXMLException(AXMLException.METHOD_NOT_FOUND);
        }
        
        private Attribute findAttrByName(String attrName) {
            for (Attribute a : attrList) {
                if (a.getQualifiedName().equals(attrName)) {
                    return a;
                }
            }
            return null;
        }
        
        public String setLayoutParams() {
            return node.getObjectName() + ".setLayoutParams(" + layoutParamName + ");\n";
        }
        
        /**
         * Init LayoutParams
         * @return
         */
        public String buildLayoutParams() {
            String javaBlock;
            Attribute attrWidth = findAttrByName("android:layout_width");
            Attribute attrHeight = findAttrByName("android:layout_height");
            width = (attrWidth == null)?
                    parentName + ".LayoutParams.WRAP_CONTENT" : translateValue(attrWidth);
            height = (attrHeight == null)?
                    parentName + ".LayoutParams.WRAP_CONTENT" : translateValue(attrHeight);
            String paramValue = width + ", " + height;
            javaBlock = parentName + ".LayoutParams " + layoutParamName +
                    " =\n\t\tnew " + parentName + ".LayoutParams(" + paramValue + ");\n";
            return javaBlock;
        }
    }
}