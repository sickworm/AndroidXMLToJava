package com.sickworm.androidx2j;

import java.util.Scanner;

import com.sickworm.androidx2j.dbbuilder.AndroidDocConverter;

/**
 * Use to convert a XML block. To convert a whole project, see ProjectConverter
 * @author sickworm
 *
 */
public class ContentConverter {
	static {
		AndroidDocConverter.init();
        AX2JStyle.setProjectTheme(Config.DEFAULT_THEME);
	}

	/**
	 * text mode
	 * @param xmlString
	 * @return
	 */
	public String convertXMLToJavaCode(String xmlString) {
		Config.IS_CONTENT_TRANSLATE = true;
		Config.R_CLASS = "R";
		Config.RESOURCES_NAME = "getResources()";
		
		try {
			LayoutTranslator translator = new LayoutTranslator(xmlString);
			String content = translator.translate();
			StringBuilder importListBuilder = new StringBuilder();
			for (String s : translator.getImportList()) {
				importListBuilder.append("import ");
				importListBuilder.append(s);
				importListBuilder.append(";\n");
			}
			return importListBuilder.toString() + "\n\n" + buildJavaMethod(content);
		} catch (Exception e) {
			e.printStackTrace();
			String errorString = "Parse XML segment failed. Exception:\n\n" + e.getLocalizedMessage() + "\n";
			StackTraceElement[] elements = e.getStackTrace();
			for (StackTraceElement element : elements) {
				errorString += element.toString() + "\n";
			}
			return errorString;
		}
	}

    public static String buildJavaMethod(String content) {
        //find the main object to return
        String returnObject = "";
        String returnClass = "";
        Scanner scan = new Scanner(content);
        finish: while (scan.hasNext()) {
            String str = scan.nextLine();
            //find out the element first built
            if (str.matches("\\w+ \\w+ *= *.+")) {
                int index = str.indexOf(' ');
                int index2 = str.indexOf('=', index + 1);
                returnClass = str.substring(0, index);
                for (Class<?> c : Config.CLASSES_LIST) {
                    if (c.getSimpleName().equals(returnClass)) {
                        returnObject = str.substring(index + 1, index2).trim();
                        break finish;
                    }
                }
            }
        }
        scan.close();
        if (returnObject.equals("")) {
        	System.out.println(content);
            throw new AX2JException(AX2JException.FILE_BUILD_ERROR, "can not find main object");
        }

        content += "return " + returnObject + ";";
		return "public " + returnClass + " initLayout(Context context) {\n"
				+ indent(content) 
				+ "}";
    }
	
	private static String indent(String content) {
		StringBuilder intentContent = new StringBuilder();
		for(String s : content.split("\n")) {
			intentContent.append(Config.INDENT);
			intentContent.append(s);
			intentContent.append("\n");
		}
		return intentContent.toString();
	}
}
