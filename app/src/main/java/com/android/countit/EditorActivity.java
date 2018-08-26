package com.android.countit;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.countit.data.ItemsContract;
import com.android.countit.data.ItemsContract.Item;
import com.android.countit.data.ItemsContract.Category;
import com.android.countit.data.ItemsDbHelper;

public class EditorActivity extends AppCompatActivity {

    public static final String ACTIVITY_IDENTIFIER = "activity_identifier";
    Spinner colorSpinner;
    long color;
    Uri itemsCategoryUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        itemsCategoryUri = getIntent().getData();

        FloatingActionButton doneFloatingActionButton = (FloatingActionButton)
                findViewById(R.id.done);
        doneFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemsCategoryUri == null) {
                    createCategoryTable();
                } else {
                    insertNewItem();
                }
                navigateUpTo(new Intent(EditorActivity.this, MainActivity.class));
            }
        });

        colorSpinner = (Spinner) findViewById(R.id.spinner_color);
        setupSpinner();

        //Hide initial item count if it has opened for entering a new category
        switch (getIntent().getExtras().getInt(ACTIVITY_IDENTIFIER)) {
            case MainActivity.MAIN_ACTIVITY_ID:
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_initial_count);
                linearLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void createCategoryTable() {
        EditText nameEditText = (EditText) findViewById(R.id.edit_pet_name);
        colorSpinner = (Spinner) findViewById(R.id.spinner_color);

        String categoryName = nameEditText.getText().toString().trim();
        Uri categoryUri = Uri.withAppendedPath(ItemsContract.BASE_CONTENT_URI, Category.CATEGORY_TABLE_NAME);

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_CATEGORY_NAME, categoryName);
        values.put(Category.COLUMN_CATEGORY_COLOR, color);

        String queryCreateTable = "CREATE TABLE " + categoryName + " ( "
                + Item._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Item.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + Item.COLUMN_ITEM_COLOR + " INTEGER NOT NULL, "
                + Item.COLUMN_ITEM_COUNT + " INTEGER DEFAULT 0); ";

        ItemsDbHelper itemsDbHelper = new ItemsDbHelper(this);
        SQLiteDatabase database = itemsDbHelper.getWritableDatabase();
        database.execSQL(queryCreateTable);


        Uri newCategoryUri = getContentResolver().insert(categoryUri, values);
    }

    private void insertNewItem() {
        EditText itemNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        colorSpinner = (Spinner) findViewById(R.id.spinner_color);
        EditText initialCountEditText = (EditText) findViewById(R.id.edit_initial_count);

        String itemName = itemNameEditText.getText().toString().trim();
        int initialCount = Integer.parseInt(initialCountEditText.getText().toString().trim());

        ContentValues newItemValues = new ContentValues();
        newItemValues.put(Item.COLUMN_ITEM_NAME, itemName);
        newItemValues.put(Item.COLUMN_ITEM_COLOR, color);
        newItemValues.put(Item.COLUMN_ITEM_COUNT, initialCount);

        Uri newItemUri = getContentResolver().insert(itemsCategoryUri, newItemValues);
    }

    private void setupSpinner() {

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.colors, android.R.layout.simple_dropdown_item_1line);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        colorSpinner.setAdapter(genderSpinnerAdapter);

        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.holo_blue_light))) {
                        color = getColor(R.color.holo_blue_light);
                    } else if (selection.equals(getString(R.string.holo_green_light))) {
                        color = getColor(R.color.holo_green_light);
                    } else if (selection.equals(getString(R.string.holo_purple_light))) {
                        color = getColor(R.color.holo_purple);
                    } else {
                        color = getColor(R.color.holo_red_light);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                color = getColor(R.color.holo_purple);
            }
        });
    }
}
