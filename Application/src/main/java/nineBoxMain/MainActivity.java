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

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

import nineBoxCandidates.CandidatesListActivity;
import nineBoxEvaluation.Evaluation;
import nineBoxQuestions.QuestionsEntryActivity;
import com.ninebox.nineboxapp.R;

/**
 * This activity is the main activity for the NineBoxMobile app.
 */
public class MainActivity extends AppCompatActivity {
//    public class MainActivity extends Activity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nine_box_main);

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        findViewById(R.id.button_add_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CandidatesListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_set_questions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Evaluation.class);
                startActivity(intent);
            }
        });


//
//        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(view.getContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        });
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        mainArrayAdapter.notifyDataSetChanged();
//    }


    //    TODO - this is temporary set-up code ... get rid of it
//    private void setUpQuestions() {
//        Questions questionSet_X_Axis = new Questions();
//        Questions questionSet_Y_Axis = new Questions();
//        ArrayList<String> tmpQuestionText;
//        ArrayList<String> tmpQuestionResp;
//        ArrayList<Integer> tmpQuestionWeight;
//        String newCandidateName = " ";
//
//        // Set up X Axis questions ...
//        questionSet_X_Axis.addQuestionText("X Question 1");
//        questionSet_X_Axis.addQuestionText("X Question 2");
//        questionSet_X_Axis.addQuestionText("X Question 3");
//        questionSet_X_Axis.addQuestionText("X Question 4");
//
//        questionSet_X_Axis.addQuestionWeight(1);
//        questionSet_X_Axis.addQuestionWeight(3);
//        questionSet_X_Axis.addQuestionWeight(5);
//        questionSet_X_Axis.addQuestionWeight(1);
//
//        System.out.println("========== Question Text ==========");
//        tmpQuestionText = questionSet_X_Axis.getQuestionText();
//        for (String qText : tmpQuestionText) {
//            System.out.println(qText.toString());
//        }
//
//        System.out.println("========== Question Weight ==========");
//        tmpQuestionWeight = questionSet_X_Axis.getQuestionWeight();
//        for (int qText : tmpQuestionWeight) {
//            System.out.println(qText);
//        }
//
//        // Set up Y Axis questions ...
//        questionSet_Y_Axis.addQuestionText("Y Question 1");
//        questionSet_Y_Axis.addQuestionText("Y Question 2");
////		questionSet_Y_Axis.addQuestionText("Y Question 3");
////		questionSet_Y_Axis.addQuestionText("Y Question 4");
//
//        questionSet_Y_Axis.addQuestionWeight(6);
//        questionSet_Y_Axis.addQuestionWeight(4);
////		questionSet_Y_Axis.addQuestionWeight(5);
////		questionSet_Y_Axis.addQuestionWeight(1);
//
//        System.out.println("========== Question Text ==========");
//        tmpQuestionText = questionSet_Y_Axis.getQuestionText();
//        for (String qText : tmpQuestionText) {
//            System.out.println(qText.toString());
//        }
//
//        System.out.println("========== Question Weight ==========");
//        tmpQuestionWeight = questionSet_Y_Axis.getQuestionWeight();
//        for (int qText : tmpQuestionWeight) {
//            System.out.println(qText);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
            case R.id.menu_add_people:
                try {
                    Intent intent = new Intent(this, CandidatesListActivity.class);
                    startActivity(intent);
                    } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //we need a handler for when the secondary activity (add new candidate) finishes it's work
    //and returns control to this activity...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null ) {
            Bundle extras = intent.getExtras();
//            String returnCandidateName = (extras != null ? extras.getString("returnKey") : "nothing returned");
//            String returnCandidateNotes = (extras != null ? extras.getString("returnNotes") : " ");

            if (resultCode == RESULT_CANCELED) {
                System.out.println(" Evaluation was Cancelled");
            }
        }
    }
}
