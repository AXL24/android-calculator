package com.example.calculator.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.calculator.models.Calculation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String PREFS_NAME = "calculator_prefs";
    private static final String HISTORY_KEY = "history";
    private static final int MAX_HISTORY = 100;

    private SharedPreferences prefs;
    private Gson gson;

    public HistoryManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addToHistory(String previous, String expression, String result) {
        List<Calculation> history = getHistory();
        history.add(0, new Calculation(previous, expression, result));

        if (history.size() > MAX_HISTORY) {
            history = history.subList(0, MAX_HISTORY);
        }

        saveHistory(history);
    }

    public List<Calculation> getHistory() {
        String json = prefs.getString(HISTORY_KEY, "[]");
        Type type = new TypeToken<ArrayList<Calculation>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void clearHistory() {
        prefs.edit().remove(HISTORY_KEY).apply();
    }

    private void saveHistory(List<Calculation> history) {
        String json = gson.toJson(history);
        prefs.edit().putString(HISTORY_KEY, json).apply();
    }
}