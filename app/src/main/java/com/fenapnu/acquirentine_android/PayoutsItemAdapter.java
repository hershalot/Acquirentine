package com.fenapnu.acquirentine_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayoutsItemAdapter extends RecyclerView.Adapter<PayoutsItemAdapter.ViewHolder> {


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_payouts, parent, false);
        return new ViewHolder(itemView);

    }



    // populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        // Get the data model based on position
        String corp = mCorporations.get(position);
        int selected_position = RecyclerView.NO_POSITION;
        holder.itemView.setSelected(selected_position == position);

        // Set item views based on your views and data model
        TextView descriptionTV = holder.descriptionTV;
        ListView listView = holder.listView;


        //Here I need to determine all corporations in list, as well as the payouts for each person involved.

        Map<String, Long> majorShareholders = new HashMap<>();
        Map<String, Object> finalCounts = new HashMap<>();
        Map<String, Object> mergeData = mGame.getMergerData();

        if(mFinalPayout){
            majorShareholders = mGame.determinePayouts(corp);
            Map<String, Long> cc = new HashMap<>();

            for(Player p : mGame.getPlayers().values()){
                long count = 0;
                if(p.cards.get(corp) != null){
                    count = p.cards.get(corp);
                }
                cc.put(p.getUserId(),count);

            }
            finalCounts.put(corp,cc);
        }else{
            majorShareholders = mGame.determinePayoutsPostMerge(corp);
            finalCounts = (Map<String, Object>) mergeData.get("cards");
        }


        List<String> values = new ArrayList<>();


        Map<String, Long> corpCounts =  (Map<String, Long>) finalCounts.get(corp);

        //we still want to add each player to the list, just add payout to those in po
        Map<String, Player> players = mGame.players;

        for (String uid : mGame.players.keySet()){

            Player p = players.get(uid);
            long num = corpCounts.get(uid);

            String s = p.getName();
            if(mFinalPayout){
                s = s + " ("  +  num  + ")";
            }else{
                if(mergeData.get(uid) != null){
                    s = s + " ("  +  num  + ")";
                }else{
                    s = s + " (0)";
                }
            }

            if(majorShareholders.get(uid) != null){

                if(mFinalPayout){
                    //add what user gets for cashing out stock
                    Long payout = majorShareholders.get(uid);
                    s = s + "  $" + payout + " + " + mGame.determineCost(corp, num);;

                }else{
                    long payout = majorShareholders.get(uid);
                    s = s + " $" + payout;
                }
            }

            values.add(s);
        }


        String[] strarray = values.toArray(new String[values.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.list_item, R.id.list_content, strarray);


        // Assign adapter to ListView
        listView.setAdapter(adapter);


        descriptionTV.setText(corp);

        switch (corp){

            case "Nestor":
                descriptionTV.setTextColor(getContext().getColor(R.color.nestorColor));
                break;
            case "Spark":
                descriptionTV.setTextColor(getContext().getColor(R.color.sparkColor));
                break;
            case "Fleet":
                descriptionTV.setTextColor(getContext().getColor(R.color.fleetColor));
                break;
            case "Rove":
                descriptionTV.setTextColor(getContext().getColor(R.color.roveColor));
                break;
            case "Etch":
                descriptionTV.setTextColor(getContext().getColor(R.color.etchColor));
                break;
            case "Bolt":
                descriptionTV.setTextColor(getContext().getColor(R.color.boltColor));
                break;
            case "Echo":
                descriptionTV.setTextColor(getContext().getColor(R.color.echoColor));
                break;

        }
    }




    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        if(mCorporations != null){

            return mCorporations.size();
        }else{
            return 0;
        }
    }





    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView descriptionTV;
        public ListView listView;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {

            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            descriptionTV = itemView.findViewById(R.id.payout_description);
            listView = itemView.findViewById(R.id.corp_payouts);

        }
    }




    // Store a member variable for the contacts
    private List<String> mCorporations;

    // Store the context for easy access
    private Context mContext;
    private GameObject mGame;
    private boolean mFinalPayout;

    // Pass in the contact array into the constructor
    public PayoutsItemAdapter(Context context, List<String> corporations, GameObject game, boolean finalPayout) {
        mFinalPayout = finalPayout;
        mGame = game;
        mCorporations = corporations;
        mContext = context;
    }

    public void setGameObject(GameObject g){

        mGame = g;
    }


    public void reset() {

        mCorporations.clear();
        notifyDataSetChanged();
    }


    // Easy access to the context object in the recyclerview
    public Context getContext() {
        return mContext;
    }



}






