package com.example.exercise5;
import android.util.Log;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private StringBuilder expressionBuilder;
    private StringBuilder currentNumber;
    private double operand1, operand2;
    private char operator;
    private boolean hasResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        expressionBuilder = new StringBuilder();
        currentNumber = new StringBuilder();
        hasResult = false;

        // Set OnClickListener for number buttons
        for (int i = 0; i <= 9; i++) {
            int id = getResources().getIdentifier("button_" + i, "id", getPackageName());
            Button button = findViewById(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasResult) {
                        expressionBuilder.setLength(0);
                        hasResult = false;
                    }
                    expressionBuilder.append(((Button) v).getText().toString());
                    textView.setText(expressionBuilder.toString());
                }
            });
        }

        // Set OnClickListener for operation buttons
        findViewById(R.id.button_add).setOnClickListener(operationClickListener);
        findViewById(R.id.button_subtract).setOnClickListener(operationClickListener);
        findViewById(R.id.button_multiply).setOnClickListener(operationClickListener);
        findViewById(R.id.button_divide).setOnClickListener(operationClickListener);
        findViewById(R.id.button_dot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasResult) {
                    expressionBuilder.setLength(0);
                    hasResult = false;
                }
                if (expressionBuilder.length() == 0 || !Character.isDigit(expressionBuilder.charAt(expressionBuilder.length() - 1))) {
                    expressionBuilder.append("0");
                }
                expressionBuilder.append(".");
                textView.setText(expressionBuilder.toString());
            }
        });

        // Set OnClickListener for equals button
        findViewById(R.id.button_equals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressionBuilder.length() > 0 && Character.isDigit(expressionBuilder.charAt(expressionBuilder.length() - 1))) {
                    evaluateExpression();
                }
            }
        });

        // Set OnClickListener for reset button
        findViewById(R.id.button_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expressionBuilder.setLength(0);
                textView.setText("");
                hasResult = false;
            }
        });
    }

    private View.OnClickListener operationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (expressionBuilder.length() > 0 && Character.isDigit(expressionBuilder.charAt(expressionBuilder.length() - 1))) {
                expressionBuilder.append(((Button) v).getText().toString());
                textView.setText(expressionBuilder.toString());
                operator = ((Button) v).getText().toString().charAt(0);
            }
        }
    };


    private void evaluateExpression() {
        Log.d("Calculator", "Evaluating expression: " + expressionBuilder.toString());
        operand2 = Double.parseDouble(expressionBuilder.toString().substring(expressionBuilder.lastIndexOf(String.valueOf(operator)) + 1));
        double result = calculateResult();
        expressionBuilder.append(" = ").append(result);
        textView.setText(expressionBuilder.toString());
        hasResult = true;
    }


    private double calculateResult() {
        Log.d("Calculator", "operand1: " + operand1 + ", operand2: " + operand2 + ", operator: " + operator);
        operand1 = Double.parseDouble(expressionBuilder.toString().substring(0, expressionBuilder.indexOf(String.valueOf(operator))));
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0)
                    throw new ArithmeticException("Cannot divide by zero");
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
    }

}
