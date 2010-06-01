package to.networld.android.foafviewer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
	private static final int MENU_ABOUT = 0x0020;

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
				case 2:
					shareFOAFFile();
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
		map.put("icon", R.drawable.foaf_map + "");
		map.put("top", "Visualize You!");
		map.put("bottom", "Shows your FOAF file on a map.");
		buttonList.add(map);
		
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.avatar_icon + "");
		map.put("top", "List Friends!");
		map.put("bottom", "List all your known agents.");
		buttonList.add(map);
		
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.share_icon + "");
		map.put("top", "Share FOAF file!");
		map.put("bottom", "Share your FOAF file with your friends or with the world.");
		buttonList.add(map);
		
		SimpleAdapter adapterMainList = new SimpleAdapter(this, buttonList, 
				R.layout.list_entry, new String[]{ "icon", "top", "bottom" },
				new int[] { R.id.icon, R.id.topText, R.id.bottomText });
		
		list.setAdapter(adapterMainList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SETTINGS, 20, "Settings").setIcon(R.drawable.settings_icon);
		menu.add(0, MENU_ABOUT, 30, "About").setIcon(R.drawable.about_icon);
		return true;
	}

	/**
	 * Show you on map!
	 * TODO: Don't add a fix URL. Force the user to add his/her own UR
	 */
	private void mapMe() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		Intent mapIntent = new Intent(FOAFViewer.this, FOAFMap.class);
		mapIntent.putExtra("myFOAF", settings.getString("FOAF", "http://devnull.networld.to/foaf.rdf"));
		this.startActivity(mapIntent);
	}
	
	/**
	 * List your friends.
	 * TODO: Don't add a fix URL. Force the user to add his/her own URL.
	 */
	private void listFriends() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Intent friendListIntent = new Intent(FOAFViewer.this, FOAFFriendListing.class);
		friendListIntent.putExtra("myFOAF", settings.getString("FOAF", "http://devnull.networld.to/foaf.rdf"));
		this.startActivity(friendListIntent);
	}
	
	/**
	 * Share your FOAF file with your friends or with the world.
	 * TODO: Don't add a fix URL. Force the user to add his/her own URL.
	 */
	private void shareFOAFFile() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, settings.getString("FOAF", "http://devnull.networld.to/foaf.rdf"));
		startActivity(shareIntent);
	}
	
	private void aboutDialog() {
        WebView wv = new WebView(this);

        StringBuffer strbuffer = new StringBuffer();
        strbuffer.append("<i>&copy; 2010 by <a href='http://devnull.networld.to/foaf.rdf#me'>Alex Oberhauser</a></i> <br/>");
        strbuffer.append("<i>licensed under the <a href='http://www.gnu.org/licenses/gpl-3.0.rdf'>GPL 3.0</a></i><p/>");
        strbuffer.append("<a href='http://foafviewer.android.networld.to'>FOAF Viewer</a> purpose is to bring the Semantic ");
        strbuffer.append("Technolgy to mobile devices. With this application your are able to visualize the XML style ");
        strbuffer.append("FOAF file in a human readable form and use the information directly with your phone");
        wv.loadData(strbuffer.toString(), "text/html", "utf-8");

        Dialog dialog = new Dialog(this);
        dialog.setTitle("About");
        dialog.addContentView(wv, new LinearLayout.LayoutParams(400, 400));
        dialog.show();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SETTINGS:
				Intent intent = new Intent(FOAFViewer.this, FOAFSettings.class);
				this.startActivity(intent);
				return true;
			case MENU_ABOUT:
				this.aboutDialog();
				return true;
		}
		return false;
	}
}