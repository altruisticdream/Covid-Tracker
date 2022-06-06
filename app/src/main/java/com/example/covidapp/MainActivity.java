package com.example.covidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
  CountryCodePicker countryCodePicker;
  TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecovered,mtodayrecovered,mdeaths,mtodaydeaths;
  String country;
  TextView mfilter;
  Spinner spinner;
  private AdView mAdView;
  String[] types={"cases","deaths","recovered","active"};
  private List<ModelClass> modelClasslist;
  private List<ModelClass> modelClasslist2;
  PieChart mpiechart;
    com.example.covidapp.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        countryCodePicker=findViewById(R.id.cp);
        //mtodayactive=findViewById(R.id.todayactive);
        mactive=findViewById(R.id.activecases);
        mdeaths=findViewById(R.id.totaldeath);
        mtodaydeaths=findViewById(R.id.todaydeath);
        mrecovered=findViewById(R.id.recoveredcases);
        mtodayrecovered=findViewById(R.id.totalrecovered);
        mtotal=findViewById(R.id.totalcases);
        mtodaytotal=findViewById(R.id.todaytotal);
        mpiechart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        RecyclerView recyclerView;
        recyclerView = findViewById(R.id.recyclerview);
        modelClasslist=new ArrayList<>();
        modelClasslist2=new ArrayList<>();
        spinner.setOnItemSelectedListener(this);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
                Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest );
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });












        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);


        ApiUtillities.getApitInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ModelClass>> call, @NonNull Response<List<ModelClass>> response) {
                assert response.body() != null;
                modelClasslist2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<List<ModelClass>> call, @NonNull Throwable t) {

            }
        });
        adapter=new Adapter(getApplicationContext(),modelClasslist2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country=countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });
       fetchdata();




    }

    private void fetchdata() {
        ApiUtillities.getApitInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(@NonNull Call<List<ModelClass>> call, @NonNull Response<List<ModelClass>> response) {
                modelClasslist.addAll(Objects.requireNonNull(response.body()));
                for(int i=0;i<modelClasslist.size();i++)
                {
                    if(modelClasslist.get(i).getCountry().equals(country))
                    {
                        mactive.setText(modelClasslist.get(i).getActive());
                        //mtodayactive.setText(modelClasslist.get(i).getCases());
                        mtodaydeaths.setText(modelClasslist.get(i).getTodayDeaths());
                        mtodayrecovered.setText(modelClasslist.get(i).getTodayRecovered());
                        mtodaytotal.setText(modelClasslist.get(i).getTodayCases());
                        mtotal.setText(modelClasslist.get(i).getCases());
                        mdeaths.setText(modelClasslist.get(i).getDeaths());
                        mrecovered.setText(modelClasslist.get(i).getRecovered());

                        int active,total,recovered,deaths;
                        active=Integer.parseInt(modelClasslist.get(i).getActive());
                        total=Integer.parseInt(modelClasslist.get(i).getCases());
                        recovered=Integer.parseInt(modelClasslist.get(i).getRecovered());
                        deaths=Integer.parseInt(modelClasslist.get(i).getDeaths());


                        updategraph(active,total,recovered,deaths);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ModelClass>> call, @NonNull Throwable t) {

            }
        });
    }

    private void updategraph(int active, int total, int recovered, int deaths) {

        mpiechart.clearChart();
        mpiechart.addPieSlice(new PieModel("Active",active,Color.parseColor("#FF4CAF50")));
        mpiechart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FFB701")));
        mpiechart.addPieSlice(new PieModel("Recovered",recovered,Color.parseColor("#38ACCD")));
        mpiechart.addPieSlice(new PieModel("Deaths",deaths,Color.parseColor("#F55c47")));
        mpiechart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        String item=types[position];
        mfilter.setText(item);
        adapter.filter(item);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}