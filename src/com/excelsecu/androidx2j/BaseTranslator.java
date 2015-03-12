package com.excelsecu.androidx2j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Attribute;

import com.excelsecu.androidx2j.AX2JCodeBlock.AX2JCode;

import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Super class for drawable and layout resources. Translate a AX2JNode to java block.
 * @author ch
 *
 */
public class BaseTranslator {
    private static AX2JTranslatorMap map = AX2JTranslatorMap.getInstance();
    private List<AX2JCodeBlock> codeBlockList = new ArrayList<AX2JCodeBlock>();
    /** translate **/
    private AX2JNode root = null;
    private File file = null;
    private List<String> importList = new ArrayList<String>();
    
    public BaseTranslator(File file) {
        this.file = file;
        if (Utils.getFileExtension(file).equals("xml")) {
            init();
        }
        AX2JNode.resetOrder();
    }
    
    public BaseTranslator(AX2JNode root) {
        this.root = root;
    }
    
    protected void init() {
        if (root == null) {
            AX2JParser parser = new AX2JParser(file);
            root = parser.parse();
            root.setObjectName(file.getName().substring(0, file.getName().indexOf('.')));
        }
    }
    
    public String translate() {
        translate(getRoot());
        return printCodeBlockList(codeBlockList);
    }
    
    protected void translate(AX2JNode root) throws AX2JException {
        AX2JCodeBlock codeBlock = translateNode(root);
        
        List<String> subImportList = codeBlock.getImportList();
        for (String importItem : subImportList) {
            addImport(importItem);
        }
        codeBlockList.add(codeBlock);
        
        for (AX2JNode child : root.getChildren()) {
            translate(child);
        }
    }
    
    private String printCodeBlockList(List<AX2JCodeBlock> codeBlockList) {
        String topBlock = "";
        List<AX2JCode> topCodeList = new ArrayList<AX2JCode>();
        for (AX2JCodeBlock codeBlock : codeBlockList) {
            List<AX2JCode> subCodeList = codeBlock.getCode(AX2JCode.PRIORITY_FIRST);
            for (int i = 0; i < subCodeList.size(); i++) {
                int j = 0;
                for (j = 0; j < topCodeList.size(); j++) {
                    if (subCodeList.get(i).isDuplicateMethod(topCodeList.get(j))) {
                        break;
                    }
                }
                if (j != topCodeList.size()) {
                    topCodeList.remove(j);
                }
            }
            topCodeList.addAll(subCodeList);
        }
        for (AX2JCode code : topCodeList) {
            topBlock += code.toString();
        }
        if (!topBlock.equals("")) {
            topBlock += "\n";
        }
        
        String normalBlock = "";
        for (AX2JCodeBlock codeBlock : codeBlockList) {
            String codeBlockString = codeBlock.toString();
            if (!codeBlock.toString().equals("")) {
                normalBlock += codeBlockString;
                //GradientDrawable just has one object
                if (!codeBlock.getType().equals(GradientDrawable.class)) {
                    normalBlock += "\n";
                }
            }
        }
        
        String bottomBlock = "";
        List<AX2JCode> bottomCodeList = new ArrayList<AX2JCode>();
        for (AX2JCodeBlock codeBlock : codeBlockList) {
            List<AX2JCode> subCodeList = codeBlock.getCode(AX2JCode.PRIORITY_LAST);
            for (int i = 0; i < bottomCodeList.size(); i++) {
                int j = 0;
                for (j = 0; j < subCodeList.size(); j++) {
                    if (bottomCodeList.get(i).isDuplicateMethod(subCodeList.get(j))) {
                        break;
                    }
                }
                if (j != subCodeList.size()) {
                    bottomCodeList.remove(i);
                }
                bottomCodeList.addAll(subCodeList);
            }
        }
        for (AX2JCode code : bottomCodeList) {
            bottomBlock += code.toString();
        }
        
        return topBlock + normalBlock + bottomBlock;
    }
    
    private final AX2JCodeBlock translateNode(AX2JNode node) {
        AX2JCodeBlock codeBlock = new AX2JCodeBlock(node.getType(), node.getObjectName());
        
        preTranslateNode(codeBlock, node);
        
        translatingNode(codeBlock, node);
        
        afterTranslateNode(codeBlock, node);
        
        return codeBlock;
    }
    
    protected void preTranslateNode(AX2JCodeBlock codeBlock, AX2JNode node) {
    }
    
    protected void translatingNode(AX2JCodeBlock codeBlock, AX2JNode node) {
        for (Attribute attribute : node.getAttributes()) {
            translateAttribute(codeBlock, attribute);
        }
    }
    
    protected void afterTranslateNode(AX2JCodeBlock codeBlock, AX2JNode node) {
    }
    
    protected void translateAttribute(AX2JCodeBlock codeBlock, Attribute attribute) {
    	translateAttribute(codeBlock, attribute, 0);
    }
    
    protected void translateAttribute(AX2JCodeBlock codeBlock, Attribute attribute, int priority) {
        Class<?> type = codeBlock.getType();
        while (true) {
            AX2JClassTranslator translator = map.get(type);
            if (translator == null) {
                codeBlock.add("//" + attribute.asXML() + "\t//not support\n", priority);
                break;
            } else {
                try {
                    translator.translate(codeBlock, attribute, priority);
                    break;
                } catch(AX2JException e) {
                    type = type.getSuperclass();
                }
            }
        }
    }
    
    protected String translateValue(AX2JCodeBlock codeBlock, Attribute attribute, Class<?> argType) {
        String value = attribute.getValue();
        Class<?> type = codeBlock.getType();
        
        while (true) {
            AX2JClassTranslator translator = map.get(type);
            if (translator == null) {
                codeBlock.add("//" + attribute.asXML() + "\t//not support\n");
                break;
            } else {
                try {
                    value = translator.translateValue(codeBlock, attribute, argType);
                    break;
                } catch(AX2JException e) {
                    type = type.getSuperclass();
                }
            }
        }
        
        return value;
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

    /**
     * Get the name of parent to build the LayoutParams
     * @param node
     * @return
     */
    public static String getParentName(AX2JNode node) {
        if (node.getParent() == null) {
            List<Attribute> attrList = node.getAttributes();
            for (Attribute a : attrList) {
                if (Config.RULE_MAP.get(a.getQualifiedName()) != null) {
                    return RelativeLayout.class.getSimpleName();
                }
                if (a.getQualifiedName().equals("android:layout_gravity")) {
                    return LinearLayout.class.getSimpleName();
                }
            }
            return ViewGroup.class.getSimpleName();
        }
        return node.getParent().getLabelName();
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
    
    public List<String> getImportList() {
        Collections.sort(importList);
        return importList;
    }

    public void setImportList(List<String> importList) {
        this.importList = importList;
    }
    
    public void addCodeBlock(AX2JCodeBlock codeBlock) {
        codeBlockList.add(codeBlock);
    }
    
}
