package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.List;

import static com.excelsecu.androidx2j.AX2JCodeBlock.AX2JCode.*;

/**
 * Manage the Java code block of an AX2JNode
 * @author sickworm
 *
 */
public class AX2JCodeBlock {
    private List<List<AX2JCode>> codeList = new ArrayList<List<AX2JCode>>();
    private List<String> importList = new ArrayList<String>();
    private String name;
    private Class<?> type;

    public AX2JCodeBlock(Class<?> type, String name) {
        this.name = name;
        this.type = type;
        for (int i = PRIORITY_FIRST; i <= PRIORITY_LAST; i++) {
        	codeList.add(new ArrayList<AX2JCode>());
        }
    }

    public void add(String codeString) {
        AX2JCode code = new AX2JCode(codeString);
        add(code);
    }

    public void add(String codeString, int priority) {
    	if (priority < PRIORITY_FIRST || priority > PRIORITY_LAST) {
    		priority = PRIORITY_DEFAULT;
    	}
        AX2JCode code = new AX2JCode(codeString, priority);
        add(code);
    }

    public void add(AX2JMethod method, String value, int type) {
        AX2JCode code = new AX2JCode(method, value, type);
        add(code);
    }

    public void add(AX2JCode code) {
        List<AX2JCode> subCodeList = get(code.priority);
        int j = 0;
        for (j = 0; j < subCodeList.size(); j++) {
        	AX2JCode originCode = subCodeList.get(j);
            if (code.isDuplicateMethod(originCode)) {
            	if (code.method != null && code.method.getArgsNum() > 1) {
            		int order = AX2JAttribute.getTypeValue(code.type, AX2JAttribute.TYPE_ARGUMENTS_ORDER);
            		originCode.setArg(order, code.getArg(order));
            	}
                break;
            }
        }
        //it's a new method code
        if (j == subCodeList.size()) {
            subCodeList.add(code);
        }
    }

    /**
     *  Add the class to the import list. If already exists, ignore.
     *  @param className the class try to be added in import list
     */
    public void addImport(String className) {
        if (className == null || className.equals("") ||
                className.equals(Void.class.getName())) {
            return;
        }
        className = className.replace("$", ".");
        if (!Utils.hasString(importList, className)) {
            importList.add(className);
        }
    }

    public void addImport(Class<?> type) {
        addImport(type.getName());
    }

    public List<AX2JCode> get(int priority) {
        return codeList.get(priority - 1);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public List<String> getImportList() {
        return importList;
    }

    public List<AX2JCode> getCode(int priority) {
        return get(priority);
    }

    public String toString(int priority) {
        StringBuffer codeBlock = new StringBuffer();
        for (AX2JCode code : get(priority)) {
            if (code.isSpecial()) {
                codeBlock.append(code);
            } else {
                codeBlock.append(name + "." + code);
            }
        }

        return codeBlock.toString();
    }

    @Override
    public String toString() {
        StringBuffer codeBlock = new StringBuffer();
        for (int i = PRIORITY_SECOND; i <= PRIORITY_SECONDLY_LAST; i++) {
            for (AX2JCode code : get(i)) {
                if (code.isSpecial()) {
                    codeBlock.append(code);
            	} else if (code.isLayoutParam()){
            		codeBlock.append(LayoutTranslator.getLayoutParamsName(name) + "." + code);
            	} else {
                    codeBlock.append(name + "." + code);
            	}
            }
        }

        return codeBlock.toString();
    }

    public class AX2JCode {
        /** file top code **/
        public static final int PRIORITY_FIRST = 1;
        /** node top code **/
        public static final int PRIORITY_SECOND = 2;
        /** code top code **/
        public static final int PRIORITY_THIRD = 3;
        /** node normal code **/
        public static final int PRIORITY_NORMAL = 4;
        /** code bottom code **/
        public static final int PRIORITY_THIRDLY_LAST = 5;
        /** node bottom code **/
        public static final int PRIORITY_SECONDLY_LAST = 6;
        /** file bottom code **/
        public static final int PRIORITY_LAST = 7;
        public static final int PRIORITY_DEFAULT = PRIORITY_NORMAL;

        private AX2JMethod method;
        private String methodString;
        private int type = AX2JAttribute.TYPE_NORMAL;
        private int priority = PRIORITY_NORMAL;
        /** not a common method **/
        public boolean special = false;
        private String[] args;

        public AX2JCode(String methodString) {
            this.methodString = methodString;
            special = true;
        }

        public AX2JCode(String methodString, int priority) {
            this.methodString = methodString;
            this.priority = priority;
            special = true;
        }

        public AX2JCode(AX2JMethod method, String value, int type) {
            this.method = method;
            this.args = new String[method.getArgsNum()];
            this.methodString = method.getMethodName();

            this.type = type;
            this.priority = AX2JAttribute.getTypeValue(type, AX2JAttribute.TYPE_PRIORITY);

            int order = AX2JAttribute.getTypeValue(type, AX2JAttribute.TYPE_ARGUMENTS_ORDER);
            if (order == AX2JAttribute.TYPE_ARGUMENTS_ALL_THE_SAME) {
            	for (int i = 1; i <= method.getArgsNum(); i++) {
            		setArg(i, value);
            	}
            } else {
            	setArg(order, value);
            }
        }

        public boolean isDuplicateMethod(AX2JCode code) {
            if (special) {
                if (this.toString().equals(code.toString())) {
                    return true;
                }
            } else if (this.method.equals(code.method)) {
                return true;
            }

            return false;
        }

        public boolean isSpecial() {
            return special;
        }

        public boolean isLayoutParam() {
            return AX2JAttribute.getTypeValue(type, AX2JAttribute.TYPE_LAYOUT_PARAMETER) != 0;
        }

        public boolean isArray() {
            return AX2JAttribute.getTypeValue(type, AX2JAttribute.TYPE_ARGUMENTS_ARRAY) != 0;
        }

        public boolean isVariableAssignment() {
            return AX2JAttribute.getTypeValue(type, AX2JAttribute.TYPE_VARIABLE_ASSIGNMENT) != 0;
        }

        public void setArg(int order, String value) {
            if (order > method.getArgsNum() || order <= 0) {
                return;
            }
            args[order - 1] = value;
        }

        public String getArg(int order) {
        	if (order > method.getArgsNum() || order <= 0) {
        	    return "";
        	}
            String value = args[order - 1];
            if (value == null) {
                value = method.getArg(AX2JCodeBlock.this, order);
            }
            return value;
        }

        @Override
        public String toString() {
            if (special) {
                return methodString;
            }

            String valueString = "";
            for (int i = 1; i <= method.getArgsNum(); i++) {
                valueString += getArg(i) + ((i == method.getArgsNum())? "" : ", ");
            }

            if (isVariableAssignment()) {
                return methodString + " = " + valueString + ";\n";
            } else if (isArray()){
                return methodString + "(new " + Utils.getValueType(method.getArgType(1)) +"[] {" + valueString + "});\n";
            } else {
                return methodString + "(" + valueString + ");\n";
            }
        }
    }
}