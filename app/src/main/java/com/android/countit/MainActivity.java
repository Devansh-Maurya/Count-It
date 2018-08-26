package com.android.countit;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.countit.data.ItemsContract;
import com.android.countit.data.ItemsContract.Category;
import com.android.countit.data.ItemsDbHelper;

public class MainActivity extends AppCompatActivity {

    public static final int MAIN_ACTIVITY_ID = 0;
    Cursor cursor;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.category_list_view);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_category);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra(EditorActivity.ACTIVITY_IDENTIFIER, MAIN_ACTIVITY_ID);
                startActivity(intent);
            }
        });

        setCursorAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long position) {
                TextView textView = (TextView) view.findViewById(R.id.main_Text);
                String categoryName = (String) textView.getText();
                Uri currentCategoryUri = Uri.withAppendedPath(ItemsContract.BASE_CONTENT_URI, categoryName);

                Intent categoryItemsIntent = new Intent(MainActivity.this, ItemsListActivity.class);
                categoryItemsIntent.setData(currentCategoryUri);
                startActivity(categoryItemsIntent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String categoryName = (String) ((TextView)view.findViewById(R.id.main_Text)).getText();
                showDeleteConfirmationDialog(categoryName);
                return true;
            }
        });
    }

    private void showDeleteConfirmationDialog(final String categoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.category_delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCategory(categoryName);

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

    private void deleteCategory(String categoryName) {
        int rowsDeleted = getContentResolver().delete(Category.CATEGORY_TABLE_URI, Category.COLUMN_CATEGORY_NAME + "=?",
                new String[] {categoryName});

        final String DROP_CATEGORY_TABLE = "DROP TABLE " + categoryName + " ;";
        ItemsDbHelper itemsDbHelper = new ItemsDbHelper(this);
        SQLiteDatabase database = itemsDbHelper.getWritableDatabase();
        database.execSQL(DROP_CATEGORY_TABLE);

        if (rowsDeleted == 1) {
            Toast.makeText(this, R.string.category_deletion_success, Toast.LENGTH_SHORT).show();
        } else if (rowsDeleted == -1) {
            Toast.makeText(this, R.string.category_deletion_failure, Toast.LENGTH_SHORT).show();
        }
    }

    private void setCursorAdapter() {
        final String[] projection = {Category._ID, ItemsContract.Category.COLUMN_CATEGORY_NAME,
                Category.COLUMN_CATEGORY_COLOR};

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                cursor = getContentResolver().query(Category.CATEGORY_TABLE_URI, projection, null, null,
                        null);
                CategoryCursorAdapter adapter = new CategoryCursorAdapter(MainActivity.this, cursor, 0);

                listView.setAdapter(adapter);
            }
        });
    }
}
