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
import nineBoxReport.ReportActivity;
import com.ninebox.nineboxapp.R;

/**
 * This activity is the main activity for the NineBoxMobile app.
 */
public class MainActivity extends AppCompatActivity {
//    public class MainActivity extends Activity {
    private Toolbar toolbar;
    static public int candidateIndex = 0;

    // TODO consider adding a FunkyNet splash screen
    // https://www.bignerdranch.com/blog/splash-screens-the-right-way/
    // 
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

        findViewById(R.id.button_see_results).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReportActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
    static public int getCurrentCandidate() {
        return candidateIndex;
    }
    static public void setCurrentCandidate(int newIndex) {
        candidateIndex = newIndex;
    }
    static public void incrementCurrentCandidate() {
        // TODO consider adding test to ensure we don't incrment it past candidatesList.size()
            candidateIndex++;
    }
}
