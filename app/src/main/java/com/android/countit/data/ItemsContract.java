package com.android.countit.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by devansh on 24/8/18.
 */

public class ItemsContract {

    public static final String CONTENT_AUTHORITY = "com.android.countit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private ItemsContract() {
    }

    public static class Item implements BaseColumns {

        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_COUNT = "item_count";
        public static final String COLUMN_ITEM_COLOR = "item_color";
    }

    public static class Category implements BaseColumns{

        public static final String CATEGORY_TABLE_NAME = "categories";
        public static final Uri CATEGORY_TABLE_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CATEGORY_TABLE_NAME);

        public static final String COLUMN_CATEGORY_NAME = "category_name";
        public static final String COLUMN_CATEGORY_COLOR = "category_color";
        public static final String COLUMN_TOTAL_ITEMS = "total_items";
    }
}
