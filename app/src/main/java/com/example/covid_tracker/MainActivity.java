package com.example.covid_tracker;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView actCases, summ, tCases, inCases, foCases, diCases, deCases;
    Spinner stateSpn;
    Button showStateBtn, showCountryBtn, showStLstBtn;
    ArrayList<DataModel> list = new ArrayList<DataModel>();

    String URLstr = "https://api.rootnet.in/covid19-in/stats/latest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        summ = findViewById(R.id.summary);
        actCases = findViewById(R.id.actCases);
        tCases = findViewById(R.id.tCases);
        inCases = findViewById(R.id.inCases);
        foCases = findViewById(R.id.foCases);
        diCases = findViewById(R.id.diCases);
        deCases = findViewById(R.id.deCases);
        stateSpn = findViewById(R.id.stateSpn);
        showStateBtn = findViewById(R.id.showStateBtn);
        showCountryBtn = findViewById(R.id.showCountryBtn);
        showStLstBtn = findViewById(R.id.showStLstBtn);

        setCountryData();

        showStateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stateNam = stateSpn.getSelectedItem().toString();
                if(stateNam.equals("--SELECT STATE--")){
                    Toast.makeText(getApplicationContext(), "Please Select State..", Toast.LENGTH_LONG).show();
                }
                else {
                    setStateData(stateNam);
                }
            }
        });

        showCountryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summ.setText("INDIA");
                setCountryData();
                stateSpn.setSelection(0);
            }
        });

        showStLstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,stateAct.class);
                startActivity(i);
            }
        });

    }

    public void setCountryData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLstr,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String total = obj.getJSONObject("data").getJSONObject("summary").getString("total");
                            tCases.setText(total);
                            String indians = obj.getJSONObject("data").getJSONObject("summary").getString("confirmedCasesIndian");
                            inCases.setText(indians);
                            String foreigners = obj.getJSONObject("data").getJSONObject("summary").getString("confirmedCasesForeign");
                            foCases.setText(foreigners);
                            String discharged = obj.getJSONObject("data").getJSONObject("summary").getString("discharged");
                            diCases.setText(discharged);
                            String deaths = obj.getJSONObject("data").getJSONObject("summary").getString("deaths");
                            deCases.setText(deaths);
                            actCases.setText(String.valueOf(Integer.parseInt(tCases.getText().toString())-
                                    (Integer.parseInt(deCases.getText().toString())+Integer.parseInt(diCases.getText().toString()))));

                        } catch (JSONException ee) {
                            ee.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void setStateData(final String stateNam){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLstr,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONObject("data").getJSONArray("regional");

                            for (int i=0; i<arr.length(); i++){
                                JSONObject obj1 = arr.getJSONObject(i);
                                DataModel d = new DataModel();

                                d.setsLoc(obj1.getString("loc"));
                                d.setsInCases(obj1.getString("confirmedCasesIndian"));
                                d.setsFoCases(obj1.getString("confirmedCasesForeign"));
                                d.setsDiCases(obj1.getString("discharged"));
                                d.setsDeCases(obj1.getString("deaths"));
                                d.setsTCases(obj1.getString("totalConfirmed"));

                                if (d.getsLoc().equals(stateNam)){
                                    summ.setText(d.getsLoc());
                                    tCases.setText(d.getsTCases());
                                    inCases.setText(d.getsInCases());
                                    foCases.setText(d.getsFoCases());
                                    diCases.setText(d.getsDiCases());
                                    deCases.setText(d.getsDeCases());

                                    i=arr.length();
                                }

                                list.add(d);
                            }

                            actCases.setText(String.valueOf(Integer.parseInt(tCases.getText().toString())-
                                    (Integer.parseInt(deCases.getText().toString())+Integer.parseInt(diCases.getText().toString()))));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
