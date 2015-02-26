package com.excelsecu.androidx2j;

public class AX2JMethodBlock {
    public static final int PRIORITY_TOP = 1;
    public static final int PRIORITY_SECONDLY_TOP = 2;
    public static final int PRIORITY_THIRDLY_TOP = 3;
    public static final int PRIORITY_NORMAL = 4;
    public static final int PRIORITY_THIRDLY_LAST = 5;
    public static final int PRIORITY_SECONDLY_LAST = 6;
    public static final int PRIORITY_LAST = 7;
    public static final int PRIORITY_DEFAULT = PRIORITY_NORMAL;
    public String content;
    public int priority;
    
    public AX2JMethodBlock(String content, int priority) {
        this.content = content;
        this.priority = priority;
    }
    
    public AX2JMethodBlock(String content) {
        this.content = content;
        this.priority = PRIORITY_NORMAL;
    }
    
    @Override
    public String toString() {
        return content;
    }
}
