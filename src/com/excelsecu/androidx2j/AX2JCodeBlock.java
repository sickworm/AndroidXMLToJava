package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.List;

public class AX2JCodeBlock {
    public static final int PRIORITY_TOP = 1;
    public static final int PRIORITY_SECONDLY_TOP = 2;
    public static final int PRIORITY_THIRDLY_TOP = 3;
    public static final int PRIORITY_NORMAL = 4;
    public static final int PRIORITY_THIRDLY_LAST = 5;
    public static final int PRIORITY_SECONDLY_LAST = 6;
    public static final int PRIORITY_LAST = 7;
    public static final int PRIORITY_DEFAULT = PRIORITY_NORMAL;
    private List<AX2JCode> codeList = new ArrayList<AX2JCode>();
    private String name;
    private Class<?> type;

    public AX2JCodeBlock(Class<?> type, String name) {
        this.name = name;
        this.type = type;
    }
    
    public void add(String code, int priority) {
        codeList.add(new AX2JCode(code, priority));
    }

    
    public void add(String code) {
        codeList.add(new AX2JCode(code));
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
        for (int i = PRIORITY_TOP; i <= PRIORITY_LAST; i++) {
            for (AX2JCode code : codeList) {
                if (code.priority == i) {
                    codeBlock.append(name + "." + code.code);
                }
            }
        }
        
        return codeBlock.toString();
    }
    
    public class AX2JCode {
        public String code;
        public int priority;
        
        public AX2JCode(String code) {
            this.code = code;
            this.priority = PRIORITY_DEFAULT;
        }
        
        public AX2JCode(String code, int priority) {
            this.code = code;
            this.priority = priority;
        }
    }
}
