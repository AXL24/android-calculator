package com.example.calculator.models;

public class Calculation {
    private String previous;
    private String expression;
    private String result;
    private long timestamp;

    public Calculation(String previous, String expression, String result) {
        this.previous = previous;
        this.expression = expression;
        this.result = result;
        this.timestamp = System.currentTimeMillis();
    }

    public String getPrevious() { return previous; }
    public String getExpression() { return expression; }
    public String getResult() { return result; }
    public long getTimestamp() { return timestamp; }
}