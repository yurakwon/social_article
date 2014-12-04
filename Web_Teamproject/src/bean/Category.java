package bean;

import java.util.*;

public class Category {
	private List<String> list;
	
	public Category() {
	}

	public Category(List<String> categorylist) {
		super();
		this.list = categorylist;
	}
	
	public List<String> getlist() { return list; }
	public void setlist(List<String> categorylist) { this.list = categorylist; }

}
