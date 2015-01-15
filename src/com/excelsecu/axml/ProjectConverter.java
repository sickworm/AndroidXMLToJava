package com.excelsecu.axml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectConverter {
    private static List<String> idList = new ArrayList<String>();
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
            if (!Util.getFileExtension(f).equals("xml")) {
                continue;
            }
            if (f.isFile() && f.getName().endsWith(".xml")) {
                System.out.println(f.getName() + "\n");
                try {
                    LayoutConverter converter = new LayoutConverter(f.getPath());
                    String content = converter.convertAsString();
                    idList.addAll(converter.getIdList());
                    Util.generateFile(f, content);
                } catch (AXMLException e) {
                    System.out.println(f.getName() + " convert error: " +
                            e.getErrorCode() + " " + e.getDetails() + "\n");
                    e.printStackTrace();
                }
            }
        }
    }
    
}
