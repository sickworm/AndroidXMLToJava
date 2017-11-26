package com.sickworm.ax2j.dbbuilder;

import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sickworm.ax2j.AX2JAttribute;
import com.sickworm.ax2j.AX2JClassTranslator;
import com.sickworm.ax2j.AX2JMethod;
import com.sickworm.ax2j.AX2JTranslatorMap;

/**
 * Generate the HTML for attribute-list.html
 * @author sickworm
 *
 */
public class AttributeListGenerator {

	public static void main(String[] args) {
		String html = getMaterialGridHTML();
		System.out.println(html);
	}
	
	public static String getAttributeList() {
		if (!AndroidDocConverter.init()) {
			System.out.println("init failed, exit");
			return null;
		}
		
		AX2JTranslatorMap map = AX2JTranslatorMap.getInstance();
		Set<Entry<Class<?>,AX2JClassTranslator>> set = map.getMap().entrySet();

		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%-20s %-40s %-40s %-20s\n", "Class", "Attribute", "Method", "Comment"));
		for (Entry<Class<?>, AX2JClassTranslator> entry : set) {
			for (AX2JAttribute attribute : entry.getValue().getAttributeList()) {
				for (AX2JMethod method : attribute.getRelativeMethodList()) {
					int assignmentType = attribute.getAssignmentType(method);
					String assignmentTypeString = assignmentType == 0? "" : AX2JAttribute.getAssignmentTypeDescriptionString(assignmentType);
					String methodString = method.toString();
					methodString = methodString.isEmpty()? "NOT SUPPORTED YET" : methodString;
					builder.append(String.format("%-20s %-40s %-40s %-20s\n",
							entry.getKey().getSimpleName(),
							attribute.getName().getQualifiedName(),
							methodString,
							assignmentTypeString));
				}
			}
		}
		
		return builder.toString();
	}

    
    public static String getMaterialGridHTML() {
    	String attributeList = getAttributeList();
    	Pattern p = Pattern.compile("(^|\n)(\\w+) +([\\w:]+) +((NOT SUPPORTED YET)|([\\w(),_]+)) +([\\w,\\- ;]*)\n");
        Matcher m = p.matcher(attributeList);

        StringBuilder builder = new StringBuilder();
        builder.append("<table class=\"striped\">\n");
        if (m.find()) {
            builder.append("<thead><tr>");
        	builder.append("<th>");
        	builder.append(m.group(2));
        	builder.append("</th>");
        	
        	builder.append("<th>");
        	builder.append(m.group(3));
        	builder.append("</th>");

        	builder.append("<th>");
        	builder.append(m.group(4));
        	builder.append("</th>");

        	builder.append("<th>");
        	builder.append(m.group(7).trim());
        	builder.append("</th>");
            builder.append("</tr></thead>\n");
        }
        builder.append("<tbody>");
        while (m.find()) {
        	builder.append("<tr>");
        	builder.append("<td>");
        	builder.append(m.group(2));
        	builder.append("</td>");
        	
        	builder.append("<td>");
        	builder.append(m.group(3));
        	builder.append("</td>");

        	builder.append("<td>");
        	if (m.group(5) != null) {
            	builder.append("<b>");
        	}
        	if (m.group(4).length() > 50) {
        		// find the index of line feed
        		int i = 50;
        		while(i-- > 0) {
            		char c = m.group(4).charAt(i);
        			if ((c > 'a' && c < 'z') ||
        					c > 'A' && c < 'Z') {
        				continue;
        			}
        			i++;
        			break;
        		}
        		if (i <= 0) {
        			i = 50;
        		}
            	builder.append(m.group(4).substring(0, i));
            	builder.append("</br>");
            	builder.append(m.group(4).substring(i));
        	} else {
            	builder.append(m.group(4));
        	}
        	if (m.group(5) != null) {
            	builder.append("</b>");
        	}
        	builder.append("</td>");

        	builder.append("<td>");
        	String comment = m.group(7).trim();
        	if(!comment.equals("")) {
            	builder.append("<a href=\"#comment\">");
        	}
        	builder.append(comment);
        	if(!comment.equals("")) {
            	builder.append("</a>");
        	}
        	builder.append("</td>");
        	builder.append("</tr>\n");
        }
        builder.append("</tbody>");
        builder.append("</table>");
        
        return builder.toString();
    }
}
