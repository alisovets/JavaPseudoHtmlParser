package alisovets.example.pseudohtmlparser;
/**
 * represents an item of contents
 */
public class ContentsItem {
	/**	the item name */
	private String name;
	
	/**	the number of page*/
	private int pageNumber;
	
	/** the nesting level of the item in hierarchical contents */ 
	private int level;
	
	/**
	 * Create a new ContentsItem with the specified attributes.  
	 * @param name the item name
	 * @param pageNumber the number of the page 
	 * @param level the nesting level of the item in hierarchical contents
	 */
	public ContentsItem(String name, int pageNumber, int level) {
		this.name = name;
		this.pageNumber = pageNumber;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
}
