package com.example.android.movieapp;


import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.Utilities.NetworkUtils;
import com.example.android.data.MovieContract;
import com.example.android.data.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/*
*
* the main UI of MovieDetailActivity and handles the logic for the view
*
* */

public class MovieDetailFragment extends Fragment {

    /*
    * private globals
    * */
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private LayoutInflater mLayoutInflater;
    private View rootView;
    private Movie movie;
    private String shareVideo;
    private ShareActionProvider mShareActionProvider;



    /*
    * blank default constructor
    * */
    public MovieDetailFragment() {
    }


    /*
    * set up of all the main logic to decide how to display the details fragment
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        mLayoutInflater = inflater;

        Bundle arguments = getArguments();
        Intent intent = getActivity().getIntent();

        if(arguments != null || intent != null && intent.hasExtra("movies_details")){

            rootView = mLayoutInflater.inflate(R.layout.fragment_movie_detail, container, false);
            if (arguments != null) {
                movie = getArguments().getParcelable("movies_details");
            }else{
                movie = intent.getParcelableExtra("movies_details");
            }
            // display the main movie info
            DisplayInfo();

            parseTrailers();
            parseReviews();

        }else{
//            rootView = mLayoutInflater.inflate(R.layout.fragment_movie_detail_placeholder,
//                    container, false);
        }

        return rootView;
    }


    /*
    * extends menu options to add share functionality whenever details fragment is used
    * */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item =
                menu.add(Menu.NONE, R.id.action_share_video, 10, R.string.action_share_video);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        mShareActionProvider = new ShareActionProvider(getActivity());
        mShareActionProvider.setShareIntent(getShareIntent());
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
    }

//
//    /*
//    * gets the shared intent for the action bar share
//    * */
    private Intent getShareIntent(){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this trailer!");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://youtu.be/" + shareVideo);
        return intent;

    }


    /*
    * add movie into favorites in the DB
    * */
    private void addToFavorites() {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, movie.getPoster());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS, movie.getReview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TRAILERS, movie.getTrailer());

         resolver.insert(uri, contentValues);
    }


    /*
    * delete movie from the favorites in the DB
    * */
    private void deleteFromFavorites() {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();

        long noDeleted = resolver.delete(uri,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{ movie.getMovieId() + "" });

    }


    /*
    * query Database to check whether movie has already been saved/favorited
    * */
    private boolean queryFavorites() {

        Uri uri = MovieContract.MovieEntry.buildMovieUri(movie.getMovieId());
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = null;

        try {

            cursor = resolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst())
                return true;

        } finally {

            if(cursor != null)
                cursor.close();

        }

        return false;
    }


    /*
    * helper function to display all the data for the selected movie
    * */
    private void DisplayInfo(){

        TextView movietitleView = (TextView) rootView.findViewById(R.id.movie_title_view);
        ImageView posterView = (ImageView) rootView.findViewById(R.id.poster_image_view);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_dateview);
        TextView voteAverageView = (TextView) rootView.findViewById(R.id.voteaverage_view);
        TextView overviewView = (TextView) rootView.findViewById(R.id.movie_overview_view);
        ImageButton addToFavorites = (ImageButton) rootView.findViewById(R.id.add_to_fav_view);
        toggleFavoritesButton();
        movietitleView.setText(movie.getTitle());
        String imageSize = getActivity().getResources().getString(R.string.imagesize);
        URL imageURL = NetworkUtils.buildImageURL(movie.getPoster(), imageSize);
        Picasso.with(getActivity())
                .load(imageURL.toString())
                .placeholder(R.drawable.poster_placeholder)
                .into(posterView);

        releaseDateView.setText(movie.getReleaseDate());
        voteAverageView.setText(movie.getVoteAverage() + "/10");
        overviewView.setText(movie.getOverview());
        addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean inFavorites = queryFavorites();

                if(inFavorites){
                    deleteFromFavorites();
                }else{
                    addToFavorites();
                }
                 toggleFavoritesButton();
            }
        });

    }


    /*
    * toggles active state for the favorited star based on whether it is found in the database
    * */
    private void toggleFavoritesButton(){
        boolean inFavorites = queryFavorites();
        ImageButton addToFav = (ImageButton) rootView.findViewById(R.id.add_to_fav_view);
        if(inFavorites){
            addToFav.setImageResource(R.drawable.favorite_added);
        }else{
            addToFav.setImageResource(R.drawable.favorite_removed);
        }
    }


    /*
    * Parses the saved json string of movie reviews for the movie
    * */
    private void parseReviews(){
            if(movie.getReview() == null)
            return;

        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        try {
            JSONObject reviewsJson = new JSONObject(movie.getReview());
            JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);
            int reviewsLength = reviewsArray.length();

            if (reviewsLength > 0){
                LinearLayout innerScrollLayout = (LinearLayout)
                        rootView.findViewById(R.id.inner_scroll_layout);
                View reviewsListView = mLayoutInflater.inflate(R.layout.review_list,
                        innerScrollLayout, false);
                innerScrollLayout.addView(reviewsListView);
                LinearLayout reviewList = (LinearLayout)
                        reviewsListView.findViewById(R.id.review_list);

                for (int i = 0; i < reviewsLength; ++i) {

                    View reviewItem = mLayoutInflater.inflate(R.layout.review_item,
                            reviewList, false);

                    JSONObject review = reviewsArray.getJSONObject(i);
                    String reviewAuthor = review.getString(AUTHOR);
                    String reviewContent = review.getString(CONTENT);

                    TextView author = (TextView) reviewItem.findViewById(R.id.review_author);
                    TextView content = (TextView) reviewItem.findViewById(R.id.review_content);
                    author.setText(reviewAuthor);
                    content.setText(reviewContent);

                    reviewList.addView(reviewItem);
                }
            }

        }catch (JSONException e){
            Log.e(LOG_TAG, "ERROR PARSING JSON STRING");
        }
    }


    /*
    * used to parse the saved json string of movie trailers for the movie
    * */
    private void parseTrailers(){

        final String RESULTS_TRAILERS = "results";
        final String TRAILER_ID = "key";
        final String TRAILER_TITLE = "name";
        try{
            if(movie.getTrailer()!=null){
            JSONObject trailersJson = new JSONObject(movie.getTrailer());
            JSONArray trailersArray = trailersJson.getJSONArray(RESULTS_TRAILERS);
            int trailersLength =  trailersArray.length();
            if(trailersLength > 0) {
                LinearLayout innerScrollLayout = (LinearLayout)
                        rootView.findViewById(R.id.inner_scroll_layout);
                View trailersListView = mLayoutInflater.inflate(R.layout.trailers_list,
                        innerScrollLayout, false);
                innerScrollLayout.addView(trailersListView);

                LinearLayout trailerList = (LinearLayout)
                        trailersListView.findViewById(R.id.trailers_list);

                for (int i = 0; i < trailersLength; ++i) {

                    View trailerItem = mLayoutInflater.inflate(R.layout.trailer_item,
                            trailerList, false);

                    JSONObject trailer = trailersArray.getJSONObject(i);
                    final String trailerId = trailer.getString(TRAILER_ID);
                    String trailerTitle = trailer.getString(TRAILER_TITLE);
                    TextView videoTitle = (TextView) trailerItem.findViewById(R.id.video_title);

                    shareVideo = trailerId;
                    videoTitle.setText(trailerTitle);
                    trailerList.addView(trailerItem);

                    trailerItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent ytappIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("vnd.youtube:" + trailerId));
                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + trailerId));
                            ytappIntent.putExtra("VIDEO_ID", trailerId);
                            webIntent.putExtra("VIDEO_ID",trailerId);
                            try{
                                startActivity(ytappIntent);
                            }catch (ActivityNotFoundException ex){
                                startActivity(webIntent);
                                Log.i(LOG_TAG, "youtube app not installed");
                            }
                        }
                    });
                }
            }

        }}catch (JSONException e){
            Log.e(LOG_TAG, "ERROR PARSING JSON STRING");
        }
    }

}
