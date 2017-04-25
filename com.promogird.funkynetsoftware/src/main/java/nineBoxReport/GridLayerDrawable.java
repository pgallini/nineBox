package nineBoxReport;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;

/**
 * Created by Paul Gallini on 5/17/16.
 */
public class GridLayerDrawable extends LayerDrawable {

    public GridLayerDrawable(Drawable[] layers) {
        super(layers);
    }

    public void addLayer(ShapeDrawable circleTemp, int height, int sizeHeight, int sizeWidth) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.addLayer(circleTemp);
            this.setLayerSize(1, sizeHeight, sizeWidth);
        } else {
            // TODO finish coding this!!!
        }
    }

    public void addLayer(Drawable drawyThing, int height, int sizeHeight, int sizeWidth) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.addLayer(drawyThing);
            this.setLayerSize(1, sizeHeight, sizeWidth);
        }
    }

    public void setWidgetPosition(int layer, double result_X_axis, double result_Y_axis, int widget_width) {
        // This method will position a given layer/Circle on the Grid
        int rightOffset = 0; // used to keep the circles off the right edge
        int bottomOffset = 0; // used to keep the circles off the ceiling
        // convert X axis result into offset based on the actual Width of the grid ...
        int leftOffset = (int) ((result_X_axis / 10.0) * (double) this.getIntrinsicWidth());
        // convert Y axis result into offset based on the actual Height of the grid ...
//        int bottomOffset = (int) ((result_Y_axis / 10.0) * (double) this.getIntrinsicHeight());
        int tmpOffset = (int) ((result_Y_axis / 10.0)  * (double) this.getIntrinsicHeight())  + ( widget_width / 2 );
        int topOffset = Math.max((this.getIntrinsicHeight() - tmpOffset), 0);
        // position this cirlce using ...
        this.setLayerInset(layer, leftOffset, topOffset, rightOffset, bottomOffset);
    }

}
