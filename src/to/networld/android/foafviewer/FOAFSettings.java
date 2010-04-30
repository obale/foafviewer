package to.networld.android.foafviewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class FOAFSettings extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
