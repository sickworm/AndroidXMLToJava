package com.excelsecu.axml;

import java.io.File;

public class ProjectConverter {
    
    public static void main(String[] argv) {
        File res = new File(Config.PROJECT_RES_PATH);
        if (!res.isDirectory()) {
            throw new AXMLException(AXMLException.PROJECT_DIR_NOT_FOUND);
        }
        File[] dirList = res.listFiles();
        for (File f : dirList) {
            String path = f.getPath();
            if (f.isFile()) {
                continue;
            }
            //linux & windows
            String tmpPath = path.replace('\\', '/');
            if (tmpPath.matches("res/layout")) {
                LayoutOutput(f);
            } else if (tmpPath.matches("res/anim")) {
            } else if (tmpPath.matches("res/drawable.*")) {
            } else if (tmpPath.matches("res/menu.*")) {
            } else if (tmpPath.matches("res/values.*")) {
            }
        }
    }
    
    private static void LayoutOutput(File dir) {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            if (f.isFile() && f.getName().endsWith(".xml")) {
                System.out.println(f.getName() + "\n");
                try {
                    LayoutConverter converter = new LayoutConverter(f.getPath());
                    String content = converter.convertAsString();
                    String sep = System.getProperty("file.separator");
                    String path = Config.PROJECT_OUT_PATH + sep + "layout" + sep +
                            f.getName().substring(0, f.getName().lastIndexOf('.')) + ".java";
                    Util.generateFile(path, content);
                } catch (AXMLException e) {
                    System.out.println(f.getName() + " convert error: " +
                            e.getErrorCode() + " " + e.getDetails());
                    e.printStackTrace();
                }
            }
        }
    }
}
