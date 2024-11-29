package io.broderamera;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.HashMap;

public class Palette extends JPanel{
    private JPanel palettePanel;
    private HashMap<Color, SymbolString> colorSymbols;
    private static JTextField colorField;

    private static Color activeColor;
    private static Color backgroundColor;
    private static Color borderColor;

    public SvgStack stack;
    public ImageManager manager;

    //private ArrayList<ColorSymbol> colorSymbols;

    public Palette() {
        backgroundColor = Color.WHITE;
        borderColor = Color.BLACK;
        
        setPreferredSize(new Dimension(300, 600));

        // Subpanel of all added colors
        palettePanel = new JPanel();
        palettePanel.setLayout(new GridLayout(0, 3));

        // List of all added colors and symbols
        colorSymbols = new HashMap<>();

        // Text field for managing colors
        colorField = new JTextField(25);
        add(colorField);

        // Initialize buttons
        addButton();
        removeButton();

        // Initialize stack
        stack = new SvgStack();
        manager = new ImageManager();
        initializeStack();

        // Add background color to palette
        colorSymbols.put(backgroundColor,new SymbolString(null, "None"));
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
                    if (!colorSymbols.keySet().contains(color)) {
                        String item = stack.popItem();
                        Image symbol = manager.getImage(item);
    
                        colorSymbols.put(color, new SymbolString(symbol, item));
                        updatePalette();
                        }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid color code");
                }
                System.out.println(colorSymbols.size());
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
                if (!colorSymbols.isEmpty()) {
                    try {
                        Color color = Color.decode(colorString);
                        // Add symbol back to stack
                        String symbolName = colorSymbols.get(color).getSymbolName();
                        stack.addItem(symbolName);
                        colorSymbols.remove(color);
                        updatePalette();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid color code");
                    }
                }
                System.out.println(colorSymbols.size());
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

    private void initializeStack() {
        File directory = new File("./src/main/resources");
        File[] files = directory.listFiles();
        String item;
        for (File file : files) {
            if (file.isFile()) {
                item = file.getName();
                stack.addItem(item);
            }
        }
    }

    private void updatePalette() {
        palettePanel.removeAll();
        for (Color color : colorSymbols.keySet()) {
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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Palette panel = new Palette();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}