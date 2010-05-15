package to.networld.android.foafviewer;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import to.networld.android.foafviewer.model.AgentSerializable;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 * @author Alex Oberhauser
 * 
 */
public class FOAFMap extends MapActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.map);
		final MapView mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		final ProgressDialog progressDialog = ProgressDialog.show(FOAFMap.this,
				null, "Parsing FOAF file...", false);

		/**
		 * Initiating the FOAF map overlay.
		 */
		Thread foafParser = new Thread() {
			public void run() {
				Looper.prepare();
				List<Overlay> mapOverlays = mapView.getOverlays();
				Drawable drawable = getResources().getDrawable(
						R.drawable.foaf_map);
				FOAFOverlay foafOverlay = new FOAFOverlay(drawable,
						FOAFMap.this);
				AgentSerializable agent = (AgentSerializable)getIntent().getSerializableExtra("agent");
				GeoPoint geoPoint = new GeoPoint(
						(int) (agent.getLatitude() * 1E6),
						(int) (agent.getLongitude() * 1E6));
				OverlayItem meItem = new OverlayItem(geoPoint, "FOAF Agent",
						setHTMLDescription(agent));
				foafOverlay.addOverlay(meItem);
				mapOverlays.add(foafOverlay);
				mapView.getController().setCenter(geoPoint);
				mapView.getController().setZoom(17);
				progressDialog.dismiss();
			}
		};
		foafParser.start();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private String setHTMLDescription(AgentSerializable _agent) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append("<center><h2><font color='blue'>");
		strbuffer.append("<a href='" + _agent.getWebsite() + "'>");
		strbuffer.append(_agent.getAgentName());
		strbuffer.append("</a>");
		strbuffer.append("</font></h2></center>");
		strbuffer.append("<center><img src='" + _agent.getImageURL()
				+ "' width='100px'></center>");
		strbuffer.append("<table align='center' border='0'>");
		strbuffer.append("<tr><td><b>GPS</b></td><td>");
		strbuffer.append(_agent.getLatitude());
		strbuffer.append(",");
		strbuffer.append(_agent.getLongitude());
		strbuffer.append("</td></tr>");
		try {
			Address address = geocoder.getFromLocation(
					_agent.getLatitude(),
					_agent.getLongitude(), 1).get(0);

			String plz = address.getPostalCode();
			String city = address.getLocality();
			strbuffer.append("<tr><td><b>Location</b></td><td>");
			strbuffer.append(plz + " " + city);
			strbuffer.append("</td></tr>");

			String street = address.getAddressLine(0);
			strbuffer.append("<tr><td><b>Streetname</b></td><td>");
			strbuffer.append(street);
			strbuffer.append("</td></tr>");
		} catch (Exception e) {
		}
		strbuffer.append("</table>");
		strbuffer.append("<hr>");
		
		Vector<String> interests = _agent.getInterests();
		strbuffer.append("<center><h3>Interests</h3></center>");
		strbuffer.append("<ul>");
		for (int count = 0; count < interests.size(); count++) {
			strbuffer.append("<li> " + interests.get(count));
		}
		strbuffer.append("</ul>");
		strbuffer.append("<hr>");
		
		Vector<String> knownAgentsName = _agent.getKnownAgentsNames();
		strbuffer.append("<center><h3>Known Agents</h3></center>");
		strbuffer.append("<ul>");
		for (int count = 0; count < knownAgentsName.size(); count++) {
			strbuffer.append("<li> " + knownAgentsName.get(count));
		}
		strbuffer.append("</ul>");
		strbuffer.append("<hr>");
		
		return strbuffer.toString();
	}
}
