package com.excelsecu.androidx2j;

import org.dom4j.Attribute;

import android.graphics.drawable.GradientDrawable;

public class ShapeTranslater extends BaseTranslator {
    private static final String[] ORIENTATION = new String[] {"RIGHT_LEFT", "BR_TL",
                        "BOTTOM_TOP", "BL_TR", "LEFT_RIGHT", "TL_BR", "TOP_BOTTOM", "TR_BL"};
    
    public ShapeTranslater(AX2JNode root) {
        super(root);
        AX2JNode.resetOrder();
    }
    
    @Override
    public String translate() {
        addImport(GradientDrawable.class.getName());
        String javaBlock = construct();
        javaBlock += super.translate();
        return javaBlock;
    }
    
    public String translateValue(Attribute attr) {
        String attrName = attr.getQualifiedName();
        String value = attr.getValue();
        if (attrName.equals("android:type")) {
            value = "GradientDrawable." + value.toUpperCase() + "_GRADIENT";
        } else if (attrName.equals("android:shape")) {
            value = "GradientDrawable." + value.toUpperCase();
        } else if (attrName.equals("android:angle")) {
            int ordinal = Integer.parseInt(value);
            ordinal = ordinal / 45;
            if (ordinal < 0) {
                ordinal += 8;
            }
            value = "GradientDrawable.Orientation." + ORIENTATION[ordinal];
        }

        //nothing change
        if (value.equals(attr.getValue())) {
            //return super.translateValue(attr);
        	return value;
        } else {
            return value;
        }
    }
    
    private String construct() {
        String javaBlock = "";
        for (AX2JNode n : getRoot().getChildren()) {
            if (n.getLabelName().equals("gradient")) {
                //color
                Attribute attrStartColor = n.findAttrByName("android:startColor");
                Attribute attrCenterColor = n.findAttrByName("android:centerColor");
                Attribute attrEndColor = n.findAttrByName("android:endColor");
                Attribute attrOrientation = n.findAttrByName("android:angle");
                String startColor = attrStartColor == null? "0" : translateValue(attrStartColor);
                String centerColor = attrCenterColor == null? null : translateValue(attrCenterColor);
                String endColor = attrEndColor == null? "0" : translateValue(attrEndColor);
                String orientation = attrOrientation == null? "GradientDrawable.Orientation." + ORIENTATION[0] : translateValue(attrOrientation);
                javaBlock += "int[] colors = new int[] {" + startColor +
                        (centerColor == null? ", " : ", " + centerColor + ", ") +
                        endColor + "};\n";
                javaBlock += "GradientDrawable " + getRoot().getObjectName() + " = new GradientDrawable(" +
                        orientation + ", colors);\n";
                
                //center
                Attribute attrCenterX = n.findAttrByName("android:centerX");
                Attribute attrCenterY = n.findAttrByName("android:centerY");
                if (attrCenterX == null && attrCenterY == null) {
                    continue;
                }
                Attribute attrType = n.findAttrByName("android:type");
                String type = attrType == null? "linear" : attrType.getValue();
                if (type.equals("linear")) {
                    javaBlock += "//Attention GradientDrawable.setGradientCenter doesn't support setting center when the type is linear, but XML does. Weird\n";
                }
                String centerX = attrCenterX == null? "0.5f" : translateValue(attrCenterX);
                String centerY = attrCenterY == null? "0.5f" : translateValue(attrCenterY);
                javaBlock += getRoot().getObjectName() + ".setGradientCenter(" + centerX + ", " + centerY + ");\n";
            } else if (n.getLabelName().equals("solid")) {
                Attribute solidcolor = n.findAttrByName("android:color");
                javaBlock += "GradientDrawable " + getRoot().getObjectName() + " = new GradientDrawable();\n";
                //javaBlock += getRoot().getObjectName() + ".setColor(" + translateAttribute(solidcolor, n) + ");\n";
            } else if (n.getLabelName().equals("stroke")) {
                Attribute attrWidth = n.findAttrByName("android:width");
                Attribute attrColor = n.findAttrByName("android:color");
                Attribute attrDashWidth = n.findAttrByName("android:dashWidth");
                Attribute attrDashGap = n.findAttrByName("android:dashGap");
                String width = attrWidth == null? null : translateValue(attrWidth);
                String color = attrColor == null? null : translateValue(attrColor);
                String dashWidth = attrDashWidth == null? null : translateValue(attrDashWidth);
                String dashGap = attrDashGap == null? null : translateValue(attrDashGap);
                
                //in fact, dash gap and dash width must set both
                if (dashGap == null || dashWidth == null) {
                    javaBlock += getRoot().getObjectName() + ".setStroke(" + width + ", " + color + ");\n";
                } else {
                    javaBlock += getRoot().getObjectName() + ".setStroke(" + width + ", " + color + ", " +
                            dashWidth + "," + dashGap + ");\n";
                }
            } else if (n.getLabelName().equals("corners")) {
                Attribute attrRadius = n.findAttrByName("android:radius");
                if (attrRadius != null) {
                    String radius = translateValue(attrRadius);
                    javaBlock += getRoot().getObjectName() + ".setCornerRadius(" + radius + ");\n";
                }
                Attribute attrTL = n.findAttrByName("android:topLeftRadius");
                Attribute attrTR = n.findAttrByName("android:topRightRadius");
                Attribute attrBR = n.findAttrByName("android:bottomRightRadius");
                Attribute attrBL = n.findAttrByName("android:bottomLeftRadius");
                if (attrTL != null || attrTR != null || attrBR != null || attrBL != null) {
                    continue;
                }
                String stringTL = attrTL == null? "0" : translateValue(attrTL);
                String stringTR = attrTR == null? "0" : translateValue(attrTR);
                String stringBR = attrBR == null? "0" : translateValue(attrBR);
                String stringBL = attrBL == null? "0" : translateValue(attrBL);
                javaBlock += "float[] radii = new float[] {" + stringTL + ", " + stringTL + ", " +
                        stringTR + ", " + stringTR + ", " + stringBR + ", " + stringBR + ", " +
                        stringBL + ", " + stringBL + "};\n";
                javaBlock += getRoot().getObjectName() + ".setCornerRadii(radii);\n";
            }
        }
        return javaBlock;
    }
    
    public class SpecialTranslator {
        private AX2JNode node;
        
        public SpecialTranslator(AX2JNode node) {
            this.node = node;
        }
        
        public String translate(Attribute attr) throws AX2JException {
            if (node.getLabelName().equals("gradient")) {
                return "";
            } else if (node.getLabelName().equals("solid")) {
                return "";
            } else if (node.getLabelName().equals("stroke")) {
                return "";
            }
            throw new AX2JException(AX2JException.METHOD_NOT_FOUND, attr.getQualifiedName());
        }
    }
}
