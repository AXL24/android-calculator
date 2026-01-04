package com.example.calculator;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calculator.adapters.HistoryAdapter;
import com.example.calculator.models.Calculation;
import com.example.calculator.utils.HistoryManager;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyManager = new HistoryManager(this);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_clear_history).setOnClickListener(v -> clearHistory());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadHistory();
    }

    private void loadHistory() {
        List<Calculation> history = historyManager.getHistory();
        adapter = new HistoryAdapter(history);
        recyclerView.setAdapter(adapter);
    }

    private void clearHistory() {
        historyManager.clearHistory();
        loadHistory();
    }
}