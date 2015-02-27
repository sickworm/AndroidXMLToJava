package com.excelsecu.androidx2j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;

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
    /** translate **/
    private AX2JNode root = null;
    private File file = null;
    private List<String> importList = new ArrayList<String>();
    
    public BaseTranslator(File file) {
        this.file = file;
        if (Utils.getFileExtension(file).equals("xml")) {
            init();
        }
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
        return printCodeBlock(translate(getRoot()));
    }
    
    protected List<AX2JCodeBlock> translate(AX2JNode root) throws AX2JException {
        List<AX2JCodeBlock> codeBlockList = new ArrayList<AX2JCodeBlock>();
        codeBlockList.add(translateNode(root));
        for (AX2JNode child : root.getChildren()) {
            List<AX2JCodeBlock> childCodeBlockList = translate(child);
            codeBlockList.addAll(childCodeBlockList);
        }
        
        return codeBlockList;
    }
    
    private String printCodeBlock(List<AX2JCodeBlock> codeBlockList) {
        List<String> codeList = new ArrayList<String>();
        for (AX2JCodeBlock codeBlock : codeBlockList) {
            codeBlock.toString(AX2JCodeBlock.PRIORITY_FIRST);
        }
    }
    
    protected void preTranslateNode(AX2JCodeBlock codeBlock) {
    }
    
    protected AX2JCodeBlock translateNode(AX2JNode node) {
        AX2JCodeBlock codeBlock = new AX2JCodeBlock(node.getType(), node.getObjectName());
        
        preTranslateNode(codeBlock);
        
        for (Attribute attribute : node.getAttributes()) {
            translateAttribute(codeBlock, attribute);
        }
        
        afterTranslateNode(codeBlock);
        
        return codeBlock;
    }
    
    protected void afterTranslateNode(AX2JCodeBlock codeBlock) {
    }
    
    protected void translateAttribute(AX2JCodeBlock codeBlock, Attribute attribute) {
        Class<?> type = codeBlock.getType();
        while (true) {
            AX2JClassTranslator translator = map.get(type);
            if (translator == null) {
                codeBlock.add("//" + attribute.asXML() + "\t//not support\n");
                break;
            } else {
                try {
                    translator.translate(codeBlock, attribute);
                    break;
                } catch(AX2JException e) {
                    type = type.getSuperclass();
                }
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
        return importList;
    }

    public void setImportList(List<String> importList) {
        this.importList = importList;
    }
    
}
