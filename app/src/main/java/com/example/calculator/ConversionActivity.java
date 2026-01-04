package com.example.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.calculator.models.ConversionType;
import com.example.calculator.utils.CurrencyAPI;
import java.text.DecimalFormat;
import java.util.Map;

public class ConversionActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private Spinner fromUnitSpinner;
    private Spinner toUnitSpinner;
    private EditText inputValue;
    private TextView outputValue;
    private ProgressBar loadingIndicator;

    private ConversionType currentType;
    private CurrencyAPI currencyAPI;
    private Map<String, Double> currencyRates;
    private DecimalFormat df = new DecimalFormat("#.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        currencyAPI = new CurrencyAPI();
        initViews();
        setupListeners();
        loadCategories();
    }

    private void initViews() {
        categorySpinner = findViewById(R.id.category_spinner);
        fromUnitSpinner = findViewById(R.id.from_unit_spinner);
        toUnitSpinner = findViewById(R.id.to_unit_spinner);
        inputValue = findViewById(R.id.input_value);
        outputValue = findViewById(R.id.output_value);
        loadingIndicator = findViewById(R.id.loading_indicator);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_swap).setOnClickListener(v -> swapUnits());
    }

    private void setupListeners() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentType = ConversionType.values()[position];
                loadUnits();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fromUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        toUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performConversion();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCategories() {
        String[] categories = new String[ConversionType.values().length];
        for (int i = 0; i < ConversionType.values().length; i++) {
            categories[i] = ConversionType.values()[i].getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void loadUnits() {
        String[] units = currentType.getUnits();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, units);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        fromUnitSpinner.setAdapter(adapter);
        toUnitSpinner.setAdapter(adapter);

        if (units.length > 1) {
            toUnitSpinner.setSelection(1);
        }

        if (currentType == ConversionType.CURRENCY) {
            loadCurrencyRates();
        }
    }

    private void loadCurrencyRates() {
        loadingIndicator.setVisibility(View.VISIBLE);
        currencyAPI.fetchRates(new CurrencyAPI.CurrencyCallback() {
            @Override
            public void onSuccess(Map<String, Double> rates) {
                loadingIndicator.setVisibility(View.GONE);
                currencyRates = rates;
                performConversion();
            }

            @Override
            public void onError(String error) {
                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(ConversionActivity.this,
                        "Failed to load currency rates: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performConversion() {
        String inputText = inputValue.getText().toString();
        if (inputText.isEmpty()) {
            outputValue.setText("0");
            return;
        }

        try {
            double input = Double.parseDouble(inputText);
            String fromUnit = fromUnitSpinner.getSelectedItem().toString();
            String toUnit = toUnitSpinner.getSelectedItem().toString();

            double result = convert(input, fromUnit, toUnit);
            outputValue.setText(df.format(result));
        } catch (NumberFormatException e) {
            outputValue.setText("Error");
        }
    }

    private double convert(double value, String from, String to) {
        switch (currentType) {
            case LENGTH:
                return convertLength(value, from, to);
            case WEIGHT:
                return convertWeight(value, from, to);
            case TEMPERATURE:
                return convertTemperature(value, from, to);
            case AREA:
                return convertArea(value, from, to);
            case CURRENCY:
                return convertCurrency(value, from, to);
            default:
                return 0;
        }
    }

    private double convertLength(double value, String from, String to) {
        // Convert to meters first
        double meters = 0;
        switch (from) {
            case "Meter": meters = value; break;
            case "Kilometer": meters = value * 1000; break;
            case "Mile": meters = value * 1609.34; break;
            case "Foot": meters = value * 0.3048; break;
            case "Inch": meters = value * 0.0254; break;
            case "Centimeter": meters = value * 0.01; break;
        }

        // Convert from meters to target unit
        switch (to) {
            case "Meter": return meters;
            case "Kilometer": return meters / 1000;
            case "Mile": return meters / 1609.34;
            case "Foot": return meters / 0.3048;
            case "Inch": return meters / 0.0254;
            case "Centimeter": return meters / 0.01;
        }
        return 0;
    }

    private double convertWeight(double value, String from, String to) {
        // Convert to kilograms first
        double kg = 0;
        switch (from) {
            case "Kilogram": kg = value; break;
            case "Gram": kg = value / 1000; break;
            case "Pound": kg = value * 0.453592; break;
            case "Ounce": kg = value * 0.0283495; break;
            case "Ton": kg = value * 1000; break;
        }

        // Convert from kg to target unit
        switch (to) {
            case "Kilogram": return kg;
            case "Gram": return kg * 1000;
            case "Pound": return kg / 0.453592;
            case "Ounce": return kg / 0.0283495;
            case "Ton": return kg / 1000;
        }
        return 0;
    }

    private double convertTemperature(double value, String from, String to) {
        // Convert to Celsius first
        double celsius = 0;
        switch (from) {
            case "Celsius": celsius = value; break;
            case "Fahrenheit": celsius = (value - 32) * 5/9; break;
            case "Kelvin": celsius = value - 273.15; break;
        }

        // Convert from Celsius to target unit
        switch (to) {
            case "Celsius": return celsius;
            case "Fahrenheit": return celsius * 9/5 + 32;
            case "Kelvin": return celsius + 273.15;
        }
        return 0;
    }

    private double convertArea(double value, String from, String to) {
        // Convert to square meters first
        double sqm = 0;
        switch (from) {
            case "Square Meter": sqm = value; break;
            case "Square Kilometer": sqm = value * 1000000; break;
            case "Square Mile": sqm = value * 2589988.11; break;
            case "Square Foot": sqm = value * 0.092903; break;
            case "Acre": sqm = value * 4046.86; break;
            case "Hectare": sqm = value * 10000; break;
        }

        // Convert from sqm to target unit
        switch (to) {
            case "Square Meter": return sqm;
            case "Square Kilometer": return sqm / 1000000;
            case "Square Mile": return sqm / 2589988.11;
            case "Square Foot": return sqm / 0.092903;
            case "Acre": return sqm / 4046.86;
            case "Hectare": return sqm / 10000;
        }
        return 0;
    }

    private double convertCurrency(double value, String from, String to) {
        if (currencyRates == null) return 0;
        return currencyAPI.convert(value, from, to, currencyRates);
    }

    private void swapUnits() {
        int fromPos = fromUnitSpinner.getSelectedItemPosition();
        int toPos = toUnitSpinner.getSelectedItemPosition();

        fromUnitSpinner.setSelection(toPos);
        toUnitSpinner.setSelection(fromPos);

        String inputText = inputValue.getText().toString();
        String outputText = outputValue.getText().toString();

        inputValue.setText(outputText);
    }
}