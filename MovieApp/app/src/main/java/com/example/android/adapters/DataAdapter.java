package com.example.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.Utilities.NetworkUtils;
import com.example.android.data.Movie;
import com.example.android.movieapp.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mauryn on 10/03/2017.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataAdapterViewHolder> {
    private static  final String TAG = DataAdapter.class.getSimpleName();
    private ArrayList<Movie> mMovieData;
    private final GridItemOnClickHandler mgridItemOnClickHandler;

    public DataAdapter(GridItemOnClickHandler gridItemOnClickHandler){
        mgridItemOnClickHandler = gridItemOnClickHandler;
  }

    @Override
    public DataAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_grid;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);


        return new DataAdapterViewHolder(view);

    }
   @Override
    public void onBindViewHolder(DataAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);

        ImageView imageView = holder.mImageView;
        String imageSize = imageView.getResources().getString(R.string.imagesize);

        URL imageURL = NetworkUtils.buildImageURL(movie.posterpath, imageSize);
        Log.d(TAG,imageURL.toString());

        Picasso.with(holder.mImageView.getContext()).
                load(imageURL.toString()).
                placeholder(R.drawable.poster_placeholder).into(holder.mImageView);


 }
      public void setMovieData(ArrayList<Movie> movieData) {
          Log.d( DataAdapter.class.getSimpleName(),"IN SETTING MOVIE DATA ");
        mMovieData = movieData;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(mMovieData==null){
           return 0;
        }else{
            return mMovieData.size();
        }
    }

    public class DataAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         public final ImageView mImageView;
        public DataAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.grid_movie_item);

            itemView.setOnClickListener(this);

        }

   @Override
        public void onClick(View view) {
           int adapterposition = getAdapterPosition();
            Movie clickedMovie = mMovieData.get(adapterposition);
            mgridItemOnClickHandler.OnClickListener(clickedMovie);



        }
    }
    public interface  GridItemOnClickHandler{
      void OnClickListener(Movie movie);
    }
}

