package to.networld.android.foafviewer.model;

import java.io.Serializable;
import java.util.Vector;

/**
 * An agent class that could be serialized safely to pass it to other intends.
 * 
 * @author Alex Oberhauser
 * 
 */
public class AgentSerializable implements Serializable {
	private static final long serialVersionUID = 2361123841481595097L;
	
	private String agentName = null;
	private String dateOfBirth = null;
	private Double latitude = 0.0;
	private Double longitude = 0.0;
	private String website = null;
	private String imageURL = null;
	private Vector<String> interests = new Vector<String>();
	private Vector<String> knownAgents = new Vector<String>();
	private Vector<String> knownAgentsNames = new Vector<String>();
	private Vector<String> phoneNumbers = new Vector<String>();
	private Vector<String> eMails = new Vector<String>();

	public void setAgentName(String _agentName) {
		this.agentName = _agentName;
	}
	

	public void setDateOfBirth(String _dateOfBirth) {
		this.dateOfBirth = _dateOfBirth;
	}	

	public void setLatitude(Double _latitude) {
		this.latitude = _latitude;
	}

	public void setLongitude(Double _longitude) {
		this.longitude = _longitude;
	}

	public void setWebsite(String _website) {
		this.website = _website;
	}

	public void setImageURL(String _imageURL) {
		this.imageURL = _imageURL;
	}

	public void setInterests(Vector<String> _interests) {
		this.interests = _interests;
	}

	public void setKnownAgents(Vector<String> _knownAgents) {
		this.knownAgents = _knownAgents;
	}
	
	public void setKnownAgentsNames(Vector<String> _knownAgentsNames) {
		this.knownAgentsNames = _knownAgentsNames;
	}

	public void setPhoneNumbers(Vector<String> _phoneNumbers) {
		this.phoneNumbers = _phoneNumbers;
	}
	
	public void setEMails(Vector<String> _eMails) {
		this.eMails = _eMails;
	}
	
	public String getAgentName() { return this.agentName; }
	public String getDateOfBirth() { return this.dateOfBirth; }
	public Double getLatitude() { return this.latitude;	}
	public Double getLongitude() { return this.longitude; }
	public String getWebsite() { return this.website; }
	public String getImageURL() {return this.imageURL; }
	public Vector<String> getInterests() { return this.interests; }
	public Vector<String> getKnownAgents() { return this.knownAgents; }
	public Vector<String> getKnownAgentsNames() { return this.knownAgentsNames;	}
	public Vector<String> getPhoneNumbers() { return this.phoneNumbers; }
	public Vector<String> getEMails() { return this.eMails; }

}
