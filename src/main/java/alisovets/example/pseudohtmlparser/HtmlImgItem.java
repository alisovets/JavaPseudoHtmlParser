package alisovets.example.pseudohtmlparser;
import java.io.File;


public class HtmlImgItem {
	private File srcFile; 
	private int width;
	private int height;
	
	private HtmlTextAttributes parent;
	
	public HtmlImgItem(File srcFile, int width, int height, HtmlTextAttributes parent) {
		this.srcFile = srcFile;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}

	public File getSrcFile() {
		return srcFile;
	}

	public void setSrcFile(File srcFile) {
		this.srcFile = srcFile;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public HtmlTextAttributes getParent() {
		return parent;
	}

	public void setParent(HtmlTextAttributes parent) {
		this.parent = parent;
	}
	
}
