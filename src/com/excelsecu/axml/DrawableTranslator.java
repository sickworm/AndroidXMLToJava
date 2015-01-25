package com.excelsecu.axml;

import java.io.File;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class DrawableTranslator extends BaseTranslator{

	public DrawableTranslator(File file) {
		super(file);
	}
	
	public String translate() {
		File file = getFile();

        if (file.getPath().contains("transparent.xml")) {
        	System.out.println("aa");
        }
        if (!Utils.getFileExtension(file).equals("xml")) {
            String outPath = file.getPath();
            outPath = Config.ASSETS_OUT_PATH + outPath.substring(outPath.indexOf(File.separatorChar));
            System.out.println("Copying " + file.getPath() + " to\n\t" +
                    new File(outPath).getPath() + "...");
            Utils.copyFile(file.getPath(), outPath);
            //return object must put in the first line
            String className = Utils.getClassName(file);
            String content = "Drawable drawable = null;\n";
            content += "InputStream inStream = " + className+ ".class.getResourceAsStream(\"" +
            		file.getPath().replace("res", "\\assets").replace("\\", "/") + "\"); \n";
            content += "drawable = Drawable.createFromStream(inStream" +
                    ", \"" + className +"\");\n";
            addImport(Drawable.class.getName());
            addImport(Context.class.getName());
            addImport(InputStream.class.getName());
            return content;
        } else {
            String content = "";
            String name = getRoot().getLabelName();
            if (name.equals("selector")) {
            	SelectorTranslator selectorConverter = new SelectorTranslator(getRoot());
                content = selectorConverter.translate();
                content = selectorConverter.getExtraMethod() + "\n" + content;
                setImportList(selectorConverter.getImportList());
                return content;
            } else if (name.equals("shape")) {
                return "";
            }
        }
        return "";
	}
}
