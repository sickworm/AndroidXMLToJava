package com.excelsecu.androidx2j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.dom4j.Attribute;

public class AX2JTranslatorMap{
    private static AX2JTranslatorMap translatorMap = null;
    private HashMap<Class<?>, AX2JTranslator> attribute2MethodMap = new LinkedHashMap<Class<?>, AX2JTranslator>();
    
    public static AX2JTranslatorMap getInstance() {
        if (translatorMap == null) {
            translatorMap = new AX2JTranslatorMap();
        }
        return translatorMap;
    }
    
    public void add(AX2JTranslator translator) {
        attribute2MethodMap.put(translator.getType(), translator);
    }
    
    public void add(String attributeString) {
        int index1 = attributeString.indexOf(',');
        int index2 = attributeString.indexOf(',', index1 + 1);
        
        String type = attributeString.substring(0, index1);
        String name = attributeString.substring(index1 + 1);
        String method = "";
        if (index2 != -1) { 
            method = attributeString.substring(index2 + 1);
            name = attributeString.substring(index1 + 1, index2);
        }
        
        add(getType(type), name, method);
    }
    
    public void add(Class<?> type, String qNameString, String methodString) {
        AX2JTranslator translator = attribute2MethodMap.get(type);
        if (translator == null) {
            translator = new AX2JTranslator(type);
            attribute2MethodMap.put(type, translator);
        }
        
        translator.add(qNameString, methodString);
    }
    
    public String translate(Class<?> type, Attribute attribute) {
        String javaBlock = "";
        while (true) {
            AX2JTranslator translator = attribute2MethodMap.get(type);
            if (translator == null) {
                javaBlock = "//" + attribute.asXML() + "\t//not support\n";
                break;
            } else {
                try {
                    javaBlock = translator.translate(attribute);
                    break;
                } catch(AX2JException e) {
                    type = type.getSuperclass();
                }
            }
        }
        return javaBlock;
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
    
    public String toString() {
        StringBuffer content = new StringBuffer();
        Iterator<Entry<Class<?>, AX2JTranslator>> iterator = attribute2MethodMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Class<?>, AX2JTranslator> entry = iterator.next();
            AX2JTranslator translator = entry.getValue();
            content.append("//" + translator.getType().getSimpleName() + "\n");
            content.append(translator.toString() + "\n");
        }
        return content.toString();
    }
}
