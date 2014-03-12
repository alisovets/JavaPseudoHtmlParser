package alisovets.example.pseudohtmlparser;

import java.awt.Font;

/**
 * contents Additional attributes text display
 */
public class HtmlTextAttributes {
	
	private int textColor;
	private int indentation;
	private Font font;
	private boolean bold;
	private boolean underline;
	private Align align;
	

	/**
	 * Create a new HtmlTextAttributes with the specified attributes.  
	 * @param textColor the text color
	 * @param identation the additional right indentation 
	 * @param font the font of a text
	 * @param align the align
	 * @param bold  the flag of the bold style 
	 * @param underline the flag of the text underline style 
	 */
	public HtmlTextAttributes(int textColor, int identation, Font font,  Align align, boolean bold, boolean underline) {
		this.textColor = textColor;
		this.indentation = identation;
		this.font = font;
		this.bold = bold;
		this.underline = underline;
		this.align = align;
	}

	@Override
	public HtmlTextAttributes clone(){
		Font fontCopy = null;
		if(font != null){
			fontCopy = new Font(font.getName(), font.getStyle(), font.getSize());
		}	
		return new HtmlTextAttributes(textColor, indentation, fontCopy, align, bold, underline);
	}
	
	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getIndentation() {
		return indentation;
	}

	public void setIndentation(int indentation) {
		this.indentation = indentation;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}
}
