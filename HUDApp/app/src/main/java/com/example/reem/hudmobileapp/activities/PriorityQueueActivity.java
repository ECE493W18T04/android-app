/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.reem.hudmobileapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.reem.hudmobileapp.constants.DefaultPriorityList;
import com.example.reem.hudmobileapp.constants.HUDObject;
import com.example.reem.hudmobileapp.constants.PriorityQueueEnum;
import com.example.reem.hudmobileapp.helper.FileManager;
import com.example.reem.hudmobileapp.views.DynamicListView;
import com.example.reem.hudmobileapp.R;
import com.example.reem.hudmobileapp.views.StableArrayAdapter;

import java.util.ArrayList;

/**
 * This application creates a listview where the ordering of the data set
 * can be modified in response to user touch events.
 *
 * An item in the listview is selected via a long press event and is then
 * moved around by tracking and following the movement of the user's finger.
 * When the item is released, it animates to its new position within the listview.
 */
public class PriorityQueueActivity extends Activity {

    private Button priorityQueueBtnOkay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.priority_queue_list_view);

        ArrayList<String>priorityList = new ArrayList<String>();
        for (int i = 0; i < DefaultPriorityList.priorityList.length; ++i) {
            priorityList.add(DefaultPriorityList.priorityList[i]);
        }

        StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.text_view, priorityList);
        final DynamicListView listView = (DynamicListView) findViewById(R.id.listview);

        listView.setPriorityQueryArray(priorityList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayList<String> priorityQueue=listView.getPriorityQueueArray();
        priorityQueueBtnOkay = (Button) findViewById(R.id.priorityQueueBtn);
        priorityQueueBtnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HUDObject hudObject=FileManager.loadFromFile(PriorityQueueActivity.this);
                hudObject.setPriorityQueue(listView.getPriorityQueueArray());
                FileManager.saveToFile(PriorityQueueActivity.this,hudObject);
                finish();
            }
        });
    }



}
