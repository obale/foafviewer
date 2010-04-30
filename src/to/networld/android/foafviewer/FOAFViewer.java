package to.networld.android.foafviewer;

import java.net.URL;

import to.networld.android.foafviewer.model.Agent;
import to.networld.android.foafviewer.model.StringSerializable;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author Alex Oberhauser
 * 
 */
public class FOAFViewer extends Activity {
	private static final int MENU_SETTINGS = 0x0010;

	private OnClickListener mapMeButtonListener = new OnClickListener() {
		public void onClick(View view) {
			mapMe();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button mapMeButton = (Button) findViewById(R.id.mapMeButton);
		if (mapMeButton != null)
			mapMeButton.setOnClickListener(this.mapMeButtonListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SETTINGS, 20, "Settings").setIcon(
				R.drawable.settings_icon);
		return true;
	}

	/**
	 * Show you on map!
	 */
	public void mapMe() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		try {
			Agent foafAgent = new Agent(new URL(settings.getString("FOAF", "")));
			Pair<Double, Double> location = foafAgent.getLocation();
			Intent mapIntent = new Intent(FOAFViewer.this, FOAFMap.class);
			mapIntent.putExtra("latitude", location.first);
			mapIntent.putExtra("longitude", location.second);
			mapIntent.putExtra("name", foafAgent.getName());
			mapIntent.putExtra("img", foafAgent.getImageURL());
			mapIntent.putExtra("website", foafAgent.getWebsite());
			mapIntent.putExtra("interests", new StringSerializable(foafAgent
					.getInterests()));
			mapIntent.putExtra("knownAgentsName", new StringSerializable(
					foafAgent.getKnownAgentsNames()));
			this.startActivity(mapIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SETTINGS:
			Intent intent = new Intent(FOAFViewer.this, FOAFSettings.class);
			this.startActivity(intent);
			return true;
		}
		return false;
	}
}