package com.example.android.movieapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.View;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.Utilities.MovieSortCriteria;
import com.example.android.Utilities.NetworkUtils;
import com.example.android.adapters.DataAdapter;
import com.example.android.data.Movie;
import com.example.android.data.MovieParser;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements
        DataAdapter.GridItemOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderCallbacks<ArrayList<Movie>> {



    private RecyclerView mRecyclerView;

    private DataAdapter mDataAdapter;
    private Menu mMenu;
    private final MovieSortCriteria mDefaultSortCritieria = MovieSortCriteria.POPULAR;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    private static int MOVIE_LOADER_ID;
   // private static final String DETAILFRAGMENT_TAG = "DFTAG";
    //private FragmentManager fragmentManager = getFragmentManager();
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycerview_movies);
        RecyclerViewConfiguration(mRecyclerView);
        LoaderCallbacks<ArrayList<Movie>> callbacks = MainActivity.this;
        Bundle bundleforLoader = null;
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundleforLoader, callbacks);


        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

//        if (findViewById(R.id.movie_detail_container) != null) {
//
//            mTwoPane = true;
//
//            if (savedInstanceState == null) {
//                fragmentManager.beginTransaction()
//                        .add(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
//                        .commit();
//            }
//        } else {
//            mTwoPane = false;
//        }

      //  fetchMovieData();
    }

    private void RecyclerViewConfiguration(RecyclerView myRecyclerView) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycerview_movies);
        GridLayoutManager gridlayoutManager = new GridLayoutManager(this, 2);
        myRecyclerView.setLayoutManager(gridlayoutManager);
        myRecyclerView.setHasFixedSize(true);

        mDataAdapter = new DataAdapter(this);
        myRecyclerView.setAdapter(mDataAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
         mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {


            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
       }


        return super.onOptionsItemSelected(item);


    }

    // Top rated sort method selected
//        if(id == R.id.action_toprated) {
//            // I// if popular is selected check it in the menu and fetchmovie data with the sort order
//            if(getSelectedMovieRequestType() != MovieSortCriteria.TOPRATED) {
//                Log.i(TAG, "Top rated was not already selected, updating data");
//                item.setChecked(true);
//                fetchMovieData();
//            }
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
    //   }

//    private MovieSortCriteria getSelectedMovieRequestType() {
//
//        // If the menu hasn't been created yet, return the default request type
//        if (mMenu == null) {
//            return mDefaultSortCritieria;
//        }
//
////         if popular has been selected
////        if(mMenu.findItem(R.id.action_popular).isChecked()) {
////            return MovieSortCriteria.POPULAR;
////        }
//////if toprated has been selected
////        if(mMenu.findItem(R.id.action_toprated).isChecked()) {
////            return MovieSortCriteria.TOPRATED;
////        }
//
//        return mDefaultSortCritieria;
//    }

    @Override
    public void OnClickListener(Movie movie) {
// Listen for a click, then use intent to start the child acitivity which holds the deatil of the movie clicked,
// pass the movie ID
//        Intent detailIntent = new Intent(this, MovieDetailActivity.class);
//       // detailIntent.putExtra(Intent.EXTRA_TEXT, movie.ID);
//        detailIntent.putExtra("movies_details", movie);
//        startActivity(detailIntent);

        if (mTwoPane) {
//            Bundle args = new Bundle();
//            args.putParcelable("movies_details", movie);
//
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(args);
//
//            fragmentManager.beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
//                    .commit();
        } else {
          Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra("movies_details", movie);
            startActivity(intent);

        }

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;

    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
         return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            ArrayList<Movie> mMovieData = null;
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    Log.d("CHECKING onstartloading" , mMovieData+"");
                    deliverResult(mMovieData);
                } else {
                 //   mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }
        @Override
            public ArrayList<Movie> loadInBackground() {
                Log.d("We are here ","lOADING IN BACKGROUND *********************************");
                Context mContext = MainActivity.this;
                URL requestURL = null;

               String key = getString(R.string.pref_sorting_criteria_key);
               String value= getString(R.string.pref_sorting_criteria_default_value);


                String sortingCriteria = MovieParser.getPreferredSortingCriteria(MainActivity.this,key,value);
                Log.d("Criteria +++++++++++++",sortingCriteria +"  &&&&&&&&&  "+
                        String.valueOf(MovieSortCriteria.POPULAR)+"  "+
                        String.valueOf(MovieSortCriteria.TOPRATED));

                if (sortingCriteria.equalsIgnoreCase("popular")) {
                   requestURL = NetworkUtils.buildURL(mContext, MovieSortCriteria.POPULAR, 0);
                    Log.d("REQESTURL ", requestURL+"");
                }
                if (sortingCriteria.equals("vote_average") ){
                    requestURL = NetworkUtils.buildURL(mContext, MovieSortCriteria.TOPRATED, 0);
                    Log.d("REQESTURL ", requestURL+"");
                }


                try {
                    // Execute the API call
                    // check if there is network connectivity

                        ArrayList<Movie> movies = new ArrayList<>();
                       if(sortingCriteria.equals("Favorites")){
                            movies= MovieParser.getFavorites(MainActivity.this);
                            return movies;
                        }else{
                            if (isOnline()) {
                            if(requestURL!=null){
                        String response = NetworkUtils.getResponseFromHttpUrl(requestURL);
                        Log.d(TAG, "Response: " + response); // json returned from the http request
                        // parse it to get the JSON
                        movies = MovieParser.parseMovieData(MainActivity.this,response, sortingCriteria);}
                        return movies;

                    } else {
                        Log.e(TAG, "No internet Connection");
                    }
                }} catch (IOException | JSONException e) {
                    Log.e(TAG, e.toString());
                }

                return null;
            }

            public void deliverResult(ArrayList<Movie> data) {
                mMovieData = data;
                Log.d("CHECKING FOR DATA" , data+"");
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
//       // mLoadingIndicator.setVisibility(View.INVISIBLE);
        Log.d("On Load Finished","Load has finished *********"+data);
        mDataAdapter.setMovieData(data);
        if (null == data) {
            //showErrorMessage();
        } else {
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
    //    mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        Log.d("showMovieDataView", "should show recycler view");
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated************************");
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }





//    public class FetchMovieDataTask extends AsyncTask<MovieSortCriteria, Void, ArrayList<Movie>> {
//
//        private final Context mContext;
//
//        public FetchMovieDataTask(Context context)
//        {
//            mContext = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//                 }
//
//        @Override
//        protected ArrayList<Movie> doInBackground(MovieSortCriteria... movieSortCriteria) {
//             URL requestURL = null;
//            MovieSortCriteria sortCriteria = movieSortCriteria[0];
//            if(sortCriteria == MovieSortCriteria.POPULAR) {
//                requestURL = NetworkUtils.buildURL(mContext,MovieSortCriteria.POPULAR,0);
//            }
//            if(sortCriteria == MovieSortCriteria.TOPRATED) {
//                requestURL = NetworkUtils.buildURL(mContext,MovieSortCriteria.TOPRATED,0);
//            }
//
//            try {
//                // Execute the API call
//                // check if there is network connectivity
//        if(isOnline()) {
//                String response = NetworkUtils.getResponseFromHttpUrl(requestURL);
//                Log.d(TAG, "Response: " + response); // json returned from the http request
//                // parse it to get the JSON
//                ArrayList<Movie> movies = MovieParser.parseMovieData(response, String.valueOf(sortCriteria));
//                return movies;
//
//            }else{
//            Log.e(TAG,"No internet Connection");}
//            }
//            catch (IOException |JSONException e) {
//                Log.e(TAG, e.toString());
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Movie> movies) {
//            super.onPostExecute(movies);
//             mDataAdapter.setMovieData(movies);
//
//        }
//    }

    // check if there is internet connectivity
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();


    }



//    public  String getPreferredSortingCriteria() {
//        Context context = MainActivity.this;
//        // COMPLETED (1) Return the user's preferred location
//        SharedPreferences prefs = PreferenceManager
//                .getDefaultSharedPreferences(context);
////        String keyForLocation = context.getString(R.string.pref_location_key);
////        String defaultLocation = context.getString(R.string.pref_location_default);
//        // return prefs.getString(keyForLocation, defaultLocation);
//        return prefs.getString(getString(R.string.pref_sorting_criteria_key), getString(R.string.pref_sorting_criteria_default_value));
//
//    }
}
