package model;

/**
 * Created by benyi on 11.12.2017.
 */

public class Weather {
    public Place place;
    public Coord coord;
    public String iconData;
    public CurrentCondition currentCond = new CurrentCondition();
    public Temperature temper = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();

}
