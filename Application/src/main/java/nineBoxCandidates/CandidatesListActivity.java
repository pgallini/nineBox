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
package nineBoxCandidates;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.ninebox.nineboxapp.R;

import java.util.ArrayList;

import nineBoxEvaluation.Evaluation;
import nineBoxMain.MainActivity;
import nineBoxQuestions.QuestionsEntryActivity;

/**
 * This activity lists out the existing candidates and allows for additions, deletions, and evaluation .
 */
public class CandidatesListActivity extends AppCompatActivity {
    private final int CANDIDATESENTRY_ACTIVITY_REQUEST_CODE = 0;
    private final int QUESTIONSENTRY_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    private CandidateOperations candidateOperations;
    private ListView mainListView;
    private ArrayAdapter<String> mainArrayAdapter;
    Context context = CandidatesListActivity.this;
    private ArrayList<String> displayList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates_list);

        // TODO add display informative graphic when list is empty

        // set-up the operations class for Candidates ...
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

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
                                // Delete icon selected - delete the current Candidate
                                showDeleteDialog(position);
                            }
                        });
                convertView.findViewById(R.id.eval_action).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO figure out how to tell Evaluate to stop after its done with this candidate
                                MainActivity.setCurrentCandidate(position);
                                Intent intent = new Intent(view.getContext(), Evaluation.class);
                                startActivity(intent);
//                                Toast.makeText(CandidatesListActivity.this,
//                                        R.string.touched_config_message,
//                                        Toast.LENGTH_SHORT).show();
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
    private void delete_candidate( int position ) {
        candidateOperations.deleteCandidate(candidatesList.get(position));
        candidatesList.remove(candidatesList.get(position));
        displayList.remove(position);
        // notify mainArrayAdapter that things have changed and a refresh is needed ...
        mainArrayAdapter.notifyDataSetChanged();

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
            String returnCandidateColor = (extras != null ? extras.getString("returnColor") : " ");
            // save to database
            Candidates candidate = candidateOperations.addCandidate(returnCandidateName, returnCandidateNotes, returnCandidateColor);

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
    private void showDeleteDialog(int position ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CandidatesListActivity.this);
        builder.setTitle(getString(R.string.confirm_delete_title));
        builder.setMessage(getString(R.string.confirm_delete_message));
        boolean returnBool = false;
        final int curr_postion = position;

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_candidate( curr_postion );
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

}
