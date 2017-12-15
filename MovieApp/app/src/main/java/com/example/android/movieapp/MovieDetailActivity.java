package com.example.android.movieapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/*
*
* Detail activity and displays the detail view of a movie
* Inflates MovieDetailFragment for the main UI
*
* */

public class MovieDetailActivity extends AppCompatActivity {

    /*
    * private globals for this class
    * */
    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private FragmentManager fragmentManager = getFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.movie_detail_container, new MovieDetailFragment())
                    .commit();
        }

    }


    /*
    * inflate menu when the details fragment is displayed by this activity
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    /*
    * handle menu items
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
