package com.sickworm.androidx2j;

import java.util.List;

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
		
		try {
			LayoutTranslator translator = new LayoutTranslator(xmlString);
			String content = translator.translate();
			StringBuilder importListBuilder = new StringBuilder();
			for (String s : translator.getImportList()) {
				importListBuilder.append("import ");
				importListBuilder.append(s);
				importListBuilder.append(";\n");
			}
			return importListBuilder.toString() + "\n\n" + warpAsMethod(content);
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
	
	private String warpAsMethod(String content) {
		return "public void initLayout(Context context) {\n"
				+ indent(content)
				+ "}";
	}
	
	private String indent(String content) {
		StringBuilder intentContent = new StringBuilder();
		for(String s : content.split("\n")) {
			intentContent.append(Config.INDENT);
			intentContent.append(s);
			intentContent.append("\n");
		}
		return intentContent.toString();
	}
}
