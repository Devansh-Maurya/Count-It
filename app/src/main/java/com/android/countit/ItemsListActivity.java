package com.android.countit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.countit.data.ItemsContract.Item;

public class ItemsListActivity extends AppCompatActivity {

    public static final int ITEMS_LIST_ACTIVITY_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        Intent newItemsListIntent = getIntent();
        final Uri itemsCategoryUri = newItemsListIntent.getData();

        FloatingActionButton floatingActionButton = (FloatingActionButton)
                findViewById(R.id.add_item);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewItemIntent = new Intent(ItemsListActivity.this, EditorActivity.class);
                addNewItemIntent.putExtra(EditorActivity.ACTIVITY_IDENTIFIER, ITEMS_LIST_ACTIVITY_ID);
                addNewItemIntent.setData(itemsCategoryUri);
                startActivity(addNewItemIntent);
            }
        });

        final ListView itemsListView = (ListView) findViewById(R.id.items_list_view);

        final String[] projection = {Item._ID, Item.COLUMN_ITEM_NAME, Item.COLUMN_ITEM_COUNT, Item.COLUMN_ITEM_COLOR};
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(itemsCategoryUri, projection, null, null,
                        null);

                ItemCursorAdapter adapter = new ItemCursorAdapter(ItemsListActivity.this, cursor, 0);
                itemsListView.setAdapter(adapter);
            }
        });

    }

    private int decrementItemCount(int itemCount) {
        return --itemCount;
    }

    private int incrementItemCount(int itemCount) {
        return ++itemCount;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.menu.menu_editor:
        }
        return true;
    }

    public class ItemCursorAdapter extends CursorAdapter {

        private LayoutInflater itemLayoutInflater;

        public ItemCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            itemLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return itemLayoutInflater.inflate(R.layout.list_item, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView itemNameTextView = (TextView) view.findViewById(R.id.item_name);
            final TextView itemCountTextView = (TextView) view.findViewById(R.id.item_count);
            Button decreaseButton = (Button) view.findViewById(R.id.decrease_button);
            Button increaseButton = (Button) view.findViewById(R.id.increase_button);

            view.setBackgroundColor((int) cursor.getLong(cursor.getColumnIndex(Item.COLUMN_ITEM_COLOR)));
            itemNameTextView.setText(cursor.getString(cursor.getColumnIndex(Item.COLUMN_ITEM_NAME)));
            itemCountTextView.setText(cursor.getString(cursor.getColumnIndex(Item.COLUMN_ITEM_COUNT)));

            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemCount = decrementItemCount(Integer.parseInt((String) itemCountTextView.getText()));
                    itemCountTextView.setText(Integer.toString(itemCount));
                }
            });

            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemCount = incrementItemCount(Integer.parseInt((String) itemCountTextView.getText()));
                    itemCountTextView.setText(Integer.toString(itemCount));
                }
            });
        }
    }
}
