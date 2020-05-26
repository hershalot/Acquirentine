package com.fenapnu.acquirentine_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayersItemAdapter extends RecyclerView.Adapter<PlayersItemAdapter.ViewHolder> {


    private int selected_position = RecyclerView.NO_POSITION;


    private OnPlayerClickListener onClickListener;


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_players, parent, false);
        return new ViewHolder(itemView);


    }



    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        // Get the data model based on position
        Player player = mPlayers.get(position);
        holder.itemView.setSelected(selected_position == position);

        // Set item views based on your views and data model
        TextView nameTextView = holder.nameTextView;
        Button startTileBtn = holder.startTileBtn;

        nameTextView.setText(player.name);

        if(mGame.isGameStarted() && mGame.turn.equals(player.userId)){
            startTileBtn.setVisibility(View.VISIBLE);
            startTileBtn.setText("...");
            startTileBtn.setBackground(getContext().getDrawable(R.drawable.rounded_green_rectangle_bordered));


        }else if(mGame.isGameStarted()){
            startTileBtn.setVisibility(View.INVISIBLE);

        }
        else{
            startTileBtn.setVisibility(View.VISIBLE);
            startTileBtn.setBackground(getContext().getDrawable(R.drawable.barely_rounded_white_rectangle_bordered));
            startTileBtn.setText(player.getStartTile());
        }


        holder.bind(player, onClickListener);

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
        if(mPlayers != null){

            return mPlayers.size();
        }else{
            return 0;
        }
    }





    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{


//      public ImageButton messageButton;
        public TextView nameTextView;
        public Button startTileBtn;



        public void bind( final Player player, final OnPlayerClickListener clickListener)
        {


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onPlayerClicked(player);
                }
            });
        }


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {

            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.user_name);
            startTileBtn = itemView.findViewById(R.id.start_tile);

        }
    }




    // Store a member variable for the contacts
    private List<Player> mPlayers;

    // Store the context for easy access
    private Context mContext;
    private GameObject mGame;

    // Pass in the contact array into the constructor
    public PlayersItemAdapter(Context context, List<Player> players, GameObject game, OnPlayerClickListener listener) {

        onClickListener = listener;

        mGame = game;
        mPlayers = players;
        mContext = context;
    }



    public void setGameObject(GameObject g){

        mGame = g;
    }


    public void reset() {

        mPlayers.clear();
        notifyDataSetChanged();
    }


    // Easy access to the context object in the recyclerview
    public Context getContext() {
        return mContext;
    }



}


interface OnPlayerClickListener{
    void onPlayerClicked(Player player);

}




