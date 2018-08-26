package com.android.countit.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.countit.data.ItemsContract.Category;

public class ItemProvider extends ContentProvider {

    private static final String LOG_TAG = ItemProvider.class.getSimpleName();
    private static final int CATEGORIES = 100;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ItemsContract.CONTENT_AUTHORITY, Category.CATEGORY_TABLE_NAME, CATEGORIES);
    }

    private ItemsDbHelper itemsDbHelper;

    public ItemProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        Uri newUri = null;
        switch (match) {
            case CATEGORIES:
                newUri = insertCategoryEntry(uri, values);
                break;
            default:
                newUri = insertItem(uri, values);
                break;
        }
        return newUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        itemsDbHelper = new ItemsDbHelper(getContext());
        SQLiteDatabase database = itemsDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case CATEGORIES:
                cursor = database.query(Category.CATEGORY_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                cursor = database.query(uri.getPath().substring(1), projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Uri insertCategoryEntry(Uri uri, ContentValues values) {
        itemsDbHelper = new ItemsDbHelper(getContext());
        SQLiteDatabase database = itemsDbHelper.getWritableDatabase();
        long id = database.insert(Category.CATEGORY_TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for: " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        itemsDbHelper = new ItemsDbHelper(getContext());
        SQLiteDatabase database = itemsDbHelper.getWritableDatabase();
        //Using substring method because getPath returns table name appended with '/'
        long id = database.insert(uri.getPath().substring(1), null, values);

        if (id == -1) {
            Toast.makeText(getContext(), "Item insertion failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Item insertion successful", Toast.LENGTH_SHORT).show();
        }

        return ContentUris.withAppendedId(uri, id);
    }
}
