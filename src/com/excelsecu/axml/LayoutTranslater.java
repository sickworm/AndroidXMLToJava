package com.excelsecu.axml;

import java.util.List;

import org.dom4j.Attribute;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Translate a Android XML Node to a Java method block.
 * @author ch
 *
 */
public class LayoutTranslater extends BaseConverter {
    private int num = 1;
	
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
        type = node.getType();
        if (type == null) {
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
	            System.out.println("<" + node.getLabelName() + "/>" + " label not support");
	            javaBlock = "//<" + node.getLabelName() + "/>\t//not support\n\n";
	            return javaBlock;
	        }
        }
        newMethod = node.getLabelName() + " " + nodeName + " = new " + 
                node.getLabelName() + "(context);\n";
	    
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
                    //translator can not translate this attribute
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