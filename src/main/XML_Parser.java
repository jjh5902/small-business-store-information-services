package main;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XML_Parser {
	
    private DocumentBuilderFactory t_dbf = DocumentBuilderFactory.newInstance();
    private DocumentBuilder t_db = null;
    private Document t_doc = null;
    private NodeList t_nodes = null;
    private Node t_node = null;
    private Element t_element = null;
    private InputSource t_is = new InputSource();

    XML_Parser() throws ParserConfigurationException{
    	t_db = t_dbf.newDocumentBuilder();
    }
    
    
    public String getTrarNm(String data) throws SAXException, IOException {
		String str = "";
        t_is.setCharacterStream(new StringReader(data));
        t_doc = t_db.parse(t_is);
        t_nodes = t_doc.getElementsByTagName("item");
        for(int i=0 ; i < t_nodes.getLength() ; i++){
            t_node = t_nodes.item(i);
            t_element = (Element)t_node;
            str += t_element.getElementsByTagName("mainTrarNm").item(0).getTextContent() + "&&";
        }
        return str;
    }
    
    public String getCoords(String data) throws SAXException, IOException {
		String str = "";
        t_is.setCharacterStream(new StringReader(data));
        t_doc = t_db.parse(t_is);
        t_nodes = t_doc.getElementsByTagName("item");
        for(int i=0 ; i < t_nodes.getLength() ; i++){
            t_node = t_nodes.item(i);
            t_element = (Element)t_node;
            str += t_element.getElementsByTagName("coords").item(0).getTextContent() +"&&";
        }
        return str;
    }
    
    public String getPolygon(String data, int order) throws SAXException, IOException {
		String str = "";
        t_is.setCharacterStream(new StringReader(data));
        t_doc = t_db.parse(t_is);
        t_nodes = t_doc.getElementsByTagName("item");
        t_node = t_nodes.item(order);
        t_element = (Element)t_node;
        str = (t_element.getElementsByTagName("coords").item(0).getTextContent()).replaceAll(" ", "%20");
        return str;
    }
    
    
    public String getStore(String data) throws SAXException, IOException {
		String str = "";
        t_is.setCharacterStream(new StringReader(data));
        t_doc = t_db.parse(t_is);
        t_nodes = t_doc.getElementsByTagName("item");
        for(int i=0 ; i < t_nodes.getLength() ; i++){
            t_node = t_nodes.item(i);
            t_element = (Element)t_node;
            str += t_element.getElementsByTagName("bizesNm").item(0).getTextContent() + "&&"; // 상가 이름
            str += t_element.getElementsByTagName("lnoAdr").item(0).getTextContent() + "&&"; // 상가 주소
            str += t_element.getElementsByTagName("indsSclsCd").item(0).getTextContent() + "&&"; // 소분류
            // 좌표
            str += t_element.getElementsByTagName("lat").item(0).getTextContent() + "&&"; 
            str += t_element.getElementsByTagName("lon").item(0).getTextContent() + "###";
        }
        return str;
    }    
}
