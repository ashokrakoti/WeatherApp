package com.example.android.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button btn_cityID, btn_getWeatherByID, btn_getWeatherByName;
    EditText et_dataInput;
    TextView tv_weatherReport;
    String weather_report = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hook up the buttons and views after inflating the layouts. inside teh onCreate method.
        btn_cityID = findViewById(R.id.btn1_findCityIdByName);
        btn_getWeatherByID = findViewById(R.id.btn2_GetReportByCityId);
        btn_getWeatherByName = findViewById(R.id.button3); //TODO change this button id value

        et_dataInput = findViewById(R.id.dataInput);
        tv_weatherReport = findViewById(R.id.reportDisplay);
        WeatherService service = new WeatherService(MainActivity.this);

        //creating the on click listeners

        //button1
        btn_cityID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 service.getCityId(et_dataInput.getText().toString(), new WeatherService.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "error occurred", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String cityID) {
                        if(cityID==null){
                            Toast.makeText(MainActivity.this,"we don't have data for this location",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Returned cityID is " + cityID, Toast.LENGTH_SHORT).show();
                            tv_weatherReport.setText(cityID);
                        }
                    }
                });
            }
        });


        //button2
        btn_getWeatherByID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                service.getCityForecastById(et_dataInput.getText().toString(), new WeatherService.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "error occurred", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String weather_report) {
                        tv_weatherReport.setText(weather_report);
                    }
                });
            }
        });

        //button3
        btn_getWeatherByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "you clicked btn3", Toast.LENGTH_SHORT).show();

                service.getCityForecastByName(et_dataInput.getText().toString(), new WeatherService.VolleyResponseListener() {
                   @Override
                   public void onError(String message) {
                       Toast.makeText(MainActivity.this, "error :"+message, Toast.LENGTH_LONG).show();
                   }

                   @Override
                   public void onResponse(String report) {
                      // Toast.makeText(MainActivity.this, "final report "+report, Toast.LENGTH_LONG).show();
                       Log.i("report", report);
                       tv_weatherReport.setText(report);
                   }
               });

            }
        });
    }
}