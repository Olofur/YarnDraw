package io.broderamera;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

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

        if (ControlPanel.showColor()) {
            this.color = Palette.getBiglyMap().get(key).color();
        } else {
            this.color = Palette.getBackgroundColor();
        }
        if (ControlPanel.showSymbol()) {
            this.symbol = Palette.getBiglyMap().get(key).symbol();
        } else {
            this.symbol = null;
        }
    }

    public boolean isRepresentedWrongly() {
        if (ControlPanel.showColor() 
                && this.color != Palette.getBiglyMap().get(key).color()) {
            return true;
        } else if (!ControlPanel.showColor() 
                && this.color != Palette.getBackgroundColor()) {
            return true;
        } else if (ControlPanel.showSymbol() 
                && this.symbol != Palette.getBiglyMap().get(key).symbol()) {
            return true;
        } else if (!ControlPanel.showSymbol() 
                && this.symbol != null) {
            if (Palette.getBiglyMap().get(key).symbol() != null) {
                return true;
            }
        }
        return false;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public BufferedImage getSymbol() {
        return symbol;
    }

    public void setSymbol(BufferedImage symbol) {
        this.symbol = symbol;
    }
}