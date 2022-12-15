package com.example.weather_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModal> WeatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModal> weatherRVModelArrayList) {
        this.context = context;
        WeatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModal modal = WeatherRVModelArrayList.get(position);
        holder.temperatureTV.setText(modal.getTemperature()+"°C");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionIV);
        holder.windTV.setText(modal.getWindSpeed()+"Km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");

        try{
            Date t=input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return WeatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView temperatureTV,conditionTV,windTV,timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temperatureTV=itemView.findViewById(R.id.TVtemperature);
            conditionTV=itemView.findViewById(R.id.TVcondition);
            windTV=itemView.findViewById(R.id.TVwindSpeed);
            timeTV=itemView.findViewById(R.id.TVtime);
            conditionIV=itemView.findViewById(R.id.IVcondition);
        }
    }
}
