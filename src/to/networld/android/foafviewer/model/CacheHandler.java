package to.networld.android.foafviewer.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import android.content.Context;

/**
 * Implements a caching function to be able to query the FOAF files also when
 * there is no internet connection.
 * 
 * TODO: Implement are adequate check to detect remote file changes.
 * 
 * @author Alex Oberhauser
 *
 */
public class CacheHandler {
	
	/**
	 * Computes the MD5 hash value from a input string. Is used to generate the filename for the local storing
	 * of the FOAF file.
	 * 
	 * @param _someString The string on that the hash value is computed. In general this should be a URI.
	 * @return
	 */
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
	 * Returns a Document that could be parsed with the help of XPath.
	 * If the file was found in the local cache it will be taken from there,
	 * otherwise the file will be downloaded.
	 *  
	 * @param _uri The URI that describes the FOAF file.
	 * @param _context The context from that the files should be read.
	 */
	public static Document getDocument(URL _url, Context _context) {
		SAXReader reader = new SAXReader();
		Document document = null;
		String filename = CacheHandler.getHashcode(_url.toString()) + ".rdf";
		try {
			FileInputStream fis = _context.openFileInput(filename);
			document = reader.read(fis);
			fis.close();
		} catch (Exception e) {
			try {
				document = reader.read(_url);
				FileOutputStream fos;
				fos = _context.openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(document.asXML().getBytes());
				fos.close();
			} catch (Exception e1) {
				e.printStackTrace();
			}
		}
		return document;
	}
	
	/**
	 * Clean all files with the ending .rdf
	 * 
	 * @param _context The context in that the files are stored. (Object of the calling program)
	 */
	public static void cleaningCache(Context _context) {
		String[] files = _context.fileList();
		for (String file : files) {
			if ( file.endsWith(".rdf") )
				_context.deleteFile(file);
		}
	}
}
