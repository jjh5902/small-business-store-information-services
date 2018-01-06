package main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.MapViewOptions;
import com.teamdev.jxmaps.swing.MapView;

public class MainFrame extends JFrame implements ActionListener {

	private Container cP;
	// GoogleMap
	private final MapViewOptions options = new MapViewOptions();
	private final GoogleMap mapView = new GoogleMap(options);

	// 분류
	private final JLabel label1 = new JLabel("대분류");
	final private JComboBox combo1 = new JComboBox(); // 대분류
	private final JLabel label2 = new JLabel("중분류");
	final private JComboBox combo2 = new JComboBox(); // 중분류
	private final JLabel label3 = new JLabel("소분류");
	final private JComboBox combo3 = new JComboBox(); // 소분류

	private HashMap<String, ArrayList<String>> classMap1 = new HashMap<String, ArrayList<String>>(); // 대분류 -> 중분류
	private HashMap<String, ArrayList<String>> classMap2 = new HashMap<String, ArrayList<String>>(); // 중분류 -> 소분류
	private HashMap<String, String> classMap = new HashMap<String, String>(); // 소분류코드명 -> 코드

	// 도로명
	private JTextField doroField = new JTextField(15);

	// 버튼
	private JButton searchBtn = new JButton("검색");
	private JButton checkBtn = new JButton("확인");

	// 상권
	final private JComboBox areaCombo = new JComboBox();
	// =======================================================================================================

	public MainFrame() {

		readExcel(); // 분류 파일 읽기
		//
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("경쟁 점포 분석 어플리케이션");
		//
		connect();
		initCombo(0, ""); // 콤보박스 초기화
		initUI();
		//
		setBounds(0, 0, 1000, 1000);
		setVisible(true);
		setLocationRelativeTo(null);

	}

	// =======================================================================================================

	public void initUI() { // UI 초기화

		// 구글 맵
		cP = getContentPane();
		cP.setLayout(null);
		mapView.setBounds(70, 20, 850, 600);
		mapView.setBorder(new TitledBorder(null, "구글 맵", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		cP.add(mapView);

		// 도로명 입력
		JPanel doroPanel = new JPanel();
		doroPanel.setBounds(60, 680, 250, 80);
		doroPanel.setBorder(new TitledBorder(null, "위치 입력", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		doroPanel.add(doroField);
		cP.add(doroPanel);

		// 분류 입력
		JPanel classPanel = new JPanel();
		classPanel.setBounds(330, 650, 600, 150);
		classPanel.setBorder(new TitledBorder(null, "분류", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(2, 3, 5, 20));
		labelPanel.add(label1);
		labelPanel.add(label2);
		labelPanel.add(label3);
		labelPanel.add(combo1);
		labelPanel.add(combo2);
		labelPanel.add(combo3);
		classPanel.add(labelPanel);
		cP.add(classPanel);

		// 검색 버튼
		searchBtn.setBounds(400, 850, 130, 60);
		cP.add(searchBtn);

	}

	public void connect() {
		combo1.addActionListener(this);
		combo2.addActionListener(this);
		combo3.addActionListener(this);
		searchBtn.addActionListener(this);
		checkBtn.addActionListener(this);
	}

	public void initCombo(int type, String key) { // 콤보 박스 내용 초기화

		switch (type) {
		case 0: // 대분류
		{
			Iterator<String> iter = classMap1.keySet().iterator();
			while (iter.hasNext()) {
				String item = new String(iter.next().toString());
				combo1.addItem(item);
			}
		}
			break;
		case 1: // 중분류
		{
			ArrayList<String> arr = classMap1.get(key);
			System.out.println("중분류 사이즈 : " + arr.size());

			combo3.removeAllItems();
			combo2.removeAllItems();
			for (int i = 0; i < arr.size(); i++) {
				String item = new String(arr.get(i));

				combo2.addItem(item);
			}
		}
			break;
		case 2: // 소분류
		{
			ArrayList<String> arr = classMap2.get(key);
			System.out.println("소분류 사이즈 : " + arr.size());

			combo3.removeAllItems();
			for (int i = 0; i < arr.size(); i++) {
				String item = new String(arr.get(i));

				combo3.addItem(item);
			}
		}
			break;
		default:
			return;
		}

	}

	public void readExcel() {

		// 엑셀 파일
		File file = new File("./src/myFile/classification_code.xlsx");

		// 엑셀 파일 오픈
		try {
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));

			for (Row row : wb.getSheetAt(0)) {
				if (row.getRowNum() < 1)
					continue;

				// 대분류-중분류
				if (classMap1.containsKey(row.getCell(1).toString())
						&& !(classMap1.get(row.getCell(1).toString()).contains((String) row.getCell(3).toString()))) {

					classMap1.get(row.getCell(1).toString()).add(row.getCell(3).toString());
				} else if (!(classMap1.containsKey(row.getCell(1).toString()))) {
					classMap1.put(row.getCell(1).toString(), new ArrayList<String>());
					classMap1.get(row.getCell(1).toString()).add(row.getCell(3).toString());
				}

				// 중분류-소분류
				if (classMap2.containsKey(row.getCell(3).toString())) {
					classMap2.get(row.getCell(3).toString()).add(row.getCell(5).toString());
				} else {
					classMap2.put(row.getCell(3).toString(), new ArrayList<String>());
					classMap2.get(row.getCell(3).toString()).add(row.getCell(5).toString());
				}

				// 소분류 코드-코드명
				classMap.put(row.getCell(5).toString(), row.getCell(4).toString());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// =======================================================================================================

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		Object obj = e.getSource();

		if (obj == combo1 && combo1.getSelectedItem() != null) {

			initCombo(1, combo1.getSelectedItem().toString());

		} else if (obj == combo2 && combo2.getSelectedItem() != null) {

			initCombo(2, combo2.getSelectedItem().toString());

		} else if (obj == combo3) {

		} else if (obj == searchBtn && !this.doroField.getText().toString().equals("")
				&& combo2.getSelectedItem().toString() != null) { // 도로명 != null && 소분류 != null

			LocalDB.clear(); // 로컬 DB의 내역 삭제

			try {
				AddressToGps atg = new AddressToGps(doroField.getText().toString());
				Float[] gps = atg.getGps(); // 좌표 Lat/Lng

				mapView.setCenter((new LatLng(gps[0].doubleValue(), gps[1].doubleValue()))); // + 기준 위치 마커 색깔 바꾸기

				GpsToData gtd = new GpsToData(gps); // xml 정보
				DataToSearch dts = new DataToSearch(gtd.getData());
				dts.SaveAreaToDB(); // xml 파싱 후 로컬 DB에 저장

				showSelectFrame(); // 상권 선택받도록 새 창 띄움

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (obj == checkBtn && !areaCombo.getSelectedItem().toString().equals("")) { // 상권 선택 후
			int idx = areaCombo.getSelectedIndex();

			String poly = LocalDB.getAreas().get(idx).getPoly();

			// 오버레이
			ArrayList<LatLng> arr = new ArrayList<LatLng>();

			System.out.println("Poly -> " + poly);
			StringTokenizer tkn = new StringTokenizer(poly, "POLYGON (),");

			while (tkn.hasMoreTokens()) {
				double Lng = Double.parseDouble(tkn.nextToken());
				double Lat = Double.parseDouble(tkn.nextToken());

				arr.add(new LatLng(Lat, Lng));
			}
			mapView.overay(arr);

			// 해당 상권의 상가 리스트를 Local DB에 저장 (대분류에 해당하는 모든)
			String cls = classMap.get(combo3.getSelectedItem().toString()); // 소분류

			AddressToGps atg;
			try {
				atg = new AddressToGps(doroField.getText().toString());
				Float[] gps = atg.getGps(); // 좌표 Lat/Lng
				GpsToData gtd = new GpsToData(gps); // xml 정보
				DataToSearch dts = new DataToSearch(gtd.getData());
				dts.SaveSearchToDB(cls, idx);

				for (int i = 0; i < LocalDB.getStores().size(); i++) {

					System.out.println(LocalDB.getStores().get(i).getName());
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// 마커 추가 (소분류에 해당하는 것들만)
			mapView.addMarkers(cls);

			// 결과 창
			showResultFrame(cls);
		}
	}

	// =======================================================================================================

	public void showSelectFrame() {

		JFrame showFrame = new JFrame();
		showFrame.setTitle("상권 리스트 선택 창");
		showFrame.setBounds(750, 300, 450, 150);
		//
		JPanel panel1 = new JPanel();
		panel1.setBounds(770, 320, 300, 100);
		panel1.setBorder(new TitledBorder(null, "상권 리스트", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		// combo
		areaCombo.removeAllItems();
		areaCombo.setBounds(770, 380, 100, 100);

		for (int i = 0; i < LocalDB.getAreas().size(); i++) {
			areaCombo.addItem(LocalDB.getAreas().get(i).getName());
		}

		panel1.add(areaCombo);
		// checkBtn
		checkBtn.setBounds(880, 600, 60, 80);
		panel1.add(checkBtn);

		showFrame.add(panel1);
		showFrame.setVisible(true);
	}

	public void showResultFrame(String cls) {

		String cls2 = cls.charAt(0) + cls.charAt(1) + cls.charAt(2) + ""; // 중분류

		JFrame resultFrame = new JFrame();
		resultFrame.setTitle("검색 결과 창");
		resultFrame.setLayout(new BorderLayout(4, 4));
		resultFrame.setBounds(0,0,1000,1000);

		// 패널1
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(1, 3, 4, 4));
		panel1.add(new JLabel(combo1.getSelectedItem().toString() ,JLabel.CENTER));
		panel1.add(new JLabel(combo2.getSelectedItem().toString(),JLabel.CENTER));
		panel1.add(new JLabel(combo3.getSelectedItem().toString(),JLabel.CENTER));

		// 패널2
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1, 3, 4, 4));
		JTextArea textScrollArea1 = new JTextArea();
		JTextArea textScrollArea2 = new JTextArea();
		JTextArea textScrollArea3 = new JTextArea();
		panel2.add(new JScrollPane(textScrollArea1));
		panel2.add(new JScrollPane(textScrollArea2));
		panel2.add(new JScrollPane(textScrollArea3));

		int size1 = 0;
		int size2 = 0;
		int size3 = 0;
		// init textScrollArea
		for (int i = 0; i < LocalDB.getStores().size(); i++) {
			String storeName = LocalDB.getStores().get(i).getName();
			String storeCls = LocalDB.getStores().get(i).getCls();// 상가 소분류
			String storeCls2 = storeCls.charAt(0) + storeCls.charAt(1) + storeCls.charAt(2) + "";// 상가 중분류

			textScrollArea1.append(storeName + "\n");
			size1++;
			if (storeCls.equals(cls)) {
				textScrollArea3.append(storeName + "\n");
				size3++;
			}
			if (storeCls2.equals(cls2)) {
				textScrollArea2.append(storeName + "\n");
				size2++;
			}
		}

		// 패널3
		JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayout(1, 3, 4, 4));
		panel3.add(new JLabel(size1 + "",JLabel.CENTER));
		panel3.add(new JLabel(size2 + "",JLabel.CENTER));
		panel3.add(new JLabel(size3 + "",JLabel.CENTER));

		// 프레임
		resultFrame.add(panel1, BorderLayout.NORTH);
		resultFrame.add(panel2, BorderLayout.CENTER);
		resultFrame.add(panel3, BorderLayout.SOUTH);
		resultFrame.setSize(500, 500);
		resultFrame.setVisible(true);

	}
}
