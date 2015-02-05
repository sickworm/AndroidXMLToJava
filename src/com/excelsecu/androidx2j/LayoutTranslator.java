package com.excelsecu.androidx2j;

import java.io.File;

import org.dom4j.Attribute;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

/**
 * Translate Android XML layout resources to Java method block.
 * @author ch
 *
 */
public class LayoutTranslator extends BaseTranslator {

    public LayoutTranslator(File file) {
		super(file);
		AX2JNode.resetOrder();
	}

    @Override
    public String translate() {
        String javaBlock = super.translate();
        String extraMethod = getExtraMethod();
        extraMethod = extraMethod.equals("")? "" : extraMethod + "\n";
        return extraMethod + javaBlock;
    }
    
    @Override
	protected String translateNode(AX2JNode node) {
        String javaBlock = "";
        String newMethod = "";
        String nodeName = node.getObjectName();
        node.setObjectName(nodeName);
        
        //include label
        if (node.getLabelName().equals("include")) {
            String layout = node.attributeValue("layout");
            layout = layout.substring(layout.indexOf('/') + 1);
            if (layout != null) {
                newMethod = "View " + nodeName + " = " +
                        Config.PACKAGE_NAME + ".layout." + layout + ".get(context);\n";
            }
        } else {
	        newMethod = node.getType().getSimpleName() + " " + nodeName + " = new " + 
	                node.getLabelName() + "(" + node.constructorParams() + ");\n";
        }
		    
        javaBlock += newMethod;
        SpecialTranslator specialTranslater = new SpecialTranslator(node);
        javaBlock += specialTranslater.prebuild();
        addImport(Context.class.getName());
        for (Attribute a : node.getAttributes()) {
            String attrMethod = translateAttribute(a, node, specialTranslater);
            if (!attrMethod.startsWith("//")) {
                extraHandle(node, a);
            }
            javaBlock += attrMethod;
        }
        
        javaBlock += specialTranslater.setLayoutParams();
        AX2JNode parent = node.getParent();
        if (parent != null) {
            String addViewMethod = parent.getObjectName() + ".addView(" + nodeName + ");\n";
            javaBlock += addViewMethod;
        }
        javaBlock += "\n";
        
	    //divider, the deviderHeight must set after divider in Java
        if (javaBlock.contains("setDivider(") && javaBlock.contains("setDividerHeight(")) {
        	if (javaBlock.indexOf("setDivider(") > javaBlock.indexOf("setDividerHeight(")) {
	        	String[] javaList = javaBlock.split("\\n");
	        	String divider = "";
	        	String dividerHeight = "";
	        	for (String code : javaList) {
	        		if (code.contains("setDivider(")) {
	         			divider = code;
	         		} else if (code.contains("setDividerHeight(")) {
	         			dividerHeight = code;
	         		}
	        	}
	        	String tmp = "<!REPLACE_BLOCK>";
	        	javaBlock = javaBlock.replace(divider, tmp);
	        	javaBlock = javaBlock.replace(dividerHeight, divider);
	        	javaBlock = javaBlock.replace(tmp, dividerHeight);
        	}
        }
        
        return javaBlock;
	}
    
	private String translateAttribute(Attribute attr, AX2JNode node,
	        SpecialTranslator specialTranslator) throws AX2JException {
        String attrMethod = "";
        try {
            attrMethod = super.translateAttribute(attr, node);
        } catch (AX2JException e) {
            try {
                //deal with the attributes that doesn't match the XML attributes table
                attrMethod = specialTranslator.translate(attr);
            } catch (AX2JException e1) {
                //translator can not translate this attribute
                attrMethod = "//" + attr.getQualifiedName() + "=\"" +
                        attr.getValue() + "\";\t//not support\n";
            }
        }
        
        return attrMethod;
	}
	
	
	
    @Override
    protected String transAttrToMethod(Attribute a, Class<?> type) {
        String methodName = super.transAttrToMethod(a, type);
        
        //when attribute has several types of value (like android:background),
        //change the method if necessary.
        String attrValue = a.getValue();
        if (methodName.equals("setBackground")) {
            if (attrValue.matches("#[0-9a-fA-F]+") ||
                    attrValue.matches("@android:color/.+") ||
                    attrValue.matches("@color/.+")) {
                methodName = "setBackgroundColor";
            } else if (Config.API_LEVEL <= 8) {
                methodName = "setBackgroundDrawable";
            }
        }
        
        return methodName;
    }

    @Override
	protected String translateValue(Attribute attr) {
		String value = super.translateValue(attr);
		
		//divider
    	String attrName = attr.getQualifiedName();
		if (attrName.equals("android:divider")) {
			if (value.matches("Color\\.parseColor\\(\"#[0-9a-fA-F]+\"\\)")) {
        		value = "new ColorDrawable(" + value + ")";
			}
        }
		
		return value;
	}
    
	@Override
	protected void extraHandle(AX2JNode node, Attribute attr) {
		super.extraHandle(node, attr);
		
		//divider
    	String attrName = attr.getQualifiedName();
		String attrValue = attr.getValue();
		if (attrName.equals("android:divider")) {
			if (attrValue.matches("#[0-9a-fA-F]+")) {
				addImport(ColorDrawable.class.getName());
			}
        }
	}

	/**
     * Handle the method not exists in the attr-to-method map.
     * @author ch
     *
     */
    public class SpecialTranslator {
        private AX2JNode node;
        private String parentName;
        private String layoutParamName;
        private String width;
        private String height;
        
        private boolean margin = false;
        private boolean padding = false;
        private boolean drawable = false;
        
        private AX2JNode styleNode = null;
        
        public SpecialTranslator(AX2JNode node) {
            this.node = node;
            parentName = Utils.getParentName(node);
            addImport(Utils.matchClass(parentName).getName());
            layoutParamName = node.getObjectName() + "Params";
        }
        
        public String translate(Attribute attr) throws AX2JException {
            String attrName = attr.getQualifiedName();
            String javaBlock = "";
            
            //LayoutParams, handled in buildLayoutParams
            if (attrName.equals("android:layout_width") || attrName.equals("android:layout_height")) {
                return "";
            }
            //style, handled in buildStyle
            if (attrName.equals("style")) {
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
            
            //padding
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
            
            //drawable direction
            if (attrName.equals("android:drawableBottom") || attrName.equals("android:drawableTop") ||
                    attrName.equals("android:drawableLeft") || attrName.equals("android:drawableRight") ||
                    attrName.equals("android:drawableStart") || attrName.equals("android:drawableEnd")) {
                if (!drawable) {
                    Attribute attrTop = findAttrByName("android:drawableTop");
                    Attribute attrBottom = findAttrByName("android:drawableBottom");
                    Attribute attrStart = findAttrByName("android:drawableStart");
                    Attribute attrEnd = findAttrByName("android:drawableEnd");
                    Attribute attrLeft = findAttrByName("android:drawableLeft");
                    Attribute attrRight = findAttrByName("android:drawableRight");
                    String top = (attrTop == null)? "null" : translateValue(attrTop);
                    String bottom = (attrBottom == null)? "null" : translateValue(attrBottom);
                    String start = (attrStart == null)? "null" : translateValue(attrRight);
                    String end = (attrEnd == null)? "null" : translateValue(attrBottom);
                    String left = (attrLeft == null)? "null" : translateValue(attrLeft);
                    String right = (attrRight == null)? "null" : translateValue(attrRight);
                    if (left != null || attrBottom != null) {
                        javaBlock += node.getObjectName() + ".setCompoundDrawablesWithIntrinsicBounds(" +
                                left + ", " + top + ", " +
                                right + ", " + bottom + ");\n";
                    }
                    //drawable direction should not be set in two ways, It may have translate problem
                    //here because of the order
                    //or I should add some warning
                    if (attrStart != null || attrEnd != null) {
                        javaBlock += node.getObjectName() + ".setCompoundDrawablesRelativeWithIntrinsicBounds(" +
                                start + ", " + top + ", " +
                                end + ", " + bottom + ");\n";
                    }
                    
                    drawable = true;
                    return javaBlock;
                }
                return "";
            }
            
            
            //RelativeLayout rules
            String rule = Utils.findRule(attrName);
            if (rule != null) {
                if (!parentName.equals("RelativeLayout")) {
                    throw new AX2JException(AX2JException.METHOD_NOT_FOUND);
                }
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
                    ruleValue = Config.R_CLASS + ".id." + ruleValue;
                } else {
                    throw new AX2JException(AX2JException.ATTRIBUTE_VALUE_ERROR, ruleValue);
                }
                javaBlock += layoutParamName + ".addRule(" + rule + ", " + ruleValue + ");\n";
                
                return javaBlock;
            }
            
            //include
            if (attrName.equals("layout")) {
                //handled in newMethod
                return "";
            }
            
            throw new AX2JException(AX2JException.METHOD_NOT_FOUND);
        }
        
        private Attribute findAttrByName(String attrName) {
            Attribute attrStyle = null;
            if (styleNode != null) {
                attrStyle = styleNode.findAttrByName(attrName);
            }
            Attribute attrNode = null;
            attrNode = node.findAttrByName(attrName);
            
            //attrNode has priority
            if (attrNode != null) {
                return attrNode;
            } else {
                return attrStyle;
            }
        }
        
        public String setLayoutParams() {
            return node.getObjectName() + ".setLayoutParams(" + layoutParamName + ");\n";
        }
        
        public String prebuild() {
            String javaBlock = "";
            javaBlock += buildLayoutParams();
            javaBlock += buildStyle();
            return javaBlock;
        }
        
        /**
         * Initialize LayoutParams object
         * @return
         */
        private String buildLayoutParams() {
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
        
        /**
         * Translate style to Java block
         * @return Java code
         */
        private String buildStyle() {
            String styleValue = node.attributeValue("style");
            String javaBlock = "";
            javaBlock = buildStyle(styleValue);
            return javaBlock;
        }
        
        private String buildStyle(String styleValue) {
            if (styleValue == null) {
                return "";
            } else {
                AX2JStyle style = AX2JStyle.getStyle(styleValue);
                if (style == null || style.equals("")) {
                    return "//style=\"" + styleValue + "\"\t//not support\n";
                }
                
                String javaBlock = "";
                javaBlock += "/** " + styleValue + " block **/\n";
                
                //if there is a parent, first handle the parent
                String parent = style.parent;
                if (parent != null && !parent.equals("")) {
                    buildStyle(parent);
                }
                
                styleNode = style.styleNode;
                for (Attribute a : styleNode.getAttributes()) {
                    String attrMethod = translateAttribute(a, node, this);
                    if (!attrMethod.startsWith("//")) {
                        extraHandle(node, a);
                    }
                    javaBlock += attrMethod;
                }
                javaBlock += "/** " + styleValue + " block **/\n";
                
                return javaBlock;
            }
            
        }
    }
}