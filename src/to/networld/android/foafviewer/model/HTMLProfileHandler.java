package to.networld.android.foafviewer.model;

import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Pair;

public final class HTMLProfileHandler {
	
	public static String getHTMLDescription(Context _context, Agent _agent) {
		Geocoder geocoder = new Geocoder(_context, Locale.getDefault());
		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append("<center><h2><font color='blue'>");
		if ( !_agent.getWebsite().equals("") ) {
			strbuffer.append("<a href='" + _agent.getWebsite() + "'>");
			strbuffer.append(_agent.getName());
			strbuffer.append("</a>");
		} else {
			strbuffer.append(_agent.getName());
		}
		strbuffer.append("</font></h2></center>");
		strbuffer.append("<center><img src='" + _agent.getImageURL() + "' width='100px'></center>");
		strbuffer.append("<table align='center' border='0'>");
		strbuffer.append("<tr><td><b>GPS</b></td><td>");
		Pair<Double, Double> geoPoint = _agent.getLocation();
		strbuffer.append(geoPoint.first);
		strbuffer.append(",");
		strbuffer.append(geoPoint.second);
		strbuffer.append("</td></tr>");
		try {
			Address address = geocoder.getFromLocation(
					geoPoint.first,
					geoPoint.second, 1).get(0);

			String plz = address.getPostalCode();
			String city = address.getLocality();
			strbuffer.append("<tr><td><b>Location</b></td><td>");
			strbuffer.append(plz + " " + city);
			strbuffer.append("</td></tr>");

			String street = address.getAddressLine(0);
			strbuffer.append("<tr><td><b>Streetname</b></td><td>");
			strbuffer.append(street);
			strbuffer.append("</td></tr>");
		} catch (Exception e) {
		}
		strbuffer.append("</table>");
		strbuffer.append("<hr>");

		strbuffer.append("<center><h3>Contact Methods</h3></center>");
		strbuffer.append("<table align='center' border='0'>");
		Vector<String> phoneNumbers = _agent.getPhoneNumbers();
		for (int count = 0; count < phoneNumbers.size(); count++) {
			strbuffer.append("<tr><td><b>Phone</b></td><td>");
			strbuffer.append(phoneNumbers.get(count));
			strbuffer.append("</td></tr>");
		}
		Vector<String> eMails = _agent.getEMails();
		for (int count = 0; count < eMails.size(); count++) {
			strbuffer.append("<tr><td><b>E-Mail</b></td><td>");
			strbuffer.append(eMails.get(count));
			strbuffer.append("</td></tr>");
		}
		strbuffer.append("</table>");
		strbuffer.append("<hr>");
		
		Vector<String> interests = _agent.getInterests();
		strbuffer.append("<center><h3>Interests</h3></center>");
		strbuffer.append("<ul>");
		for (int count = 0; count < interests.size(); count++) {
			strbuffer.append("<li> " + interests.get(count));
		}
		strbuffer.append("</ul>");
		strbuffer.append("<hr>");
		
		Vector<String> knownAgentsName = _agent.getKnownAgentsNames();
		strbuffer.append("<center><h3>Known Agents</h3></center>");
		strbuffer.append("<ul>");
		for (int count = 0; count < knownAgentsName.size(); count++) {
			strbuffer.append("<li> " + knownAgentsName.get(count));
		}
		strbuffer.append("</ul>");
		strbuffer.append("<hr>");
		
		return strbuffer.toString();
	}
}
