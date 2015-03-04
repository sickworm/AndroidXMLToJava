package com.excelsecu.androidx2j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;

import android.content.Context;

/**
 * Translate Android XML layout resources to Java method block.
 * @author ch
 *
 */
public class LayoutTranslator extends BaseTranslator {
    private static List<String> idList = new ArrayList<String>();
    
    public LayoutTranslator(File file) {
        super(file);
        AX2JNode.resetOrder();
    }
    
    @Override
    protected void preTranslateNode(AX2JCodeBlock codeBlock, AX2JNode node) {
        super.preTranslateNode(codeBlock, node);

        addImport(Context.class.getName());
        //include label
        String newMethod = "";
        if (node.getLabelName().equals("include")) {
            String layout = node.attributeValue("layout");
            layout = layout.substring(layout.indexOf('/') + 1);
            newMethod = "View " + node.getObjectName() + " = " +
                    Config.PACKAGE_NAME + ".layout." + layout + ".get(context);\n";
            codeBlock.add(newMethod);
            return;
        }
        
    	newMethod = node.getType().getSimpleName() + " " + node.getObjectName() + " = new " + 
    			node.getLabelName() + "(" + node.constructorParams() + ");\n";
        codeBlock.add(newMethod);
        
        String parentName = getParentName(node);
        String params = parentName + ".LayoutParams.WRAP_CONTENT";
        codeBlock.add(parentName + ".LayoutParams " +
        		getLayoutParamsName(node.getObjectName()) + " =\n\t\tnew " + parentName +
        		".LayoutParams(" + params + ", " + params + ");\n");
        
//      String id = attrValue.substring(attrValue.indexOf('/') + 1);
//      if (!Utils.hasString(idList, id)) {
//          idList.add(id);
//      }
    }
    
    @Override
	protected void translatingNode(AX2JCodeBlock codeBlock, AX2JNode node) {
        if (node.getLabelName().equals("include")) {
        	return;
        }
        
		super.translatingNode(codeBlock, node);
	}
    
    @Override
    protected void afterTranslateNode(AX2JCodeBlock codeBlock, AX2JNode node) {
        super.afterTranslateNode(codeBlock, node);     

        if (!node.getLabelName().equals("include")) {
	        String setLayoutParamsMethod = node.getObjectName() + ".setLayoutParams(" + getLayoutParamsName(node.getObjectName()) + ");\n";
	        codeBlock.add(setLayoutParamsMethod);
        }
        
        AX2JNode parent = node.getParent();
        if (parent != null) {
            String addViewMethod = parent.getObjectName() + ".addView(" + node.getObjectName() + ");\n";
            codeBlock.add(addViewMethod);
        }
    }
    
    @Override
	protected void translateAttribute(AX2JCodeBlock codeBlock,
			Attribute attribute) {
    	String name = attribute.getQualifiedName();
    	String value = attribute.getValue();
		if (name.equals("android:layout_width") ||
				name.equals("android:layout_height")) {
			//default value
			if (value.equals("wrap_content")) {
				return;
			}
		}
		super.translateAttribute(codeBlock, attribute);
	}
    
	public static List<String> getIdList() {
        return idList;
    }

	public static String getLayoutParamsName(String objectName) {
    	return objectName + "_LayoutParams";
    }
    
//
//    /**
//     * Handle the method not exists in the attr-to-method map.
//     * @author ch
//     *
//     */
//    public class SpecialTranslator {
//        private AX2JNode node;
//        private String parentName;
//        private String layoutParamName;
//        private String width;
//        private String height;
//        
//        private boolean margin = false;
//        private boolean padding = false;
//        private boolean drawable = false;
//        
//        private AX2JNode styleNode = null;
//        
//        public SpecialTranslator(AX2JNode node) {
//            this.node = node;
//            parentName = Utils.getParentName(node);
//            addImport(Utils.matchClass(parentName).getName());
//            layoutParamName = node.getObjectName() + "Params";
//        }
//        
//        public String translate(Attribute attr) throws AX2JException {
//            String attrName = attr.getQualifiedName();
//            String javaBlock = "";
//            
//            
//            //textAppearance
//            if (attrName.equals("android:textAppearance") && node.getLabelName().equals("TextView")) {
//                String style = AX2JStyle.getStyle(attr.getValue()).name;
//                style = style.replace('.', '_');
//                style = "android.R.style." + style;
//                return node.getObjectName() + ".setTextAppearance(context, " + style + ");\n";
//            }
//            
//            //padding
//            if (attrName.equals("android:paddingBottom") || attrName.equals("android:paddingTop") ||
//                    attrName.equals("android:paddingLeft") || attrName.equals("android:paddingRight") ||
//                    attrName.equals("android:paddingStart") || attrName.equals("android:paddingEnd") ||
//                    attrName.equals("android:padding")) {
//                if (!padding) {
//                    if (attrName.equals("android:padding")) {
//                        String attrValue = translateValue(attr);
//                        javaBlock = node.getObjectName() + ".setPadding(" +
//                                attrValue + ", " + attrValue + ", " +
//                                attrValue + ", " + attrValue + ");\n";
//                    } else {
//                        Attribute attrTop = findAttrByName("android:paddingTop");
//                        Attribute attrBottom = findAttrByName("android:paddingBottom");
//                        Attribute attrStart = findAttrByName("android:paddingStart");
//                        Attribute attrEnd = findAttrByName("android:paddingEnd");
//                        Attribute attrLeft = findAttrByName("android:paddingLeft");
//                        Attribute attrRight = findAttrByName("android:paddingRight");
//                        String top = (attrTop == null)? "0" : translateValue(attrTop);
//                        String bottom = (attrBottom == null)? "0" : translateValue(attrBottom);
//                        String start = (attrStart == null)? "0" : translateValue(attrRight);
//                        String end = (attrEnd == null)? "0" : translateValue(attrBottom);
//                        String left = (attrLeft == null)? "0" : translateValue(attrLeft);
//                        String right = (attrRight == null)? "0" : translateValue(attrRight);
//                        if (left != null || attrBottom != null) {
//                            javaBlock += node.getObjectName() + ".setPadding(" +
//                                    left + ", " + top + ", " +
//                                    right + ", " + bottom + ");\n";
//                        }
//                        //padding should not be set in two ways, It may have translate problem
//                        //here because of the order
//                        //or I should add some warning
//                        if (attrStart != null || attrEnd != null) {
//                            javaBlock += node.getObjectName() + ".setPaddingRelative(" +
//                                    start + ", " + top + ", " +
//                                    end + ", " + bottom + ");\n";
//                        }
//                    }
//                    
//                    padding = true;
//                    return javaBlock;
//                }
//                return "";
//            }
//            
//            //drawable direction
//            if (attrName.equals("android:drawableBottom") || attrName.equals("android:drawableTop") ||
//                    attrName.equals("android:drawableLeft") || attrName.equals("android:drawableRight") ||
//                    attrName.equals("android:drawableStart") || attrName.equals("android:drawableEnd")) {
//                if (!drawable) {
//                    Attribute attrTop = findAttrByName("android:drawableTop");
//                    Attribute attrBottom = findAttrByName("android:drawableBottom");
//                    Attribute attrStart = findAttrByName("android:drawableStart");
//                    Attribute attrEnd = findAttrByName("android:drawableEnd");
//                    Attribute attrLeft = findAttrByName("android:drawableLeft");
//                    Attribute attrRight = findAttrByName("android:drawableRight");
//                    String top = (attrTop == null)? "null" : translateValue(attrTop);
//                    String bottom = (attrBottom == null)? "null" : translateValue(attrBottom);
//                    String start = (attrStart == null)? "null" : translateValue(attrRight);
//                    String end = (attrEnd == null)? "null" : translateValue(attrBottom);
//                    String left = (attrLeft == null)? "null" : translateValue(attrLeft);
//                    String right = (attrRight == null)? "null" : translateValue(attrRight);
//                    if (left != null || attrBottom != null) {
//                        javaBlock += node.getObjectName() + ".setCompoundDrawablesWithIntrinsicBounds(" +
//                                left + ", " + top + ", " +
//                                right + ", " + bottom + ");\n";
//                    }
//                    //drawable direction should not be set in two ways, It may have translate problem
//                    //here because of the order
//                    //or I should add some warning
//                    if (attrStart != null || attrEnd != null) {
//                        javaBlock += node.getObjectName() + ".setCompoundDrawablesRelativeWithIntrinsicBounds(" +
//                                start + ", " + top + ", " +
//                                end + ", " + bottom + ");\n";
//                    }
//                    
//                    drawable = true;
//                    return javaBlock;
//                }
//                return "";
//            }
//            
//            
//            //RelativeLayout rules
//            String rule = Utils.findRule(attrName);
//            if (rule != null) {
//                if (!parentName.equals("RelativeLayout")) {
//                    throw new AX2JException(AX2JException.METHOD_NOT_FOUND);
//                }
//                rule = "RelativeLayout." + rule;
//                String ruleValue = attr.getValue();
//                String className = Utils.getParentName(node);
//                //false means nothing
//                if (ruleValue.equals("false")) {
//                    return "";
//                }
//                if (ruleValue.equals("true")) {
//                    ruleValue = className + ".TRUE";
//                } else if (ruleValue.startsWith("@id/.*") ||
//                        ruleValue.startsWith("@+id/")){
//                    ruleValue = ruleValue.substring(ruleValue.indexOf('/') + 1);
//                    ruleValue = Config.R_CLASS + ".id." + ruleValue;
//                } else {
//                    throw new AX2JException(AX2JException.ATTRIBUTE_VALUE_ERROR, ruleValue);
//                }
//                javaBlock += layoutParamName + ".addRule(" + rule + ", " + ruleValue + ");\n";
//                
//                return javaBlock;
//            }
//            
//            throw new AX2JException(AX2JException.METHOD_NOT_FOUND);
//        }
//        
//        private Attribute findAttrByName(String attrName) {
//            Attribute attrStyle = null;
//            if (styleNode != null) {
//                attrStyle = styleNode.findAttrByName(attrName);
//            }
//            Attribute attrNode = null;
//            attrNode = node.findAttrByName(attrName);
//            
//            //attrNode has priority
//            if (attrNode != null) {
//                return attrNode;
//            } else {
//                return attrStyle;
//            }
//        }
//        
//        public String setLayoutParams() {
//            return node.getObjectName() + ".setLayoutParams(" + layoutParamName + ");\n";
//        }
//        
//        public String prebuild() {
//            String javaBlock = "";
//            javaBlock += buildLayoutParams();
//            javaBlock += buildStyle();
//            return javaBlock;
//        }
//        
//        /**
//         * Initialize LayoutParams object
//         * @return
//         */
//        private String buildLayoutParams() {
//            String javaBlock;
//            Attribute attrWidth = findAttrByName("android:layout_width");
//            Attribute attrHeight = findAttrByName("android:layout_height");
//            width = (attrWidth == null)?
//                    parentName + ".LayoutParams.WRAP_CONTENT" : translateValue(attrWidth);
//            height = (attrHeight == null)?
//                    parentName + ".LayoutParams.WRAP_CONTENT" : translateValue(attrHeight);
//            String paramValue = width + ", " + height;
//            javaBlock = parentName + ".LayoutParams " + layoutParamName +
//                    " =\n\t\tnew " + parentName + ".LayoutParams(" + paramValue + ");\n";
//            return javaBlock;
//        }
//        
//        /**
//         * Translate style to Java block
//         * @return Java code
//         */
//        private String buildStyle() {
//            String styleValue = node.attributeValue("style");
//            if (styleValue == null) {
//                return "";
//            }
//            
//            String javaBlock = "";
//            try {
//                javaBlock += "/** " + styleValue + " block **/\n";
//                List<Attribute> styleAttrList = new ArrayList<Attribute>();
//                buildStyleAttrList(styleValue, styleAttrList);
//                for (Attribute a : styleAttrList) {
//                    String attrMethod = translateAttribute(a, node, this);
//                    if (!attrMethod.startsWith("//")) {
//                        extraHandle(node, a);
//                    }
//                    javaBlock += attrMethod;
//                }
//                javaBlock += "/** " + styleValue + " block **/\n";
//            } catch (AX2JException e) {
//                javaBlock = "// style=\"" + styleValue + "\"not support\n";
//            }
//            return javaBlock;
//        }
//        
//        private void buildStyleAttrList(String styleValue, List<Attribute> styleAttrList) {
//            AX2JStyle style = AX2JStyle.getStyle(styleValue);
//            
//            //if there is a parent, first handle the parent
//            String parent = style.parent;
//            if (parent != null && !parent.equals("")) {
//                buildStyleAttrList(parent, styleAttrList);
//            }
//            
//            //remove the same attribute, the new replace the old
//            for (int i = 0; i < styleAttrList.size(); i++) {
//                int j = 0;
//                for (j = 0; j < style.attrList.size(); j++) {
//                    if (styleAttrList.get(i).getQualifiedName().equals(
//                            style.attrList.get(j).getQualifiedName())) {
//                        break;
//                    }
//                }
//                if (j != style.attrList.size()) {
//                    styleAttrList.remove(i);
//                }
//            }
//            
//            styleAttrList.addAll(style.attrList);
//        }
//    }
}