package main;

import com.teamdev.jxmaps.*;
import com.teamdev.jxmaps.Polygon;
import com.teamdev.jxmaps.swing.MapView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GoogleMap extends MapView implements MapReadyHandler {

	private Map map;
	private Marker curMarker;
	private Polygon polygon;
	private Marker[] markers = new Marker[5000];

	public GoogleMap(MapViewOptions options) {
		super(options);
		setOnMapReadyHandler(this);

	}

	@Override
	public void onMapReady(MapStatus status) {
		// TODO Auto-generated method stub
		if (status == MapStatus.MAP_STATUS_OK) {
			map = getMap();
			map.setZoom(15.0);
			//
			for (int i = 0; i < markers.length; i++) {
				markers[i] = new Marker(map);
			}
			//
			polygon = new Polygon(map);
			//
			GeocoderRequest request = new GeocoderRequest(map);
			request.setAddress("서울, 한국");

			getServices().getGeocoder().geocode(request, new GeocoderCallback(map) {

				@Override
				public void onComplete(GeocoderResult[] result, GeocoderStatus status) {
					// TODO Auto-generated method stub
					if (status == GeocoderStatus.OK) {
						map.setCenter(result[0].getGeometry().getLocation());

						curMarker = new Marker(map);
						curMarker.setPosition(result[0].getGeometry().getLocation());

						final InfoWindow window = new InfoWindow(map);
						window.setContent("기준 위치");
						window.open(map, curMarker);
					}

				}
			});

		}
	}

	public void setCenter(LatLng latLng) {
		map.setCenter(latLng);
		curMarker.setPosition(latLng);
	}

	public void overay(ArrayList<LatLng> arr) {
		LatLng[] path = new LatLng[arr.size()];
		for (int i = 0; i < path.length; i++) {
			path[i] = arr.get(i);
		}

		polygon.setPath(path);

		PolygonOptions options = new PolygonOptions();
		options.setFillColor("#FF0000");
		options.setFillOpacity(0.35);
		options.setStrokeColor("#FF0000");
		options.setStrokeOpacity(0.8);
		options.setStrokeWeight(2.0);
		polygon.setOptions(options);
	}

	public void addMarkers(String cls) {

		for (int i = 0; i < markers.length; i++) {
			if(markers[i] == null)
				continue;
			
			markers[i].remove();
		}

		for (int i = 0; i < LocalDB.getStores().size(); i++) {
			if(!LocalDB.getStores().get(i).getCls().equals(cls)) // 소분류가 같은 것들만 마커 생성
					continue;
			
			double lat = Double.parseDouble(LocalDB.getStores().get(i).getLat());
			double lng = Double.parseDouble(LocalDB.getStores().get(i).getLon());

			markers[i] = new Marker(map);
			markers[i].setPosition(new LatLng(lat, lng));

			InfoWindow window = new InfoWindow(map);
			window.setContent(LocalDB.getStores().get(i).getName() + "\n" + LocalDB.getStores().get(i).getAddr());
			window.open(map, markers[i]);
		}
	}
}
