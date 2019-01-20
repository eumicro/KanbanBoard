package org.kanban.shared;

import java.io.File;

public class Paths {

	public File getLayoutFile(String filename){
		ClassLoader loader = getClass().getClassLoader();
		File file = new File(loader.getResource("templates/"+filename).getFile());
		return file;
	}
	public String getCSSFilePath(String cssFileName){
//		ClassLoader loader = getClass().getClassLoader();
//		URL path = loader.getResource("style/default/"+cssFileName);
		return "style/default/"+cssFileName;
	}
	public String getJavaScriptFilePath(String javaScriptFileName){
//		ClassLoader loader = getClass().getClassLoader();
//		String path = loader.getResource("javascript/"+javaScriptFileName).getFile();

		return "javascript/"+javaScriptFileName;
	}
}
