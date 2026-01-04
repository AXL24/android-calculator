package com.example.calculator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calculator.R;
import com.example.calculator.models.Calculation;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Calculation> calculations;

    public HistoryAdapter(List<Calculation> calculations) {
        this.calculations = calculations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calculation calc = calculations.get(position);

        // Show previous expression if available
        if (calc.getPrevious() != null && !calc.getPrevious().isEmpty()) {
            holder.previous.setText(calc.getPrevious());
            holder.previous.setVisibility(View.VISIBLE);
        } else {
            holder.previous.setVisibility(View.GONE);
        }

        holder.expression.setText(calc.getExpression());
        holder.result.setText("= " + calc.getResult());
    }

    @Override
    public int getItemCount() {
        return calculations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView previous;
        TextView expression;
        TextView result;

        ViewHolder(View itemView) {
            super(itemView);
            previous = itemView.findViewById(R.id.text_previous);
            expression = itemView.findViewById(R.id.text_expression);
            result = itemView.findViewById(R.id.text_result);
        }
    }
}