package com.example.calculator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.icu.text.DecimalFormatSymbols;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String buffer = ""; // storage of the first number for mathematical calculations
    private char operation = ' '; // mathematical operation
    private TextView textView = null; // TextView for number input
    private boolean isNewNumber = true;
    private char decimal_separator;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // find current decimal separator (comma or point)
        Locale local = Locale.getDefault();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(local);
        decimal_separator = dfs.getDecimalSeparator();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // store variable when state is changed
        outState.putString("displayed_number", textView.getText().toString());
        outState.putBoolean("isNewNumber", isNewNumber);
        outState.putString("buffer", buffer);
        outState.putChar("operation", operation);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // restore variable when state is changed
        textView.setText(savedInstanceState.getString("displayed_number", ""));
        isNewNumber = savedInstanceState.getBoolean("isNewNumber", true);
        buffer = savedInstanceState.getString("buffer", "");
        operation = savedInstanceState.getChar("operation", ' ');
    }

    @Override
    protected void onStart() {
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Orientation landscape", Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(), "Orientation landscape", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Orientation portrait", Toast.LENGTH_LONG).show();
        }
/*
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            textView.setText(String.format("%d x %d",width,height));
*/
        textView = findViewById(R.id.dispaly);

        // assign current decimal separator to the button 'buttonPoint'
        Button btn = findViewById(R.id.buttonPoint);
        btn.setText(Character.toString(decimal_separator));

        super.onStart();
    }

    public void addOperation(View v){
        /* add operation when the operation button was pressed */
        operation = ((Button)v).getText().toString().charAt(0);
        isNewNumber = true;
        buffer = (String) textView.getText();
    }

    public void addToNumber(View v){
        /* add a digit or decimal point or sign when the corresponding button was pressed */
        String currentNumber,
                pressedSymbol = ((Button)v).getText().toString();

        if (isNewNumber) {
            isNewNumber = false;
            if (pressedSymbol.charAt(0)==decimal_separator)
                currentNumber = "0"+decimal_separator;
            else
                currentNumber = pressedSymbol;
        }
        else{
            currentNumber = textView.getText().toString();
            if (pressedSymbol.charAt(0)=='±') {
                // change sign
                if (currentNumber.charAt(0) == '-')
                    currentNumber = currentNumber.substring(1);
                else
                    currentNumber = pressedSymbol + currentNumber;
            }
            else {
                // put decimal point
                if (pressedSymbol.charAt(0)==decimal_separator) {
                    if (!currentNumber.contains(((Character) decimal_separator).toString()))
                        currentNumber += decimal_separator;
                }
                else {
                    currentNumber += pressedSymbol;
                }
            }
        }
        textView.setText(currentNumber);
    }

    public void clearNumber(View v){
        /* clear buffer and textView when the button 'Clear' was pressed */
        buffer = "";
        operation = ' ';
        textView.setText("");
    }

    public boolean checkIfNumberInString(String buffer){
        try {
            float result = Float.parseFloat(buffer);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
    public float getNumberFromString(@NonNull String buffer){
        float result;

        // check string
        if(buffer.charAt(buffer.length()-1)==decimal_separator) {
            buffer += '0';
        }

        try {
            result = Float.parseFloat(buffer);
        }
        catch (Exception e){
            result = 0;
        }
        return result;
    }

    public void result(View v){
        /* perform an action when the button '=' was pressed */

        // read current number from buffer
        float result = getNumberFromString(buffer);
        // read current number from textView
        float currentNumber = getNumberFromString(textView.getText().toString());

        isNewNumber = true;

        // check if buffer is correct
        if (!checkIfNumberInString(buffer)) {
            operation = ' ';
        }

        // perform a mathematical operation on the contents of the "operation" attribute, or do nothing
        switch (operation) {
            case '+':
                result += currentNumber;
                break;
            case '-':
                result -= currentNumber;
                break;
            case '×':
                result *= currentNumber;
                break;
            case '÷':
                result /= currentNumber;
                break;
            default:
                return;
        }
        // put the result to the textView
        textView.setText(Float.toString(result));
//        textView.setText(String.format("%g", result));
    }
}