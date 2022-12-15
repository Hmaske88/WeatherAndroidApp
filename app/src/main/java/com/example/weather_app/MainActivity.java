package com.example.weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar progressBar;
    private ImageView backIV,iconIV,searchIV;
    private TextInputLayout textInputLayout;
    private TextView cityNameTV,conditionTV,temperatureTV;
    private LinearLayout linearLayout;
    private TextInputEditText textInputEditText;
    private RecyclerView recyclerView;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int permission_code=1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        homeRL=findViewById(R.id.RLHome);
        progressBar=findViewById(R.id.progressBarLoading);
        backIV=findViewById(R.id.IVBack);
        iconIV=findViewById(R.id.IVicon);
        searchIV=findViewById(R.id.IVSearch);
        textInputLayout=findViewById(R.id.TICityName);
        cityNameTV=findViewById(R.id.TVCityName);
        conditionTV=findViewById(R.id.TVcondition);
        temperatureTV=findViewById(R.id.TVtemperature);
        textInputEditText=findViewById(R.id.TIEdtCityName);
        linearLayout=findViewById(R.id.LLEdt);
        recyclerView=findViewById(R.id.RvWeather);

        weatherRVModalArrayList= new ArrayList<>();
        weatherRVAdapter= new WeatherRVAdapter(this,weatherRVModalArrayList);
        recyclerView.setAdapter(weatherRVAdapter);

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},permission_code);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        cityName=getCity(location.getLongitude(),location.getLatitude());

//        getWeatherInfo(cityName);

        if (location != null){cityName = getCity(location.getLongitude(),location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            cityName = "Nagpur";
            getWeatherInfo(cityName);
        }


        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city =textInputEditText.getText().toString();
                if(city.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==permission_code)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCity(double longitude , double latitude)
    {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses=gcd.getFromLocation(latitude,longitude,10);

            for(Address adr:addresses){
                if(adr!=null){
                    String city=adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName=city;
                    }
                    else{
                        Log.d("tag","City not found");
                        Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName)
    {
        String url="http://api.weatherapi.com/v1/forecast.json?key=1fbafd8545de44afafd135702221412&q="+cityName+"&days=1&aqi=no&alerts=no";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();

                try {
                    String temperature=response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°C");
                    int isDay= response.getJSONObject("current").getInt("is_day");
                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    if(isDay==1)
                    {
                        //morning
                        Picasso.get().load("https://media.istockphoto.com/id/509197208/photo/beautiful-sky-with-white-cloud-background.jpg?b=1&s=170667a&w=0&k=20&c=_2Nf75uwDM8pwdY-GlUb4wX4D2n2__IhH5jo9eLo3EM=").into(backIV);

                    }
                    else
                    {
                        Picasso.get().load("https://images.pexels.com/photos/1723637/pexels-photo-1723637.jpeg").into(backIV);
                    }

                    JSONObject forecastObj=response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray= forecastO.getJSONArray("hour");

                    for(int i=0;i<hourArray.length();i++)
                    {
                        JSONObject hourObj=hourArray.getJSONObject(i);
                        String time=hourObj.getString("time");
                        String tempr=hourObj.getString( "temp_c");
                        String img=hourObj.getJSONObject("condition").getString("icon");
                        String wind=hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time,tempr,img,wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name...", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

}