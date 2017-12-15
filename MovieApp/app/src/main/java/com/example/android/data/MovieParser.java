package com.example.android.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.AsyncTasks.FetchMoviesComponents;
import com.example.android.Utilities.MovieSortCriteria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MovieParser {

    public static final String TAG = MovieParser.class.getSimpleName();

    private static Context mContext=null;
    public static ArrayList<Movie> parseMovieData(Context context,String jsonData, String movieSortCriteria) throws JSONException {
         ArrayList<Movie> movieData = new ArrayList<>();
        mContext =context;
        JSONObject jsonObject = new JSONObject(jsonData);
        Log.d("MovieParser #####", jsonData);
        Log.d("Sort Criteria" , movieSortCriteria);
        if (movieSortCriteria.equals("vote_average") ||  movieSortCriteria.equalsIgnoreCase("popular")){
      //  if(movieSortCriteria == String.valueOf(MovieSortCriteria.POPULAR)|| movieSortCriteria ==  String.valueOf(MovieSortCriteria.TOPRATED)) {
            if(jsonObject.has("results")) {
                JSONArray results = jsonObject.getJSONArray("results"); // resutlts is the key that holds the array of movies
                 for(int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    Movie movie = createMovieObjectFromJson(mContext,result, movieSortCriteria);
                    movieData.add(movie);

                     Log.d("UPDATED  &&&&   DATA", "#"+movie);
                }
            }
        }

        if(movieSortCriteria ==  String.valueOf(MovieSortCriteria.DETAILS)) {
            Movie movie = createMovieObjectFromJson(mContext,jsonObject, movieSortCriteria);
            movieData.add(movie);
        }



        return movieData;
    }


    private static Movie createMovieObjectFromJson(Context context,JSONObject jsonresult,String requestType) throws JSONException {
       // Movie movie = new Movie();

//        movie.title = jsonresult.getString("title");
//        movie.posterpath = jsonresult.getString("poster_path");
//        movie.ID= jsonresult.getInt("id");
//        movie.vote_average = jsonresult.getDouble("vote_average");
//        movie.overview = jsonresult.getString("overview");
//        movie.releasedate = jsonresult.getString("release_date");


        String title = jsonresult.getString("title");
        String posterpath = jsonresult.getString("poster_path");
        int ID= jsonresult.getInt("id");
        String vote_average = jsonresult.getString("vote_average");
        String overview = jsonresult.getString("overview");
        String releasedate = jsonresult.getString("release_date");

        Movie movie = new Movie(ID, title, posterpath, overview, vote_average, releasedate);




//         fetch reviews which will be stored as a JSON string
//         and add it to the new movie in new asyncTask
        FetchMoviesComponents fetchReviews = new FetchMoviesComponents(context,movie,
                "reviews");
        fetchReviews.execute();

        // fetch previews which will be stored as a JSON string
        // and add it to the new movie in new asyncTask
        FetchMoviesComponents fetchPreviews = new FetchMoviesComponents(context,movie,
                "videos");
        fetchPreviews.execute();




        return movie;
    }
    public static String getPreferredSortingCriteria(Context context , String key, String value) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getString(key, value);

    }

    public static ArrayList<Movie>  getFavorites(Context context){
        ArrayList<Movie> movieData = new ArrayList<>();
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;

        try {

            cursor = resolver.query(uri, null, null, null, null);

            // clear movies
            movieData.clear();

            if (cursor.moveToFirst()){
                do {
                    Movie movie = new Movie(cursor.getInt(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4), cursor.getString(5),
                            cursor.getString(6));

                    movie.setReview(cursor.getString(7));
                    movie.setTrailer(cursor.getString(8));
                    movieData.add(movie);

                } while (cursor.moveToNext());
            }

        } finally {

            if(cursor != null)
                cursor.close();

        }
return movieData;
    }
}
