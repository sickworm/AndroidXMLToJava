package com.excelsecu.ian.dbbuilder;

import java.util.ArrayList;
import java.util.List;
import org.htmlparser.*;

public class AndroidDocConverter {
	public static String PATH = "";
	
	public static void main(String[] argv) {
		List<String> listPage = listPage();
		
	}
	
	public static List<String> listPage() {
		List<String> list = new ArrayList<String>();
		list.add("D:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/android/view/View.html");
		return list;
	}
}
