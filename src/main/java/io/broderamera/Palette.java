package io.broderamera;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

public class Palette extends JPanel{
    private JPanel palettePanel;
    private ArrayList<Color> colors;
    private static JTextField colorField;
    private static Color activeColor;
    private static Color backgroundColor;
    private static Color borderColor;

    //private ArrayList<ColorSymbol> colorSymbols;

    public Palette() {
        backgroundColor = Color.WHITE;
        borderColor = Color.BLACK;
        
        setPreferredSize(new Dimension(300, 600));

        // Subpanel of all added colors
        palettePanel = new JPanel();
        palettePanel.setLayout(new GridLayout(0, 3));

        // List of all added colors
        colors = new ArrayList<>();

        // Text field for managing colors
        colorField = new JTextField(25);
        add(colorField);

        // Initialize buttons
        addButton();
        removeButton();

        // Add background color to palette
        colors.add(backgroundColor);
        colors.add(Color.RED);
        updatePalette();
    }

    public void addButton() {
        JButton Button = new JButton("Add Color");
        Button.setPreferredSize(new Dimension(136, 20));
        Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colorString = colorField.getText();
                try {
                    Color color = Color.decode(colorString);
                    if (!colors.contains(color)) {
                        colors.add(color);
                        updatePalette();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid color code");
                }
                System.out.println(colors);
            }
        });
        add(Button);
    }

    public void removeButton() {
        JButton Button = new JButton("Remove Color");
        Button.setPreferredSize(new Dimension(136, 20));
        Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colorString = colorField.getText();
                if (!colors.isEmpty()) {
                    try {
                        Color color = Color.decode(colorString);
                        colors.remove(color);
                        updatePalette();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid color code");
                    }
                }
                System.out.println(colors);
            }
        });
        add(Button);
    }

    public void colorButton(Color color) {
        JButton Button = new JButton();
        Button.setBackground(color);
        Button.setPreferredSize(new Dimension(100, 50));
        Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setActiveColor(color);
            }
        });
        palettePanel.add(Button);
    }

    private void updatePalette() {
        palettePanel.removeAll();
        for (Color color : colors) {
            colorButton(color);
        }
        add(palettePanel);
        palettePanel.revalidate();
        palettePanel.repaint();
    }

    public static void setActiveColor(Color color) {
        activeColor = color;
    }

    public static Color getActiveColor() {
        return activeColor;
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Color getBorderColor() {
        return borderColor;
    }

    public static void insertText(String text) {
        colorField.setText(text);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Palette panel = new Palette();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}