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
import com.ninebox.nineboxapp.R;

import nineBoxCandidates.Candidates;
import nineBoxQuestions.Questions;

/**
 * This activity demonstrates the <b>borderless button</b> styling from the Holo visual language.
 * The most interesting bits in this sample are in the layout files (res/layout/).
 * <p>
 * See <a href="http://developer.android.com/design/building-blocks/buttons.html#borderless">
 * borderless buttons</a> at the Android Design guide for a discussion of this visual style.
 */
//public class MainActivity extends ListActivity {
public class MainActivity extends Activity {
//    TODO  get rid of this or change it to something useful
    private static final Uri DOCS_URI = Uri.parse(
            "http://developer.android.com/design/building-blocks/buttons.html#borderless");
    public ArrayList<Candidates> canidatesList = new ArrayList<Candidates>();
    public ArrayAdapter<String> arrayAdapter = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        // Create initial listAdapter - populate with initial entry
        String[] itemList = new String[] { "Empty List" };
        ArrayList<String> displayList = new ArrayList<String>();
        displayList.addAll(Arrays.asList(itemList));

        // find the ListView so we can work with it ...
        ListView mainListView = (ListView) findViewById( R.id.list);
        arrayAdapter =new ArrayAdapter<String>(this, R.layout.list_item, R.id.canidate, displayList);
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
                finish();
            }
        });
    }

    private BaseAdapter mListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return canidatesList.size();
        }

        @Override
//        public Object getItem(int position) {
//            return null;
//        }
        public Object getItem(int position) {
            return canidatesList.get(position).getCanidateName();
        }

        @Override
        public long getItemId(int position) {
            return position + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }

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

            convertView.findViewById(R.id.secondary_action).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this,
                                    R.string.touched_secondary_message,
                                    Toast.LENGTH_SHORT).show();
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
        String newCanidateName = " ";

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
        for( String qText : tmpQuestionText) {
            System.out.println(qText.toString());
        }

        System.out.println("========== Question Weight ==========");
        tmpQuestionWeight = questionSet_X_Axis.getQuestionWeight();
        for( int qText : tmpQuestionWeight) {
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
        for( String qText : tmpQuestionText) {
            System.out.println(qText.toString());
        }

        System.out.println("========== Question Weight ==========");
        tmpQuestionWeight = questionSet_Y_Axis.getQuestionWeight();
        for( int qText : tmpQuestionWeight) {
            System.out.println(qText);
        }
    }

    //    TODO - this is temporary set-up code ... get rid of it
    private ArrayList<Candidates> setUpCanidates() {
        ArrayList<Candidates> canidatesList = new ArrayList<Candidates>();
        canidatesList.add(new Candidates( "Bob" ));
        canidatesList.add(new Candidates( "Suresh" ));
        canidatesList.add(new Candidates( "Kathy" ));

        return canidatesList;
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
                    canidatesList = setUpCanidates();

                    System.out.println("=== Canidate Info ===");
                    for (int i = 0; i < canidatesList.size(); i++) {

                        // add each Candidates name to our listAdapter ...
                        arrayAdapter.add(canidatesList.get(i).getCanidateName());
                        arrayAdapter.notifyDataSetChanged();
                        System.out.println("name = " + canidatesList.get(i).getCanidateName());
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
