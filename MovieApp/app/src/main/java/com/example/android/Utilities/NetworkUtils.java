package com.example.android.Utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.movieapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



/**
 * Created by Mauryn on 10/1/2017.
 */


public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String PARAM_HTTP = "https";
    private static final String MOVIE_BASE_URL = "api.themoviedb.org";
    private static final String MOVIEDB_VERSION = "3";
    private static final String APIPARAM_PATH = "movie";
    private static final String IMAGE_BASE_URL = "image.tmdb.org";
    private static final String VIDEO_BASE_URL ="videos";
    private static final String REVIEW_BASE_URL ="reviews";


// build url to  show images in the child/detail =activity
  public static URL buildImageURL(String imagepath, String imagesize) {

        imagepath = imagepath.replace("/", "");

        Uri.Builder uri_builder = new Uri.Builder();
           uri_builder.scheme(PARAM_HTTP).authority(IMAGE_BASE_URL).appendPath("t")
                .appendPath("p").appendPath(imagesize).appendPath(imagepath);

        Uri uri = uri_builder.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error in creating URL :: " + url.toString());
        }
             return url;
    }
    public static URL buildTrailersURL (Context context,String movieid)
     {

         Uri.Builder builder = new Uri.Builder();
         builder.scheme(PARAM_HTTP).authority(MOVIE_BASE_URL).appendPath(MOVIEDB_VERSION).appendPath(APIPARAM_PATH).appendPath(String.valueOf(movieid)).
                 appendPath(VIDEO_BASE_URL);
         // builder.appendPath(String.valueOf(id));
         Uri uri = builder
                 .appendQueryParameter("api_key", context.getString(R.string.API_KEY))
                 .build();

         URL url = null;
         try {
             url = new URL(uri.toString());
             Log.d("NETWORKUTILS URI" , uri.toString());
         } catch (MalformedURLException e) {
             Log.e(TAG, "Error in creating URL ::" + uri.toString());
         }
         Log.d(TAG, url.toString());
         return url;
     }


    public static URL buildReviewsURL (String movieid)
    {
        Uri.Builder uri_builder = new Uri.Builder();
        uri_builder.scheme(PARAM_HTTP).authority(IMAGE_BASE_URL).appendPath(movieid).appendPath(REVIEW_BASE_URL);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PARAM_HTTP).authority(MOVIE_BASE_URL).appendPath(MOVIEDB_VERSION).appendPath(REVIEW_BASE_URL);



        Uri uri = uri_builder.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
            Log.d("NETWORK UTILS REVIEWS  ",url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error in creating URL :: " + url.toString());
        }
        return url;



    }


    //build the url to be used to return the Movies
    public static URL buildURL(Context context, MovieSortCriteria sortCriteria, int id) {



        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PARAM_HTTP).authority(MOVIE_BASE_URL).appendPath(MOVIEDB_VERSION).appendPath(APIPARAM_PATH);


        switch(sortCriteria) {
             case POPULAR:
                builder.appendPath(context.getString(R.string.popularity));
                break;
             case TOPRATED:
                builder.appendPath(context.getString(R.string.toprated));
                break;
             case DETAILS:
                builder.appendPath(String.valueOf(id));
                break;
             case FAVORITES:
                builder.appendPath("favorites");
                break;
        }

        Uri uri = builder
                .appendQueryParameter("api_key", context.getString(R.string.API_KEY))
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error in creating URL ::" + uri.toString());
        }
        Log.d(TAG, url.toString());
        return url;
    }




    //build the url to be used to return the Movies
    public static URL buildReviewsURL(Context context, String id) {
       // https://api.themoviedb.org/3/movie/372058/reviews?api_key=eb526494064b9176a734e28126f7e443

         Uri.Builder builder = new Uri.Builder();
         builder.scheme(PARAM_HTTP).authority(MOVIE_BASE_URL).appendPath(MOVIEDB_VERSION).appendPath(APIPARAM_PATH).appendPath(String.valueOf(id)).
                 appendPath(REVIEW_BASE_URL);
        // builder.appendPath(String.valueOf(id));
         Uri uri = builder
                .appendQueryParameter("api_key", context.getString(R.string.API_KEY))
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
            Log.d("NETWORKUTILS URI" , uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error in creating URL ::" + uri.toString());
        }
        Log.d(TAG, url.toString());
        return url;
    }


    public static URL buildURL (Context context,int movieid, String type)
    {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(PARAM_HTTP).authority(MOVIE_BASE_URL).appendPath(MOVIEDB_VERSION).appendPath(APIPARAM_PATH).appendPath(String.valueOf(movieid)).
                appendPath(type);
        // builder.appendPath(String.valueOf(id));
        Uri uri = builder
                .appendQueryParameter("api_key", context.getString(R.string.API_KEY))
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
            Log.d("NETWORKUTILS URI" , uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error in creating URL ::" + uri.toString());
        }
        Log.d(TAG, url.toString());
        return url;



    }




    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
