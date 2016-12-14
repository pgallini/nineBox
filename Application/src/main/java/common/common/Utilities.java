package common.common;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Paul Gallini on 12/13/16.
 * <p/>
 * Class to hold common utilities, functions, and methods.
 */
public class Utilities extends AppCompatActivity {
    public static Point getPointTarget(View targetView, int x_divisor) {
        // given a resource id, return a point to use for the tutorial
        // note that this is set-up to alighn to the right
        // change the / 6 to / 2 to center it
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        int x = location[0] + targetView.getWidth() / x_divisor;
        int y = location[1] + targetView.getHeight() / 2;
        return new Point(x, y);
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
