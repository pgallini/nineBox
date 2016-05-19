package nineBoxReport;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;

/**
 * Created by Paul Gallini on 5/17/16.
 */
public class GridLayerDrawable extends LayerDrawable {

    public GridLayerDrawable(Drawable[] layers) {
        super(layers);
    }

    public void addLayer(ShapeDrawable circleTemp, int height, int sizeHeight, int sizeWidth) {
        this.addLayer(circleTemp);

        // TODO not sure if this works as I expect
//        this.setLayerHeight(1, height);
        // This only works properly when it's done after setWidgetPosition() for some reason
//        this.setLayerSize(1, sizeHeight, sizeWidth);
    }

    public void setWidgetPosition(int layer, double result_X_axis, double result_Y_axis) {
        // This method will position a given layer/Circle on the Grid
        int rightOffset = 8; // used to keep the circles off the right edge
        int bottomOffset = 8; // used to keep the circles off the bottom
        // convert X axis result into offset based on the actual Width of the grid ...
        int leftOffset = (int) ((result_X_axis / 10.0) * (double) this.getIntrinsicWidth());
        // convert Y axis result into offset based on the actual Height of the grid ...
        int topOffset = (int) ((result_Y_axis / 10.0) * (double) this.getIntrinsicHeight());
        // position this cirlce using ...
        this.setLayerInset(layer, leftOffset, topOffset, rightOffset, bottomOffset);
    }

}
