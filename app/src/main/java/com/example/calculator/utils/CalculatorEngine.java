package com.example.calculator.utils;

import java.text.DecimalFormat;
import java.util.Stack;

public class CalculatorEngine {
    private String currentExpression = "";
    private String previousExpression = "";
    private String result = "";

    private static final DecimalFormat df = new DecimalFormat("#.##########");

    public void addInput(String input) {
        if (result != null && !result.isEmpty() && isOperator(input)) {
            currentExpression = result + input;
            result = "";
        } else if (result != null && !result.isEmpty() && !isOperator(input)) {
            currentExpression = input;
            result = "";
        } else {
            currentExpression += input;
        }
    }

    public void deleteLastChar() {
        if (currentExpression.length() > 0) {
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
        }
    }

    public void clear() {
        currentExpression = "";
        previousExpression = "";
        result = "";
    }

    public String calculate(boolean isRadian) {
        try {
            previousExpression = currentExpression;
            result = evaluateExpression(currentExpression, isRadian);
            currentExpression = result;
            return result;
        } catch (Exception e) {
            result = "Error";
            return null;
        }
    }

    public String getPreview(boolean isRadian) {
        if (currentExpression.isEmpty() || currentExpression.equals(result)) {
            return "";
        }
        try {
            return evaluateExpression(currentExpression, isRadian);
        } catch (Exception e) {
            return "";
        }
    }

    private String evaluateExpression(String expression, boolean isRadian) {
        if (expression.isEmpty()) return "0";

        // Replace special symbols
        expression = expression.replace("×", "*")
                .replace("÷", "/")
                .replace("π", String.valueOf(Math.PI))
                .replace("e", String.valueOf(Math.E));

        // Handle functions
        expression = handleFunctions(expression, isRadian);

        // Evaluate
        double result = evaluate(expression);
        return df.format(result);
    }

    private String handleFunctions(String expr, boolean isRadian) {
        // Handle sqrt
        while (expr.contains("√(")) {
            int start = expr.indexOf("√(");
            int end = findClosingParenthesis(expr, start + 1);
            String inner = expr.substring(start + 2, end);
            double val = Double.parseDouble(evaluate(inner) + "");
            expr = expr.substring(0, start) + Math.sqrt(val) + expr.substring(end + 1);
        }

        // Handle sin
        while (expr.contains("sin(")) {
            int start = expr.indexOf("sin(");
            int end = findClosingParenthesis(expr, start + 3);
            String inner = expr.substring(start + 4, end);
            double val = Double.parseDouble(evaluate(inner) + "");
            if (!isRadian) val = Math.toRadians(val);
            expr = expr.substring(0, start) + Math.sin(val) + expr.substring(end + 1);
        }

        // Handle cos
        while (expr.contains("cos(")) {
            int start = expr.indexOf("cos(");
            int end = findClosingParenthesis(expr, start + 3);
            String inner = expr.substring(start + 4, end);
            double val = Double.parseDouble(evaluate(inner) + "");
            if (!isRadian) val = Math.toRadians(val);
            expr = expr.substring(0, start) + Math.cos(val) + expr.substring(end + 1);
        }

        // Handle tan
        while (expr.contains("tan(")) {
            int start = expr.indexOf("tan(");
            int end = findClosingParenthesis(expr, start + 3);
            String inner = expr.substring(start + 4, end);
            double val = Double.parseDouble(evaluate(inner) + "");
            if (!isRadian) val = Math.toRadians(val);
            expr = expr.substring(0, start) + Math.tan(val) + expr.substring(end + 1);
        }

        // Handle log
        while (expr.contains("log(")) {
            int start = expr.indexOf("log(");
            int end = findClosingParenthesis(expr, start + 3);
            String inner = expr.substring(start + 4, end);
            double val = Double.parseDouble(evaluate(inner) + "");
            expr = expr.substring(0, start) + Math.log10(val) + expr.substring(end + 1);
        }

        // Handle ln
        while (expr.contains("ln(")) {
            int start = expr.indexOf("ln(");
            int end = findClosingParenthesis(expr, start + 2);
            String inner = expr.substring(start + 3, end);
            double val = Double.parseDouble(evaluate(inner) + "");
            expr = expr.substring(0, start) + Math.log(val) + expr.substring(end + 1);
        }

        return expr;
    }

    private int findClosingParenthesis(String expr, int openPos) {
        int count = 1;
        for (int i = openPos + 1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') count++;
            if (expr.charAt(i) == ')') {
                count--;
                if (count == 0) return i;
            }
        }
        return expr.length() - 1;
    }

    private double evaluate(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());
                if (eat('!')) {
                    x = factorial((int)x);
                }

                return x;
            }
        }.parse();
    }

    private double factorial(int n) {
        if (n < 0) throw new RuntimeException("Factorial of negative number");
        if (n == 0 || n == 1) return 1;
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private boolean isOperator(String str) {
        return str.equals("+") || str.equals("-") || str.equals("×") ||
                str.equals("÷") || str.equals("*") || str.equals("/");
    }

    public String getCurrentExpression() {
        return currentExpression.isEmpty() ? "0" : currentExpression;
    }

    public String getPreviousExpression() {
        return previousExpression;
    }
}