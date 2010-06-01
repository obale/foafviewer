package to.networld.android.foafviewer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Alex Oberhauser
 * 
 */
public class FOAFViewer extends Activity {
	private static final int MENU_SETTINGS = 0x0010;

	private OnItemClickListener listClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			switch (position) {
				case 0:
					mapMe();
					break;
				case 1:
					listFriends();
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ListView list = (ListView) findViewById(R.id.MAIN);
		list.setOnItemClickListener(this.listClickListener);
		ArrayList<HashMap<String, String>> buttonList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("icon", R.drawable.map + "");
		map.put("top", "Visualize You!");
		map.put("bottom", "Shows your FOAF file on a map.");
		buttonList.add(map);
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.foaf_map + "");
		map.put("top", "List Friends!");
		map.put("bottom", "List all your known agents.");
		buttonList.add(map);
		
		SimpleAdapter adapterMainList = new SimpleAdapter(this, buttonList, 
				R.layout.list_entry, new String[]{ "icon", "top", "bottom" },
				new int[] { R.id.icon, R.id.topText, R.id.bottomText });
		
		list.setAdapter(adapterMainList);
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
	private void mapMe() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		Intent mapIntent = new Intent(FOAFViewer.this, FOAFMap.class);
		mapIntent.putExtra("myFOAF", settings.getString("FOAF", "http://devnull.networld.to/foaf.rdf"));
		this.startActivity(mapIntent);
	}
	
	/**
	 * List your friends.
	 */
	private void listFriends() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Intent friendListIntent = new Intent(FOAFViewer.this, FOAFFriendListing.class);
		friendListIntent.putExtra("myFOAF", settings.getString("FOAF", ""));
		this.startActivity(friendListIntent);
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