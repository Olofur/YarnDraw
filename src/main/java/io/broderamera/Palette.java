package io.broderamera;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.util.HashMap;

public class Palette extends JPanel{
    private static HashMap<Color, ColorSymbol> colorSymbols;
    private static JPanel palettePanel;
    private static JTextField colorField;

    private static Color activeColor;
    private static BufferedImage activeSymbol;

    private static Color backgroundColor;
    private static Color borderColor;

    private SvgStack stack;
    private ImageManager manager;

    //private ArrayList<ColorSymbol> colorSymbols;

    public Palette() {
        backgroundColor = Color.WHITE;
        borderColor = Color.BLACK;
        
        setPreferredSize(new Dimension(300, 600));

        // Subpanel of all added colors
        palettePanel = new JPanel();
        palettePanel.setLayout(new GridLayout(0, 3));
        palettePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                for (int i = 0; i < palettePanel.getComponentCount(); i++) {
                    ColorPanel panel = (ColorPanel) palettePanel.getComponent(i);
                    if (panel.getBounds().contains(x, y)) {
                        setActiveColor(panel.getColor());
                        setActiveSymbol(panel.getSymbol());
                    }
                }
            }
        });

        // List of all added colors and symbols
        colorSymbols = new HashMap<>();

        // Text field for managing colors
        colorField = new JTextField(25);
        add(colorField);

        // Initialize add and remove buttons
        add(addButton());
        add(removeButton());

        // Initialize palette
        add(palettePanel);

        // Initialize stack
        stack = new SvgStack();
        manager = new ImageManager();
        initializeStack();

        // Add background color to palette
        colorSymbols.put(backgroundColor, new ColorSymbol(null, "None"));
        updatePalette();
    }

    public JButton addButton() {
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
                        BufferedImage symbol = manager.getImage(item);
                        colorSymbols.put(color, new ColorSymbol(symbol, item));
                        updatePalette();
                        }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid color code");
                }
                System.out.println("Number of colors: " + colorSymbols.size());
            }
        });
        return Button;
    }

    public JButton removeButton() {
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
                        String symbolName = colorSymbols.get(color).symbolName();
                        stack.addItem(symbolName);
                        colorSymbols.remove(color);
                        updatePalette();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid color code");
                    }
                }
                System.out.println("Number of colors: " + colorSymbols.size());
            }
        });
        return Button;
    }

    public static ColorPanel colorButton(Color color) {
        ColorPanel Button = new ColorPanel();
        if (ControlPanel.showColor()) {
            Button.setColor(color);
        } else {
            Button.setColor(backgroundColor);
        }
        if (ControlPanel.showSymbol()) {
            Button.setSymbol(colorSymbols.get(color).symbol());
        }
        Button.setPreferredSize(new Dimension(100, 50));
        return Button;
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

    public static void updatePalette() {
        // Do not remuve all, instead see what has changed and update only that
        palettePanel.removeAll();
        for (Color color : colorSymbols.keySet()) {
            ColorPanel button = colorButton(color);
            button.setBorder(BorderFactory.createLineBorder(getBorderColor()));
            palettePanel.add(button);
        }
        palettePanel.revalidate();
        palettePanel.repaint();
    }

    public static void setActiveColor(Color color) {
        activeColor = color;
        System.out.println("Active color changed to: " + activeColor);
    }

    public static Color getActiveColor() {
        return activeColor;
    }

    public static void setActiveSymbol(BufferedImage symbol) {
        activeSymbol = symbol;
        System.out.println("Active symbol changed");
    }

    public static BufferedImage getActiveSymbol() {
        return activeSymbol;
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