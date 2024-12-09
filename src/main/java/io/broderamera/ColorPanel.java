package io.broderamera;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

/**
 * @author Olofur
 */
public class ColorPanel extends JPanel {
    private int key;
    private Color color;
    private BufferedImage symbol;

    public ColorPanel() {
        super();
        this.key = 0;
        this.color = null;
        this.symbol = null;
    }

    public ColorPanel(int key, Color color, BufferedImage symbol) {
        super();
        this.key = key;
        this.color = color;
        this.symbol = symbol;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.setBackground(color);
        g.drawImage(symbol, 0, 0, getWidth(), getHeight(), null);
    }

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

    public int key() {
        return key;
    }

    public Color color() {
        return color;
    }

    public BufferedImage symbol() {
        return symbol;
    }
}