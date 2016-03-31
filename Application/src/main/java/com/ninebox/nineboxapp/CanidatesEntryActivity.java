package com.ninebox.nineboxapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

//import nineBoxCanidates.Canidates;
import nineBoxCanidates.*;
import nineBoxMain.*;

public class CanidatesEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        System.out.println(extras.getString("myKey"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canidates_entry);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void saveCanidate(View view) {
        System.out.println("========== inside saveCanidate   ==========");
        // find the ListView so we can work with it ...
        EditText text = (EditText) findViewById( R.id.EditTextName);
        String canidateName = text.getText().toString();

        System.out.println(canidateName);

        // TODO figure out how to really add the new canidate to the list that gets displayed
//        ArrayList<Canidates> canidatesList = new ArrayList<Canidates>();
//        canidatesList.add(new Canidates(canidateName));
//        this.kill_activity();

        //create a new intent so we can return Canidate Data ...
        Intent intent = new Intent();
        //add "returnKey" as a key and assign it the value
        //in the textbox...
        intent.putExtra("returnKey",canidateName);
        //get ready to send the result back to the caller (MainActivity)
        //and put our intent into it (RESULT_OK will tell the caller that
        //we have successfully accomplished our task..
        setResult(RESULT_OK,intent);
        finish();
    }

    public void CancelSave(View view) {
//        System.out.println("========== inside Cancel Save  ==========");
        this.kill_activity();
    }

    void kill_activity()
    {
        finish();
    }

}
