package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private final String apiKey = "8dcb61d9285f2154d99b40f2b8a6405d";
    private final String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=ru&units=metric ";
    private final String textViewAttrStr = "%s\nТемпература - %s\u2103\nНа улице - %s";

    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textViewAttr);
    }

    public void onClick_find_weather(View view) {
        String city = editText.getText().toString();
        WeatherTask weatherTask = new WeatherTask();
        try {
            JSONObject jsonWeather = weatherTask.execute(String.format(apiUrl, city, apiKey)).get();
            setText(jsonWeather);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void setText (JSONObject jsonObject) {
        try {
            String city = jsonObject.getString("name");
            String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            int temp = (int) jsonObject.getJSONObject("main").getDouble("temp");
            String resultAttr = String.format(textViewAttrStr, city, temp, weather);
            textView.setText(resultAttr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class WeatherTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.i("weatherJson" ,jsonObject.toString());

        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return new JSONObject(result.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
    }
}