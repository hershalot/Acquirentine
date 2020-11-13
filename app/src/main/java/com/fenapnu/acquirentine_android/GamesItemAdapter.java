package com.fenapnu.acquirentine_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class GamesItemAdapter extends RecyclerView.Adapter<GamesItemAdapter.ViewHolder> {


    private OnGameClickListener onClickListener;


    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public GamesItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_games, parent, false);
        return new ViewHolder(itemView);


    }



    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(GamesItemAdapter.ViewHolder holder, int position) {


        // Get the data model based on position
        GameObject game = mGames.get(position);

        int selected_position = RecyclerView.NO_POSITION;
        holder.itemView.setSelected(selected_position == position);


        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        TextView lobbyPreview = holder.messagePreview;

        nameTextView.setText(game.gameName);

        String s = game.getPlayers().size() + " in Lobby";
        if(game.isGameStarted()){
            s = "Game in progress (" + game.getPlayers().size() + ")";
        }

        lobbyPreview.setText(s);

        holder.bind(game, onClickListener);

    }


    public static String removeLastCharacters(String str, int num) {
        String result = "";
        if ((str != null) && (str.length() > num)) {
            result = str.substring(0, str.length() - num);
        }
        return result;
    }


    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if(mGames != null){

            return mGames.size();
        }else{
            return 0;
        }
    }





    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder{


//      public ImageButton messageButton;
        public TextView nameTextView;
        public TextView messagePreview;



        public void bind( final GameObject game, final OnGameClickListener clickListener)
        {


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onGameClicked(game);
                }
            });
        }


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {

            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            messagePreview = itemView.findViewById(R.id.lobby_details);
            nameTextView = itemView.findViewById(R.id.user_name);

        }
    }




    // Store a member variable for the contacts
    private List<GameObject> mGames;

    // Store the context for easy access
    private Context mContext;


    // Pass in the contact array into the constructor
    public GamesItemAdapter(Context context, List<GameObject> games, OnGameClickListener listener) {

        onClickListener = listener;

        mGames = games;
        mContext = context;
    }



    public void reset() {

        mGames.clear();
        notifyDataSetChanged();
    }


    // Easy access to the context object in the recyclerview
    public Context getContext() {
        return mContext;
    }



}






interface OnGameClickListener{
    void onGameClicked(GameObject game);

}




