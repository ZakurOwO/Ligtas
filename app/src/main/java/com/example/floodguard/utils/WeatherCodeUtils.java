package com.example.floodguard.utils;

public class WeatherCodeUtils {
    public static String getCondition(int code) {
        switch (code) {
            case 0: return "Clear Sky";
            case 1: case 2: case 3: return "Mainly Clear / Partly Cloudy";
            case 45: case 48: return "Foggy";
            case 51: case 53: case 55: return "Drizzle";
            case 61: case 63: case 65: return "Rain";
            case 71: case 73: case 75: return "Snow Fall";
            case 80: case 81: case 82: return "Rain Showers";
            case 95: return "Thunderstorm";
            case 96: case 99: return "Thunderstorm with Hail";
            default: return "Unknown";
        }
    }

    public static String getSeverity(int code) {
        if (code >= 80) return "Danger";
        if (code >= 51) return "Caution";
        return "Normal";
    }
}
