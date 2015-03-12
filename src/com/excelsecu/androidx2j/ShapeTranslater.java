package com.excelsecu.androidx2j;

import org.dom4j.Attribute;

import com.excelsecu.androidx2j.AX2JCodeBlock.AX2JCode;

import android.graphics.drawable.GradientDrawable;

public class ShapeTranslater extends BaseTranslator {
    public final static String[] ORIENTATION = new String[] {"RIGHT_LEFT", "BR_TL",
            "BOTTOM_TOP", "BL_TR", "LEFT_RIGHT", "TL_BR", "TOP_BOTTOM", "TR_BL"};
    
    public ShapeTranslater(AX2JNode root) {
        super(root);
        addImport(GradientDrawable.class.getName());
    }
    
    @Override
    public String translate() {
        AX2JCodeBlock codeBlock = new AX2JCodeBlock(GradientDrawable.class, getRoot().getObjectName());
        for (AX2JNode node : getRoot().getChildren()) {
        	if (node.getLabelName().equals("gradient")) {
        	    String orientation;
        		Attribute attribute = node.findAttrByName("android:angle");
        		orientation = (attribute == null)? "Orientation.TOP_BOTTOM" : translateValue(codeBlock, attribute, Integer.class);
        		codeBlock.add("GradientDrawable " + getRoot().getObjectName() + " = new GradientDrawable(" +
        		        orientation + ", null);\n", AX2JCode.PRIORITY_SECOND);
        	} else if(node.getLabelName().equals("solid")) {
        		codeBlock.add("GradientDrawable " + getRoot().getObjectName() + " = new GradientDrawable();\n", AX2JCode.PRIORITY_SECOND);
        	}
        }
        addCodeBlock(codeBlock);

        return super.translate();
    }

	@Override
    protected String translateValue(AX2JCodeBlock codeBlock,
            Attribute attribute, Class<?> argType) {
        String name = attribute.getQualifiedName();
        String value = attribute.getValue();
        
        //shape
        if (name.equals("android:type")) {
            value = "GradientDrawable." + value.toUpperCase() + "_GRADIENT";
        } else if (name.equals("android:shape")) {
            value = "GradientDrawable." + value.toUpperCase();
        } else if (name.equals("android:angle")) {
            int ordinal = Integer.parseInt(value);
            ordinal = ordinal / 45;
            if (ordinal < 0) {
                ordinal += 8;
            }
            value = "GradientDrawable.Orientation." + ORIENTATION[ordinal];
        }
        
        //nothing change
        if (value.equals(attribute.getValue())) {
            return super.translateValue(codeBlock, attribute, argType);
        } else {
            return value;
        }
    }

    @Override
	protected void translateAttribute(AX2JCodeBlock codeBlock,
			Attribute attribute) {
        String name = attribute.getQualifiedName();
    	if (name.equals("android:centerX") || name.equals("android:centerY")) {
            codeBlock.add("//Attention: GradientDrawable.setGradientCenter doesn't support setting center when the type is linear, but XML does. Weird\n");
    	} else if (name.equals("android:angle")) {
            return;
        }
        
		super.translateAttribute(codeBlock, attribute);
	}
}
