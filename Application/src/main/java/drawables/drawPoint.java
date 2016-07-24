package drawables;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import com.ninebox.nineboxapp.R;


/**
 * Created by Paul Gallini on 5/27/16.
 */
public class drawPoint extends LayerDrawable {

    private int shapeHeight;
    private int shapeWidth;
    private int color;
    private Context context;
    private Resources resources;

    public drawPoint(Context context, Drawable[] layers, int width, int height, int tmpcolor) {
        super(layers);
        setShapeHeight(height);
        setShapeWidth(width);
        setColor(tmpcolor);
        this.context = context;
        resources = this.context.getResources();
    }

    public int getShapeHeight() {
        return shapeHeight;
    }

    public void setShapeHeight(int shapeHeight) {
        this.shapeHeight = shapeHeight;
    }

    public int getShapeWidth() {
        return shapeWidth;
    }

    public void setShapeWidth(int shapeWidth) {
        this.shapeWidth = shapeWidth;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public LayerDrawable getPoint(String initials) {

        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight(getShapeHeight());
        oval.setIntrinsicWidth(getShapeWidth());
        oval.getPaint().setColor(getColor());
        // get icon padding from the dimens file
        Resources res =  this.context.getResources();
        int iconPadding = (int) res.getDimension(R.dimen.icon_padding);
        oval.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        this.addLayer(oval);
        Drawable textLayer = getDrawable( initials );
        this.addLayer(textLayer);
        return this;
    }
    public Drawable getDrawable(String gText) {
        try {
            float scale = resources.getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setAlpha(80);
            paint.setStyle(Paint.Style.FILL);
            Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            paint.setColor(Color.BLUE);

            // get icon text size from the integers file
            Resources res =  this.context.getResources();
            int iconTextSize = (int) res.getInteger(R.integer.iconTextSize);
            paint.setTextSize((int) (iconTextSize * scale));
            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(gText, 0, gText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = (bitmap.getHeight() + bounds.height())/2;
            canvas.drawText(gText, x, y, paint);

            BitmapDrawable drawable = new BitmapDrawable(this.resources, bitmap);
            return drawable;
        }
        catch(Exception ex) {return null;}
    }

}
