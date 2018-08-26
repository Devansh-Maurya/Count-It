package com.android.countit;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.countit.data.ItemsContract;

/**
 * Created by devansh on 25/8/18.
 */

public class CategoryCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;

    public CategoryCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return cursorInflater.inflate(R.layout.category_items, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long color = cursor.getLong(cursor.getColumnIndex(ItemsContract.Category.COLUMN_CATEGORY_COLOR));

        view.setBackgroundColor((int) color);
        TextView textView1 = (TextView) view.findViewById(R.id.main_Text);

        String categoryName = cursor.getString(cursor.getColumnIndex(ItemsContract.Category.COLUMN_CATEGORY_NAME));

        textView1.setText(categoryName);
    }
}
