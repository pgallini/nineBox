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
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.ninebox.nineboxapp.CandidatesEntryActivity;
import com.ninebox.nineboxapp.R;

import nineBoxCandidates.Candidates;
import nineBoxCandidates.CandidateOperations;
import nineBoxQuestions.Questions;

/**
 * This activity is the main activity for the NineBoxMobile app.
 */
public class MainActivity extends Activity {
    //    TODO  get rid of this or change it to something useful
    private static final Uri DOCS_URI = Uri.parse(
            "http://developer.android.com/design/building-blocks/buttons.html#borderless");
    private final int CANDIDATESENTRY_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();

    private CandidateOperations candidateOperations;
    private ListView mainListView;
    private ArrayAdapter<String> mainArrayAdapter;
    Context context = MainActivity.this;
    private ArrayList<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

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
                // TODO remove this
                System.out.println("inside getView ");

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
                                Toast.makeText(MainActivity.this,
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
                                Toast.makeText(MainActivity.this,
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
            System.out.println("returned name = " + returnCandidateName);
//            mainArrayAdapter.add(returnCandidateName);
            // save to database
            Candidates candidate = candidateOperations.addCandidate(returnCandidateName, returnCandidateNotes);

            // TODO  should we add Notes?
            candidatesList.add(candidate);
            displayList.add(candidate.getCandidateName());
            // notify mainArrayAdapter that things have changed and a refresh is needed ...
            mainArrayAdapter.notifyDataSetChanged();
        }
    }

    //    TODO - this is temporary set-up code ... get rid of it
    private void setUpQuestions() {
        Questions questionSet_X_Axis = new Questions();
        Questions questionSet_Y_Axis = new Questions();
        ArrayList<String> tmpQuestionText;
        ArrayList<String> tmpQuestionResp;
        ArrayList<Integer> tmpQuestionWeight;
        String newCandidateName = " ";

        // Set up X Axis questions ...
        questionSet_X_Axis.addQuestionText("X Question 1");
        questionSet_X_Axis.addQuestionText("X Question 2");
        questionSet_X_Axis.addQuestionText("X Question 3");
        questionSet_X_Axis.addQuestionText("X Question 4");

        questionSet_X_Axis.addQuestionWeight(1);
        questionSet_X_Axis.addQuestionWeight(3);
        questionSet_X_Axis.addQuestionWeight(5);
        questionSet_X_Axis.addQuestionWeight(1);

        System.out.println("========== Question Text ==========");
        tmpQuestionText = questionSet_X_Axis.getQuestionText();
        for (String qText : tmpQuestionText) {
            System.out.println(qText.toString());
        }

        System.out.println("========== Question Weight ==========");
        tmpQuestionWeight = questionSet_X_Axis.getQuestionWeight();
        for (int qText : tmpQuestionWeight) {
            System.out.println(qText);
        }

        // Set up Y Axis questions ...
        questionSet_Y_Axis.addQuestionText("Y Question 1");
        questionSet_Y_Axis.addQuestionText("Y Question 2");
//		questionSet_Y_Axis.addQuestionText("Y Question 3");
//		questionSet_Y_Axis.addQuestionText("Y Question 4");

        questionSet_Y_Axis.addQuestionWeight(6);
        questionSet_Y_Axis.addQuestionWeight(4);
//		questionSet_Y_Axis.addQuestionWeight(5);
//		questionSet_Y_Axis.addQuestionWeight(1);

        System.out.println("========== Question Text ==========");
        tmpQuestionText = questionSet_Y_Axis.getQuestionText();
        for (String qText : tmpQuestionText) {
            System.out.println(qText.toString());
        }

        System.out.println("========== Question Weight ==========");
        tmpQuestionWeight = questionSet_Y_Axis.getQuestionWeight();
        for (int qText : tmpQuestionWeight) {
            System.out.println(qText);
        }
    }

    //    TODO - this is temporary set-up code ... get rid of it
    private ArrayList<Candidates> setUpCandidates() {
        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(new Candidates("Bob"));
        candidatesList.add(new Candidates("Suresh"));
        candidatesList.add(new Candidates("Kathy"));

        return candidatesList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.docs_link:
                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, DOCS_URI));
//                    TODO replace this with something real...
//                    candidatesList = setUpCandidates();
//
//                    System.out.println("=== Candidate Info ===");
//                    for (int i = 0; i < candidatesList.size(); i++) {
//
//                        // add each Candidates name to our listAdapter ...
//                        arrayAdapter.add(candidatesList.get(i).getCandidateName());
//                        arrayAdapter.notifyDataSetChanged();
//                        System.out.println("name = " + candidatesList.get(i).getCandidateName());
//                    }

                    //    TODO remove this ...
                    setUpQuestions();


                } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
