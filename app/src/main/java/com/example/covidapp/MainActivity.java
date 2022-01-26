package com.example.covidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
  CountryCodePicker countryCodePicker;
  TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecovered,mtodayrecovered,mdeaths,mtodaydeaths;
  String country;
  TextView mfilter;
  Spinner spinner;
  String[] types={"cases","deaths","recovered","active"};
  private List<ModelClass> modelClasslist;
  private List<ModelClass> modelClasslist2;
  PieChart mpiechart;
  private RecyclerView recyclerView;
  com.example.covidapp.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        countryCodePicker=findViewById(R.id.cp);
        mtodayactive=findViewById(R.id.todayactive);
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
        recyclerView=findViewById(R.id.recyclerview);
        modelClasslist=new ArrayList<>();
        modelClasslist2=new ArrayList<>();
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);


        ApiUtillities.getApitInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClasslist2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

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
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClasslist.addAll(response.body());
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
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

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