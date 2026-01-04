package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.calculator.utils.CalculatorEngine;
import com.example.calculator.utils.HistoryManager;

public class MainActivity extends AppCompatActivity {

    private TextView displayPrevious;
    private TextView displayCurrent;
    private TextView displayPreview;
    private View scientificPanel;
    private boolean isScientificMode = false;
    private boolean isRadianMode = true;

    private CalculatorEngine calculatorEngine;
    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculatorEngine = new CalculatorEngine();
        historyManager = new HistoryManager(this);

        initViews();
        setupClickListeners();
        updateDisplay();
    }

    private void initViews() {
        displayPrevious = findViewById(R.id.display_previous);
        displayCurrent = findViewById(R.id.display_current);
        displayPreview = findViewById(R.id.display_preview);
        scientificPanel = findViewById(R.id.scientific_panel);
    }

    private void setupClickListeners() {
        // Menu buttons
        findViewById(R.id.btn_menu).setOnClickListener(v -> showMenu());
        findViewById(R.id.btn_scientific_toggle).setOnClickListener(v -> toggleScientificMode());
        findViewById(R.id.btn_history).setOnClickListener(v -> openHistory());

        // Number buttons
        setNumberButtonListener(R.id.btn_0, "0");
        setNumberButtonListener(R.id.btn_00, "00");
        setNumberButtonListener(R.id.btn_1, "1");
        setNumberButtonListener(R.id.btn_2, "2");
        setNumberButtonListener(R.id.btn_3, "3");
        setNumberButtonListener(R.id.btn_4, "4");
        setNumberButtonListener(R.id.btn_5, "5");
        setNumberButtonListener(R.id.btn_6, "6");
        setNumberButtonListener(R.id.btn_7, "7");
        setNumberButtonListener(R.id.btn_8, "8");
        setNumberButtonListener(R.id.btn_9, "9");
        setNumberButtonListener(R.id.btn_dot, ".");

        // Operation buttons
        setOperationButtonListener(R.id.btn_add, "+");
        setOperationButtonListener(R.id.btn_subtract, "-");
        setOperationButtonListener(R.id.btn_multiply, "×");
        setOperationButtonListener(R.id.btn_divide, "÷");
        setOperationButtonListener(R.id.btn_percent, "%");

        // Function buttons
        findViewById(R.id.btn_clear).setOnClickListener(v -> clear());
        findViewById(R.id.btn_delete).setOnClickListener(v -> delete());
        findViewById(R.id.btn_equals).setOnClickListener(v -> calculate());
        findViewById(R.id.btn_parenthesis_open).setOnClickListener(v -> addInput("("));
        findViewById(R.id.btn_parenthesis_close).setOnClickListener(v -> addInput(")"));

        // Scientific buttons
        setupScientificButtons();
    }

    private void setupScientificButtons() {
        findViewById(R.id.btn_sin).setOnClickListener(v -> addFunction("sin("));
        findViewById(R.id.btn_cos).setOnClickListener(v -> addFunction("cos("));
        findViewById(R.id.btn_tan).setOnClickListener(v -> addFunction("tan("));
        findViewById(R.id.btn_log).setOnClickListener(v -> addFunction("log("));
        findViewById(R.id.btn_ln).setOnClickListener(v -> addFunction("ln("));
        findViewById(R.id.btn_sqrt).setOnClickListener(v -> addFunction("√("));
        findViewById(R.id.btn_power).setOnClickListener(v -> addInput("^"));
        findViewById(R.id.btn_factorial).setOnClickListener(v -> addInput("!"));
        findViewById(R.id.btn_pi).setOnClickListener(v -> addInput("π"));
        findViewById(R.id.btn_e).setOnClickListener(v -> addInput("e"));
        findViewById(R.id.btn_inv).setOnClickListener(v -> addFunction("inv"));

        findViewById(R.id.btn_rad_deg).setOnClickListener(v -> toggleAngleMode());
    }

    private void setNumberButtonListener(int id, String value) {
        findViewById(id).setOnClickListener(v -> addInput(value));
    }

    private void setOperationButtonListener(int id, String operation) {
        findViewById(id).setOnClickListener(v -> addInput(operation));
    }

    private void addInput(String input) {
        calculatorEngine.addInput(input);
        updateDisplay();
    }

    private void addFunction(String function) {
        if (function.equals("inv")) {
            calculatorEngine.addInput("1/");
        } else {
            calculatorEngine.addInput(function);
        }
        updateDisplay();
    }

    private void delete() {
        calculatorEngine.deleteLastChar();
        updateDisplay();
    }

    private void clear() {
        calculatorEngine.clear();
        updateDisplay();
    }

    private void calculate() {
        String result = calculatorEngine.calculate(isRadianMode);
        if (result != null) {
            historyManager.addToHistory(
                    calculatorEngine.getPreviousExpression(),
                    calculatorEngine.getCurrentExpression(),
                    result
            );
        }
        updateDisplay();
    }

    private void updateDisplay() {
        displayPrevious.setText(calculatorEngine.getPreviousExpression());
        displayCurrent.setText(calculatorEngine.getCurrentExpression());

        String preview = calculatorEngine.getPreview(isRadianMode);
        if (preview != null && !preview.isEmpty()) {
            displayPreview.setText("= " + preview);
            displayPreview.setVisibility(View.VISIBLE);
        } else {
            displayPreview.setVisibility(View.GONE);
        }

        // Update angle mode button
        TextView radDegBtn = findViewById(R.id.btn_rad_deg);
        radDegBtn.setText(isRadianMode ? "rad" : "deg");
    }

    private void toggleScientificMode() {
        isScientificMode = !isScientificMode;
        scientificPanel.setVisibility(isScientificMode ? View.VISIBLE : View.GONE);

        // Show toast notification
        String message = isScientificMode ? "Scientific mode enabled - Swipe to see more" : "Standard mode enabled";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Scroll to show scientific panel when enabled
        if (isScientificMode) {
            HorizontalScrollView scrollView = findViewById(R.id.keyboard_container);
            scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
        }
    }

    private void toggleAngleMode() {
        isRadianMode = !isRadianMode;
        updateDisplay();
    }

    private void showMenu() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Menu");
        String[] items = {"Conversion", "History", "Clear History"};
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    openConversion();
                    break;
                case 1:
                    openHistory();
                    break;
                case 2:
                    historyManager.clearHistory();
                    break;
            }
        });
        builder.show();
    }

    private void openConversion() {
        Intent intent = new Intent(this, ConversionActivity.class);
        startActivity(intent);
    }

    private void openHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}