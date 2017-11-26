package com.sickworm.ax2j.dbbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

import com.sickworm.ax2j.AX2JAttribute;
import com.sickworm.ax2j.AX2JClassTranslator;

/**
 * Filter XML attributes and related methods in Android documents.
 * @author sickworm
 *
 */
public class Filter {
    private Class<?> type;

    public Filter(Class<?> type) {
        this.type = type;
    }

    public AX2JClassTranslator filterDoc(String fileName) throws AndroidDocException {
        String attributesContent = getXMLAttributesContent(fileName);
        String methodsContent = getMethodsContent(fileName);
        AX2JClassTranslator attrToMethodList = filter(attributesContent, methodsContent);
        return attrToMethodList;
    }

    private String getXMLAttributesContent(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), Config.ENCODE));
            StringBuilder content = new StringBuilder();
            String buf;

            boolean start = false;
            while ((buf = reader.readLine())!= null) {
                // content between <!-- XML Attributes --> and <!-- Enum Values -->
                if (buf.equals("<!-- XML Attributes -->")) {
                    start = true;
                }
                if (buf.equals("<!-- Enum Values -->")) {
                	break;
                }
                if (start) {
                    buf = buf.trim();
                    if (buf.isEmpty())
                        continue;
                    content.append(buf);
                    content.append("\n");
                }
            }
            reader.close();
            return content.toString();
        } catch( Exception e ) {
            throw new AndroidDocException(AndroidDocException.DOC_READ_ERROR);
        }
    }

    private String getMethodsContent(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), Config.ENCODE));
            StringBuilder content = new StringBuilder();
            String buf;

            boolean start = false;
            while ((buf = reader.readLine())!= null) {
                // content between <!-- ========= METHOD DETAIL ======== --> and <!-- ========= END OF CLASS DATA ========= -->
                if (buf.equals("<!-- ========= METHOD DETAIL ======== -->")) {
                    start = true;
                }
                if (buf.equals("<!-- ========= END OF CLASS DATA ========= -->")) {
                	break;
                }
                if (start) {
                    buf = buf.trim();
                    // a trick to speed up the method details parsing
                    if (buf.startsWith("<div class=\"api apilevel-")) {
                    	buf = "<div class=\"api apilevel-\">";
                    }
                    if (buf.isEmpty())
                        continue;
                    content.append(buf);
                    content.append("\n");
                }
            }
            reader.close();
            return content.toString();
        } catch( Exception e ) {
            throw new AndroidDocException(AndroidDocException.DOC_READ_ERROR);
        }
    }

    private AX2JClassTranslator filter(String attributesContent, String methodsContent) {
        try {
        	// generate method details
        	Map<String, MethodDetails> methodDetails = new HashMap<>();
            Parser methodDetailsParser = Parser.createParser(methodsContent, Config.ENCODE);
            // trick to speed up the method details parsing, see getMethodsContent()
            AndFilter methodDetailsFilter =
                    new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class","api apilevel-"));
            NodeList methodDetailsNodeList = methodDetailsParser.parse(methodDetailsFilter);
            NodeIterator methodDetailsTableIt = methodDetailsNodeList.elements();
            while (methodDetailsTableIt.hasMoreNodes()) {
                Node trNode = methodDetailsTableIt.nextNode();
                NodeList trNodeList = trNode.getChildren();
                String methodName = trNodeList.elementAt(1).toPlainTextString();
                String apiLimiString = trNodeList.elementAt(3).toPlainTextString();
                int apiLimit = Integer.parseInt(
                		apiLimiString.substring(apiLimiString.lastIndexOf(' ') + 1, apiLimiString.length() - 1));
                methodDetails.put(methodName, new MethodDetails(methodName, apiLimit));
            }
        	
        	// generate attribute -> method relationship
            Parser attributeParser = Parser.createParser(attributesContent, Config.ENCODE);
            AndFilter attributeFilter =
                    new AndFilter(new TagNameFilter("h3"), new HasAttributeFilter("class","api-name"));
            AndFilter methodFilter =
                    new AndFilter(new TagNameFilter("ul"), new HasAttributeFilter("class","nolist"));
            OrFilter orFilter = new OrFilter(attributeFilter, methodFilter);
            NodeList tableNodeList = attributeParser.parse(orFilter);
            NodeIterator tableIt = tableNodeList.elements();

            AX2JClassTranslator map = new AX2JClassTranslator(type);
            String attr = null;
            String method = null;
            while(tableIt.hasMoreNodes()) {
                Node trNode = tableIt.nextNode();
                NodeList trNodeList = trNode.getChildren();

                String value = trNodeList.elementAt(0).toPlainTextString();
                if (value.startsWith("android:")) {
                	if (attr != null) {
                        map.add(attr, "");
                        attr = null;
                	}
                	attr = value;
                } else {
                	method = trNodeList.elementAt(1).toPlainTextString();
                	int apiLimit = methodDetails.get(method.substring(0, method.indexOf('('))).apiLimit;
                	apiLimit = apiLimit == 1? 0 : apiLimit;
                    map.add(attr, method, apiLimit << AX2JAttribute.TYPE_API_LIMIT_INDEX);
                    attr = null;
                    method = null;
                }
            }
            return map;
        } catch (Exception e) {
            throw new AndroidDocException(AndroidDocException.AXML_FORMAT_ERROR);
        }
    }
    
    private static class MethodDetails {
    	@SuppressWarnings("unused")
        public String methodName;
    	public int apiLimit;
    	
    	public MethodDetails(String methodName, int apiLimit) {
    		this.methodName = methodName;
    		this.apiLimit = apiLimit;
    	}
    }
}
