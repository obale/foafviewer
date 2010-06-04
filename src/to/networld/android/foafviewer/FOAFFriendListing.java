package to.networld.android.foafviewer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import to.networld.android.foafviewer.model.Agent;
import to.networld.android.foafviewer.model.AgentHandler;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
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
	private final Context context = FOAFFriendListing.this;
	
	private Agent foafAgent;
	private HashMap<String, String> results = new HashMap<String, String>();
	private ArrayAdapter<String> friendListAdapter = null;
	
	private final Handler guiHandler = new Handler();
	private final Runnable updateFriends = new Runnable() {
		@Override
		public void run() {
			 updateFriendsInUI();
		}
	};
	
	private final OnDismissListener errorDialogDismissedListener = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			finish();
		}
	};
	
	private final OnItemClickListener listClickedListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CharSequence agentName = ((TextView) view).getText();
			String friendFoafURL = results.get(agentName);
			Intent profileIntent = new Intent(FOAFFriendListing.this, FOAFProfile.class);
			profileIntent.putExtra("agent", friendFoafURL);
			startActivity(profileIntent);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		final ProgressDialog progressDialog = ProgressDialog.show(FOAFFriendListing.this, null, "Searching for Friends...", true);
		
		this.friendListAdapter = new ArrayAdapter<String>(this, R.layout.friendlist, R.id.friend_list);
		this.setListAdapter(this.friendListAdapter);
		ListView lv = this.getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(this.listClickedListener);
		
		String agentURL = getIntent().getStringExtra("myFOAF");
		try {
			foafAgent = AgentHandler.initAgent(agentURL, context);
			
			Thread seeker = new Thread() {
				@Override
				public void run(){			
					Iterator<String> iter = foafAgent.getKnownAgents().iterator();
					Agent entry = null;
					while ( iter.hasNext() ) {
						try {
							String friendURL = iter.next();
							entry = AgentHandler.initAgent(friendURL, context);
							synchronized(results) {
								results.put(entry.getName(), friendURL);
							}
						} catch (Exception e) {}
					}
					guiHandler.post(updateFriends);
					progressDialog.dismiss();
				}
			};
			seeker.start();
		} catch (Exception e) {
			progressDialog.dismiss();
			GenericDialog errorDialog = new GenericDialog(context, "Error", e.getLocalizedMessage(), R.drawable.error_icon);
			errorDialog.setOnDismissListener(errorDialogDismissedListener);
			errorDialog.show();
		}
	}
	
	private void updateFriendsInUI() {
		if ( this.results != null) {
			Set<String> friendNames = this.results.keySet();
			for ( String name : friendNames ) {
				this.friendListAdapter.add(name);
			}
		}
	}

}
