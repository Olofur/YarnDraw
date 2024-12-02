package io.broderamera;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.util.HashMap;
import java.util.Map;

public class Palette extends JPanel{
    private static HashMap<Integer, ColorSymbol> biglyMap;

    private static JPanel palettePanel;

    private static int activeKey;
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
                        setActive(panel.getKey(), panel.getColor(), panel.getSymbol());
                        String hexColor = String.format("#%06X", (0xFFFFFF & panel.getColor().getRGB()));
                        insertText(hexColor);
                    }
                }
            }
        });

        // Hashmap of all added colors and symbols
        biglyMap = new HashMap<Integer, ColorSymbol>();

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
        ColorSymbol basePanel = new ColorSymbol(backgroundColor, null, "None");
        biglyMap.put(basePanel.getKey(), basePanel);
        updatePalette();
    }

    public JButton addButton() {
        JButton Button = new JButton("Add Color");
        Button.setPreferredSize(new Dimension(136, 20));
        Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colorString = ColorWheel.getColorFieldText();
                try {
                    Color color = Color.decode(colorString);
                    if (getKeyForColor(color) == -1) {
                        String item = stack.popItem();
                        BufferedImage symbol = manager.getImage(item);
                        ColorSymbol cs = new ColorSymbol(color, symbol, item);
                        biglyMap.put(cs.getKey(), cs);
                        updatePalette();
                        }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid color code");
                }
                System.out.println("Number of colors: " + biglyMap.size());
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
                String colorString = ColorWheel.getColorFieldText();
                if (!biglyMap.isEmpty()) {
                    try {
                        Color color = Color.decode(colorString);
                        int key = getKeyForColor(color);
                        if (key == 1) {
                            System.out.println("Cannot remove base color");
                            return;
                        }
                        // Add symbol back to stack
                        String symbolName = biglyMap.get(key).symbolName();
                        stack.addItem(symbolName);
                        biglyMap.remove(key);
                        for (Map.Entry<Integer, ColorSymbol> entry : biglyMap.entrySet()) {
                            if (entry.getValue().color().equals(color)) {
                                biglyMap.remove(entry.getKey());
                                break;
                            }
                        }
                        updatePalette();
                        // in case the active color is deleted
                        updateActive();
                        ClickableGridPanel.updateGrid();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid color code");
                    }
                }
                System.out.println("Number of colors: " + biglyMap.size());
            }
        });
        return Button;
    }

    public static ColorPanel colorButton(Color color) {
        ColorPanel Button = new ColorPanel();
        int key = getKeyForColor(color);
        Button.setKey(key);
        if (ControlPanel.showColor()) {
            Button.setColor(color);
        } else {
            Button.setColor(backgroundColor);
        }
        if (ControlPanel.showSymbol()) {
            Button.setSymbol(biglyMap.get(key).symbol());
        } else {
            Button.setSymbol(null);
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
        for (int key : biglyMap.keySet()) {
            ColorPanel button = colorButton(biglyMap.get(key).color());
            button.setBorder(BorderFactory.createLineBorder(getBorderColor()));
            palettePanel.add(button);
        }
        palettePanel.revalidate();
        palettePanel.repaint();
    }

    public static void updateActive() {
        int key = getActiveKey();
        if (!biglyMap.keySet().contains(key)) {
            key = 1;
        }
        Color keyColor = biglyMap.get(key).color();
        BufferedImage keySymbol = Palette.biglyMap.get(key).symbol();
        if (ControlPanel.showColor()) {
            setActiveColor(keyColor);
        } else {
            setActiveColor(backgroundColor);
        }
        System.out.println("Active color changed to: " + activeColor);
        if (ControlPanel.showSymbol()) {
            setActiveSymbol(keySymbol);
        } else {
            setActiveSymbol(null);
        }
        System.out.println("Active symbol changed to: " + activeSymbol);
    }

    public static void setActive(int key, Color color, BufferedImage symbol) {
        activeKey = key;
        System.out.println("Active key changed to: " + activeKey);        setActiveColor(color);
        activeColor = color;
        System.out.println("Active color changed to: " + activeColor);
        activeSymbol = symbol;
        System.out.println("Active symbol changed");
    }


    public static int getActiveKey() {
        return activeKey;
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

    public static int getKeyForColor(Color color) {
        for (Map.Entry<Integer, ColorSymbol> entry : biglyMap.entrySet()) {
            if (entry.getValue().color().equals(color)) {
                return entry.getKey();
            }
        }
        System.out.println("No key found for color: " + color + ", defaulting to -1");
        return -1;
    }

    public static HashMap<Integer, ColorSymbol> getBiglyMap() {
        return biglyMap;
    }

    public static void insertText(String text) {
        ColorWheel.setColorFieldText(text);
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