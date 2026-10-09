package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Main screen for the calculator app.
 * On launch it personalizes the input panel with the developer's
 * last name and CWU ID, then handles all key presses.
 */
public class MainActivity extends AppCompatActivity {

    /** Developer last name shown on the initial panel. */
    private static final String NAME = "   Fulya Bilgin";

    /** Developer CWU ID shown on the initial panel. */
    private static final String CWU_ID = "50593580";

    /** The display panel where input and results are shown. */
    private TextView display;

    /** Holds the expression the user is building. */
    private final StringBuilder expr = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Initial default view: last name and CWU ID (Screenshot 1).
        display.setText(NAME + "\n" + CWU_ID);
    }

    // ---- Button handlers ----

    /** Handles digits, ".", operators, and brackets. (Piece 3) */
    public void onInput(View view) {
        String key = ((Button) view).getText().toString();
        expr.append(key);
        display.setText(expr.toString());
    }

    /** Evaluates the current expression and shows the result. (Piece 5) */
    public void onEquals(View view) {
        try {
            double result = evaluate(expr.toString());
            String text = (result == Math.floor(result) && !Double.isInfinite(result))
                    ? String.valueOf((long) result)
                    : String.valueOf(result);
            display.setText(text);
            expr.setLength(0);
            expr.append(text);
        } catch (Exception e) {
            display.setText("Error");
            expr.setLength(0);
        }
    }

    /** Deletes the most recent entry. (Piece 4 — "C" key) */
    public void onClear(View view) {
        if (expr.length() > 0) {
            expr.deleteCharAt(expr.length() - 1);
        }
        display.setText(expr.length() == 0 ? "0" : expr.toString());
    }

    /** Clears the entire panel. (Piece 4 — "AC" key) */
    public void onAllClear(View view) {
        expr.setLength(0);
        display.setText("0");
    }

    /** Evaluates +, -, *, / with decimals and parentheses. */
    private double evaluate(final String s) {
        return new Object() {
            int pos = -1, ch;
            void next() { ch = (++pos < s.length()) ? s.charAt(pos) : -1; }
            boolean eat(int c) {
                while (ch == ' ') next();
                if (ch == c) { next(); return true; }
                return false;
            }
            double parse() {
                next();
                double x = expression();
                if (pos < s.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }
            double expression() {
                double x = term();
                for (;;) {
                    if (eat('+')) x += term();
                    else if (eat('-')) x -= term();
                    else return x;
                }
            }
            double term() {
                double x = factor();
                for (;;) {
                    if (eat('*')) x *= factor();
                    else if (eat('/')) x /= factor();
                    else return x;
                }
            }
            double factor() {
                if (eat('+')) return factor();
                if (eat('-')) return -factor();
                double x;
                int start = pos;
                if (eat('(')) { x = expression(); eat(')'); }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') next();
                    x = Double.parseDouble(s.substring(start, pos));
                } else throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }
        }.parse();
    }
}