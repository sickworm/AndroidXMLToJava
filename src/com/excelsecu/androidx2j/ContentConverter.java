package com.excelsecu.androidx2j;

import com.excelsecu.androidx2j.dbbuilder.AndroidDocConverter;

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

	public String convertXMLToJavaCode(String xmlString) {
		try {
			LayoutTranslator translator = new LayoutTranslator(xmlString);
			return translator.translate();
		} catch (Exception e) {
			e.printStackTrace();
			String errorString = e.getLocalizedMessage() + "\n";
			StackTraceElement[] elements = e.getStackTrace();
			for (StackTraceElement element : elements) {
				errorString += element.toString() + "\n";
			}
			return errorString;
		}
	}
}
