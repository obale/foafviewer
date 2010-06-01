package to.networld.android.foafviewer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import to.networld.android.foafviewer.model.AgentSerializable;
import to.networld.android.foafviewer.model.ImageHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Shows the Profile of an Agent. Please assure that you assign 
 * a serializable object of AgentSerializable with the attribute
 * "agent".
 * 
 * intentObj.putExtra("agent", foafAgent.getSerializableObject());
 * 
 * @author Alex Oberhauser
 *
 */
public class FOAFProfile extends Activity {
	private final Context context = this;
	
	private AgentSerializable agent = null;
	
	private static final String BOTTOM = "bottom";
	private static final String TOP = "top";
	private static final String ICON = "icon";
	
	private static final String NAME = "Name";
	private static final String BIRTHDAY = "Date of Birth";
	private static final String LOCATION = "Location";
	private static final String MAIL = "E-Mail";
	private static final String INTEREST = "Interest";
	private static final String PHONE_NUMBER = "Phone Number";
	
	private ArrayList<HashMap<String, String>> profileList;
	
	private OnItemClickListener listClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position, long id) {
			HashMap<String, String> entry = profileList.get(position);

			if ( entry.get(TOP).equals(MAIL) ) {
				final Intent eMailIntent = new Intent(android.content.Intent.ACTION_SEND);
				eMailIntent.setType("text/html");
				eMailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ entry.get(BOTTOM) });
				startActivity(eMailIntent);
			} else if ( entry.get(TOP).equals(NAME) ) {
				Dialog picDialog = new Dialog(context);
				picDialog.setContentView(R.layout.pic_dialog);
				ImageView portrait = (ImageView) picDialog.findViewById(R.id.dialog_portrait);
				try {
					portrait.setImageDrawable(ImageHelper.getDrawable(agent.getImageURL()));
				} catch (MalformedURLException e) {
				} catch (IOException e) {}
				picDialog.show();
			} else if ( entry.get(TOP).equals(PHONE_NUMBER) ) {
				final Intent phoneIntent = new Intent(android.content.Intent.ACTION_CALL);
				phoneIntent.setData(Uri.parse("tel:" + entry.get(BOTTOM)));
				startActivity(phoneIntent);
			} else if ( entry.get(TOP).equals(LOCATION) ) {
				final Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW);
				String location = entry.get(BOTTOM).replace(" ", "+");
				mapIntent.setData(Uri.parse("geo:0,0?q=" + location));
				startActivity(mapIntent);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		agent = (AgentSerializable)getIntent().getSerializableExtra("agent");
			
		ListView list = (ListView) findViewById(R.id.profileList);
		list.setOnItemClickListener(this.listClickListener);
		this.profileList = new ArrayList<HashMap<String, String>>();
		
		/**
		 * Name
		 */
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(ICON, R.drawable.foaf + "");
		map.put(TOP, NAME);
		map.put(BOTTOM, agent.getAgentName());
		profileList.add(map);
		
		/**
		 * Date of Birth
		 */
		String dateOfBirth = agent.getDateOfBirth();
		if ( dateOfBirth != null && !dateOfBirth.equals("") ) {
			map = new HashMap<String, String>();
			map.put(ICON, R.drawable.birthday_icon + "");
			map.put(TOP, BIRTHDAY);
			map.put(BOTTOM, dateOfBirth);
			profileList.add(map);
		}
		
		/**
		 * Location
		 */
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			Address address = geocoder.getFromLocation(agent.getLatitude(), agent.getLongitude(), 1).get(0);
			map = new HashMap<String, String>();
			map.put(ICON, R.drawable.location_icon + "");
			map.put(TOP, LOCATION);
			map.put(BOTTOM, address.getAddressLine(0) + ", " + address.getPostalCode() + " " + address.getLocality());
			this.profileList.add(map);
		} catch (Exception e) {}
		
		/**
		 * E-Mails
		 */
		Vector<String> mails = agent.getEMails();
		for (String eMail : mails) {
			map = new HashMap<String, String>();
			map.put(ICON, R.drawable.email_icon + "");
			map.put(TOP, MAIL);
			map.put(BOTTOM, eMail);
			this.profileList.add(map);
		}
		
		/**
		 * Phone Numbers
		 */
		Vector<String> phoneNumbers = agent.getPhoneNumbers();
		for (String phoneNumber : phoneNumbers) {
			map = new HashMap<String, String>();
			map.put(ICON, R.drawable.tel_icon + "");
			map.put(TOP, PHONE_NUMBER);
			map.put(BOTTOM, phoneNumber);
			this.profileList.add(map);
		}
		
		/**
		 * TODO: Twitter Account
		 */
		
		/**
		 * TODO: Facebook Account
		 */
		
		/**
		 * Interests
		 */
		Vector<String> interests = agent.getInterests();
		for (String interest : interests) {
			map = new HashMap<String, String>();
			map.put(ICON, R.drawable.leisure_icon + "");
			map.put(TOP, INTEREST);
			map.put(BOTTOM, interest);
			this.profileList.add(map);
			
		}
		
		/**
		 * TODO: Known Agents
		 */
		
		SimpleAdapter adapterProfileList = new SimpleAdapter(this, profileList, 
				R.layout.list_entry, new String[]{ "icon", "top", "bottom" },
				new int[] { R.id.icon, R.id.topText, R.id.bottomText });
		
		list.setAdapter(adapterProfileList);
	}

}
