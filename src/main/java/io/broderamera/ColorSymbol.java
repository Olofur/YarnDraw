package io.broderamera;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Represents a color symbol object, that contains a key, color, symbol, and
 * symbol name, as well as setter and getter methods for these properties. The
 * program has a static keyCount variable that is used to assign unique keys to
 * each color symbol and which can also be reset using resetKeyCount.
 * 
 * @author Olofur
 */
class ColorSymbol {
    private static int keyCount = 1;
    private int key;
    private Color color;
    private BufferedImage symbol;
    private String symbolName;

    /**
     * Default constructor.
     */
    public ColorSymbol() {
        this.key = keyCount;
        keyCount++;

        this.color = null;
        this.symbol = null;
        this.symbolName = null;
    }

    /**
     * Constructor that takes a symbol and symbol name as arguments.
     * 
     * @param symbol The symbol image.
     * @param symbolName The name of the symbol.
     */
    public ColorSymbol(BufferedImage symbol, String symbolName) {
        this.key = keyCount;
        keyCount++;

        this.color = null;
        this.symbol = symbol;
        this.symbolName = symbolName;
    }

    /**
     * Constructor that takes a color, symbol, and symbol name as arguments.
     * 
     * @param color The color of the symbol.
     * @param symbol The symbol image.
     * @param symbolName The name of the symbol.
     */
    public ColorSymbol(Color color, BufferedImage symbol, String symbolName) {
        this.key = keyCount;
        keyCount++;

        this.color = color;
        this.symbol = symbol;
        this.symbolName = symbolName;
    }

    /**
     * Resets the key count back to 1.
     */
    public static void resetKeyCount() {
        keyCount = 1;
    }

    /**
     * Returns the key of the color symbol.
     * 
     * @return int
     */
    public int getKey() {
        return key;
    }

    /**
     * Returns the color of the color symbol.
     * 
     * @return Color
     */
    public Color color() {
        return color;
    }

    /**
     * Sets the color of the color symbol.
     * 
     * @param color The color to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the symbol of the color symbol.
     * 
     * @return BufferedImage
     */
    public BufferedImage symbol() {
        return symbol;
    }

    /**
     * Sets the symbol of the color symbol.
     * 
     * @param symbol The symbol to set.
     */
    public void setSymbol(BufferedImage symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the name of the symbol. This name is used to identify the symbol
     * when loading a pattern from a file and also when putting it back in the 
     * stack after a color is removed.
     * 
     * @return String
     */
    public String symbolName() {
        return symbolName;
    }

    /**
     * Sets the name of the symbol.
     * 
     * @param symbolName The name to set.
     */
    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }
}