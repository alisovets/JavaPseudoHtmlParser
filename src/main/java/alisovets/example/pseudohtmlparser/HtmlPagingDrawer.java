package alisovets.example.pseudohtmlparser;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The class is intended for placement of the formatted texts 
 * and images on pages of a given size and saving of pages in the file.
 */
public class HtmlPagingDrawer {
	private static int HEIGHT_UNDERLINE = 3;
	private static int SHADOW_COLOR = 0;
	private static int DARK_BG_SHADOW_COLOR = 0x606060;
	private static int HEADER_HEIGHT = 30;
	private static int FOOTER_HEIGHT = 20;
	private static int HEADER_FONT_COLOR = 0x000040;
	private static Font HEADER_FONT = new Font(Font.SERIF, Font.PLAIN, 12);
	
	/* the width of pages */
	private int width;
	
	/* the height of pages */
	private int height;
	
	/* the background color of the page in RGB*/
	private int bgColor;
	
	/* the left margin of page */ 
	private int leftMargin;
	
	/* the right margin of page */
	private int rightMargin;
	
	/* the top position of content on the page */ 
	private int startYPosition;
	
	/* the lowermost position of contents */  
	private int maxYPosition;

	/* text shadow color */
	private int shadowColor;
	
	/* the path name for the render result directory */
	private String renderDirectoryName;
	
	/* the path name for the html file directory*/
	private String htmlDirectoryName;
	
	/* the number of current page */
	private int currentPage;
	
	/* the image for rendering of the whole page */
	private BufferedImage currentPageImage;
	
	/* the image for rendering of the current row */
	private BufferedImage currentLineImage;
	
	/* the vertical position of the currently rendering row */  
	private int currentLineTop;
	
	/* the horizontal position of the currently rendering element relatively began row */
	private int currentLineWidth;
	
	private HtmlTextAttributes currentTextAttributes;
	private String pageTitle;
	private String nextPageTitle;
	private int lastNamedPage;

	/**
	 * Create a new HtmlPagingDrawer with the specified attributes.
	 * @param width the width of pages
	 * @param height the height of pages
	 * @param bgColor the background color of the page in RGB
	 * @param leftMargin the left margin of pages
	 * @param rightMargin the right margin of pages
	 * @param topMargin the top margin of pages
	 * @param bottomMargin the bottom margin of pages
	 * @param fontSize size of base font on the page
	 * @param fileName path name  html source file
	 */
	public HtmlPagingDrawer(int width, int height, int bgColor, int leftMargin, int rightMargin, int topMargin,
			int bottomMargin, int fontSize, String fileName) {
				
		if(leftMargin + rightMargin + 6 * fontSize > width){
			throw new IllegalArgumentException("is too narrow window for this page");
		} 
		
		if(topMargin + bottomMargin + HEADER_HEIGHT + FOOTER_HEIGHT + 2 * fontSize > height){
			throw new IllegalArgumentException("is too low window for this page");
		}
		
		this.width = width;
		this.height = height;
		this.bgColor = bgColor;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.renderDirectoryName = fileName + ".render";
		File file = new File(fileName);
		this.pageTitle = file.getName();		
		this.nextPageTitle = this.pageTitle;
		this.htmlDirectoryName = file.getParent();
		this.lastNamedPage = 0;
		this.startYPosition = topMargin + HEADER_HEIGHT;
		this.maxYPosition = height - bottomMargin - FOOTER_HEIGHT - 1; 
		currentPage = 0;
		selectShadowColor();
		cleanOrCreateDirectory();
	}
	
	// calculates the text shadow color based on current bgcolor
	private void selectShadowColor(){
		if(((bgColor & 0xff0000) > 0x200000) && ((bgColor & 0xff00) > 0x2000) && ((bgColor & 0xff) > 0x20)){
			shadowColor = SHADOW_COLOR;
		}
		else{
			shadowColor = DARK_BG_SHADOW_COLOR;
		}
	}

	// cleans renderDirectory if it exists or creates new
	private void cleanOrCreateDirectory() {
		File directory = new File(renderDirectoryName);
		if (directory.exists()) {
			for (File file : directory.listFiles()) {
				if (file.isFile())
					file.delete();
			}
		} else {
			directory.mkdir();
		}
	}

	// saves the image in the file with specified name
	private void saveImage(String filename, BufferedImage image) {
		File file = new File(filename);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Image loadScaledImage(String fileName, int imgWidth, int imgHeight) {
		File file = new File(fileName);
		if(!file.isAbsolute()){
			file = new File(htmlDirectoryName, fileName);
		}
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(img == null){
			int size = currentTextAttributes.getFont().getSize();
			img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		}		
		if ((imgWidth <= 0) || (imgWidth <= 0)) {
			imgWidth = img.getWidth(); 
			imgHeight = img.getHeight();
		}
		int maxWidth = width - leftMargin - rightMargin;
		if (imgWidth > maxWidth) {
			float k = (float) maxWidth / imgWidth;
			imgWidth = maxWidth;
			imgHeight = (int) (imgHeight * k);
		}
		int maxHeight = maxYPosition - startYPosition;
		if (imgHeight > maxHeight) {
			float k = (float) maxHeight / imgHeight;
			imgHeight = maxHeight;
			imgWidth = (int) (imgWidth * k);
		}		
		
		return img.getScaledInstance (imgWidth, imgHeight, Image.SCALE_FAST);
	}
	
	/**
	 * sets name for current page
	 * @param pageTitle the new title of page
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
		this.nextPageTitle = pageTitle;
	}

	/**
	 * places the image from the file with specified path name with specified attributes on page images   
	 * @param textAttributes the formating attributes for the drawing text   
	 * @param filePath the file path to image source
	 * @param imgWidth  width of the image
	 * @param imgHeight height of he image
	 */
	public void drawImage(HtmlTextAttributes textAttributes, String filePath, int imgWidth, int imgHeight){
		currentTextAttributes = textAttributes;
		if (currentPage == 0) {
			createNewPage();
			createCurentLineImage();
		}
		
		Image image = loadScaledImage(filePath, imgWidth, imgHeight);
		
		if(currentLineWidth + image.getWidth(null) <= currentLineImage.getWidth() ){
			addImageOnLine(image);
		}
		else{
			addLineOnPage();
			createCurentLineImage();
			addImageOnLine(image);
		}	
	}
	
	/**
	 * places the specified text on page images using the text attributes and return the number of the initial page.      
	 * @param textAttributes the formating attributes for the drawing text 
	 * @param text the text for drawing
	 * @param useForTitle if true usage text as the page title
	 * @return the number of the initial page where the text was placed
	 */
	public int drawText(HtmlTextAttributes textAttributes, String text, boolean useForTitle) {
		currentTextAttributes = textAttributes;
		
		if (currentPage == 0) {
			createNewPage();
			createCurentLineImage();
		}

		String[] words = text.split("\\s");
		Font font = currentTextAttributes.getFont();
		FontMetrics fontMetrix = currentLineImage.getGraphics().getFontMetrics(font);
		String str = "";
		for (int i = 0; i < words.length; i++) {
			int stringWidth;
			String newString;

			if ((i == 0) && (currentLineWidth == 0)) {
				newString = words[i];
			} else {
				newString = str + " " + words[i];
			}

			stringWidth = currentLineWidth + fontMetrix.stringWidth(str + " " + words[i]);
			if (stringWidth <= currentLineImage.getWidth()) {
				str = newString;
			} else {
				// newline
				addStringOnLine(str);
				checkPageName(text, useForTitle);
				addLineOnPage();
				str = words[i];
				createCurentLineImage();
			}
		}
		
		if (str.length() > 0) {
			addStringOnLine(str);
		}

		checkPageName(text, useForTitle);		
		return lastNamedPage;
	}

	//change the current page title if it is necessary 
	void checkPageName(String name, boolean useForName){
		if(!useForName){
			return;
		}
		nextPageTitle = name;
		if((lastNamedPage != currentPage) && (pageTitle != name)){
			//
			pageTitle = name;
			lastNamedPage = currentPage;
		}
	}
	
	
	/**
	 * completes the not empty current line to the following line to print a new row 
	 * @param textAttributes
	 */
	public void endLine(HtmlTextAttributes textAttributes){
		currentTextAttributes = textAttributes;
		if (currentPage == 0) {
			createNewPage();
			createCurentLineImage();
		}
		else if(currentLineWidth > 0){
			addLineOnPage();
			createCurentLineImage();
		}
	}
	
	/**
	 * completes the current line to the following line to print a new row 
	 * @param textAttributes
	 */
	public void nextLine(HtmlTextAttributes textAttributes) {
		currentTextAttributes = textAttributes;
		if (currentPage == 0) {
			createNewPage();
			createCurentLineImage();
		}
		addLineOnPage();	
		currentTextAttributes = textAttributes;
		createCurentLineImage();
		
	}

	
	//places the textString on the currentLineImage
	private void addStringOnLine(String str) {
		checkLineHeightAndResize();
		Graphics g = currentLineImage.getGraphics();
		Font font = currentTextAttributes.getFont();
		FontMetrics fontMetrix = g.getFontMetrics(font);
		int yPosition = currentLineImage.getHeight() - fontMetrix.getDescent() - HEIGHT_UNDERLINE;
		g.setFont(font);
		if(currentTextAttributes.isBold()){
			g.setColor(new Color(shadowColor));
			g.drawString(str, currentLineWidth + 1, yPosition + 1);
		}		
		g.setColor(new Color(currentTextAttributes.getTextColor()));
		g.drawString(str, currentLineWidth, yPosition);

		int strLength = fontMetrix.stringWidth(str);
		if (currentTextAttributes.isUnderline()) {
			if(currentLineWidth > 0){
				int correction = fontMetrix.stringWidth(" ");
				drawUnderline(g, currentLineWidth + correction, strLength - correction);
			}
			else{
				drawUnderline(g, currentLineWidth, strLength);
			}	
		}
		currentLineWidth += strLength;
	}
	
	
	//places the Image on the currentLineImage
	private void addImageOnLine(Image image){
		checkLineHeightAndResize(image.getHeight(null));
		Graphics g = currentLineImage.getGraphics();
		g.drawImage(image, currentLineWidth, 0, null);
		currentLineWidth += image.getWidth(null);
	}

	
	//draws wavy underline under the text 
	private void drawUnderline(Graphics g, int startX, int length) {
		int yPosition = currentLineImage.getHeight() - 1;
		while (length > 1) {
			g.drawLine(startX, yPosition, startX + 2, yPosition - 2);
			length -= 2;
			startX += 2;
			if (length > 1) {
				g.drawLine(startX, yPosition - 2, startX + 2, yPosition);
				length -= 2;
				startX += 2;
			}
		}
	}

	//replace the current line image on the page image
	private void addLineOnPage() {
		int lineWidth = currentLineImage.getWidth();
		if(currentLineWidth < lineWidth){
			lineWidth = currentLineWidth; 
		}
		int lineHeight = currentLineImage.getHeight();
		
		int startX;
		int startY = currentLineTop;
		Align align = currentTextAttributes.getAlign();
		if(align == Align.RIGHT){
			startX = width - rightMargin - lineWidth;
		}
		else if(align == Align.CENTER){
			startX = (width + leftMargin + currentTextAttributes.getIndentation() - rightMargin - lineWidth)/2;
		}
		else{
			startX = leftMargin + currentTextAttributes.getIndentation();		
		}
	
		Graphics g = currentPageImage.getGraphics();
		g.drawImage(currentLineImage, startX, startY, null);
		
		currentLineTop += lineHeight * 1.2;
	}

	//creates the new page image
	private void createNewPage() {
		currentPageImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = currentPageImage.getGraphics();
		g.setColor(new Color(bgColor));
		g.fillRect(0, 0, width, height);
		currentPage++;
		currentLineTop = startYPosition;
	}
	
	
	//draws text on the page header or footer in specified vertical position 
	private void drawPageHeaderFooter(String text,  int yPosition){
		Graphics g = currentPageImage.getGraphics();
		
		g.setColor(new Color(HEADER_FONT_COLOR));
		g.setFont(HEADER_FONT);
		 
		FontMetrics fontMetrix = g.getFontMetrics(HEADER_FONT); 
		int xPosition = (width - fontMetrix.stringWidth(text)) / 2;
		if(xPosition < 10){
			xPosition = 10;
		}
		g.drawString(text, xPosition, yPosition );
	}
	
	//draws the page header
	private void drawPageHeader(){
		int yPosition = HEADER_FONT.getSize() * 2 ;
		drawPageHeaderFooter(pageTitle, yPosition); 
	}
	
	//draws the page footer
	private void drawPageNumberOnFooter(){
		int yPosition = height - HEADER_FONT.getSize();
		String numberStr = "" + currentPage; 
		drawPageHeaderFooter(numberStr, yPosition); 
	}


	//checks if the current line image fits into the current page
	private void checkEndPageAndCreateNew(){
		if(currentLineImage.getHeight() + currentLineTop <= maxYPosition){
			return;
		}
		endPage();
		pageTitle = nextPageTitle; 
	}
	
	//finishes to draw the current page and saves it
	private void endPage(){
		drawPageNumberOnFooter();
		drawPageHeader();
		drawPageHeader();
		String filename = String.format("%s/%02d.png", renderDirectoryName, currentPage);
		saveImage(filename, currentPageImage);
		createNewPage();
	}
	
	/**
	 * completes the current page to the following line to print a new page 
	 * @param textAttributes
	 */
	public void nextPage(){
		endLine(currentTextAttributes);
		endPage();
	}
	
	//creates an image for current row 
	private void createCurentLineImage() {
		int needLineHeight = currentTextAttributes.getFont().getSize() + HEIGHT_UNDERLINE + 1;
		currentLineImage = createNewLineImage(needLineHeight);
		currentLineWidth = 0;
		checkEndPageAndCreateNew();
	}

	//creates an image with the specified height for current row
	private BufferedImage createNewLineImage(int needLineHeight) {
		int lineWidth = width - leftMargin - rightMargin - currentTextAttributes.getIndentation();
		BufferedImage newLineImage = new BufferedImage(lineWidth, needLineHeight, BufferedImage.TYPE_INT_RGB);
		Graphics g = newLineImage.getGraphics();
		g.setColor(new Color(bgColor));
		g.fillRect(0, 0, lineWidth, needLineHeight);
		return newLineImage;

	}

	//checks or the current font size fit into the current line image and resize it  
	private void checkLineHeightAndResize() {
		int needLineHeight = currentTextAttributes.getFont().getSize() + HEIGHT_UNDERLINE + 1;
		checkLineHeightAndResize(needLineHeight);
	}

	//checks or the current line image not lower then than required and resize it
	private void checkLineHeightAndResize(int needLineHeight){
		if (currentLineImage.getHeight() >= needLineHeight) {
			return;
		}

		BufferedImage newLineImage = createNewLineImage(needLineHeight);

		// rearrange image from currentLineImage on newLineImage
		int lineHeight = currentLineImage.getHeight();

		Graphics g = newLineImage.getGraphics();
		g.drawImage(currentLineImage, 0, needLineHeight - lineHeight, null);

		currentLineImage = newLineImage;
		checkEndPageAndCreateNew();
	}
}
