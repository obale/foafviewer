package to.networld.android.foafviewer.model;

import java.io.Serializable;
import java.util.Vector;

public class StringSerializable implements Serializable {
	private static final long serialVersionUID = 1L;
	private Vector<String> stringVector = new Vector<String>();
	
	public StringSerializable(Vector<String> _stringVector) {
		this.stringVector = _stringVector;
	}
	
	public Vector<String> getVectorString() { return this.stringVector; }
	

}
