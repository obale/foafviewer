package to.networld.android.foafviewer;

import java.util.List;

import to.networld.android.foafviewer.model.Agent;
import to.networld.android.foafviewer.model.AgentHandler;
import to.networld.android.foafviewer.model.HTMLProfileHandler;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * The map that visualizes your FOAF file.
 * 
 * @author Alex Oberhauser
 * @deprecated
 * 
 */
public class FOAFMap extends MapActivity {
	private final Context context = FOAFMap.this;
	private Agent agent = null;
	
	private final OnDismissListener errorDialogDismissedListener = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			finish();
		}
	};
	

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		final ProgressDialog progressDialog = ProgressDialog.show(FOAFMap.this, null, "Parsing FOAF file...", true);
		setContentView(R.layout.map);
		final MapView mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		
		/**
		 * TODO: Read this values from a configuration file.
		 */
		mapView.setSatellite(false);
		mapView.setStreetView(true);

		
		String agentURL = getIntent().getStringExtra("agent");
		try {
			agent = AgentHandler.initAgent(agentURL, context);
			
		/**
		 * Initiating the FOAF map overlay.
		 */
		Thread foafParser = new Thread() {
			public void run() {
					List<Overlay> mapOverlays = mapView.getOverlays();
					Drawable drawable = getResources().getDrawable(
							R.drawable.foaf_map);
					FOAFOverlay foafOverlay = new FOAFOverlay(drawable,
							FOAFMap.this);
				
					try {
						Pair<Double, Double> gpsPoint = agent.getLocation();
						GeoPoint geoPoint = new GeoPoint(
								(int) (gpsPoint.first * 1E6),
								(int) (gpsPoint.second * 1E6));
						OverlayItem meItem = new OverlayItem(geoPoint, "FOAF Agent",
								HTMLProfileHandler.getHTMLDescription(context, agent));
						foafOverlay.addOverlay(meItem);
						mapOverlays.add(foafOverlay);
						mapView.getController().setCenter(geoPoint);
						mapView.getController().setZoom(17);
						progressDialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						progressDialog.dismiss();
					}
				}
			};
			foafParser.start();
		} catch (Exception e) {
			progressDialog.dismiss();
			GenericDialog errorDialog = new GenericDialog(context, "Error", e.getLocalizedMessage(), R.drawable.error_icon);
			errorDialog.setOnDismissListener(errorDialogDismissedListener);
			errorDialog.show();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
