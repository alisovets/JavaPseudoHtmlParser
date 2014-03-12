package alisovets.example.pseudohtmlparser;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * pseudo html parser parses an render a pseudo html document
 */
public class HtmlParser extends DefaultHandler {
	private static int DEFAULT_BGCOLOR = 0xFFFFFF;
	private static int DEFAULT_TEXT_COLOR = 0;
	private static int DEFAULT_LMARGIN = 10;
	private static int DEFAULT_RMARGIN = 10;
	private static int DEFAULT_TMARGIN = 10;
	private static int DEFAULT_BMARGIN = 10;
	private static String DEFAULT_FONT_FAMILI = Font.SERIF;
	private static int DEFAULT_FONT_SIZE = 14;
	private static Align DEFAULT_ALIGN = Align.LEFT;

	/*
	 * suppurted tags
	 */
	private enum Tag {
		HTML, HEAD, TITLE, BODY, H1, H2, H3, H4, H5, H6, BR, B, I, U, P, IMG
	};

	/* file path to html document */
	private String filePath;

	/* initial page tittle */
	private String initialTitle;

	/* the parsed portion of the text for the placement on the page */
	private StringBuilder currentText = new StringBuilder();

	/* width of page */
	private int width;

	/* height of page */
	private int height;

	/* base font size */
	private int baseFontSize;

	/* attributes for the current portion of text (for the current tag ) */
	private HtmlTextAttributes currentAttributes;

	/* the stack of attributes for outer tags */
	private Stack<HtmlTextAttributes> attributesStack;
	private HtmlPagingDrawer drawer;

	/* list of contents items the documents */
	private List<ContentsItem> contents = new ArrayList<ContentsItem>();

	/* parses document */
	private void parseDocument() {
		initialTitle = filePath;

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			System.out.println("Parser started");
			saxParser.parse(filePath, this);
			System.out.println("Parser finished");

		} catch (SAXException se) {
			System.err.println("SAXException: " + se.getMessage());
		} catch (ParserConfigurationException pce) {
			System.err.println("ParserConfigurationException: " + pce.getMessage());
		} catch (IOException ie) {
			System.err.println("IOException: " + ie.getMessage());
		} catch (IllegalArgumentException iae) {
			System.err.println("IllegalArgumentException: " + iae.getMessage());
		}

	}

	@Override
	// starts tag processors
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		Tag tag = null;
		try {
			tag = Tag.valueOf(qName.toUpperCase());
		} catch (IllegalArgumentException e) {
			// ignore unknown tags
			return;
		}
		switch (tag) {
		case TITLE:
			titleTagStart();
			break;
		case BODY:
			bodyTagStart(attributes);
			break;
		case H1:
			hTagStart(1, attributes);
			break;
		case H2:
			hTagStart(2, attributes);
			break;
		case H3:
			hTagStart(3, attributes);
			break;
		case H4:
			hTagStart(4, attributes);
			break;
		case H5:
			hTagStart(5, attributes);
			break;
		case H6:
			hTagStart(6, attributes);
			break;
		case BR:
			brTagStart();
			break;
		case B:
			bTagStart(attributes);
			break;
		case I:
			iTagStart(attributes);
			break;
		case U:
			uTagStart(attributes);
			break;
		case P:
			pTagStart(attributes);
			break;
		case IMG:
			imgTagStart(attributes);
			break;

		default:
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		Tag tag = null;
		try {
			tag = Tag.valueOf(qName.toUpperCase());
		} catch (IllegalArgumentException e) {
			// ignore unknown tags
			return;
		}

		switch (tag) {
		case TITLE:
			titleTagEnd();
			break;
		case H1:
			hTagEnd(1);
			break;
		case H2:
			hTagEnd(2);
			break;
		case H3:
			hTagEnd(3);
			break;
		case H4:
			hTagEnd(4);
			break;
		case H5:
			hTagEnd(5);
			break;
		case H6:
			hTagEnd(6);
			break;
		case B:
			bTagEnd();
			break;
		case I:
			bTagEnd();
			break;
		case U:
			bTagEnd();
			break;
		case P:
			pTagEnd();
			break;
		case BODY:
			bodyTagEnd();
			break;

		default:
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		//accumulate text data 
		currentText.append(ch, start, length);
	}

	// begin <title> tag render
	private void titleTagStart() {
		currentText = new StringBuilder();
	}

	// finish <title> tag render
	private void titleTagEnd() {
		initialTitle = currentText.toString().trim().replaceAll("\\s+", " ");
		currentText = new StringBuilder();
	}

	//<br> tag render
	private void brTagStart() {
		if (currentText.length() > 0) {
			drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		}
		drawer.nextLine(currentAttributes);
		currentText = new StringBuilder();
	}

	// begin <b> tag render 
	private void bTagStart(Attributes attributes) {
		drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		currentText = new StringBuilder();
		HtmlTextAttributes newHtmlAttributes = currentAttributes.clone();
		Font oldFont = newHtmlAttributes.getFont();
		Font font = new Font(oldFont.getName(), oldFont.getStyle() | Font.BOLD, oldFont.getSize());
		newHtmlAttributes.setFont(font);
		newHtmlAttributes.setBold(true);
		setTextColorInHtmlAttributes(newHtmlAttributes, attributes);
		attributesStack.push(currentAttributes);
		currentAttributes = newHtmlAttributes;
	}

	// begin <i> tag render
	private void iTagStart(Attributes attributes) {
		drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		currentText = new StringBuilder();
		HtmlTextAttributes newHtmlAttributes = currentAttributes.clone();
		Font oldFont = newHtmlAttributes.getFont();
		Font font = new Font(oldFont.getName(), oldFont.getStyle() | Font.ITALIC, oldFont.getSize());
		newHtmlAttributes.setFont(font);
		setTextColorInHtmlAttributes(newHtmlAttributes, attributes);
		attributesStack.push(currentAttributes);
		currentAttributes = newHtmlAttributes;
	}

	// begin <u> tag render
	private void uTagStart(Attributes attributes) {
		drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		currentText = new StringBuilder();
		HtmlTextAttributes newHtmlAttributes = currentAttributes.clone();
		Font oldFont = newHtmlAttributes.getFont();
		Font font = new Font(oldFont.getName(), oldFont.getStyle(), oldFont.getSize());
		newHtmlAttributes.setFont(font);
		newHtmlAttributes.setUnderline(true);
		setTextColorInHtmlAttributes(newHtmlAttributes, attributes);
		attributesStack.push(currentAttributes);
		currentAttributes = newHtmlAttributes;
	}

	// finish <b>, <u>, <i> tags render
	private void bTagEnd() {
		if (currentText.length() > 0) {
			drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
			currentText = new StringBuilder();
		}
		currentAttributes = attributesStack.pop();
	}

	//sets the text color attribute from xml parser result
	private void setTextColorInHtmlAttributes(HtmlTextAttributes destHtmlAttributes, Attributes srcAttributes) {
		for (int i = 0; i < srcAttributes.getLength(); i++) {
			if (!srcAttributes.getQName(i).toLowerCase().equals("color")) {
				continue;
			}
			try {
				int textColor = Integer.parseInt(srcAttributes.getValue(i).substring(1), 16);
				destHtmlAttributes.setTextColor(textColor);
			} catch (NumberFormatException e) {
				System.err.println(e.getMessage());
			}
			break;
		}
	}

	
	private void setAlignInHtmlAttributes(HtmlTextAttributes destHtmlAttributes, Attributes srcAttributes) {
		for (int i = 0; i < srcAttributes.getLength(); i++) {
			if (!srcAttributes.getQName(i).toLowerCase().equals("align")) {
				continue;
			}
			try {
				Align align = Align.valueOf(srcAttributes.getValue(i).toUpperCase());
				destHtmlAttributes.setAlign(align);
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
			}

			break;
		}
	}

	// begin <p> tag render
	private void pTagStart(Attributes attributes) {
		drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		currentText = new StringBuilder();
		HtmlTextAttributes newHtmlAttributes = currentAttributes.clone();
		setAlignInHtmlAttributes(newHtmlAttributes, attributes);
		attributesStack.push(currentAttributes);
		currentAttributes = newHtmlAttributes;
		drawer.endLine(currentAttributes);
	}

	// finish <p> tag render
	private void pTagEnd() {
		if (currentText.length() > 0) {
			drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		}
		currentAttributes = attributesStack.pop();
		drawer.endLine(currentAttributes);
		currentText = new StringBuilder();
	}

	// <img> tag render
	private void imgTagStart(Attributes attributes) {
		drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		currentText = new StringBuilder();
		String filePath = "";
		int imgWidth = 0;
		int imgHeight = 0;
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			String qName = attributes.getQName(i).toLowerCase();
			if (qName.equals("src")) {
				filePath = attributes.getValue(i);
			} else if (qName.equals("width")) {
				try {
					imgWidth = Integer.parseInt(attributes.getValue(i));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			} else if (qName.equals("height")) {
				try {
					imgHeight = Integer.parseInt(attributes.getValue(i));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		if ((imgHeight > 0) && (imgWidth > 0)) {
			drawer.drawImage(currentAttributes, filePath, imgWidth, imgHeight);
		}
		// else ignore tag
	}

	// begin <h1>...<h6> tag render
	private void hTagStart(int level, Attributes attributes) {
		drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		currentText = new StringBuilder();

		HtmlTextAttributes newHtmlAttributes = currentAttributes.clone();
		Font oldFont = newHtmlAttributes.getFont();
		Font font = new Font(oldFont.getName(), Font.PLAIN | Font.BOLD, baseFontSize + 12 - level * 2);
		newHtmlAttributes.setFont(font);
		newHtmlAttributes.setBold(false);
		newHtmlAttributes.setUnderline(false);
		setTextColorInHtmlAttributes(newHtmlAttributes, attributes);
		attributesStack.push(currentAttributes);
		currentAttributes = newHtmlAttributes;
		drawer.endLine(currentAttributes);
		drawer.nextLine(currentAttributes);
	}

	// finish <h1>...<h6> tag render
	private void hTagEnd(int level) {
		if (currentText.length() > 0) {
			String currentStr = currentText.toString().replaceAll("\\s+", " ");
			int pageNumber = drawer.drawText(currentAttributes, currentStr, true);
			ContentsItem contentItem = new ContentsItem(currentStr.trim(), pageNumber, level);
			if ((pageNumber > 1) && (contents.size() == 0)) {
				ContentsItem firstContentItem = new ContentsItem(initialTitle, 1, 1);
				contents.add(firstContentItem);
			}
			contents.add(contentItem);
		}
		currentAttributes = attributesStack.pop();
		drawer.endLine(currentAttributes);
		currentText = new StringBuilder();
	}

	// begin <body> tag render
	private void bodyTagStart(Attributes attributes) {
		currentText = new StringBuilder();

		int bgColor = DEFAULT_BGCOLOR;
		int textColor = DEFAULT_TEXT_COLOR;
		int lMargin = DEFAULT_LMARGIN;
		int rMargin = DEFAULT_RMARGIN;
		int topMargin = DEFAULT_TMARGIN;
		int bottomMargin = DEFAULT_BMARGIN;
		String fontFamily = DEFAULT_FONT_FAMILI;
		baseFontSize = DEFAULT_FONT_SIZE;

		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			String qName = attributes.getQName(i).toLowerCase();
			if (qName.equals("bgcolor")) {
				bgColor = Integer.parseInt(attributes.getValue(i).substring(1), 16);
			} else if (qName.equals("text")) {
				try {
					textColor = Integer.parseInt(attributes.getValue(i).substring(1), 16);
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			} else if (qName.equals("leftmargin")) {
				try {
					lMargin = Integer.parseInt(attributes.getValue(i));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			} else if (qName.equals("topmargin")) {
				try {
					topMargin = Integer.parseInt(attributes.getValue(i));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			} else if (qName.equals("rightmargin")) {
				try {
					rMargin = Integer.parseInt(attributes.getValue(i));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			} else if (qName.equals("bottommargin")) {
				try {
					bottomMargin = Integer.parseInt(attributes.getValue(i));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			} else if (qName.equals("font-size")) {
				try {
					baseFontSize = Integer.parseInt(attributes.getValue(i));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			} else if (qName.equals("font-family")) {
				fontFamily = attributes.getValue(i);
			}

		}
		drawer = new HtmlPagingDrawer(width, height, bgColor, lMargin, rMargin, topMargin, bottomMargin, baseFontSize,
				filePath);
		if (initialTitle != null) {
			drawer.setPageTitle(initialTitle);
		}
		currentAttributes = new HtmlTextAttributes(textColor, 0, new Font(fontFamily, Font.PLAIN, baseFontSize),
				DEFAULT_ALIGN, false, false);
		attributesStack = new Stack<HtmlTextAttributes>();
	}

	// finish <body> tag render
	private void bodyTagEnd() {
		if (currentText.length() > 0) {
			drawer.drawText(currentAttributes, currentText.toString().replaceAll("\\s+", " "), false);
		}
		drawer.nextPage();
		currentText = new StringBuilder();
		renderContents();

	}

	
	private void renderContents() {
		int pageNumber = drawer.drawText(currentAttributes, "Contents", true);
		ContentsItem contentItem = new ContentsItem("Contents", pageNumber, 1);
		contents.add(contentItem);

		int identationUnit = currentAttributes.getFont().getSize();
		for (ContentsItem item : contents) {
			drawer.endLine(currentAttributes);
			String itemString = item.getName() + " " + item.getPageNumber();
			currentAttributes.setIndentation(identationUnit * (item.getLevel() - 1));
			drawer.drawText(currentAttributes, itemString, false);
		}
		drawer.endLine(currentAttributes);
		drawer.nextPage();
	}

	
	public static void main(String[] args) {
		//expects parameters in the format: '-i <filepath> -w <width> -h <hight>'

		if ((args.length != 6) || !args[0].equals("-i") || !args[2].equals("-w") || !args[4].equals("-h")) {
			throw new IllegalArgumentException("Wrong arguments! Use '-i filepath -w width -h hight'!");
		}

		int width = Integer.parseInt(args[3]);
		int height = Integer.parseInt(args[5]);

		HtmlParser htmlPatrser = new HtmlParser();
		htmlPatrser.filePath = args[1];
		htmlPatrser.width = width;
		htmlPatrser.height = height;
		htmlPatrser.parseDocument();
	}
}
