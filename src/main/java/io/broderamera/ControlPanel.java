package io.broderamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {
    private static boolean color;
    private static boolean symbol;
    
    public ControlPanel() {
        color = true;
        symbol = false;

        JButton buttonA = new JButton("Colors");
        JButton buttonB = new JButton("Symbols");

        Palette.updatePalette();

        buttonA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (color) {
                    buttonA.setBackground(Color.WHITE);
                } else {
                    buttonA.setBackground(Color.LIGHT_GRAY);
                }
                setColor();
                Palette.updatePalette();
                Palette.updateActive();
                ClickableGridPanel.updateGrid();
                System.out.println("Color representation is: " + color);
            }
        });

        buttonB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (symbol) {
                    buttonB.setBackground(Color.WHITE);
                } else {
                    buttonB.setBackground(Color.LIGHT_GRAY);
                }
                setSymbol();
                Palette.updatePalette();
                Palette.updateActive();
                ClickableGridPanel.updateGrid();
                System.out.println("Symbol representation is: " + symbol);
            }
        });

        buttonA.setBackground(Color.LIGHT_GRAY);
        buttonB.setBackground(Color.WHITE);

        this.add(buttonA);
        this.add(buttonB);
    }

    public static void setColor() {
        ControlPanel.color = !ControlPanel.color;
    }

    public static boolean showColor() {
        return color;
    }
    
    public static void setSymbol() {
        ControlPanel.symbol = !ControlPanel.symbol;
    }

    public static boolean showSymbol() {
        return symbol;
    }

    public void main(String[] args) {
        JFrame frame = new JFrame();
        ControlPanel panel = new ControlPanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
