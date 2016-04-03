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
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import com.ninebox.nineboxapp.CandidatesEntryActivity;
import com.ninebox.nineboxapp.R;

import nineBoxCandidates.Candidates;
import nineBoxCandidates.CandidateOperations;
import nineBoxQuestions.Questions;

/**
 * This activity demonstrates the <b>borderless button</b> styling from the Holo visual language.
 * The most interesting bits in this sample are in the layout files (res/layout/).
 * <p/>
 * See <a href="http://developer.android.com/design/building-blocks/buttons.html#borderless">
 * borderless buttons</a> at the Android Design guide for a discussion of this visual style.
 */
public class MainActivity extends Activity {
    //    TODO  get rid of this or change it to something useful
    private static final Uri DOCS_URI = Uri.parse(
            "http://developer.android.com/design/building-blocks/buttons.html#borderless");
    private final int CANDIDATESENTRY_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    public ArrayAdapter<String> arrayAdapter = null;
    private CandidateOperations candidateOperations;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

//        setAdapter(mAdapter);  this is wrong!!

        // Create initial listAdapter - populate with initial entry
        // TODO get rid of this ... display informative graphic when list is empty
//        String[] itemList = new String[]{"Empty List"};
        ArrayList<String> displayList = new ArrayList<String>();
//        displayList.addAll(Arrays.asList(itemList));

        // set-up the operations class for Candidates ...
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();

        // create a list of candidates from what's in the database ...
        displayList = candidateOperations.getAllCandidates();

        // find the ListView so we can work with it ...
        ListView mainListView = (ListView) findViewById(R.id.list);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.candidate, displayList);
        mainListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

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

    //we need a handler for when the secondary activity finishes it's work
    //and returns control to this activity...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null ) {
            Bundle extras = intent.getExtras();
            String returnCandidateName = (extras != null ? extras.getString("returnKey") : "nothing returned");
            String returnCandidateNotes = (extras != null ? extras.getString("returnNotes") : " ");
            System.out.println("returned name = " + returnCandidateName);
            arrayAdapter.add(returnCandidateName);
            // save to database
            Candidates candidate = candidateOperations.addCandidate(returnCandidateName, returnCandidateNotes);

            // TODO is this needed? should we add Notes?
            new Candidates(returnCandidateName);
            arrayAdapter.notifyDataSetChanged();
        }
    }
    // TODO add a onResume() method where we refresh the list of candidates
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return candidatesList.size();
        }

        @Override
        public Object getItem(int position) {
            return candidatesList.get(position).getCandidateName();
        }

        @Override
        public long getItemId(int position) {
            return position + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // TODO remove this
            System.out.println("inside getView ");

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }
            // Because the list item contains multiple touch targets, you should not override
            // onListItemClick. Instead, set a click listener for each target individually.
            convertView.findViewById(R.id.primary_target).setOnClickListener(
                    // TODO figure out why these listeners aren't working!!
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO remove this
                            System.out.println("name clicked");
                            Toast.makeText(MainActivity.this,
                                    R.string.touched_primary_message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            convertView.findViewById(R.id.delete_action).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // TODO remove this
                            System.out.println("Delete clicked");

                            // Delete icon selected - delete the current Candidate
                            candidateOperations.deleteCandidate(candidatesList.get(R.id.candidate));
//                            Toast.makeText(MainActivity.this,
//                                    R.string.touched_secondary_message,
//                                    Toast.LENGTH_SHORT).show();
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
                    candidatesList = setUpCandidates();

                    System.out.println("=== Candidate Info ===");
                    for (int i = 0; i < candidatesList.size(); i++) {

                        // add each Candidates name to our listAdapter ...
                        arrayAdapter.add(candidatesList.get(i).getCandidateName());
                        arrayAdapter.notifyDataSetChanged();
                        System.out.println("name = " + candidatesList.get(i).getCandidateName());
                    }

                    //    TODO remove this ...
                    setUpQuestions();


                } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
