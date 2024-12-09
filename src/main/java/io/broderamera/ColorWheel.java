package io.broderamera;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * @author Olofur
 */
public class ColorWheel extends JPanel {
    int diameter; 
    int radius;

    private JPanel colorWheelPanel;
    private BufferedImage colorWheelImage;
    private static JTextField colorField;

    public ColorWheel() {
        this(200);
    }

    public ColorWheel(int diameter) {
        radius = diameter / 2;
    
        colorWheelPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (colorWheelImage != null) {
                    g.drawImage(colorWheelImage, 0, 0, null);
                }
            }
        };
        colorWheelPanel.setPreferredSize(new Dimension(diameter, diameter + 20));
        colorWheelPanel.addMouseListener(new ColorWheelMouseListener());

        colorWheelImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                double angle = Math.atan2(y - radius, x - radius);
                double distance = Math.sqrt(Math.pow(x - radius, 2) + Math.pow(y - radius, 2));
                if (distance > radius) {
                    colorWheelImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    int hue = (int) ((angle + Math.PI) / (2 * Math.PI) * 360);
                    int saturation = (int) (distance / radius * 255);
                    int brightness = 255;
                    Color color = Color.getHSBColor(hue / 360f, saturation / 255f, brightness / 255f);
                    colorWheelImage.setRGB(x, y, color.getRGB());
                }
            }
        }

        colorField = new JTextField(25);
        colorField.setPreferredSize(new Dimension(diameter, 20));
        colorField.setToolTipText("Color format #RRGGBB");

        setLayout(new BorderLayout());
        add(colorWheelPanel, BorderLayout.CENTER);
        add(colorField, BorderLayout.SOUTH);
    }

    private class ColorWheelMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            Color color = new Color(colorWheelImage.getRGB(x, y));
            String hexColor = Palette.getHexFromColor(color);
            System.out.println("Selected color: " + hexColor);
            colorField.setText(hexColor);
        }
    }

    public static String getColorFieldText() {
        return colorField.getText();
    }

    public static void setColorFieldText(String text) {
        colorField.setText(text);
    }

    public static void main(String[] args) {
        ColorWheel panel = new ColorWheel();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}