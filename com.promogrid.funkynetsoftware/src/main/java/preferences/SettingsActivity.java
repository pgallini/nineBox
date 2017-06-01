package preferences;

import android.app.Activity;
import android.os.Bundle;

import nineBoxMain.MainActivity;

/**
 * Created by Paul Gallini on 11/17/16.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainActivity.SettingsFragment())
                .commit();
    }
}