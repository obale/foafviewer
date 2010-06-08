package to.networld.android.foafviewer.model;

/**
 * Useful functions to handle the <a href="http://scubadive.networld.to" target="_blank">scuba dive ontology</a> 
 * 
 * @author Alex Oberhauser
 *
 */
public final class ScubaDiveHelper {
	
	/**
	 * Maps the Scuba Dive Certificate URL to human readable string.
	 * 
	 * The mapping is implemented here statically (but ontology conform), because
	 * the perfomance issue. So it is not need to query another file.
	 * 
	 * @param _url The URL specified at http://scubadive.networld.to/padi.rdf
	 * @return A string representation of the scuba dive certificate.
	 */
	public static String getScubaDiveCertificate(String _url) {
		if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#None") )
			return "No Certification Yet";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#SD") )
			return "PADI Scuba Diver";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#OW") )
			return "Open Water";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#AOW") )
			return "Advanced Open Water";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#Rescue") )
			return "Rescue Diver";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#MSD") )
			return "Master Scuba Diver";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#Divemaster") )
			return "Divemaster";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#OWSI") )
			return "Open Water Scuba Instructor";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#AI") )
			return "Assistent Instructor";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#MSDT") )
			return "Master Scuba Diver Trainer";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#IDCSI") )
			return "IDC Staff Instructor";
		else if ( _url.equalsIgnoreCase("http://scubadive.networld.to/padi.rdf#IDCSI") )
			return "Course Director";
		return _url;
	}
}
