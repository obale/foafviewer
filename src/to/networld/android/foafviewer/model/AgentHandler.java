package to.networld.android.foafviewer.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.content.Context;
import android.util.Pair;

/**
 * Handles a FOAF Agent. The information are read out with the help of XPath queries.
 * 
 * @author Alex Oberhauser
 *
 */
public class AgentHandler implements Serializable {
	private static final long serialVersionUID = -7983879026533503759L;
	
	private SAXReader reader = new SAXReader();
	private final Context context;
	private Document document = null;
	private String queryPrefix = "";
	
	/**
	 * XXX: The cache should be stored on the sd card. 
	 * 
	 * @param _url
	 * @param _context
	 * @throws DocumentException
	 */
	public AgentHandler(URL _url, Context _context) throws DocumentException {
		this.context = _context;
		String filename = this.saveRDFFile(_url, _context);
		if ( filename != null ) {
			this.document = this.reader.read(_url);
			FileOutputStream fos;
			try {
				fos = _context.openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(this.document.asXML().getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.setQueryPrefix();
	}

	/**
	 * TODO: Implement here a suitable function that checks if the file was stored on the local device (SDCARD!?!)
	 *  
	 * @param _uri The URI that describes the FOAF file.
	 * @param _context The context from that the files should be read.
	 * @throws IOException 
	 */
	private String saveRDFFile(URL _url, Context _context) {
		String filename = getHashcode(_url.toString()) + ".rdf";
		try {
			FileInputStream fis = _context.openFileInput(filename);
			this.document = this.reader.read(fis);
			fis.close();
			return null;
		} catch (Exception e) {
			return filename;
		}
	}
	
	private static String getHashcode(String _someString) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(_someString.getBytes());
			byte[] hashArray = algorithm.digest();
			StringBuffer hexString = new StringBuffer();
			for (byte b : hashArray) {
				hexString.append(Integer.toHexString(0xFF & b));
			}
			return hexString.toString();
		} catch ( NoSuchAlgorithmException e){
			String hashcode = _someString.hashCode() + "";
			return hashcode.replace("-", "");
		}
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
			knownAgents.add(knownNodes.get(count).valueOf("@resource"));
		}
		return knownAgents;
	}
	
	public Vector<String> getKnownAgentsNames() {
		Vector<String> knownAgentsName = new Vector<String>();
		List<Element> knownNodes = this.getLinkNodes(this.queryPrefix + "/foaf:knows");
		for (int count=0; count < knownNodes.size(); count++) {
			try {
				AgentHandler friend = new AgentHandler(new URL(knownNodes.get(count).valueOf("@resource")), this.context);
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
	
	/*
	public AgentSerializable getSerializableObject() {
		AgentSerializable agent = new AgentSerializable();
		agent.setAgentName(this.getName());
		agent.setDateOfBirth(this.getDateOfBirth());
		agent.setImageURL(this.getImageURL());
		agent.setInterests(this.getInterests());
		Pair<Double, Double> location = this.getLocation();
		agent.setLatitude(location.first);
		agent.setLongitude(location.second);
		agent.setWebsite(this.getWebsite());
		agent.setKnownAgents(this.getKnownAgents());
		agent.setKnownAgentsNames(this.getKnownAgentsNames());
		agent.setPhoneNumbers(this.getPhoneNumbers());
		agent.setEMails(this.getEMails());
		return agent;
	}
	*/
}
