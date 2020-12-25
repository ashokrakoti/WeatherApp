package com.example.android.weatherapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherService {

    public static final String CITY_ID_URI = "https://www.metaweather.com/api/location/search/?query=";
    public static final String WEATHER_BY_CITY_ID_URI = "https://www.metaweather.com/api/location/";

    Context context;
    String cityId;
    public WeatherService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }


    public void getCityId(String cityName,  VolleyResponseListener volleyResponseListener){
                                                    //  Creation and Instantiation of the RequestQueue. this will happen each time we
                                                    //  RequestQueue queue = Volley.newRequestQueue(context);
        String url = CITY_ID_URI + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String cityID = null;
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("weatherService.class", e.getMessage());// trying to print the error messages. see the console for messages.
                }

                volleyResponseListener.onResponse(cityID);
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError(error.getMessage());
            }
        });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(request);

    }

  public void getCityForecastById(String cityID, VolleyResponseListener volleyResponseListener){

        String url = WEATHER_BY_CITY_ID_URI + cityID;
      WeatherReportModel model = new WeatherReportModel();

      JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
              WeatherReportModel model = new WeatherReportModel();
            //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

              try {
                   JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");

                   //get the first item in the array and set them to a day's forecast.
                  JSONObject day_one_report = consolidated_weather_list.getJSONObject(0);

                  //Toast.makeText(context, day_one_report.toString(), Toast.LENGTH_LONG).show();

                  model.setId((day_one_report.getInt("id")));
                  model.setWeather_state_name(day_one_report.getString("weather_state_name"));
                  model.setWeather_state_abbr(day_one_report.getString("weather_state_abbr"));
                  model.setWind_direction_compass(day_one_report.getString("wind_direction_compass"));
                  model.setCreated(day_one_report.getString("created"));
                  model.setApplicable_date(day_one_report.getString("applicable_date"));
                  model.setMin_temp(day_one_report.getDouble("min_temp"));
                  model.setMax_temp(day_one_report.getDouble("max_temp"));
                  model.setThe_temp(day_one_report.getDouble("the_temp"));
                  model.setWind_speed(day_one_report.getDouble("wind_speed"));
                  model.setWind_direction(day_one_report.getDouble("wind_direction"));
                  model.setAir_pressure(day_one_report.getDouble("air_pressure"));
                  model.setHumidity(day_one_report.getInt("humidity"));
                  model.setVisibility(day_one_report.getDouble("visibility"));
                  model.setPredictability(day_one_report.getInt("predictability"));

                  Log.i("weatherService", model.toString());

              } catch (JSONException e) {
                  e.printStackTrace();
                  Log.e("WeatherService.class",e.getMessage());
              }
              volleyResponseListener.onResponse(model.toString());
          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
              Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
              volleyResponseListener.onError(error.getMessage());
          }
      });
      MySingleton.getInstance(context).addToRequestQueue(request);
  }





   public void getCityForecastByName(String cityName, VolleyResponseListener responseListener){
        //take the name and find the id


      getCityId(cityName, new VolleyResponseListener() {
          @Override
          public void onError(String message) {
              Toast.makeText(context, "error in getcityId()", Toast.LENGTH_LONG).show();
              responseListener.onError(message);

          }

          @Override
          public void onResponse(String cityID) {// null check is  needed for this case.when any random name is entered we get a null cityId value.
             // Toast.makeText(context, "Returned cityID is "+ cityID, Toast.LENGTH_SHORT).show();

              if(cityID!= null)
              {
                  Log.i("WeatherService.class", cityID);
                  getCityForecastById(cityID, new VolleyResponseListener() {
                       @Override
                       public void onError(String message) {
                           Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                       }

                       @Override
                       public void onResponse(String weather_report) {
                          // Toast.makeText(context, "Report is " + weather_report, Toast.LENGTH_SHORT).show();
                           Log.i("weatherservice-getbynme", weather_report);
                           responseListener.onResponse(weather_report);
                       }
                   });
               }else{
                  Toast.makeText(context, "we dont have data for this location.", Toast.LENGTH_LONG).show();
              }
          }
      });
    }
}
