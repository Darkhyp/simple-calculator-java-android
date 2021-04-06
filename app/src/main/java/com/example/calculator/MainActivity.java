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

//public class MainActivity extends Activity {
public class MainActivity extends AppCompatActivity {
    private String buffer = ""; // storage of the first number for mathematical calculations
    private char operation = ' '; // mathematical operation
    private TextView display = null; // TextView for number input
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
        outState.putString("displayed_number", display.getText().toString());
        outState.putBoolean("isNewNumber", isNewNumber);
        outState.putString("buffer", buffer);
        outState.putChar("operation", operation);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // restore variable when state is changed
        display.setText(savedInstanceState.getString("displayed_number", ""));
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
            display.setText(String.format("%d x %d",width,height));
*/
        display = findViewById(R.id.dispaly);

        // assign current decimal separator to the button 'buttonPoint'
        Button btn = findViewById(R.id.buttonPoint);
        btn.setText(Character.toString(decimal_separator));

        super.onStart();

    }


    public void addOperation(View v){
        /* add operation when the operation button was pressed */
        operation = ((Button)v).getText().toString().charAt(0);
        isNewNumber = true;
        buffer = (String) display.getText();
    }

    public void changeSign(View v){
        String currentNumber = display.getText().toString();

        // change sign
        if (currentNumber.charAt(0) == '-') {
            currentNumber = currentNumber.substring(1);
        }
        else {
            if (currentNumber.length() != 0) {
                currentNumber = '-' + currentNumber;
            }
        }
        display.setText(currentNumber);
    }

    public void addToNumber(View v){
        /* add a digit or decimal point or sign when the corresponding button was pressed */
        String currentNumber;
        String pressedSymbol = ((Button)v).getText().toString();

        if (isNewNumber) {
            // if new number
            isNewNumber = false;
            if (pressedSymbol.charAt(0)==decimal_separator)
                currentNumber = "0"+decimal_separator;
            else
                currentNumber = pressedSymbol;
        }
        else{
            // add symbol to the number string
            currentNumber = display.getText().toString();
            // put decimal point
            if (pressedSymbol.charAt(0)==decimal_separator) {
                if (!currentNumber.contains(((Character) decimal_separator).toString()))
                    currentNumber += decimal_separator;
            }
            else {
                currentNumber += pressedSymbol;
            }
        }
        display.setText(currentNumber);
    }

    public void clearNumber(View v){
        /* clear buffer and display when the button 'Clear' was pressed */
        buffer = "";
        operation = ' ';
        display.setText("");
    }

    public String removeNonUsefullSymbols(@NonNull String buffer, boolean isBothSides){
        int pos, ind_end;
        // remove starting symbols
        if (isBothSides)
            while (true){
                pos = buffer.indexOf(decimal_separator);
                // remove decimal point at the end or zeros after decimal point
                if(pos<0 & buffer.charAt(0)=='0' & buffer.length()>1) {
                    // remove final symbol
                    buffer = buffer.substring(1);
                    continue;
                }
                break;
            }

        // remove final symbols
        while (true){
            // remove decimal point at the end or zeros after decimal point
            pos = buffer.indexOf(decimal_separator);
            if(pos>=0){
                // usefulness zeros at the end if decimal point is present
                ind_end = buffer.length()-1;
                if(buffer.charAt(ind_end)=='0') {
                    // remove final symbol
                    buffer = buffer.substring(0, ind_end);
                }
                ind_end = buffer.length()-1;
                if(buffer.charAt(ind_end)==decimal_separator) {
                    // remove final symbol
                    buffer = buffer.substring(0, ind_end);
                }
                continue;
            }
            break;
        }
        return buffer;
    }

    public void result(View v){
        /* perform an action when the button '=' was pressed */

        // read current number from buffer
        Float result = Float.parseFloat(removeNonUsefullSymbols(buffer, true));
        // read current number from display
        Float currentNumber = Float.parseFloat(removeNonUsefullSymbols(display.getText().toString(),true));

        isNewNumber = true;

        // check if buffer is correct
        if (result == null) {
            operation = ' ';
        }

        // perform a mathematical operation on the contents of the "operation" attribute, or do nothing
        try {
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
        }
        catch (Exception e){
            // pass
        }
        // put the result to the display
        display.setText(removeNonUsefullSymbols(result.toString(), false));
//        display.setText(String.format("%g", result));
    }
}