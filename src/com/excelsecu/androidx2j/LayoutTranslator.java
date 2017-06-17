package com.excelsecu.androidx2j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;

import com.excelsecu.androidx2j.AX2JCodeBlock.AX2JCode;

import android.content.Context;
import android.view.View;

/**
 * Translate Android XML layout resources to Java method block.
 * @author sickworm
 *
 */
public class LayoutTranslator extends BaseTranslator {
    private static List<String> idList = new ArrayList<String>();

    public LayoutTranslator(File file) {
        super(file);
        init();
    }

    public LayoutTranslator(String content) {
        super(content);
        init();
    }
    
    private void init() {
        addImport(Context.class);
    }

    @Override
    protected void preTranslateNode(AX2JCodeBlock codeBlock, AX2JNode node) {
        super.preTranslateNode(codeBlock, node);

        // include label
        String newMethod = "";
        if (node.getLabelName().equals("include")) {
        	if (Config.IS_CONTENT_TRANSLATE) {
                codeBlock.add("//" + node.asXML() + "\t//not support\n", AX2JCode.PRIORITY_DEFAULT);
                return;
        	}
            String layout = node.attributeValue("layout");
            layout = layout.substring(layout.indexOf('/') + 1);
            newMethod = "View " + node.getObjectName() + " = " +
                    Config.PACKAGE_NAME + ".layout." + layout + ".get(context);\n";
            codeBlock.add(newMethod, AX2JCode.PRIORITY_SECOND);
            codeBlock.addImport(View.class);
            return;
        }

    	newMethod = node.getType().getSimpleName() + " " + node.getObjectName() + " = new " +
    			node.getLabelName() + "(" + node.constructorParams() + ");\n";
        codeBlock.add(newMethod, AX2JCode.PRIORITY_SECOND);

        String parentName = getParentType(node).getSimpleName();
        String params = parentName + ".LayoutParams.WRAP_CONTENT";
        String paramsMethod = parentName + ".LayoutParams " +
        		getLayoutParamsName(node.getObjectName()) + " =\n\t\tnew " + parentName +
        		".LayoutParams(" + params + ", " + params + ");\n";
        codeBlock.add(paramsMethod, AX2JCode.PRIORITY_SECOND);
        addImport(getParentType(node));
    }

    @Override
	protected void translatingNode(AX2JCodeBlock codeBlock, AX2JNode node) {
    	// <include> handled in preTranslateNode()
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
	        codeBlock.add(setLayoutParamsMethod, AX2JCode.PRIORITY_SECONDLY_LAST);
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

    	//not strict enough, need to put this into AX2JClassTranslator
        if (value.startsWith("@+id/")) {
        	String id = value.substring(value.indexOf('/') + 1);
        	if (!Utils.hasString(idList, id)) {
        		idList.add(id);
        	}
        }

		if (name.equals("android:layout_width") ||
				name.equals("android:layout_height")) {
			//default value
			if (value.equals("wrap_content")) {
				return;
			}
		} else if (name.equals("style")) {
			buildStyle(codeBlock, value);
			return;
		}

		super.translateAttribute(codeBlock, attribute);
	}

    /**
     * Translate style to Java block
     * @return Java code
     */
    private void buildStyle(AX2JCodeBlock codeBlock, String styleValue) {
    	try {
    		codeBlock.add("/** <style=" + styleValue + "> **/\n", AX2JCode.PRIORITY_SECOND);
    		List<Attribute> styleAttrList = new ArrayList<Attribute>();
    		buildStyleAttrList(styleValue, styleAttrList);
    		for (Attribute attribute : styleAttrList) {
    			translateAttribute(codeBlock, attribute, AX2JCode.PRIORITY_SECOND);
    		}
    		codeBlock.add("/** </style=" + styleValue + "> **/\n", AX2JCode.PRIORITY_SECOND);
    	} catch (AX2JException e) {
    		codeBlock.add("// style=\"" + styleValue + "\"not support\n", AX2JCode.PRIORITY_SECOND);
    	}
    }

    private void buildStyleAttrList(String styleValue, List<Attribute> styleAttrList) {
    	AX2JStyle style = AX2JStyle.getStyle(styleValue);

    	//if there is a parent, first handle the parent
    	String parent = style.parent;
    	if (parent != null && !parent.equals("")) {
    		buildStyleAttrList(parent, styleAttrList);
    	}

    	//remove the same attribute, the new replace the old
    	for (int i = 0; i < styleAttrList.size(); i++) {
    		int j = 0;
    		for (j = 0; j < style.attrList.size(); j++) {
    			if (styleAttrList.get(i).getQualifiedName().equals(
    					style.attrList.get(j).getQualifiedName())) {
    				break;
    			}
    		}
    		if (j != style.attrList.size()) {
    			styleAttrList.remove(i);
    		}
    	}

    	styleAttrList.addAll(style.attrList);
    }

	public static List<String> getIdList() {
		return idList;
	}

	public static String getLayoutParamsName(String objectName) {
      	return objectName + "_LayoutParams";
	}
}