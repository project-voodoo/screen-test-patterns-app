
package org.projectvoodoo.screentestpatterns;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PatternGeneratorOptions extends PreferenceActivity {

    public static final String prefName = "hcfr_generator";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(prefName);
        addPreferencesFromResource(R.xml.preferences);

        App.reloadGenerator = true;
    }

}
