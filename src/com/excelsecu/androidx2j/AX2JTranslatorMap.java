package com.excelsecu.androidx2j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class AX2JTranslatorMap {
    private static AX2JTranslatorMap singleton = null;
    private HashMap<Class<?>, AX2JClassTranslator> attribute2MethodMap = new LinkedHashMap<Class<?>, AX2JClassTranslator>();

    private AX2JTranslatorMap() {
    }

    public static AX2JTranslatorMap getInstance() {
        if (singleton == null) {
            singleton = new AX2JTranslatorMap();
        }
        return singleton;
    }

    /** build map **/
    public void add(AX2JClassTranslator translator) {
        attribute2MethodMap.put(translator.getType(), translator);
    }

    public void add(String attributeString) {
        int index1 = attributeString.indexOf(',');
        int index2 = attributeString.indexOf(',', index1 + 1);
        int index3 = attributeString.lastIndexOf(',');
        String type = attributeString.substring(0, index1);
        String name = attributeString.substring(index1 + 1, index2);
        String method = attributeString.substring(index2 + 1, index3);
        String methodType = attributeString.substring(index3 + 1);

        add(getType(type), name, method, (methodType.equals(""))? 0 : Integer.decode(methodType));
    }

    public void add(Class<?> type, String qNameString, String methodString) {
        AX2JClassTranslator translator = attribute2MethodMap.get(type);
        if (translator == null) {
            translator = new AX2JClassTranslator(type);
            attribute2MethodMap.put(type, translator);
        }

        translator.add(qNameString, methodString);
    }

    public void add(Class<?> type, String qNameString, String methodString, int methodType) {
        AX2JClassTranslator translator = attribute2MethodMap.get(type);
        if (translator == null) {
            translator = new AX2JClassTranslator(type);
            attribute2MethodMap.put(type, translator);
        }

        translator.add(qNameString, methodString, methodType);
    }

    public boolean remove(String attributeString) {
        int index1 = attributeString.indexOf(',');
        int index2 = attributeString.indexOf(',', index1 + 1);
        int index3 = attributeString.lastIndexOf(',');
        String typeString = attributeString.substring(0, index1);
        String qNameString = attributeString.substring(index1 + 1, index2);
        String methodString = attributeString.substring(index2 + 1, index3);

        Class<?> type = getType(typeString);
        AX2JClassTranslator translator = attribute2MethodMap.get(type);
        if (translator == null) {
            return false;
        }

        return translator.remove(qNameString, methodString);
    }

    public AX2JClassTranslator get(Class<?> type) {
        return attribute2MethodMap.get(type);
    }

    public HashMap<Class<?>, AX2JClassTranslator> getMap() {
        return attribute2MethodMap;
    }

    public Class<?> getType(String typeString) {
        for (Class<?> type : Config.CLASSES_LIST) {
            if (typeString.equals(type.getSimpleName())) {
                return type;
            }
        }
        throw new AX2JException(AX2JException.CLASS_NOT_FOUND, typeString);
    }

    public String toString() {
        StringBuffer content = new StringBuffer();
        Iterator<Entry<Class<?>, AX2JClassTranslator>> iterator = attribute2MethodMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Class<?>, AX2JClassTranslator> entry = iterator.next();
            AX2JClassTranslator translator = entry.getValue();
            content.append("//" + translator.getType().getSimpleName() + "\n");
            content.append(translator.toString() + "\n");
        }
        return content.toString();
    }
}