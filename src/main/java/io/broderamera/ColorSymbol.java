package io.broderamera;

import java.awt.*;
import java.awt.image.BufferedImage;

class ColorSymbol {
    private Color color;
    private BufferedImage symbol;
    private String symbolName;

    public ColorSymbol() {
        this.color = null;
        this.symbol = null;
        this.symbolName = null;
    }

    public ColorSymbol(BufferedImage symbol, String symbolName) {
        this.color = null;
        this.symbol = symbol;
        this.symbolName = symbolName;
    }

    public ColorSymbol(Color color, BufferedImage symbol, String symbolName) {
        this.color = color;
        this.symbol = symbol;
        this.symbolName = symbolName;
    }

    public Color color() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public BufferedImage symbol() {
        return symbol;
    }

    public void setSymbol(BufferedImage symbol) {
        this.symbol = symbol;
    }

    public String symbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }
}