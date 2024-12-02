package io.broderamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ColorWheel extends JPanel {
    private JPanel colorWheelPanel;
    private BufferedImage colorWheelImage;
    private static JTextField colorField;

    public ColorWheel() {
        setLayout(new BorderLayout());

        colorWheelPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (colorWheelImage != null) {
                    g.drawImage(colorWheelImage, 0, 0, null);
                }
            }
        };
        colorWheelPanel.setPreferredSize(new Dimension(300, 300));
        colorWheelPanel.addMouseListener(new ColorWheelMouseListener());

        colorWheelImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 300; y++) {
            for (int x = 0; x < 300; x++) {
                double angle = Math.atan2(y - 150, x - 150);
                double distance = Math.sqrt(Math.pow(x - 150, 2) + Math.pow(y - 150, 2));
                if (distance > 150) {
                    colorWheelImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    int hue = (int) ((angle + Math.PI) / (2 * Math.PI) * 360);
                    int saturation = (int) (distance / 150 * 255);
                    int brightness = 255;
                    Color color = Color.getHSBColor(hue / 360f, saturation / 255f, brightness / 255f);
                    colorWheelImage.setRGB(x, y, color.getRGB());
                }
            }
        }
        add(colorWheelPanel, BorderLayout.CENTER);

        colorField = new JTextField(25);
        colorField.setToolTipText("Color format #RRGGBB");
        add(colorField, BorderLayout.SOUTH);
    }

    private class ColorWheelMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            Color color = new Color(colorWheelImage.getRGB(x, y));
            String hexColor = String.format("#%06X", (0xFFFFFF & color.getRGB()));
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
        new ColorWheel();
    }
}