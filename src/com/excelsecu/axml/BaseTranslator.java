package com.excelsecu.axml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;

import com.excelsecu.axml.dbbuilder.AndroidDocConverter;

import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Super class for drawable and layout resources. Translate a AXMLNode to java block.
 * @author ch
 *
 */
public class BaseTranslator {
    private static List<String> idList = new ArrayList<String>();
    private static HashMap<String, String> map = null;
    private String extraMethod = "";
    private List<String> importList = new ArrayList<String>();
    
    private AXMLNode root = null;
    private File file = null;
    
    /** record of {@link LayoutTranslator#extraHandle(String attrName , String attrValue)} **/
    private boolean scale = false;
    /** record of {@link LayoutTranslator#extraHandle(String attrName , String attrValue)} **/
    private boolean resources = false;

    public BaseTranslator(File file) {
        this.file = file;
        if (Utils.getFileExtension(file).equals("xml")) {
        	init();
        }
    }
    
	public BaseTranslator(AXMLNode root) {
		this.root = root;
        map = AndroidDocConverter.getMap();
        init();
	}
	
	protected void init() {
		if (root == null) {
	        AXMLParser parser = new AXMLParser(file);
	        root = parser.parse();
		}
	}
	
	public String translate() {
        return translate(getRoot());
	}
	
	protected String translate(AXMLNode node) {
        String javaBlock = "";
        String nodeJavaBlock = translateNode(node);
        javaBlock += nodeJavaBlock;
        for (AXMLNode n : node.getChildren()) {
            javaBlock += translate(n);
        }
        return javaBlock;
    }	
	
	protected String translateNode(AXMLNode node) {
	    String javaBlock = "";
        for (Attribute a : node.getAttributes()) {
            javaBlock += translateAttribute(a, node);
        }
        return javaBlock;
	}

    protected String translateAttribute(Attribute attr, AXMLNode node) throws AXMLException {
        String attrMethod = "";
        String methodName = transAttrToMethod(attr, node.getType());
        String methodValue = translateValue(attr);
        attrMethod = methodName + "(" + methodValue + ")";
        attrMethod = node.getObjectName() + "." + attrMethod + ";\n";
        return attrMethod;
    }
            
	/**
	 * Translate XML element's attribute to Android method without parameters.
	 * @param attrName	The name of attribute.
	 * @return Android method matches the attribute without parameters.
	 */
	protected String transAttrToMethod(Attribute a, Class<?> type) {
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
        //change the method if necessary.
        if (methodName.equals("setBackground(Drawable)")) {
            if (attrValue.matches("#[0-9a-fA-F]+") ||
                    attrValue.matches("@android:color/.+") ||
                    attrValue.matches("@color/.+")) {
                methodName = "setBackgroundColor(int)";
            } else if (Config.API_LEVEL <= 8) {
            	methodName = "setBackgroundDrawable(Drawable)";
            }
        }

        methodName = methodName.substring(0, methodName.indexOf("("));
        return methodName;
	}
	
	/**
	 * Translate a XML attribute's value to a Java method's value.
	 * @param attr the attribute to be translated
	 * @return the value after translating
	 */
	protected String translateValue(Attribute attr) {
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
        } else if (value.matches("[0-9]+\\.[0-9]+") &&
                !(attrName.contains("text") || attrName.contains("hint"))) {
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
            value = "R.string." + value;
            value = Config.RESOURCES_NAME + ".getString(" + value + ")";
        } else if (attrName.equals("android:text") ||
                attrName.equals("android:hint")) {
            value = "\"" + value + "\"";
        }
	    
	    //color
	    else if (value.matches("#[0-9a-fA-F]+")) {
	        value = "Color.parseColor(\"" + value + "\")";
	    } else if (value.matches("@android:color/.+")) {
	        value = value.substring(value.indexOf('/') + 1);
	        value = value.toUpperCase();
            value = "Color." + value;
        } else if (value.matches("@color/.+")) {
            value = value.substring(value.indexOf('/') + 1);
            value = "R.color." + value;
            value = Config.RESOURCES_NAME + ".getColor(" + value + ")";
        }
	    
	    //visibility
	    else if (value.equals("gone") || value.equals("visibile") ||
	            value.equals("invisibile")) {
	        value = "View." + value.toUpperCase();
	    }
	    
	    //drawable
        else if (value.startsWith("@drawable/")) {
            value = value.substring(value.indexOf('/') + 1);
            value = "R.drawable." + value;
            //ColorStateList is not a drawable, should use another method
            if (attrName.contains("Color") ||
                    attrName.contains("TintList")) {
                value = "resources.getColorStateList(" + value + ")";
            } else {
                value = "resources.getDrawable(" + value + ")";
            }
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
            value = Utils.prefixParams(value, "InputType");
        } else if (attrName.equals("android:ellipsize")) {
            value = value.toUpperCase();
            value = "TextUtils.TruncateAt." + value;
        }
	    
        return value;
	}
	
	/**
	 * Find out what extra constant, id or import need to be added.
	 * @param attrName
	 * @param attrValue
	 */
	protected void extraHandle(AXMLNode node, Attribute attr) {
		addImport(node.getType().getName());
        
        String attrValue = attr.getValue();
        String attrName = attr.getQualifiedName();
        if (attrValue.matches("[0-9]+dp")) {
            if (!scale) {
                extraMethod += "final float scale = context.getResources().getDisplayMetrics().density;\n";
                scale = true;
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
        } else if (attrValue.matches("#[0-9a-fA-F]+") ||
        		attrValue.matches("@android:color/.+")) {
            addImport(Color.class.getName());
        } else if (attrName.equals("android:gravity") ||
                attr.getQualifiedName().equals("android:layout_gravity")) {
            addImport(Gravity.class.getName());
        } else if (attrName.equals("android:password")) {
            addImport(PasswordTransformationMethod.class.getName());
        } else if (attrName.equals("android:singleLine")) {
            addImport(SingleLineTransformationMethod.class.getName());
        } else if (attrName.equals("android:inputType")) {
            addImport(InputType.class.getName());
        } else if (attrName.equals("android:ellipsize")) {
            addImport(TextUtils.class.getName());
        }
        
        else if (attrValue.startsWith("@drawable/") ||
                attrValue.startsWith("@color/") ||
                attrValue.startsWith("@string/")) {
            addImport(Config.PACKAGE_NAME + ".AXMLResources");
            addImport(Config.PACKAGE_NAME + ".R");
            if (!resources) {
                extraMethod += "AXMLResources " + Config.RESOURCES_NAME + " = new AXMLResources(context);\n";
                resources = true;
            }
        }
	}

	/**
	 *  Add the class to the import list. If already exists, ignore. 
	 *  @param className the class try to be added in import list
	 */
    protected void addImport(String className) {
    	if (className == null || className.equals("") ||
    			className.equals(Void.class.getName())) {
    		return;
    	}
        if (!Utils.hasString(importList, className)) {
            importList.add(className);
        }
    }
    
    public AXMLNode getRoot() {
    	return root;
    }
    
    public Class<?> getType() {
    	return root.getType();
    }
    
    public File getFile() {
    	return file;
    }
    
    public String getExtraMethod() {
        return extraMethod;
    }
    
    public List<String> getImportList() {
        return importList;
    }

    public void setImportList(List<String> importList) {
        this.importList = importList;
    }
    
    public static List<String> getIdList() {
        return idList;
    }
}
