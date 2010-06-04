package to.networld.android.foafviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import to.networld.android.foafviewer.model.Agent;
import to.networld.android.foafviewer.model.AgentHandler;
import to.networld.android.foafviewer.model.CacheHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
					showMe();
					break;
				case 1:
					listFriends();
					break;
				case 2:
					shareFOAFFile();
					break;
				case 3:
					generateFOAFFile();
					break;
				case 4:
					updateFriendList();
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
		
		ListView list = (ListView) findViewById(R.id.MAIN);
		list.setOnItemClickListener(this.listClickListener);
		ArrayList<HashMap<String, String>> buttonList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;
		
		
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.profile_icon + "");
		map.put("top", "Show Profile!");
		map.put("bottom", "Shows your FOAF file in the same style as your friends are shown.");
		buttonList.add(map);
		
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.avatar_icon + "");
		map.put("top", "List Friends!");
		map.put("bottom", "List all your known agents.");
		buttonList.add(map);
		
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.share_icon + "");
		map.put("top", "Share FOAF URL!");
		map.put("bottom", "Share your FOAF URL with your friends or with the world.");
		buttonList.add(map);
		
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.foaf_gen + "");
		map.put("top", "Generate FOAF file!");
		map.put("bottom", "TODO: Implement this feature!");
		buttonList.add(map);
		
		map = new HashMap<String, String>();
		map.put("icon", R.drawable.update_icon + "");
		map.put("top", "Update Cache!");
		map.put("bottom", "Refreshing your FOAF file and that of your friends.");
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
	
	private void showMe() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		String ownFOAF = settings.getString("FOAF", "");
		if ( !ownFOAF.equals("") ) {
			Intent mapIntent = new Intent(FOAFViewer.this, FOAFProfile.class);
			mapIntent.putExtra("agent", ownFOAF);
			this.startActivity(mapIntent);
		} else {
			new GenericDialog(this, "Missing FOAF file", "Please set your FOAF file!", R.drawable.error_icon).show();
		}
	}
	
	/**
	 * List your friends.
	 */
	private void listFriends() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String ownFOAF = settings.getString("FOAF", "");
		if ( !ownFOAF.equals("") ) {
			Intent friendListIntent = new Intent(FOAFViewer.this, FOAFFriendListing.class);
			friendListIntent.putExtra("agent", ownFOAF);
			this.startActivity(friendListIntent);
		} else {
			new GenericDialog(this, "Missing FOAF file", "Please set your FOAF file!", R.drawable.error_icon).show();			
		}
	}
	
	/**
	 * Share your FOAF file with your friends or with the world.
	 */
	private void shareFOAFFile() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		String ownFOAF = settings.getString("FOAF", "");
		if ( !ownFOAF.equals("") ) {
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, ownFOAF);
			this.startActivity(shareIntent);
		} else {
			new GenericDialog(this, "Missing FOAF file", "Please set your FOAF file!", R.drawable.error_icon).show();
		}
	}
	
	/**
	 * TODO: Implement the FOAF generation code.
	 */
	private void generateFOAFFile() {
		new GenericDialog(this, "Unimplemented Feature", "This feature is planned in a future release!", R.drawable.error_icon).show();
	}
	
	/**
	 * Updates the FOAF files. The deep of the update is 1. That means your FOAF file and that of your
	 * known agents are updated.
	 */
	private void updateFriendList() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String ownFOAF = settings.getString("FOAF", "");
		if ( !ownFOAF.equals("") ) {
			CacheHandler.cleaningCache(this);
			try {
				Agent agent = AgentHandler.initAgent(ownFOAF, this);
				Vector<String> knownAgents = agent.getKnownAgents();
				for ( String knownAgent : knownAgents ) {
					try {
						AgentHandler.initAgent(knownAgent, this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				new GenericDialog(this, "Refreshing Cache Successful", "Your FOAF file and that of your friends are refreshed!", R.drawable.ok_icon).show();
			} catch (Exception e) {
				new GenericDialog(this, "Refreshing Cache Failed", e.getLocalizedMessage(), R.drawable.error_icon).show();
			}
		} else {
			new GenericDialog(this, "Missing FOAF file", "Please set your FOAF file!", R.drawable.error_icon).show();
		}
	}
	
	/**
	 * A Dialog with the license and a short description what the program is about.
	 */
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