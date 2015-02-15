package com.excelsecu.androidx2j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;

import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Super class for drawable and layout resources. Translate a AX2JNode to java block.
 * @author ch
 *
 */
public class BaseTranslator {
    private static List<String> idList = new ArrayList<String>();
    private static AX2JTranslatorMap map = null;
    private String extraMethod = "";
    private List<String> importList = new ArrayList<String>();
    
    private AX2JNode root = null;
    private File file = null;
    
    private boolean scale = false;
    private boolean resources = false;
    
    public BaseTranslator(File file) {
        this.file = file;
        if (Utils.getFileExtension(file).equals("xml")) {
        	init();
        }
    }
    
	public BaseTranslator(AX2JNode root) {
		this.root = root;
        map = AX2JTranslatorMap.getInstance();
        init();
	}
	
	protected void init() {
		if (root == null) {
	        AX2JParser parser = new AX2JParser(file);
	        root = parser.parse();
		}
	}
	
	public String translate() {
        return translate(getRoot());
	}
	
	protected String translate(AX2JNode node) {
        String javaBlock = "";
        String nodeJavaBlock = translateNode(node);
        javaBlock += nodeJavaBlock;
        for (AX2JNode n : node.getChildren()) {
            javaBlock += translate(n);
        }
        
        return javaBlock;
    }	
	
	protected String translateNode(AX2JNode node) {
	    String javaBlock = "";
        for (Attribute a : node.getAttributes()) {
            javaBlock += translateAttribute(a, node);
        }
        
        return javaBlock;
	}

    protected String translateAttribute(Attribute attribute, AX2JNode node) throws AX2JException {
        return map.translate(node.getType(), attribute);
    }
	
	/**
	 * Find out what extra constant, id or import need to be added.
	 * @param attrName
	 * @param attrValue
	 */
	protected void extraHandle(AX2JNode node, Attribute attr) {
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
            addImport(Config.PACKAGE_NAME + "." + Config.R_CLASS);
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
            addImport(Config.PACKAGE_NAME + "." + Config.RESOURCES_CLASS);
            addImport(Config.PACKAGE_NAME + "." + Config.R_CLASS);
            if (!resources) {
                extraMethod += Config.RESOURCES_CLASS + " " + Config.RESOURCES_NAME + " = new " + Config.RESOURCES_CLASS + "(context);\n";
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
    
    public AX2JNode getRoot() {
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
