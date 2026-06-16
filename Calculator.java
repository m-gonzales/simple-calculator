import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Calculator {
    int boardWidth = 360;
    int boardHeight = 600;

    Color customLightGray = new Color(212, 212, 210);
    Color customDarkGray = new Color(80, 80, 80);
    Color customBlack = new Color(28, 28, 28);
    Color customOrange = new Color(255, 149, 0);
    Color customBlue = new Color(0, 122, 255);

    String[] buttonValues = {
        "AC", "DEL", "(", ")", 
        "+/-", "%", "√", "÷",
        "7", "8", "9", "×", 
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "=", "HIS"
    };

    String[] rightSymbols = {"÷", "×", "-", "+", "="};
    String[] topSymbols = {"AC", "DEL", "+/-", "%"};

    JFrame frame = new JFrame("Calculator");
    JLabel displayLabel = new JLabel();
    JPanel displayPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

    DefaultListModel<String> historyList = new DefaultListModel<>();

    ScriptEngine engine;

    Calculator() {

        engine = new ScriptEngineManager().getEngineByName("JavaScript");

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        displayLabel.setBackground(customBlue);
        displayLabel.setForeground(Color.white);
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setText("0");
        displayLabel.setOpaque(true);

        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(displayLabel);
        frame.add(displayPanel, BorderLayout.NORTH);

        buttonsPanel.setLayout(new GridLayout(6, 4));
        buttonsPanel.setBackground(customBlack);
        frame.add(buttonsPanel);

        for (String buttonValue : buttonValues) {
            JButton button = new JButton(buttonValue);
            button.setFont(new Font("Arial", Font.PLAIN, 30));
            button.setFocusable(false);
            button.setBorder(new LineBorder(customBlack));

            if (Arrays.asList(topSymbols).contains(buttonValue)) {
                button.setBackground(customLightGray);
                button.setForeground(customBlack);
            }
            else if (Arrays.asList(rightSymbols).contains(buttonValue) || buttonValue.equals("HIS")) {
                button.setBackground(customOrange);
                button.setForeground(Color.white);
            }
            else {
                button.setBackground(customDarkGray);
                button.setForeground(Color.white);
            }

            buttonsPanel.add(button);

            button.addActionListener(e -> handleButton(buttonValue));
        }

        frame.setVisible(true);
    }

    void handleButton(String buttonValue) {

        String current = displayLabel.getText();

        // HISTORY WINDOW
        if (buttonValue.equals("HIS")) {
            openHistoryWindow();
            return;
        }

        // CLEAR
        if (buttonValue.equals("AC")) {
            displayLabel.setText("0");
            return;
        }

        // DELETE
        if (buttonValue.equals("DEL")) {
            if (current.length() > 1) {
                displayLabel.setText(current.substring(0, current.length() - 1));
            } else {
                displayLabel.setText("0");
            }
            return;
        }

        // SIGN CHANGE
        if (buttonValue.equals("+/-")) {
            try {
                double num = Double.parseDouble(current);
                num *= -1;
                displayLabel.setText(removeZeroDecimal(num));
            } catch (Exception ex) {}
            return;
        }

        // PERCENT
        if (buttonValue.equals("%")) {
            try {
                double num = Double.parseDouble(current);
                num /= 100;
                displayLabel.setText(removeZeroDecimal(num));
            } catch (Exception ex) {}
            return;
        }

        // SQUARE ROOT
        if (buttonValue.equals("√")) {
            try {
                double num = Double.parseDouble(current);
                num = Math.sqrt(num);
                displayLabel.setText(removeZeroDecimal(num));
            } catch (Exception ex) {}
            return;
        }

        // EQUALS → Evaluate full expression
        if (buttonValue.equals("=")) {
            try {
                String expression = current;

                // Replace symbols for JS engine
                expression = expression.replace("×", "*");
                expression = expression.replace("÷", "/");

                Object result = engine.eval(expression);

                String resultStr = removeZeroDecimal(Double.parseDouble(result.toString()));
                displayLabel.setText(resultStr);

                historyList.addElement(expression + " = " + resultStr);

            } catch (Exception ex) {
                displayLabel.setText("Error");
            }
            return;
        }

        // Append numbers, operators, parentheses
        if (current.equals("0") && !".".equals(buttonValue)) {
            displayLabel.setText(buttonValue);
        } else {
            displayLabel.setText(current + buttonValue);
        }
    }

    // HISTORY WINDOW
    void openHistoryWindow() {
    JFrame historyFrame = new JFrame("History");
    historyFrame.setSize(320, 450);
    historyFrame.setLocationRelativeTo(null);
    historyFrame.setLayout(new BorderLayout());

    // Header panel
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(40, 40, 40));

    JLabel title = new JLabel("Calculation History");
    title.setForeground(Color.WHITE);
    title.setFont(new Font("Arial", Font.BOLD, 20));
    title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JButton clearBtn = new JButton("Clear");
    clearBtn.setFont(new Font("Arial", Font.PLAIN, 16));
    clearBtn.setBackground(new Color(200, 50, 50));
    clearBtn.setForeground(Color.WHITE);
    clearBtn.setFocusPainted(false);
    clearBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    clearBtn.addActionListener(e -> {
        historyList.clear();
    });

    header.add(title, BorderLayout.WEST);
    header.add(clearBtn, BorderLayout.EAST);

    // History list
    JList<String> list = new JList<>(historyList);
    list.setFont(new Font("Consolas", Font.PLAIN, 18));
    list.setBackground(new Color(230, 230, 230));

    JScrollPane scroll = new JScrollPane(list);

    historyFrame.add(header, BorderLayout.NORTH);
    historyFrame.add(scroll, BorderLayout.CENTER);

    historyFrame.setVisible(true);
}


    String removeZeroDecimal(double num) {
        if (num % 1 == 0) {
            return Integer.toString((int) num);
        }
        return Double.toString(num);
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
