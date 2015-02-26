package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.List;

public class AX2JCodeBlock {
    public static final int PRIORITY_FIRST = 1;
    public static final int PRIORITY_SECOND = 2;
    public static final int PRIORITY_THIRD = 3;
    public static final int PRIORITY_NORMAL = 4;
    public static final int PRIORITY_THIRDLY_LAST = 5;
    public static final int PRIORITY_SECONDLY_LAST = 6;
    public static final int PRIORITY_LAST = 7;
    public static final int PRIORITY_DEFAULT = PRIORITY_NORMAL;
    private List<List<String>> codeList = new ArrayList<List<String>>();
    private String name;
    private Class<?> type;

    public AX2JCodeBlock(Class<?> type, String name) {
        this.name = name;
        this.type = type;
        for (int i = PRIORITY_FIRST; i <= PRIORITY_LAST; i++) {
        	codeList.add(new ArrayList<String>());
        }
    }
    
    public void add(String code, int priority) {
        codeList.get(priority).add(code);
    }

    
    public void add(String code) {
        codeList.get(PRIORITY_DEFAULT).add(code);
    }
    
    public String getName() {
        return name;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    @Override
    public String toString() {
        StringBuffer codeBlock = new StringBuffer();
        for (List<String> codeListPriority : codeList) {
            for (String code : codeListPriority) {
            	if (code.startsWith("//")) {
            		codeBlock.append(code);
            	} else {
            		codeBlock.append(name + "." + code);
            	}
            }
        }
        
        return codeBlock.toString();
    }
}
