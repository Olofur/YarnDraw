package io.broderamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class ClickableGridPanel extends JPanel {
    private int zoomLevel = 1;
    private int previousZoomLevel = 1;
    private int zoomMax = 20;

    private static ColorPanel[][] gridPanels;

    private static int gridX;
    private static int gridY;

    public ClickableGridPanel(int x, int y) {
        gridX = x;
        gridY = y;

        setLayout(new GridLayout(gridX, gridY));
        gridPanels = new ColorPanel[gridX][gridY];

        initializeGrid();

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                for (int i = 0; i < gridX; i++) {
                    for (int j = 0; j < gridY; j++) {
                        ColorPanel panel = gridPanels[i][j];
                        if (panel.getBounds().contains(x, y)) {
                            int activeKey = Palette.getActiveKey();
                            Color activeColor = Palette.getActiveColor();
                            BufferedImage activeSymbol = Palette.getActiveSymbol();
                            if (panel.getColor() != activeColor) {
                                panel.setKey(activeKey);
                                panel.setColor(activeColor);
                                panel.setSymbol(activeSymbol);
                                panel.revalidate();
                                panel.repaint();
                            }
                        }
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                for (int i = 0; i < gridX; i++) {
                    for (int j = 0; j < gridY; j++) {
                        ColorPanel panel = gridPanels[i][j];
                        if (panel.getBounds().contains(x, y)) {
                            int activeKey = Palette.getActiveKey();
                            Color activeColor = Palette.getActiveColor();
                            BufferedImage activeSymbol = Palette.getActiveSymbol();
                            if (activeColor == null) {
                                JOptionPane.showMessageDialog(null,
                                            "No color has been selected",
                                            "Error",
                                            JOptionPane.WARNING_MESSAGE);
                            }
                            if (panel.getColor() != activeColor) {
                                panel.setKey(activeKey);
                                panel.setColor(activeColor);
                                panel.setSymbol(activeSymbol);
                            } else {
                                panel.setKey(1);
                                panel.setColor(Palette.getBackgroundColor());
                                panel.setSymbol(null);
                            }
                            panel.revalidate();
                            panel.repaint();
                        }
                    }
                }
            }
        });

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                if (rotation > 0 && zoomLevel > 0) {
                    zoomLevel--;
                    System.out.println("Zoom level: " + zoomLevel);
                    updateZoom(e);
                } else if (rotation < 0 && zoomLevel < zoomMax) {
                    zoomLevel++;
                    System.out.println("Zoom level: " + zoomLevel);
                    updateZoom(e);
                } else {
                    return;
                }
            }
        });
    }   

    private void updateZoom(MouseWheelEvent e) {
        // Zoom the grid
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                gridPanels[i][j].setPreferredSize(new Dimension(5 * zoomLevel, 5 * zoomLevel));    
            }
        }

        int totalWidth = 5 * zoomLevel * gridX;
        int totalHeight = 5 * zoomLevel * gridY;

        revalidate();
        repaint();

        // Get the JScrollPane that contains the ClickableGridPanel
        JScrollPane scrollPane;
        try {
            Container parent = getParent();
            while (!(parent instanceof JScrollPane)) {
                parent = parent.getParent();
            }
            scrollPane = (JScrollPane) parent;
        } catch (Exception ex) {
            System.out.print("Window is not zoomable! No parent scrollpane fount...\n");
            return;
        }

        // Get the JViewport
        JViewport viewport = scrollPane.getViewport();

        // Convert the cursor position to the coordinates of the JViewport
        Point cursorPoint = e.getPoint();
        SwingUtilities.convertPointToScreen(cursorPoint, e.getComponent());
        SwingUtilities.convertPointFromScreen(cursorPoint, viewport);

        // View (local) coordiates of the cursor
        double cursorX = cursorPoint.getX();
        double cursorY = cursorPoint.getY();

        // Grid (global) coordinates of the cursor
        double cursorXInGrid = scrollPane.getHorizontalScrollBar().getValue() + cursorX;
        double cursorYInGrid = scrollPane.getVerticalScrollBar().getValue() + cursorY;

        double zoomRatio = ((double) zoomLevel / previousZoomLevel);

        int newX = (int) ((cursorXInGrid * zoomRatio) - cursorX);
        int newY = (int) ((cursorYInGrid * zoomRatio) - cursorY);
        Point corner = new Point(newX, newY);

        scrollPane.getHorizontalScrollBar().setValue(totalWidth / 2 - viewport.getWidth() / 2);
        scrollPane.getVerticalScrollBar().setValue(totalHeight / 2 - viewport.getHeight() / 2);
        viewport.setViewPosition(corner);

        previousZoomLevel = zoomLevel;
    }

    public void initializeGrid() {
        gridPanels = new ColorPanel[gridX][gridY];
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                Color background = Palette.getBackgroundColor();
                Color border = Palette.getBorderColor();
                gridPanels[i][j] = new ColorPanel(1, background, null);
                gridPanels[i][j].setPreferredSize(new Dimension(10, 10));
                gridPanels[i][j].setBorder(BorderFactory.createLineBorder(border));
                add(gridPanels[i][j]);
            }
        }
    }

    public static void updateGrid() {
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                ColorPanel panel = gridPanels[i][j];
                int key = panel.getKey();
                Color keyColor = Palette.biglyMap.get(key).color();
                BufferedImage keySymbol = Palette.biglyMap.get(key).symbol();
                if (ControlPanel.showColor()) {
                    panel.setColor(keyColor);
                } else {
                    panel.setColor(Palette.getBackgroundColor());
                }
                if (ControlPanel.showSymbol()) {
                    panel.setSymbol(keySymbol);
                } else {
                    panel.setSymbol(null);
                }
                panel.revalidate();
                panel.repaint();
            }
        }
        return;
    }

    public void resetGrid() {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the grid?");
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                gridPanels[i][j].setBackground(Palette.getBackgroundColor());
                gridPanels[i][j].setSymbol(null);       
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ClickableGridPanel panel = new ClickableGridPanel(100, 100);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}