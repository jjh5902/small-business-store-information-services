package main;

public class Store {
	private String name; // 상가명
	private String addr; // 상가 주소
	private String cls; // (소)분류
	
	private String lat;
	private String lon;
	
	public Store(String name, String addr,String cls, String lat, String lon) {
		this.name = name;
		this.addr =addr;
		this.cls = cls;
		this.lat =lat;
		this.lon = lon;
	}
	
	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	
	

}
