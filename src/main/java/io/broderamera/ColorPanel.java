package io.broderamera;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class ColorPanel extends JPanel {
    private Color color;
    private BufferedImage symbol;

    public ColorPanel() {
        super();
        this.color = null;
        this.symbol = null;
    }

    public ColorPanel(Color color, BufferedImage symbol) {
        super();
        this.color = color;
        this.symbol = symbol;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.setBackground(color);
        g.drawImage(symbol, 0, 0, getWidth(), getHeight(), null);      
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