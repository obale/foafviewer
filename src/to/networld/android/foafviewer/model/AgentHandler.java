package to.networld.android.foafviewer.model;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.util.Pair;

/**
 * Handles a FOAF Agent. The information are read out with the help of XPath queries.
 * 
 * @author Alex Oberhauser
 *
 */
public class AgentHandler implements Serializable {
	private static final long serialVersionUID = 1L;
	private SAXReader reader = new SAXReader();
	private Document document = null;
	private String queryPrefix = "";
	
	public AgentHandler(URL _uri) throws DocumentException {
		this.document = this.reader.read(_uri);
		this.setQueryPrefix();
	}
	
	public AgentHandler(File _file) throws DocumentException {
		this.document = this.reader.read(_file);
		this.setQueryPrefix();
	}
	
	/**
	 * Set the query prefix that handles the node of the person that is described by the FOAF file.
	 */
	private void setQueryPrefix() {
		List<Element> nameNodes = this.getLinkNodes("/rdf:RDF/foaf:PersonalProfileDocument/foaf:primaryTopic");
		this.queryPrefix = "/rdf:RDF/foaf:Person[@*='" + nameNodes.get(0).valueOf("@resource") + "']";
		if ( this.getLinkNodes(this.queryPrefix).size() > 0 )
			return;
		this.queryPrefix = "/rdf:RDF/foaf:Person[@*='" + nameNodes.get(0).valueOf("@resource").replace("#", "") + "']";
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
			knownAgents.add(knownNodes.get(count).valueOf("@resource"));
		}
		return knownAgents;
	}
	
	public Vector<String> getKnownAgentsNames() {
		Vector<String> knownAgentsName = new Vector<String>();
		List<Element> knownNodes = this.getLinkNodes(this.queryPrefix + "/foaf:knows");
		for (int count=0; count < knownNodes.size(); count++) {
			try {
				AgentHandler friend = new AgentHandler(new URL(knownNodes.get(count).valueOf("@resource")));
				knownAgentsName.add(friend.getName());
			} catch (Exception e) {}
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
	
	public AgentSerializable getSerializableObject() {
		AgentSerializable agent = new AgentSerializable();
		agent.setAgentName(this.getName());
		agent.setImageURL(this.getImageURL());
		agent.setInterests(this.getInterests());
		Pair<Double, Double> location = this.getLocation();
		agent.setLatitude(location.first);
		agent.setLongitude(location.second);
		agent.setWebsite(this.getWebsite());
		agent.setKnownAgents(this.getKnownAgents());
		agent.setKnownAgentsNames(this.getKnownAgentsNames());
		return agent;
	}
}
