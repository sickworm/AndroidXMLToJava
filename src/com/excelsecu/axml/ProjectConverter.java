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
            //linux & windows
            String tmpPath = path.replace('\\', '/');
            if (tmpPath.matches("res/layout")) {
                System.out.println(path);
            } else if (tmpPath.matches("res/anim")) {
                System.out.println(path);
            } else if (tmpPath.matches("res/drawable.*")) {
                System.out.println(path);
            } else if (tmpPath.matches("res/menu.*")) {
                System.out.println(path);
            } else if (tmpPath.matches("res/values.*")) {
                System.out.println(path);
            }
        }
    }
}
