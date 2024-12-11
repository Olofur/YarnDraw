package io.broderamera;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Represents the palette that is used to select active colors and color symbol.
 * 
 * @author Olofur
 */
public class Palette extends JPanel{
    private static int activeKey;
    private static Color activeColor;
    private static BufferedImage activeSymbol;
    private static int activeClickableGridPanel;

    private static Color backgroundColor = Color.WHITE;
    private static Color borderColor = Color.BLACK;

    private static SvgStack stack;
    private static ImageManager manager;
    private static HashMap<Integer, ColorSymbol> colorSymbolMap;

    private static JPanel palettePanel;

    /**
     * Default constructor for the Palette class.
     */
    public Palette() {
        this(200);
    }

    /**
     * Constructor for the Palette class with a specified width.
     * 
     * @param width
     */
    public Palette(int width) {
        activeClickableGridPanel = 0;

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
                        setActive(panel.key());
                        String hexColor = getHexFromColor(panel.color());
                        insertTextField(hexColor);
                        updateBorderHighlight();
                    }
                }
            }
        });

        // Initialize add, remove and color buttons
        add(addButton());
        add(removeButton());
        add(palettePanel);
        setPreferredSize(new Dimension(width, 500));

        // Initialize stack, imager manager and hashmap
        stack = initializeNewStack();
        manager = new ImageManager();
        colorSymbolMap = new HashMap<Integer, ColorSymbol>();

        // Add background color to palette
        ColorSymbol baseColorSymbol = new ColorSymbol(backgroundColor, null, "None");
        colorSymbolMap.put(baseColorSymbol.getKey(), baseColorSymbol);
        updatePalette();
    }

    /**
     * Method to create an add color button
     * 
     * @return JButton
     */
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
                        ColorSymbol colorSymbol = new ColorSymbol(color, symbol, item);
                        insertcolorSymbolMap(colorSymbol.getKey(), colorSymbol);
                        updatePalette();
                        }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid color code");
                }
                System.out.println("Number of colors: " + colorSymbolMap.size());
            }
        });
        return Button;
    }

    /**
     * Method to create a remove color button
     * 
     * @return JButton
     */
    public JButton removeButton() {
        JButton Button = new JButton("Remove Color");
        Button.setPreferredSize(new Dimension(136, 20));
        Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colorString = ColorWheel.getColorFieldText();
                if (!colorSymbolMap.isEmpty()) {
                    try {
                        Color color = Color.decode(colorString);
                        int key = getKeyForColor(color);
                        if (key == 1) {
                            System.out.println("Cannot remove base color");
                            return;
                        }
                        // Add symbol back to stack
                        String symbolName = colorSymbolMap.get(key).symbolName();
                        stack.addItem(symbolName);
                        colorSymbolMap.remove(key);
                        for (Map.Entry<Integer, ColorSymbol> entry : colorSymbolMap.entrySet()) {
                            if (entry.getValue().color().equals(color)) {
                                colorSymbolMap.remove(entry.getKey());
                                break;
                            }
                        }
                        setActive(key);
                        updatePalette();

                        ZoomableClickableGridPanel activePanel = ZoomableClickableGridPanelTabs.getPanel(activeClickableGridPanel);
                        activePanel.updateGrid();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid color code");
                    }
                }
                System.out.println("Number of colors: " + colorSymbolMap.size());
            }
        });
        return Button;
    }

    /**
     * Method to create a color button for the palette and with a specified
     * color
     * 
     * @param color
     * @return ColorPanel
     */
    public static ColorPanel colorButton(Color color) {
        ColorPanel Button = new ColorPanel();
        int key = getKeyForColor(color);
        Button.setStats(key);
        Button.setPreferredSize(new Dimension(100, 50));
        return Button;
    }

    /**
     * Method to initialize a new stack of svg files containing the symbols
     * 
     * @return
     */
    public static SvgStack initializeNewStack() {
        SvgStack stack = new SvgStack();
        File directory = new File("./src/main/resources");
        File[] files = directory.listFiles();
        String item;
        for (File file : files) {
            if (file.isFile()) {
                item = file.getName();
                stack.addItem(item);
            }
        }
        return stack;
    }

    /**
     * Method to update the palette
     */
    public static void updatePalette() {
        // Do not remuve all, instead see what has changed and update only that
        palettePanel.removeAll();
        for (int key : colorSymbolMap.keySet()) {
            ColorPanel button = colorButton(colorSymbolMap.get(key).color());
            if (activeKey == key) {
                button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 4));
            } else {
                button.setBorder(BorderFactory.createLineBorder(getBorderColor()));
            }
            palettePanel.add(button);
        }
        palettePanel.revalidate();
        palettePanel.repaint();
    }

    /**
     * Method to clear the palette of colors
     */
    public static void clearPalette() {
        colorSymbolMap.clear();
        palettePanel.removeAll();
        palettePanel.revalidate();
        palettePanel.repaint();
    }

    /**
     * Method to update the border highlight
     */
    public static void updateBorderHighlight() {
        for (int i = 0; i < palettePanel.getComponentCount(); i++) {
            ColorPanel panel = (ColorPanel) palettePanel.getComponent(i);
            if (activeKey == panel.key()) {
                panel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 4));
            } else {
                panel.setBorder(BorderFactory.createLineBorder(getBorderColor()));
            }
        }
    }

    /**
     * Method to set the active key
     * 
     * @param key
     */
    public static void setActive(int key) {
        if (colorSymbolMap.keySet().contains(key)) {
            activeKey = key;
        } else {
            activeKey = 1;
        }
        System.out.println("Active key changed to: " + activeKey);
        if (ControlPanel.displayColor()) {
            activeColor = colorSymbolMap.get(activeKey).color();
        } else {
            activeColor = backgroundColor;
        }
        System.out.println("Active color changed to: " + activeColor);
        if (ControlPanel.displaySymbol()) {
            activeSymbol = Palette.colorSymbolMap.get(activeKey).symbol();
        } else {
            activeSymbol = null;
        }
        System.out.println("Active symbol changed");
    }

    /**
     * Method to set the active clickable grid panel
     * 
     * @param key
     */
    public static void setActiveClickableGridPanel(int key) {
        activeClickableGridPanel = key;
    }

    /**
     * Method to get the active clickable grid panel
     * 
     * @return
     */
    public static int getActiveClickableGridPanel() {
        return activeClickableGridPanel;
    }

    /**
     * Method to get the active key
     * 
     * @return int
     */
    public static int getActiveKey() {
        return activeKey;
    }

    /**
     * Method to get the active color
     * 
     * @return Color
     */
    public static Color getActiveColor() {
        return activeColor;
    }

    /**
     * Method to get the active symbol
     *  
     * @return BufferedImage
     */
    public static BufferedImage getActiveSymbol() {
        return activeSymbol;
    }

    /**
     * Method to get the background color
     * 
     * @return Color
     */
    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Method to get the border color
     * 
     * @return Color
     */
    public static Color getBorderColor() {
        return borderColor;
    }

    /**
     * Method to get the symbol name stack
     * 
     * @return SvgStack
     */
    public static SvgStack getStack() {
        return stack;
    }

    /**
     * Method to get the image manager
     * 
     * @return ImageManager
     */
    public static ImageManager getManager() {
        return manager;
    }

    /**
     * Method to get the color symbol map
     * 
     * @return HashMap<Integer, ColorSymbol>
     */
    public static HashMap<Integer, ColorSymbol> getcolorSymbolMap() {
        return colorSymbolMap;
    }

    /**
     * Method to get the key for a given color from the color symbol map
     * 
     * @param color
     * @return int
     */
    public static int getKeyForColor(Color color) {
        for (Map.Entry<Integer, ColorSymbol> entry : colorSymbolMap.entrySet()) {
            if (entry.getValue().color().equals(color)) {
                return entry.getKey();
            }
        }
        System.out.println("No key found for color: " + color + ", defaulting to -1");
        return -1;
    }

    /**
     * Method to get the hex code of format #RRGGBB for a given color
     * 
     * @param color
     * @return String
     */
    public static String getHexFromColor(Color color) {
        return String.format("#%06X", (0xFFFFFF & color.getRGB()));
    }

    /**
     * Method to insert a color symbol into the color symbol map
     * 
     * @param key
     * @param colorSymbol
     */
    public static void insertcolorSymbolMap(int key, ColorSymbol colorSymbol) {
        colorSymbolMap.put(key, colorSymbol);
    }

    /**
     * Method to set the color field text
     * 
     * @param text
     */
    public static void insertTextField(String text) {
        ColorWheel.setColorFieldText(text);
    }

    public static void main(String[] args) {
        Palette panel = new Palette();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}