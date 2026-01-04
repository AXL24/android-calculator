package com.example.calculator.utils;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CurrencyAPI {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    private static Map<String, Double> cachedRates = new HashMap<>();
    private static long lastFetchTime = 0;
    private static final long CACHE_DURATION = 3600000; // 1 hour

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface CurrencyCallback {
        void onSuccess(Map<String, Double> rates);
        void onError(String error);
    }

    public void fetchRates(CurrencyCallback callback) {
        // Return cached rates if still valid
        if (System.currentTimeMillis() - lastFetchTime < CACHE_DURATION && !cachedRates.isEmpty()) {
            mainHandler.post(() -> callback.onSuccess(cachedRates));
            return;
        }

        executor.execute(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject rates = json.getJSONObject("rates");

                cachedRates.clear();
                cachedRates.put("USD", 1.0);

                String[] currencies = {"EUR", "GBP", "JPY", "VND", "CNY", "KRW", "AUD"};
                for (String currency : currencies) {
                    if (rates.has(currency)) {
                        cachedRates.put(currency, rates.getDouble(currency));
                    }
                }

                lastFetchTime = System.currentTimeMillis();

                mainHandler.post(() -> callback.onSuccess(cachedRates));

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public double convert(double amount, String from, String to, Map<String, Double> rates) {
        if (!rates.containsKey(from) || !rates.containsKey(to)) {
            return 0;
        }

        // Convert to USD first, then to target currency
        double inUSD = amount / rates.get(from);
        return inUSD * rates.get(to);
    }
}