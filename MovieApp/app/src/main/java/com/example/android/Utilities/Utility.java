package com.example.android.Utilities;

import android.content.Context;
import android.database.Cursor;

import com.example.android.data.MovieContract;

/**
 * Created by Mauryn on 11/17/2017.
 */

public class Utility {


    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[] { Integer.toString(id) },   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }
}
