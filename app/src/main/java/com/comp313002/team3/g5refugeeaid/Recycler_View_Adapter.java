package com.comp313002.team3.g5refugeeaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comp313002.team3.g5refugeeaid.models.G5UserData;

import java.util.List;

class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {

    List<G5UserData> list;
    Context context;

    public Recycler_View_Adapter(List<G5UserData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_users, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, final int position) {
        // populate the current row on the RecyclerView
        holder.fName.setText(list.get(position).fName);
        holder.lName.setText(list.get(position).lName);
        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, list.get(position).fName, Toast.LENGTH_SHORT).show();
            }
        });
        //holder.imageView.setImageResource(list.get(position).hashCode());

        //animate(holder);
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, G5UserData data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(G5UserData data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }
}
