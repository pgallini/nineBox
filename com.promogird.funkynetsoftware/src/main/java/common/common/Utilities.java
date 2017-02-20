package common.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.support.v7.app.AppCompatActivity;

//import com.ninebox.nineboxapp.R;
import com.promogird.funkynetsoftware.R;

import nineBoxMain.MainActivity;

/**
 * Created by Paul Gallini on 12/13/16.
 * <p/>
 * Class to hold common utilities, functions, and methods.
 */
public class Utilities extends AppCompatActivity {
    public static Point getPointTarget(View targetView, double x_divisor) {
        // given a resource id, return a point to use for the tutorial
        // note that this is set-up to align to the right
        // change the / 6 to / 2 to center it
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        int x = (int) (location[0] + (int) targetView.getWidth() / x_divisor);
        int y = location[1] + targetView.getHeight() / 2;
        return new Point(x, y);
    }

    public static Point getPointTarget(View targetView, double x_divisor, double y_divisor) {
        // given a resource id, return a point to use for the tutorial
        // note that this is set-up to align to the right
        // change the / 6 to / 2 to center it
        // this variation takes a divisor for Y
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        int x = (int) (location[0] + (int) targetView.getWidth() / x_divisor);
        int y = (int) ((int) location[1] + (int) targetView.getHeight() / y_divisor);
        return new Point(x, y);
    }

    public static void evalTutorialToggles(SharedPreferences.Editor editor) {
        if (!MainActivity.displayTutorialMain && !MainActivity.displayTutorialAdd && !MainActivity.displayTutorialEval &&
                !MainActivity.displayTutorialRpt) {
            // if ALL of the individual tutorials have now been displayed, turn-off the preference
//            SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("pref_sync", false);
            editor.apply();
            editor.commit();
        }
    }
//
//    public RelativeLayout.LayoutParams getLayoutParms() {
//        // set-up Layout parameters for the Tutorial
//        //   Some more ideas on targets:
//        //        http://stackoverflow.com/questions/33379121/using-showcaseview-to-target-action-bar-menu-item
//        //
//        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
//        lps.setMargins(margin, margin, margin, margin);
//        return lps;
//    }
}
