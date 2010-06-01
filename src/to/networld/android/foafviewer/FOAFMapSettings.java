package to.networld.android.foafviewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class FOAFMapSettings extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.map_settings);
	}

}
