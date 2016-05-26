package nineBoxCandidates;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ninebox.nineboxapp.R;

import java.util.ArrayList;
import java.util.List;

import databaseOpenHelper.DatabaseOpenHelper;

/**
 * Created by Paul Gallini on 5/19/16.
 */
public class SpinnerArrayAdapter extends ArrayAdapter<String> {
    Context context;
    private int resource;
    List<String> items = new ArrayList<String>();

    public SpinnerArrayAdapter(Context context, int textViewResourceId, List<String> labels ) {
        super(context, textViewResourceId, labels);
        this.items = labels;
        this.resource=textViewResourceId;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    R.layout.spinner_item, parent, false);
        }

        String color = getLabelColor(position);
        TextView tv = (TextView) convertView
                .findViewById(R.id.textview);

        // TODO Remove
        System.out.println( " items.get(position) =" );
        System.out.println( items.get(position) );

        tv.setText(items.get(position));
        int tmpcolor = Color.parseColor(color);
        tv.setTextColor(tmpcolor);
        // TODO change this to use a dimen entry
        tv.setTextSize(30);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // TODO Remove
            System.out.println( "in getView, convertView was null ") ;
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    R.layout.spinner_item, parent, false);
        }
//        LayoutInflater inflater=
//                ((Activity) context).getLayoutInflater();
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View row = inflater.inflate(R.layout.spinner_item,parent,false);

        // android.R.id.text1 is default text view in resource of the android.
        // android.R.layout.simple_spinner_item is default layout in resources of android.

        String color = getLabelColor(position);
        TextView tv = (TextView) convertView.findViewById(R.id.textview);
//        tv.setText(items.get(position));
//        tv.setTextColor(color);
//        // TODO change this to use a dimen entry
//        tv.setTextSize(30);
//        tv.setBackgroundColor(color);
        // this does nothing ...
//        super.getView(position, convertView, parent);
        return convertView;
//        View view = super.getView(position, convertView, parent);
//        int color = getLabelColor(position);
//        // TODO Remove
//        System.out.println( "color = ") ;
//        System.out.println(color) ;
//        view.setBackgroundColor(color);
//
//        // TODO Remove
//        System.out.println("parent.getChildCount() =");
//        System.out.println(parent.getChildCount());
//        return view;
    }

    public String getLabelColor( int index ) {
        DatabaseOpenHelper dbHelper;
        dbHelper = new DatabaseOpenHelper(getContext());
        ArrayList<appColor> colorList = dbHelper.getAllColors();
        return colorList.get( index ).getColor_number();
    }
}
