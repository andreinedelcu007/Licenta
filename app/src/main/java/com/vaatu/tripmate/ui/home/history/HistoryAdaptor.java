package com.vaatu.tripmate.ui.home.history;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vaatu.tripmate.R;
import com.vaatu.tripmate.data.remote.network.FirebaseDB;
import com.vaatu.tripmate.utils.TripModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdaptor extends RecyclerView.Adapter<HistoryAdaptor.ViewHolder> {

    List<TripModel> list = new ArrayList<>();
    Context cntxt;
    FirebaseDB mFirebaseDB;

    //cand creem adapterul asteptam lista de tripmodel si contextul
    public HistoryAdaptor(List<TripModel> list, Context cntxt) {
        this.list = list;
        this.cntxt = cntxt;
    }


    //se apleaza pentru fiecare element din lista si initializeaza interfata grafica pentru card-ul aferent unei entitati
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(parent.getContext());
        View v = inflate.inflate(R.layout.history_card_row, parent, false);
        mFirebaseDB = FirebaseDB.getInstance();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //se apleaza pentru fiecare card si seteaza valorile elementelor din el (textview etc)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.start.setText(list.get(position).startloc);
        holder.name.setText(list.get(position).tripname);
        holder.date.setText(list.get(position).date);
        Log.i("STATUS_COLOR",holder.status.getText().toString());
        if(list.get(position).status.equals("Done!")){
          holder.status.setTextColor(cntxt.getResources().getColor(R.color.green));
        }
        else{
            holder.status.setTextColor(cntxt.getResources().getColor(R.color.red));
        }
        holder.status.setText(list.get(position).status);
        holder.time.setText(list.get(position).time);
        holder.end.setText(list.get(position).endloc);

        //seteaza on click pentru punctele alea 3
        holder.popup.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(cntxt, holder.popup);
            popupMenu.inflate(R.menu.history_menu);

            //seteaza actiunile pentru elemntele din meniu
            popupMenu.setOnMenuItemClickListener(item -> {
                //handle item selection from the card pop menu

                //daca dai click pe delete sterge din firebase
                if (item.getItemId() == R.id.delete) {

                    Toast.makeText(cntxt, "Trip Deleted!", Toast.LENGTH_LONG).show();
                    mFirebaseDB.removeTripFromHistory(list.get(position));
                    notifyDataSetChanged();
                }

                //daca dai click pe viewnotes iti arata notitele
                if(item.getItemId() == R.id.viewnotes){


                    PopupMenu pop = new PopupMenu(cntxt, v);
                    pop.inflate(R.menu.notes_menu);
                    for(String n : list.get(position).getNotes()){

                        pop.getMenu().add(n);
                    }
                    pop.show();
                }

                return false;
            });

            popupMenu.show();

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //todo de refactorizat
        Button popup;
        TextView start, end, date, time, name, status;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            start = itemView.findViewById(R.id.start_loc_id);
            end = itemView.findViewById(R.id.end_loc_id);
            time = itemView.findViewById(R.id.Time_id);
            name = itemView.findViewById(R.id.trip_name_id);
            date = itemView.findViewById(R.id.Date_id);
            status = itemView.findViewById(R.id.status);
            popup = itemView.findViewById(R.id.pop_menu_id);

        }
    }
}
