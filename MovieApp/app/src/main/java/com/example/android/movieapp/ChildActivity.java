package com.example.android.movieapp;

/**
 * Created by Mauryn on 10/09/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.android.Utilities.MovieSortCriteria;
import com.example.android.Utilities.NetworkUtils;
import com.example.android.data.Movie;
import com.example.android.data.MovieParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ChildActivity extends AppCompatActivity {

    private static final String TAG = ChildActivity.class.getSimpleName();

    private int movieid;
    private TextView mTitleTextView;
    private ImageView mThumbnailImageView;
    private TextView mReleaseDataTextView;
    private TextView mVoteAverage;
    private TextView mOverview;
    private  TextView mReview;
    private ImageView mTrailerView;
    Movie mMovie;



//    private LinearListView mTrailersView;
//    private LinearListView mReviewsView;


    private CardView mReviewsCardview;
    private CardView mTrailersCardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        // Get ids for all the views in activity_child.xml
//        mTitleTextView = (TextView)findViewById(R.id.movie_details_title);
//
//
//        mReleaseDataTextView = (TextView)findViewById(R.id.movie_details_releasedate);
//        mVoteAverage = (TextView)findViewById(R.id.movie_details_voteaverage);
//        mOverview = (TextView)findViewById(R.id.movie_details_overview);
//        mThumbnailImageView = (ImageView)findViewById(R.id.movie_thumbnail);
      // mTrailerView = (ImageView) findViewById(R.id.trailervideoview);

        Intent startchildIntent = getIntent();
        if(startchildIntent != null) {
            if(startchildIntent.hasExtra(Intent.EXTRA_TEXT)) {
                movieid = startchildIntent.getIntExtra(Intent.EXTRA_TEXT, 0);

            }
        }
        FetchMovieDataTask task = new FetchMovieDataTask(this);

        task.execute();
    }


        public class FetchMovieDataTask extends AsyncTask<Void, Void, Movie> {
          private final Context mContext;

        public FetchMovieDataTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                 }

        @Override
        protected Movie doInBackground(Void... voids) {

            URL requestURL= NetworkUtils.buildURL(mContext, MovieSortCriteria.DETAILS, movieid);

            try {
                if(isOnline()) {
                String response = NetworkUtils.getResponseFromHttpUrl(requestURL);
                Log.d(TAG, "Response: " + response);
                ArrayList<Movie> movielist = MovieParser.parseMovieData(ChildActivity.this,response, String.valueOf(MovieSortCriteria.DETAILS));
                Movie movie = null;
                try {
                    movie = movielist.get(0);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, e.toString());
                }
                    return movie;}
                else{
                    Log.e(TAG, "No internet connection");
                }

            } catch (IOException |JSONException e) {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            if(movie != null) {
                String imageSize = mContext.getResources().getString(R.string.imagesize);
                URL imageURL = NetworkUtils.buildImageURL(movie.posterpath, imageSize);
                Picasso.with(mContext)
                        .load(imageURL.toString())
                        .placeholder(R.drawable.poster_placeholder)
                        .into(mThumbnailImageView);

                String rating ="";
                rating =String.valueOf(movie.getVoteAverage()) + "/10";
                mTitleTextView.setText(movie.getTitle());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    String date = DateUtils.formatDateTime(ChildActivity.this,
                            formatter.parse(movie.getReleaseDate()).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                    mReleaseDataTextView.setText(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mVoteAverage.setText(rating);
                mOverview.setText(movie.overview);

            }

        }
    }





    // check for internet connectivity
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();


    }


}
