package to.networld.android.foafviewer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.dom4j.DocumentException;

import to.networld.android.foafviewer.model.AgentHandler;
import to.networld.android.foafviewer.model.AgentSerializable;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
			AgentSerializable foafAgent = results.get(agentName);
			Intent profileIntent = new Intent(FOAFFriendListing.this, FOAFProfile.class);
			profileIntent.putExtra("agent", foafAgent);
			startActivity(profileIntent);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		this.friendListAdapter = new ArrayAdapter<String>(this, R.layout.friendlist, R.id.friend_list);
		this.setListAdapter(this.friendListAdapter);
		ListView lv = this.getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(this.listClickedListener);
		
		final ProgressDialog progressDialog = ProgressDialog.show(FOAFFriendListing.this, null, "Searching for Friends...", true);
		Thread seeker = new Thread() {
			public void getFriends() throws MalformedURLException, DocumentException {
				String agentURL = getIntent().getStringExtra("myFOAF");
				AgentHandler foafAgent = new AgentHandler(new URL(agentURL));
				AgentSerializable agent = foafAgent.getSerializableObject();
					
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
			
			@Override//
			public void run(){
				try {
					this.getFriends();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				guiHandler.post(updateFriends);
				progressDialog.dismiss();
			}
		};
		seeker.start();
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
