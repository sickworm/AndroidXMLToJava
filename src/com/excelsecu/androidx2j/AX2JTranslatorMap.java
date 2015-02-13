package com.excelsecu.androidx2j;

import java.util.HashMap;

public class AX2JTranslatorMap{
    private static AX2JTranslatorMap translatorMap = null;
    private HashMap<Class<?>, AX2JTranslator> attribute2MethodMap = new HashMap<Class<?>, AX2JTranslator>();
    
    public static AX2JTranslatorMap getInstance() {
        if (translatorMap == null) {
            translatorMap = new AX2JTranslatorMap();
        }
        return translatorMap;
    }
    
    public void put(Class<?> clazz, AX2JTranslator translator) {
        attribute2MethodMap.put(clazz, translator);
    }
    
    public void put(String attributeString) {
        int index1 = attributeString.indexOf(',');
        int index2 = attributeString.indexOf(',', index1 + 1);
        String type = attributeString.substring(0, index1);
        String name = attributeString.substring(index1 + 1, index2);
        String method = attributeString.substring(index2 + 1);
        
        put(getType(type), name, method);
    }
    
    public void put(Class<?> clazz, String qNameString, String methodString) {
        AX2JTranslator translator = attribute2MethodMap.get(clazz);
        if (translator == null) {
            translator = new AX2JTranslator(clazz);
            attribute2MethodMap.put(clazz, translator);
        }
        
        translator.add(qNameString, methodString);
    }
    
    public HashMap<Class<?>, AX2JTranslator> getMap() {
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
}
