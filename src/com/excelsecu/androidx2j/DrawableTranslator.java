package com.excelsecu.androidx2j;

import java.io.File;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class DrawableTranslator extends BaseTranslator {

    public DrawableTranslator(File file) {
        super(file);
    }

    @Override
    public String translate() {
        File file = getFile();
        String content = "";
        if (!Utils.getFileExtension(file).equals("xml")) {
            String outPath = file.getPath();
            outPath = Config.ASSETS_OUT_PATH + outPath.substring(outPath.indexOf(File.separatorChar)).replace('-', '_');
            System.out.println("Copying " + file.getPath() + " to\n\t" +
                    new File(outPath).getPath() + "...");
            Utils.copyFile(file.getPath(), outPath);
            //return object must put in the first line
            String className = Utils.getClassName(file);
            content += "InputStream inStream = " + className + ".class.getResourceAsStream(\"" +
                    file.getPath().replace(Config.PROJECT_RES_PATH.replace('/', File.separatorChar), "/assets/").replace("\\", "/").replace('-', '_') + "\"); \n";
            content += "Drawable drawable = Drawable.createFromStream(inStream" +
                    ", \"" + className +"\");\n";
            addImport(Drawable.class);
            addImport(Context.class);
            addImport(InputStream.class);
        } else {
            String name = getRoot().getLabelName();
            BaseTranslator translator = new SelectorTranslator(getRoot());
            if (name.equals("selector")) {
                translator = new SelectorTranslator(getRoot());
            } else if (name.equals("shape")) {
                translator = new ShapeTranslater(getRoot());
            }
            content = translator.translate();
            setImportList(translator.getImportList());
        }
        return content;
    }
}
