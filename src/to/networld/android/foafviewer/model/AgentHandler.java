package to.networld.android.foafviewer.model;

import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import android.content.Context;
import android.util.Pair;

/**
 * Handles a FOAF Agent. The information are read out with the help of XPath queries.
 * 
 * @author Alex Oberhauser
 *
 */
public class AgentHandler {	
	private final Context context;
	private Document document = null;
	private String queryPrefix = "";
	
	/**
	 * TODO: Implement more possibilities for the XPath queries to be able to handle more different FOAF files. 
	 * 
	 * @param _url
	 * @param _context
	 * @throws DocumentException
	 */
	public AgentHandler(URL _url, Context _context) throws DocumentException {
		this.context = _context;
		this.document = CacheHandler.getDocument(_url, _context);
		this.setQueryPrefix();
	}
	
	/**
	 * Set the query prefix that handles the node of the person that is described by the FOAF file.
	 */
	private void setQueryPrefix() {
		List<Element> nameNodes = this.getLinkNodes("/rdf:RDF/foaf:PersonalProfileDocument/foaf:primaryTopic");
		if (nameNodes.size() > 0) {
			this.queryPrefix = "/rdf:RDF/foaf:Person[@*='" + nameNodes.get(0).valueOf("@resource") + "']";
			if ( this.getLinkNodes(this.queryPrefix).size() > 0 )
				return;
			this.queryPrefix = "/rdf:RDF/foaf:Person[@*='" + nameNodes.get(0).valueOf("@resource").replace("#", "") + "']";
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getLinkNodes(String _query) {
		return (List<Element>) this.document.selectNodes(_query);
	}

	public String getName() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:name");
		if ( nameNodes.size() == 0 ) return "";
		return nameNodes.get(0).getText();
	}
	
	public String getImageURL() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:img");
		if ( nameNodes.size() > 0 )
			return nameNodes.get(0).valueOf("@resource");
		nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:depiction");
		if  (nameNodes.size() > 0)
			return nameNodes.get(0).valueOf("@resource");
		return "";
	}
	
	public String getDateOfBirth() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:dateOfBirth");
		if ( nameNodes.size() == 0 ) return "";
		return nameNodes.get(0).getText();
	}
	
	public String getWebsite() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:homepage");
		if ( nameNodes.size() == 0 ) return "";
		return nameNodes.get(0).valueOf("@resource");
	}
	
	public Pair<Double, Double> getLocation() {
		double lat = 0.0;
		double lon = 0.0;
		try {
			lat = Double.parseDouble(this.getLinkNodes(this.queryPrefix + "//geo:lat").get(0).getText());
			lon = Double.parseDouble(this.getLinkNodes(this.queryPrefix + "//geo:long").get(0).getText());
		} catch (Exception e) {}
		return new Pair<Double, Double>(lat, lon);
	}
	
	public Vector<String> getKnownAgents() {
		Vector<String> knownAgents = new Vector<String>();
		List<Element> knownNodes = this.getLinkNodes(this.queryPrefix + "/foaf:knows");
		for (int count=0; count < knownNodes.size(); count++) {
			String friendURL = knownNodes.get(count).valueOf("@resource");
			if ( !friendURL.equals("") )
				knownAgents.add(friendURL);
		}
		return knownAgents;
	}
	
	public Vector<String> getKnownAgentsNames() {
		Vector<String> knownAgentsName = new Vector<String>();
		List<Element> knownNodes = this.getLinkNodes(this.queryPrefix + "/foaf:knows");
		for (int count=0; count < knownNodes.size(); count++) {
			try {
				String friendURL = knownNodes.get(count).valueOf("@resource");
				if ( !friendURL.equals("") ) {
					AgentHandler friend = new AgentHandler(new URL(friendURL), this.context);
					knownAgentsName.add(friend.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return knownAgentsName;
	}
	
	public Vector<String> getInterests() {
		Vector<String> interests = new Vector<String>();
		List<Element> interestsNodes = this.getLinkNodes(this.queryPrefix + "/foaf:interest");
		for (int count=0; count < interestsNodes.size(); count++) {
			interests.add(interestsNodes.get(count).valueOf("@label"));
		}
		return interests;
	}
	
	public Vector<String> getEMails() {
		Vector<String> eMails = new Vector<String>();
		List<Element> eMailNodes = this.getLinkNodes(this.queryPrefix + "/foaf:mbox");
		for (int count=0; count < eMailNodes.size(); count++) {
			String mail = eMailNodes.get(count).valueOf("@resource").replaceAll("mailto:", "");
			if ( !mail.equals("") )
				eMails.add(mail);
		}
		return eMails;
	}
	
	public Vector<String> getPhoneNumbers() {
		Vector<String> phoneNumbers = new Vector<String>();
		List<Element> phoneNumberNodes = this.getLinkNodes(this.queryPrefix + "/foaf:phone");
		for (int count=0; count < phoneNumberNodes.size(); count++) {
			phoneNumbers.add(phoneNumberNodes.get(count).valueOf("@resource").replaceAll("tel:", ""));
		}
		return phoneNumbers;
	}
}
