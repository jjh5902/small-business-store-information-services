package main;

public class Area {
	private String name;
	private String poly;
	
	public Area(String name, String poly) {
		this.name = name;
		this.poly = poly;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPoly() {
		return poly;
	}
	public void setPoly(String poly) {
		this.poly = poly;
	}

}
