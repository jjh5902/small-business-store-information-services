package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class DataToSearch {
	String data;
	String polygon;
	
	private String myurl = "http://apis.data.go.kr/B553077/api/open/sdsc/storeListInPolygon?";
	private String key = "&ServiceKey=Put your key"; // * Put your key from https://www.data.go.kr/dataset/15012005/openapi.do
	private String parameter = "";
	URL url = null;
	XML_Parser parser = new XML_Parser();

	
	DataToSearch(String data) throws ParserConfigurationException {
		this.data = data;
	}
	
	
	public void SaveAreaToDB() throws UnsupportedEncodingException, IOException, SAXException {
		LocalDB myDB = new LocalDB();
		StringTokenizer areaST = new StringTokenizer(parser.getTrarNm(data), "&&");
		StringTokenizer ployST = new StringTokenizer(parser.getCoords(data), "&&");
		while(areaST.hasMoreTokens() && ployST.hasMoreTokens())
			myDB.getAreas().add(new Area(areaST.nextToken(),ployST.nextToken()));
	}
	
	public void SaveSearchToDB(String clsCd, int order) throws SAXException, IOException {
		polygon = parser.getPolygon(data, order);

        System.out.println("검색중..");
        // 검색제한 수 "&numOfRows=" + "10" +  
        // http://apis.data.go.kr/B553077/api/open/sdsc/storeListInPolygon?key=POLYGON%20((126.9204728%2035.1523226,126.9192728%2035.1531925,126.9181257%2035.1522507,126.9193808%2035.1514315,126.9204728%2035.1523226))&ServiceKey=
        parameter = "key=" + polygon;
        System.out.println(myurl+parameter+key + "");
        url = new URL(myurl+parameter+key+"&indsLclsCd=" + clsCd.charAt(0) + "&numOfRows=1000");

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		String inLine;
		String data ="";
		while((inLine = in.readLine()) != null)
			data+=inLine;
		
//		String store = parser.getStore(data);
		StringTokenizer storeST = new StringTokenizer(parser.getStore(data), "###");
		StringTokenizer informST;
		LocalDB myDB = new LocalDB();
		while(storeST.hasMoreTokens()) {
			informST = new StringTokenizer(storeST.nextToken(), "&&");
			myDB.getStores().add(new Store(informST.nextToken(), informST.nextToken(),informST.nextToken(), informST.nextToken(), informST.nextToken()));
		}
	}
}
