package com.android.countit;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.countit.data.ItemsContract.Item;

public class ItemsListActivity extends AppCompatActivity {

    public static final int ITEMS_LIST_ACTIVITY_ID = 1;

    ListView itemsListView;
    Uri itemsCategoryUri;

    //Using SparseArray instead of HashMap because it is memory efficient
    //SparseArray assumes only integer keys
    //Using SparseIntArray to avoid autoboxing
    SparseIntArray updatedItemCounts = new SparseIntArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        Intent newItemsListIntent = getIntent();
        itemsCategoryUri = newItemsListIntent.getData();


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

       itemsListView = (ListView) findViewById(R.id.items_list_view);

       itemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               String itemName = (String) ((TextView)view.findViewById(R.id.item_name)).getText();
               showDeleteConfirmationDialog(itemName);
               return false;
           }
       });

       setCursorAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void deleteItem(String itemName) {
        int rowsDeleted = getContentResolver().delete(itemsCategoryUri, Item.COLUMN_ITEM_NAME + "=?",
                new String[] {itemName});

        if (rowsDeleted == 1) {
            Toast.makeText(this, R.string.item_deletion_success, Toast.LENGTH_SHORT).show();
        } else if (rowsDeleted == -1) {
            Toast.makeText(this, R.string.item_deletion_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(final String itemName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem(itemName);
                setCursorAdapter();
                //finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setCursorAdapter() {
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

    public void updateCategoryItem(int id, int itemCount) {
        ContentValues values = new ContentValues();
        values.put(Item.COLUMN_ITEM_COUNT, itemCount);

        int rowsUpdated = getContentResolver().update(itemsCategoryUri, values,
                Item._ID + "=?", new String[] {Integer.toString(id)});

        if (rowsUpdated == -1) {
            Toast.makeText(ItemsListActivity.this, R.string.item_updation_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ItemsListActivity.this, R.string.item_updation_successful, Toast.LENGTH_SHORT).show();
        }
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
            LinearLayout textLinearLayout = (LinearLayout) view.findViewById(R.id.text_linear_layout);
            TextView itemNameTextView = (TextView) view.findViewById(R.id.item_name);
            final TextView itemCountTextView = (TextView) view.findViewById(R.id.item_count);
            Button decreaseButton = (Button) view.findViewById(R.id.decrease_button);
            Button increaseButton = (Button) view.findViewById(R.id.increase_button);

            view.setBackgroundColor((int) cursor.getLong(cursor.getColumnIndex(Item.COLUMN_ITEM_COLOR)));
            itemNameTextView.setText(cursor.getString(cursor.getColumnIndex(Item.COLUMN_ITEM_NAME)));
            itemCountTextView.setText(cursor.getString(cursor.getColumnIndex(Item.COLUMN_ITEM_COUNT)));

            final int currentItemId = cursor.getInt(cursor.getColumnIndex(Item._ID));

            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemCount = decrementItemCount(Integer.parseInt((String) itemCountTextView.getText()));
                    itemCountTextView.setText(Integer.toString(itemCount));
                    updatedItemCounts.append(currentItemId, itemCount);
                }
            });

            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemCount = incrementItemCount(Integer.parseInt((String) itemCountTextView.getText()));
                    itemCountTextView.setText(Integer.toString(itemCount));
                    updatedItemCounts.append(currentItemId, itemCount);
                }
            });

            textLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCategoryItem(currentItemId, Integer.parseInt((String)itemCountTextView.getText()));
                }
            });
        }
    }
}
