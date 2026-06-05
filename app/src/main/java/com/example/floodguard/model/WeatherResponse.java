package com.example.floodguard.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("current")
    private Current current;

    @SerializedName("daily")
    private Daily daily;

    public Current getCurrent() { return current; }
    public Daily getDaily() { return daily; }
    public void setCurrent(Current current) { this.current = current; }
    public void setDaily(Daily daily) { this.daily = daily; }

    public static class Current {
        @SerializedName("temperature_2m")
        private double temperature;
        @SerializedName("relative_humidity_2m")
        private double humidity;
        @SerializedName("apparent_temperature")
        private double feelsLike;
        @SerializedName("precipitation")
        private double precipitation;
        @SerializedName("wind_speed_10m")
        private double windSpeed;
        @SerializedName("weather_code")
        private int weatherCode;

        public double getTemperature() { return temperature; }
        public double getHumidity() { return humidity; }
        public double getFeelsLike() { return feelsLike; }
        public double getPrecipitation() { return precipitation; }
        public double getWindSpeed() { return windSpeed; }
        public int getWeatherCode() { return weatherCode; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public void setHumidity(double humidity) { this.humidity = humidity; }
        public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
        public void setPrecipitation(double precipitation) { this.precipitation = precipitation; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
        public void setWeatherCode(int weatherCode) { this.weatherCode = weatherCode; }
    }

    public static class Daily {
        @SerializedName("time")
        private List<String> time;
        @SerializedName("weather_code")
        private List<Integer> weatherCode;
        @SerializedName("temperature_2m_max")
        private List<Double> tempMax;
        @SerializedName("temperature_2m_min")
        private List<Double> tempMin;

        public List<String> getTime() { return time; }
        public List<Integer> getWeatherCode() { return weatherCode; }
        public List<Double> getTempMax() { return tempMax; }
        public List<Double> getTempMin() { return tempMin; }
        public void setTime(List<String> time) { this.time = time; }
        public void setWeatherCode(List<Integer> weatherCode) { this.weatherCode = weatherCode; }
        public void setTempMax(List<Double> tempMax) { this.tempMax = tempMax; }
        public void setTempMin(List<Double> tempMin) { this.tempMin = tempMin; }
    }
}
