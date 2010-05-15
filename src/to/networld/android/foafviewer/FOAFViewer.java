package to.networld.android.foafviewer;

import java.net.URL;

import to.networld.android.foafviewer.model.AgentHandler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	
	private OnClickListener listFriendsButtonListener = new OnClickListener() {
		public void onClick(View view) {
			listFriends();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button mapMeButton = (Button) findViewById(R.id.mapMeButton);
		if (mapMeButton != null)
			mapMeButton.setOnClickListener(this.mapMeButtonListener);
		
		Button listFriendsButton = (Button) findViewById(R.id.listFriendsButton);
		if (listFriendsButton != null)
			listFriendsButton.setOnClickListener(this.listFriendsButtonListener);
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
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		try {
			AgentHandler foafAgent = new AgentHandler(new URL(settings.getString("FOAF", "")));
			Intent mapIntent = new Intent(FOAFViewer.this, FOAFMap.class);
			mapIntent.putExtra("agent", foafAgent.getSerializableObject());
			this.startActivity(mapIntent);
		} catch (Exception e) {
			/*
			 * TODO: Handle the exception.
			 */
			e.printStackTrace();
		}
	}
	
	/**
	 * List your friends.
	 */
	public void listFriends() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		try {
			AgentHandler foafAgent = new AgentHandler(new URL(settings.getString("FOAF", "")));
			Intent friendListIntent = new Intent(FOAFViewer.this, FOAFFriendListing.class);
			friendListIntent.putExtra("agent", foafAgent.getSerializableObject());
			this.startActivity(friendListIntent);
		} catch (Exception e) {
			/*
			 * TODO: Handle the exception.
			 */
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