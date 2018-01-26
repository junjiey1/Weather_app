package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Place;
import model.Weather;
import utils.Utils;

/**
 * Created by benyi on 11.12.2017.
 */

public class JsonWeatherParser {
    //Käy läpi ja hakea sieltä tarvittava Json tiedot , mikä on luotu WeatherHttpClient -luokassa.
    public static Weather getWeather(String data){
        Weather weather = new Weather();
        try {
            //Haetaan sijainnin (lat, lon)
            JSONObject jsonObject = new JSONObject(data);
            Place place = new Place();
            JSONObject coordObj = Utils.getObject("coord", jsonObject);
            place.setLat(Utils.getFloat("lat", coordObj));
            place.setLon(Utils.getFloat("lon", coordObj));

            //Haetaan paikan tiedot
            JSONObject sysObj = Utils.getObject("sys", jsonObject);
            place.setContry(Utils.getString("country", sysObj));
            place.setLastupdate(Utils.getInt("dt", jsonObject));
            place.setSunrise(Utils.getInt("sunrise", sysObj));
            place.setSunset(Utils.getInt("sunset", sysObj));
            place.setCity(Utils.getString("name", jsonObject));
            weather.place = place;
            //Haetaan sää tidot
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCond.setWeatherId(Utils.getInt("id", jsonWeather));
            weather.currentCond.setDescription(Utils.getString("description", jsonWeather));
            weather.currentCond.setCondition(Utils.getString("main", jsonWeather));
            weather.currentCond.setIcon(Utils.getString("icon", jsonWeather));
            //Haetaan tuulen tiedot
            JSONObject windObj = Utils.getObject("wind", jsonObject);
            weather.wind.setSpeed(Utils.getFloat("speed", windObj));
            weather.wind.setDeg(Utils.getFloat("deg", windObj));
            //Haetaan pilvin tiedot
            JSONObject cloudObj = Utils.getObject("clouds", jsonObject);
            weather.clouds.setPrecipitation(Utils.getInt("all", cloudObj));

            JSONObject mainObj = Utils.getObject("main", jsonObject);
            weather.currentCond.setHumidity(Utils.getInt("humidity", mainObj));
            weather.currentCond.setPressure(Utils.getFloat("pressure", mainObj));
            weather.currentCond.setMinTemp(Utils.getFloat("temp_min", mainObj));
            weather.currentCond.setMaxTemp(Utils.getFloat("temp_max", mainObj));
            weather.currentCond.setTemperature(Utils.getFloat("temp", mainObj));


            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
