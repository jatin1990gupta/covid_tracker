package com.example.covid_tracker;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class stateAct extends AppCompatActivity {

    TextView stateLstTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        stateLstTv= findViewById(R.id.stateLstTV);

        String URLstr = "https://api.rootnet.in/covid19-in/stats/latest";
        stateLstTv.setText("");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLstr,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONObject("data").getJSONArray("regional");

                    for (int i = 0; i < arr.length(); i++) {
                        String stName = arr.getJSONObject(i).getString("loc");
                        String totalCases = arr.getJSONObject(i).getString("totalConfirmed");
                        String recovered = arr.getJSONObject(i).getString("discharged");
                        String deaths = arr.getJSONObject(i).getString("deaths");
                        String activeCases = String.valueOf(Integer.parseInt(totalCases)-(Integer.parseInt(recovered)+Integer.parseInt(deaths)));

                        stateLstTv.append("--> "+stName.toUpperCase() + "\n(Total Cases: " + totalCases + ", Active Cases: " + activeCases + ")\n\n");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(stateAct.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(stateAct.this);
        requestQueue.add(stringRequest);
    }
}
