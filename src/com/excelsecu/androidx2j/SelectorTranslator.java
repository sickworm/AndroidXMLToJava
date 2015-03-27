package com.excelsecu.androidx2j;
import org.dom4j.Attribute;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class SelectorTranslator extends BaseTranslator {

    public SelectorTranslator(AX2JNode node) {
        super(node);
        AX2JNode.resetOrder();
    }

    public String translate() {
        AX2JCodeBlock codeBlock = new AX2JCodeBlock(getRoot().getType(), getRoot().getObjectName());
        if (getType() == ColorStateList.class) {
            codeBlock = translateToColorStateList();
        } else if (getType() == StateListDrawable.class) {
            codeBlock = translateToStateListDrawable();
        }
        addCodeBlock(codeBlock);

        return printCodeBlockList();
    }

    private AX2JCodeBlock translateToColorStateList() {
        addImport(Context.class);
        addImport(ColorStateList.class);
        AX2JCodeBlock codeBlock = new AX2JCodeBlock(ColorStateList.class, getRoot().getObjectName());
        String stateSetList = "";
        String colorList = "";
        for (AX2JNode n : getRoot().getChildren()) {
            if (!n.getLabelName().equals("item")) {
                continue;
            }
            String stateSet = "";
            String color = "";
            for (Attribute a : n.getAttributes()) {
                String attrName = a.getQualifiedName();
                if (attrName.equals("android:color")) {
                    color = translateValue(codeBlock, a, Integer.class);
                } else {
                    String state = "android.R.attr." + a.getName();
                    if (a.getValue().equals("false")) {
                        state = "-" + state;
                    }
                    if (stateSet.equals("")) {
                        stateSet = state;
                    } else {
                        stateSet += ", " + state;
                    }
                }
            }
            if (colorList.equals("")) {
                colorList = color;
            } else {
                colorList += ", " + color;
            }
            stateSetList += "\n\t{" + stateSet + "},";
        }
        //remove comma
        stateSetList = stateSetList.substring(0, stateSetList.length() - 1);

        codeBlock.add("int[][] stateSet" + " = new int[][] {" + stateSetList + "};\n");
        codeBlock.add("int[] colorSet" + " = new int[] {" + colorList + "};\n");
        codeBlock.add("ColorStateList colorStateList = new ColorStateList(stateSet, colorSet);\n");
        return codeBlock;
    }

    private AX2JCodeBlock translateToStateListDrawable() {
        addImport(Context.class);
        addImport(StateListDrawable.class);
        AX2JCodeBlock codeBlock = new AX2JCodeBlock(ColorStateList.class, getRoot().getObjectName());
        int num = 0;

        codeBlock.add("StateListDrawable stateListDrawable = new StateListDrawable();\n");
        for (AX2JNode node : getRoot().getChildren()) {
            if (!node.getLabelName().equals("item")) {
                continue;
            }
            String stateSet = "";
            String drawable = "";
            for (Attribute attribute : node.getAttributes()) {
                String attrName = attribute.getQualifiedName();
                if (attrName.equals("android:drawable")) {
                    drawable = translateValue(codeBlock, attribute, Drawable.class);
                } else {
                    String state = "android.R.attr." + attribute.getName();
                    if (attribute.getValue().equals("false")) {
                        state = "-" + state;
                    }
                    if (stateSet.equals("")) {
                        stateSet = state;
                    } else {
                        stateSet += ", " + state;
                    }
                }
            }

            String setName = "stateSet" + num;
            codeBlock.add("int[] " + setName + " = new int[] {" + stateSet + "};\n");
            codeBlock.add("stateListDrawable.addState(" + setName + ", " + drawable + ");\n");
            num++;
        }

        return codeBlock;
    }
}