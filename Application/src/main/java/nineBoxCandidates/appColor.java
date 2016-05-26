package nineBoxCandidates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Gallini on 5/20/16.
 */
public class appColor {
    private String color_text;
    private String color_number;
    private int color_inuse;

    public appColor(String color_text, String color_number, int color_inuse) {
        super();
        setColor_text(color_text);
        setColor_number(color_number);
        setColor_inuse(color_inuse);
    }

    public String getColor_text() {
        return color_text;
    }

    public void setColor_text(String color_text) {
        this.color_text = color_text;
    }

    public String getColor_number() {
        return color_number;
    }

    public void setColor_number(String color_number) {
        this.color_number = color_number;
    }

    public int getColor_inuse() {
        return color_inuse;
    }

    public void setColor_inuse(int color_inuse) {
        this.color_inuse = color_inuse;
    }

}
