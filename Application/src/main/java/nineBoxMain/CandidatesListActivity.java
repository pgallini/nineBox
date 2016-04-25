/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.ninebox.nineboxapp;
package nineBoxMain;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninebox.nineboxapp.R;

import java.util.ArrayList;

import nineBoxCandidates.CandidateOperations;
import nineBoxCandidates.Candidates;
import nineBoxCandidates.CandidatesEntryActivity;
import nineBoxQuestions.QuestionsEntryActivity;

/**
 * This activity is the main activity for the NineBoxMobile app.
 */
public class CandidatesListActivity extends Activity {
    private final int CANDIDATESENTRY_ACTIVITY_REQUEST_CODE = 0;
    private final int QUESTIONSENTRY_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    private CandidateOperations candidateOperations;
    private ListView mainListView;
    private ArrayAdapter<String> mainArrayAdapter;
    Context context = CandidatesListActivity.this;
    private ArrayList<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates_list);

        // TODO add display informative graphic when list is empty

        // set-up the operations class for Candidates ...
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();

        // create a list of candidates from what's in the database ...
        candidatesList = candidateOperations.getAllCandidates();
        // make an array of just the Names for the purpose of displaying ...
        displayList = buildDisplayList( candidatesList );

        // find the ListView so we can work with it ...
        mainListView = (ListView) findViewById(R.id.list);

        mainArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.candidate, displayList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                Context context = parent.getContext();

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                }

                View view = super.getView(position, convertView, parent);

                TextView candidateText = (TextView) view.findViewById(R.id.candidate);
                candidateText.setText(displayList.get(position).toString());

                // Because the list item contains multiple touch targets, you should not override
                // onListItemClick. Instead, set a click listener for each target individually.
                convertView.findViewById(R.id.primary_target).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO make this an edit feature
                                Toast.makeText(CandidatesListActivity.this,
                                        R.string.touched_primary_message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                convertView.findViewById(R.id.delete_action).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO add confirmation
                                // Delete icon selected - delete the current Candidate
                                candidateOperations.deleteCandidate(candidatesList.get(position));
                                candidatesList.remove(candidatesList.get(position));
                                displayList.remove(position);
                                // notify mainArrayAdapter that things have changed and a refresh is needed ...
                                mainArrayAdapter.notifyDataSetChanged();
                            }
                        });
                convertView.findViewById(R.id.config_action).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(CandidatesListActivity.this,
                                        R.string.touched_config_message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                return convertView;

            }
        };

        mainListView.setAdapter(mainArrayAdapter);
        mainArrayAdapter.notifyDataSetChanged();

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), CandidatesEntryActivity.class);
                intent.putExtra("myKey", "sampleText");
                startActivityForResult(intent, CANDIDATESENTRY_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        mainArrayAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> buildDisplayList( ArrayList<Candidates> candidatesList ) {
        ArrayList<String> returnList = new ArrayList();
        for(int i = 0; i <  candidatesList.size(); i++ ) {
            returnList.add( candidatesList.get(i).getCandidateName() );
        }
        return returnList;
    }

    //we need a handler for when the secondary activity (add new candidate) finishes it's work
    //and returns control to this activity...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null ) {
            Bundle extras = intent.getExtras();
            String returnCandidateName = (extras != null ? extras.getString("returnKey") : "nothing returned");
            String returnCandidateNotes = (extras != null ? extras.getString("returnNotes") : " ");
            // save to database
            Candidates candidate = candidateOperations.addCandidate(returnCandidateName, returnCandidateNotes);

            // TODO  should we add Notes?
            candidatesList.add(candidate);
            displayList.add(candidate.getCandidateName());
            // notify mainArrayAdapter that things have changed and a refresh is needed ...
            mainArrayAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // TODO - is this needed here AND in MainActivity?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_configure_questions:
                try {
                    Intent intent = new Intent(this, QuestionsEntryActivity.class);
                    this.startActivity(intent);

                } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
