package io.broderamera;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

/**
 * Represents a color panel extending from the JPanel class. The panel contains
 * three main properties: key, color, and symbol. They each have their own
 * getter methods and are all set simultaneously through the setStats method.
 * The paint component method is overridden to display the color as background 
 * color and draws the symbol using drawImage.
 * 
 * The isRepresentedWrongly method is used to check if the panel is represented
 * correctly according to the display booleans displayColor and displaySymbol
 * in the ControlPanel class.
 * 
 * The constructor takes either the three properties as arguments or no
 * arguments. If no arguments are given, the panel will be initialized with
 * key = 0, color = null, and symbol = null.
 * 
 * @author Olofur
 */
public class ColorPanel extends JPanel {
    private int key;
    private Color color;
    private BufferedImage symbol;

    /**
     * Overrides the paintComponent method to display the color as background
     * color and draws the symbol using drawImage.
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.setBackground(color);
        g.drawImage(symbol, 0, 0, getWidth(), getHeight(), null);
    }

    /**
     * Default constructor.
     */
    public ColorPanel() {
        super();
        this.key = 0;
        this.color = null;
        this.symbol = null;
    }

    /**
     * Constructor that sets the three main properties of the panel: key,
     * color, and symbol.
     * 
     * @param key
     * @param color
     * @param symbol
     */
    public ColorPanel(int key, Color color, BufferedImage symbol) {
        super();
        this.key = key;
        this.color = color;
        this.symbol = symbol;
    }

    /**
     * Sets the three main properties of the panel: key, color, and symbol.
     * Only the key is given as argument, and the other two are found in the
     * Palette colorSymbolMap, which maps all keys to colors and symbols.
     * 
     * @param key
     */
    public void setStats(int key) {
        this.key = key;

        if (ControlPanel.displayColor()) {
            this.color = Palette.getcolorSymbolMap().get(key).color();
        } else {
            this.color = Palette.getBackgroundColor();
        }
        if (ControlPanel.displaySymbol()) {
            this.symbol = Palette.getcolorSymbolMap().get(key).symbol();
        } else {
            this.symbol = null;
        }
    }

    /**
     * Checks if the panel is represented correctly according to the ControlPanel
     * display booleans displayColor and displaySymbol.
     * 
     * @return boolean
     */
    public boolean isRepresentedWrongly() {
        if (ControlPanel.displayColor() 
                && this.color != Palette.getcolorSymbolMap().get(key).color()) {
            return true;
        } else if (!ControlPanel.displayColor() 
                && this.color != Palette.getBackgroundColor()) {
            return true;
        } else if (ControlPanel.displaySymbol() 
                && this.symbol != Palette.getcolorSymbolMap().get(key).symbol()) {
            return true;
        } else if (!ControlPanel.displaySymbol() 
                && this.symbol != null) {
            if (Palette.getcolorSymbolMap().get(key).symbol() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the key of the panel.
     * 
     * @return int
     */
    public int key() {
        return key;
    }

    /**
     * Returns the color of the panel.
     * 
     * @return Color
     */
    public Color color() {
        return color;
    }

    /**
     * Returns the symbol of the panel.
     * 
     * @return BufferedImage
     */
    public BufferedImage symbol() {
        return symbol;
    }
}