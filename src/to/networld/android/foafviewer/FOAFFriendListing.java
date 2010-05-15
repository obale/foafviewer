package to.networld.android.foafviewer;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import to.networld.android.foafviewer.model.AgentHandler;
import to.networld.android.foafviewer.model.AgentSerializable;
import to.networld.android.foafviewer.model.HTMLProfileHandler;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FOAFFriendListing extends ListActivity {
	private Hashtable<String, AgentSerializable> results = new Hashtable<String, AgentSerializable>();
	private ArrayAdapter<String> friendListAdapter = null;
	private final Handler guiHandler = new Handler();
	private final Runnable updateFriends = new Runnable() {
		@Override
		public void run() {
			 updateFriendsInUI();
		}
	};
	
	private final OnItemClickListener listClickedListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CharSequence agentName = ((TextView) view).getText();
			showProfile(results.get(agentName));
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.friendListAdapter = new ArrayAdapter<String>(this, R.layout.friendlist, R.id.friend_list);
		this.setListAdapter(this.friendListAdapter);
		ListView lv = this.getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(this.listClickedListener);
		
		final ProgressDialog progressDialog = ProgressDialog.show(FOAFFriendListing.this, null, "Searching for Friends...", true);
		Thread seeker = new Thread() {
			
			public void getFriends() {
				AgentSerializable agent = (AgentSerializable)getIntent().getSerializableExtra("agent");
				Iterator<String> iter = agent.getKnownAgents().iterator();
				AgentSerializable entry = null;
				while ( iter.hasNext() ) {
					try {
						entry = new AgentHandler(new URL(iter.next())).getSerializableObject();
						synchronized(results) {
							results.put(entry.getAgentName(), entry);
						}
					} catch (Exception e) {}
				}
			}
			
			@Override
			public void run(){
				this.getFriends();
				
				guiHandler.post(updateFriends);
				progressDialog.dismiss();
			}
		};
		seeker.start();
	}
	
	public void showProfile(AgentSerializable _agent) {
		WebView wv = new WebView(this);
		Dialog dialog = new Dialog(this);
		wv.loadData(HTMLProfileHandler.getHTMLDescription(this, _agent), "text/html", "UTF-8");
		dialog.setTitle("FOAF Profile");
		dialog.setContentView(wv, new LinearLayout.LayoutParams(450, 500));
		dialog.show();
	}
	
	private void updateFriendsInUI() {
		if ( this.results != null) {
			Enumeration<String> enumStrings = this.results.keys();
			while ( enumStrings.hasMoreElements() ) {
				this.friendListAdapter.add(enumStrings.nextElement());
			}
		}
	}

}
