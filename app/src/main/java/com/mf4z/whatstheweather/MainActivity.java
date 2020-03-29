package com.mf4z.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.camera2.TotalCaptureResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private String country = "London";


    EditText etCountryName;
    TextView txtMainWeather;
    TextView txtExtraWeatherInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCountryName = (EditText) findViewById(R.id.editText_city_name);
        txtMainWeather = (TextView) findViewById(R.id.textView_mainweather_info);
        txtExtraWeatherInfo = (TextView) findViewById(R.id.textView_weather_info);


    }


    public class DownloadWeatherData extends AsyncTask<String, Void, String> {

        URL url;
        HttpURLConnection connection;
        String result = "";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i("json_s ", s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("weather info", weatherInfo);

                JSONArray array = new JSONArray(weatherInfo);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonPart = array.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String desc = jsonPart.getString("description");

                    if (!main.isEmpty() && !desc.isEmpty()) {
                        Log.i("main", main);
                        //Set main weather info
                        txtMainWeather.setText(main);
                        Log.i("desc", desc);
                        //Set extra weather info
                        txtExtraWeatherInfo.setText(desc);
                    } else {

                        Toast.makeText(getApplicationContext(), "Could not find weather info :(", Toast.LENGTH_SHORT).show();
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }

            //Used effect changes on the UI
        }


        @Override
        protected String doInBackground(String... urls) {

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }

        }
    }

    public void searchWeather(View view) {

        DownloadWeatherData task = new DownloadWeatherData();

        country = etCountryName.getText().toString();
        String encodedCityName = "";
        String result = null;

        try {
            encodedCityName = URLEncoder.encode(country, "UTF-8");
            result = task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22").get();

            //Handles closing of keyboard after button is pressed
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(etCountryName.getWindowToken(), 0);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, "Error,Could not find weather :(", Toast.LENGTH_SHORT).show();
        }


    }
}
