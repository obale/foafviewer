package to.networld.android.foafviewer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.dom4j.DocumentException;

import to.networld.android.foafviewer.model.AgentHandler;
import to.networld.android.foafviewer.model.AgentSerializable;
import to.networld.android.foafviewer.model.HTMLProfileHandler;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
	private final Context context = this;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.map);
		final MapView mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		
		/**
		 * TODO: Read this values from the config file.
		 */
		mapView.setSatellite(false);
		mapView.setStreetView(true);

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
				
				String agentURL = getIntent().getStringExtra("myFOAF");
				try {
					AgentSerializable agent = new AgentHandler(new URL(agentURL)).getSerializableObject();
					GeoPoint geoPoint = new GeoPoint(
							(int) (agent.getLatitude() * 1E6),
							(int) (agent.getLongitude() * 1E6));
					OverlayItem meItem = new OverlayItem(geoPoint, "FOAF Agent",
							HTMLProfileHandler.getHTMLDescription(context, agent));
					foafOverlay.addOverlay(meItem);
					mapOverlays.add(foafOverlay);
					mapView.getController().setCenter(geoPoint);
					mapView.getController().setZoom(17);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				} finally {
					progressDialog.dismiss();
				}
			}
		};
		foafParser.start();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
