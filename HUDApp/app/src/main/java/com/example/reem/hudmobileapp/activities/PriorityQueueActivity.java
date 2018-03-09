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
import android.widget.ArrayAdapter;

import com.example.reem.hudmobileapp.views.DynamicListView;
import com.example.reem.hudmobileapp.R;

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

    private static final String LOG_TAG = "tasks365";
    private ArrayAdapter adapter;
    private ArrayList<String> priorityList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.priority_queue_list_view);

        // Set a random list for now
        priorityList = new ArrayList<String>();
        priorityList.add("A");
        priorityList.add("B");
        priorityList.add("C");
        priorityList.add("D");

        adapter = new ArrayAdapter(this, R.layout.text_view, priorityList);
        DynamicListView listView = (DynamicListView) findViewById(R.id.listview);



        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ViewHolder viewHolder = (ViewHolder) view.getTag();
//                showQuickActionBar(viewHolder.title, position);
//            }
//        });
        listView.setDropListener(new DynamicListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from == to) {
                    return;
                }
                moveTaskToPosition(from, to);
            }
        });
    }

    private void moveTaskToPosition(int originalPosition, int newPosition) {
        Log.d(LOG_TAG, "moveTaskToPosition: " + originalPosition + " -> " + newPosition);

        if (newPosition >= adapter.getCount()) {
            Log.w(LOG_TAG, "moveTaskToPosition: Invalid new position " + newPosition);
            return;
        }

        int targetPosition = newPosition;
        if (newPosition > originalPosition) {
            targetPosition = newPosition + 1;
        }

        String priorityTobeMoved = (String) adapter.getItem(originalPosition);
        String priorityTobeReplacedWith =(String) adapter.getItem(newPosition);
        System.out.print(priorityList);
        Log.i("Old position",priorityList.toString());
        priorityList.set(newPosition,priorityTobeMoved);
        priorityList.set(originalPosition,priorityTobeReplacedWith);
        Log.i("New postion",priorityList.toString());
        adapter.notifyDataSetChanged();

    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        setContentView(R.layout.priority_queue_list_view);
//
//        ListView lv = (ListView) findViewById(R.id.listview);
//
//        List<String> items = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            items.add(Integer.toString(i));
//        }
//
//        lv.setAdapter(new MyAdapter(items));
//
//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                //
//                // start dragging
//                //
//                MyAdapter.ViewHolder vh = (MyAdapter.ViewHolder) view.getTag();
//
//                final int touchedX = (int) (vh.lastTouchedX + 0.5f);
//                final int touchedY = (int) (vh.lastTouchedY + 0.5f);
//
//                view.startDrag(null, new View.DragShadowBuilder(view) {
//                    @Override
//                    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
//                        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
//                        shadowTouchPoint.x = touchedX;
//                        shadowTouchPoint.y = touchedY;
//                    }
//
//                    @Override
//                    public void onDrawShadow(Canvas canvas) {
//                        super.onDrawShadow(canvas);
//                    }
//                }, view, 0);
//
//                view.setVisibility(View.INVISIBLE);
//
//                return true;
//            }
//        });
//
//        lv.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                if (event.getAction() == DragEvent.ACTION_DROP) {
//                    //
//                    // finish dragging
//                    //
//                    View view = (View) event.getLocalState();
//                    view.setVisibility(View.VISIBLE);
//                }
//                return true;
//            }
//        });
//    }
//
//    static class MyAdapter extends BaseAdapter implements View.OnTouchListener {
//        private List<String> mItems;
//
//        private static class ViewHolder {
//            public TextView text;
//            public float lastTouchedX;
//            public float lastTouchedY;
//
//            public ViewHolder(View v) {
//                text = (TextView) v;
//            }
//        }
//
//        MyAdapter(List<String> items) {
//            mItems = items;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view;
//            ViewHolder vh;
//
//            if (convertView == null) {
//                view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
//                view.setOnTouchListener(this);
//                vh = new ViewHolder(view);
//                view.setTag(vh);
//            } else {
//                view = convertView;
//                vh = (ViewHolder) view.getTag();
//            }
//
//            vh.text.setText(mItems.get(position));
//
//            return view;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return 0;
//        }
//
//        @Override
//        public int getCount() {
//            return mItems.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mItems.get(position);
//        }
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            ViewHolder vh = (ViewHolder) v.getTag();
//
//            vh.lastTouchedX = event.getX();
//            vh.lastTouchedY = event.getY();
//
//            return false;
//        }
//    }
}
