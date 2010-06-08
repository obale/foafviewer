package to.networld.android.foafviewer.model;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import android.content.Context;
import android.util.Pair;

/**
 * Handles a FOAF Agent. The information are read out with the help of XPath queries.
 * 
 * @author Alex Oberhauser
 *
 */
public final class Agent {		
	private final String uri;
	
	private final Document document;
	private String queryPrefix = "/rdf:RDF/foaf:Person";
	
	/**
	 * TODO: Exception handling should be improved!!! 
	 * 
	 * @param _url The URL that points to a valid FOAF file
	 * @param _context The context of the activity that calls this class (needed to access the cache files).
	 * @throws Exception Generic exception, doesn't matter what error occurs the agent could not be instantiated.
	 */
	protected Agent(URL _url, Context _context) throws Exception {
		this.uri = _url.toString();
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
			if ( this.getLinkNodes(this.queryPrefix).size() > 0 )
				return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getLinkNodes(String _query) {
		return (List<Element>) this.document.selectNodes(_query);
	}
	
	@SuppressWarnings("unchecked")
	private List<Element> getLinkNodes(String _query, HashMap<String, String> _namespaces) {
		try {
			XPath xpath = new Dom4jXPath(_query);
			xpath.setNamespaceContext(new SimpleNamespaceContext(_namespaces));
			return (List<Element>) xpath.selectNodes(this.document);
		} catch (JaxenException e) {
			e.printStackTrace();
			return this.document.selectNodes("");
		}
	}

	public String getName() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:name");
		if ( nameNodes.size() == 0 ) {
			String firstname = this.getLinkNodes(this.queryPrefix + "/foaf:firstName").get(0).getText();
			String surname = this.getLinkNodes(this.queryPrefix + "/foaf:surname").get(0).getText();
			return firstname + " " + surname;
		}
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
		if ( nameNodes.size() == 0 ) return null;
		return nameNodes.get(0).getText();
	}
	
	public String getWebsite() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:homepage");
		if ( nameNodes.size() == 0 ) return null;
		return nameNodes.get(0).valueOf("@resource");
	}
	
	public String getWeblog() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:weblog");
		if ( nameNodes.size() == 0 ) return null;
		String weblog = nameNodes.get(0).valueOf("@resource");
		if ( weblog.equals("") ) return null;
		return weblog;
	}
	
	public String getSchoolHomepage() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:schoolHomepage");
		if ( nameNodes.size() == 0 ) return null;
		return nameNodes.get(0).valueOf("@resource");
	}
	
	public String getWorkplaceHomepage() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:workplaceHomepage");
		if ( nameNodes.size() == 0 ) return null;
		return nameNodes.get(0).valueOf("@resource");
	}
	
	public String getOpenid() {
		List<Element> nameNodes = this.getLinkNodes(this.queryPrefix + "/foaf:openid");
		if ( nameNodes.size() == 0 ) return null;
		return nameNodes.get(0).valueOf("@resource");
	}
	
	public Pair<Double, Double> getLocation() {
		double lat = 0.0;
		double lon = 0.0;
		try {
			lat = Double.parseDouble(this.getLinkNodes(this.queryPrefix + "//geo:lat").get(0).getText());
			lon = Double.parseDouble(this.getLinkNodes(this.queryPrefix + "//geo:long").get(0).getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Pair<Double, Double>(lat, lon);
	}
	
	public Vector<String> getKnownAgents() {
		Vector<String> knownAgents = new Vector<String>();
		List<Element> knownNodes = this.getLinkNodes(this.queryPrefix + "/foaf:knows//rdfs:seeAlso");
		knownAgents.addAll(this._getKnownAgents(knownNodes));
		
		knownNodes = this.getLinkNodes(this.queryPrefix + "/foaf:knows");
		knownAgents.addAll(this._getKnownAgents(knownNodes));
		
		return knownAgents;
	}
	
	private Vector<String> _getKnownAgents(List<Element> _nodes) {
		Vector<String> knownAgents = new Vector<String>();
		for (int count=0; count < _nodes.size(); count++) {
			String friendURL = _nodes.get(count).valueOf("@resource");
			if ( !friendURL.equals("") )
				knownAgents.add(friendURL);
		}
		return knownAgents;
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
			phoneNumbers.add(phoneNumberNodes.get(count).valueOf("@resource"));
		}
		return phoneNumbers;
	}
	
	/**
	 * Reads out the dive certificate (see http://scubadive.networld.to for the ontology).
	 * 
	 * @return The scuba dive certificate.
	 */
	public String getDiveCertificate() {
		HashMap<String, String> namespace = new HashMap<String, String>();
		namespace.put("dive", "http://scubadive.networld.to/dive.rdf#");
		namespace.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		namespace.put("foaf", "http://xmlns.com/foaf/0.1/");
		List<Element> diveCertificateEle = this.getLinkNodes(this.queryPrefix + "/dive:hasCertification", namespace);
		if ( diveCertificateEle.size() > 0 ) {
			String diveCertificate = diveCertificateEle.get(0).valueOf("@resource");
			if ( !diveCertificate.equals("") || diveCertificate != null )
				return diveCertificate;
		}
		return null;
	}
	
	public String getURI() { return this.uri; }
}
