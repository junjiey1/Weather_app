package com.example.benyi.weather_app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import data.CityPreference;
import data.JsonWeatherParser;
import data.WeatherHttpClient;
import model.Weather;
import utils.Utils;

public class MainActivity extends AppCompatActivity  {
    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView cloud;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private TextView description;


    Weather weather = new Weather();
    LocationManager locationManager;
    String provider;
    static double lat, lng;
    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityText);
        iconView = (ImageView) findViewById(R.id.weatherIcon);
        temp = (TextView) findViewById(R.id.temptext);
        cloud = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humText);
        pressure = (TextView) findViewById(R.id.pressText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.riseText);
        sunset = (TextView) findViewById(R.id.setText);
        updated = (TextView) findViewById(R.id.updateText);
        description = (TextView) findViewById(R.id.descriptionText);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        CityPreference cityprefs = new CityPreference(MainActivity.this);
        renderWeatherData("Helsinki");

    }

    public void renderWeatherData(String city){
        WeatherTask weatherT = new WeatherTask();
        weatherT.execute( new String[]{ city + "&APPID=" + Utils.API_KEY + "&units=metric"} );
    }



    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImage(params[0]);
        }

        //Kun Image on ladattu sivulta.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);

        }


        private Bitmap downloadImage(String code){
            final DefaultHttpClient client = new DefaultHttpClient();
            final HttpGet getRequest =new HttpGet(Utils.Icon_URL + code + ".png");
            //final HttpGet getRequest = new HttpGet("http://api.openweathermap.org/img/w/13n.png");

            try {
                HttpResponse response = client.execute(getRequest);

                final int statusCode = response.getStatusLine().getStatusCode();

                if(statusCode != HttpStatus.SC_OK){
                    Log.e("DownloadImage", "Error:" + statusCode);
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                if(entity != null){
                    InputStream inputStream = null;
                    inputStream = entity.getContent();
                    //käsitellään inputStreamissä oleva sisällöt Bitmapin avulla.
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
        }

    }


    private class WeatherTask extends AsyncTask<String, Void, Weather>{

        //doInBacckgroudissa oleva koodit ajetaan taustalla. Eikä vaikuttaa muiden saikeiden ajaamista.
        @Override
        protected Weather doInBackground(String... params) {

            String data = ((new WeatherHttpClient())).getWeatherData(params[0]);
            weather = JsonWeatherParser.getWeather(data);
            weather.iconData=weather.currentCond.getIcon();
            new DownloadImageAsyncTask().execute(weather.iconData);
            return weather;
        }
        //asetetaan TextView:lle uudet tekstit
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            //käännetään Unix aika
            Date riseDate = new Date(weather.place.getSunrise()*1000L);
            Date setDate = new Date(weather.place.getSunset()*1000L);
            Date upDate = new Date(weather.place.getLastupdate()*1000L);

            SimpleDateFormat rise_set = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat updateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            rise_set.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
            updateF.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
            String sunriseDate = rise_set.format(riseDate);
            String sunsetDate = rise_set.format(setDate);
            String updatedDate = updateF.format(upDate);
            //otetaan desimaalit pois lämpötilasta
            DecimalFormat decimalF = new DecimalFormat("#.#");
            String tempF = decimalF.format(weather.currentCond.getTemperature());
            String cityString = getResources().getString(R.string.city_name);
            cityName.setText(cityString + weather.place.getCity() + ", " + weather.place.getContry());

            temp.setText("" + tempF + " \u2103");

            String windString = getResources().getString(R.string.wind);
            wind.setText(windString+" "+ weather.wind.getSpeed() + "m/s");

            String cloudString = getResources().getString(R.string.cloud_text);
            cloud.setText(cloudString + " " + weather.clouds.getPrecipitation());

            String pressureStr = getResources().getString(R.string.pressure_text);
            pressure.setText(pressureStr + " " + weather.currentCond.getPressure() + "hPa");

            String humidityStr = getResources().getString(R.string.humidity_text);
            humidity.setText(humidityStr + " " + weather.currentCond.getHumidity()+ "%");

            String riseStr = getResources().getString(R.string.rise_text);
            //String sunriseDate = df.format(new Date(weather.place.getSunrise()));
            sunrise.setText(riseStr + " " + sunriseDate);

            String setStr = getResources().getString(R.string.set_Text);
            //String sunsetDate = df.format(new Date(weather.place.getSunset()));
            sunset.setText(setStr + " " + sunsetDate);

            String updateStr = getResources().getString(R.string.update_text);
            //String updatedDate = df.format(new Date(weather.place.getLastupdate()));
            updated.setText(updateStr + " " + updatedDate);

            String descriptionStr = getResources().getString(R.string.description_text);
            description.setText(descriptionStr + " " + weather.currentCond.getCondition() +
                    "("+weather.currentCond.getDescription().substring(0, 1).toUpperCase() +
                    weather.currentCond.getDescription().substring(1)+")");


        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.change_city));

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Helsinki,FI");
        builder.setView(cityInput);
        builder.setPositiveButton(getResources().getString(R.string.submitBtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPref = new CityPreference(MainActivity.this);
                cityPref.setCity(cityInput.getText().toString());

                String newCity = cityPref.getCity();
                renderWeatherData(newCity);
            }
        });
        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.change_city){
            showInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }

}
