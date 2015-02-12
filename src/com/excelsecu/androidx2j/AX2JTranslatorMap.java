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
}
