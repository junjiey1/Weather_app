package utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by benyi on 9.12.2017.
 */

public class Utils {
    public static final String Data_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String Icon_URL = "http://api.openweathermap.org/img/w/";
    public static final String LatLon_URL = "api.openweathermap.org/data/2.5/weather?" ;
    public static final String API_KEY = "bc6eb3bc535b6b9a61c9cde39c14ba1f";

    public static JSONObject getObject(String tagName, JSONObject jsonObj ) throws JSONException{
        JSONObject jObj = jsonObj.getJSONObject(tagName);
        return jObj;
    }
    public static String getString(String tagName, JSONObject jsonObj) throws JSONException{
        return jsonObj.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jsonObj) throws JSONException{
        return (float)jsonObj.getDouble(tagName);
    }
    public static double getDouble(String tagName, JSONObject jsonObj) throws JSONException{
        return (float) jsonObj.getDouble(tagName);
    }
    public static int getInt(String tagName, JSONObject jsonObj) throws JSONException{
        return jsonObj.getInt(tagName);
    }

}
