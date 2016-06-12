package colorPicker;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.ninebox.nineboxapp.R;

//import com.ninebox.colorPicker.*;
//import com.ninebox.nineboxapp.ColorPickerSwatch.OnColorSelectedListener;
import com.ninebox.nineboxapp.*;

/**
 * Created by Paul Gallini on 6/4/16.
 */

public class ColorPicker extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // TODO Remove
        System.out.println(" inside onCreate - ColorPicker! ");
//        System.out.println(color);

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();

        // TODO switch this out to use our custom list of colors
        colorPickerDialog.initialize(R.string.color_select_title, new int[]{Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW}, Color.YELLOW, 3, 2);
        FragmentManager fragMan = getFragmentManager();
        colorPickerDialog.show(fragMan, "colorpicker");

        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                                                         @Override
                                                         public void onColorSelected(int color) {
                                                             // Toast.makeText(MainActivity.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
                                                             // TODO Remove
                                                             System.out.println(" color selected = ");
                                                             System.out.println(color);
                                                         }
                                                     }
        );
//        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
//            }
//        });
    }
}


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
