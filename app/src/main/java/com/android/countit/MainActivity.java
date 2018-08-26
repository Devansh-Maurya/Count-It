package com.android.countit;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.countit.data.ItemsContract;
import com.android.countit.data.ItemsContract.Category;

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
    }
}
