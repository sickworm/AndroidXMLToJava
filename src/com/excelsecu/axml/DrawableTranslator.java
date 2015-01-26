package com.excelsecu.axml;

import java.io.File;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class DrawableTranslator extends BaseTranslator{

	public DrawableTranslator(File file) {
		super(file);
	}
	
	@Override
	public String translate() {
		File file = getFile();
        String content = "";
        if (!Utils.getFileExtension(file).equals("xml")) {
            String outPath = file.getPath();
            outPath = Config.ASSETS_OUT_PATH + outPath.substring(outPath.indexOf(File.separatorChar));
            System.out.println("Copying " + file.getPath() + " to\n\t" +
                    new File(outPath).getPath() + "...");
            Utils.copyFile(file.getPath(), outPath);
            //return object must put in the first line
            String className = Utils.getClassName(file);
            content += "InputStream inStream = " + className+ ".class.getResourceAsStream(\"" +
            		file.getPath().replace("res", "\\assets").replace("\\", "/") + "\"); \n";
            content += "Drawable drawable = Drawable.createFromStream(inStream" +
                    ", \"" + className +"\");\n";
            addImport(Drawable.class.getName());
            addImport(Context.class.getName());
            addImport(InputStream.class.getName());
        } else {
            String name = getRoot().getLabelName();
            BaseTranslator translator = new SelectorTranslator(getRoot());
            if (name.equals("selector")) {
                translator = new SelectorTranslator(getRoot());
            } else if (name.equals("shape")) {
                translator = new ShapeTranslater(getRoot());
            }
            content = translator.translate();
            content = translator.getExtraMethod() + "\n" + content;
            setImportList(translator.getImportList());
        }
        return content;
	}
}
