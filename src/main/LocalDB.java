package main;

import java.util.ArrayList;

public class LocalDB {

	private static ArrayList<Area> areas = new ArrayList<Area>();
	private static ArrayList<Store> stores = new ArrayList<Store>();
	
	public static ArrayList<Area> getAreas() {
		return areas;
	}
	public static ArrayList<Store> getStores() {
		return stores;
	}
	
	public static void clear() {
		areas.clear();
		stores.clear();
	}
}
