package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class GpsToData {
	private Float[] gps;
	private String myurl = "http://apis.data.go.kr/B553077/api/open/sdsc/storeZoneInRadius";
	private String key = "&ServiceKey=Put your key"; // * Put your key from https://www.data.go.kr/dataset/15012005/openapi.do
	private String parameter = "";
	URL url = null;
	
	GpsToData(Float[] coords) {
		gps = coords;
		parameter = "&radius="+ 1000 + "&cx=" + gps[1] +"&cy=" + gps[0];
	}
	
	
	public String getData() throws UnsupportedEncodingException, IOException {
		url = new URL(myurl+key+parameter);
		System.out.println("url --> " + url);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		String inLine;
		String data ="";
		while((inLine = in.readLine()) != null) {
			data+=inLine;
		}
		System.out.println("Data --> " + data);
		return data;
		
	}

}
