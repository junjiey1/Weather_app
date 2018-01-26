package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import utils.Utils;

/**
 * Created by benyi on 11.12.2017.
 */

public class WeatherHttpClient {
public String stream = null;
public String latLonStream = null;
public HttpURLConnection connection = null;
public InputStream inputStream = null;
    public WeatherHttpClient(){

    }
    public String getLatLon(String lat, String lng){
        StringBuilder sb = new StringBuilder(Utils.LatLon_URL);
        sb.append(String.format("lat=" + lat + "&lon="+lng + "&APPID=" + Utils.API_KEY + "&units=metric"));
        return sb.toString();
    }
    public String getLatLonData(String urlString){
        URL url = null;
        try {
            url = new URL(urlString);
            connection=(HttpURLConnection)url.openConnection();
            if(connection.getResponseCode()==200){//200-OK
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line + "\r\n");
                    latLonStream = sb.toString();
                    connection.disconnect();
                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLonStream;
    }

    public String getWeatherData(String place){


        try {
            connection = (HttpURLConnection)(new URL(Utils.Data_URL + place)).openConnection();
            if(connection.getResponseCode()==200){//200-OK
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line + "\r\n");
                    stream = sb.toString();
                    connection.disconnect();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;

/**
        try {
            //luodaan yhteys
            connection = (HttpURLConnection)(new URL(Utils.Data_URL + place)).openConnection();
            connection.setRequestMethod("GET");
            //connection.setDoInput(true);
            connection.setDoInput(true);
            connection.connect();

            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();

            //BufferedReader kerää data InpuStreamilta ja kännä se String:ksi.
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferReader.readLine()) != null){
               stringBuffer.append(line + "\r\n");
            }
            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
 **/
    }

}
