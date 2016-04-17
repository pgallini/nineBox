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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nineBoxCandidates.CandidatesEntryActivity;
import nineBoxQuestions.QuestionsEntryActivity;
import com.ninebox.nineboxapp.R;

import nineBoxCandidates.Candidates;
import nineBoxCandidates.CandidateOperations;

/**
 * This activity is the main activity for the NineBoxMobile app.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nine_box_main);

        findViewById(R.id.button_add_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CandidatesListActivity.class);
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
}
