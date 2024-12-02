package io.broderamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionAdapter;

public class ClickableGridPanel extends JPanel {
    private int zoomLevel = 1;
    private int previousZoomLevel = 1;
    private int zoomMax = 20;

    private static boolean fillIn = false;

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
                            if (panel.getKey() != activeKey) {
                                panel.setStats(activeKey);
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
                            if (activeKey <= 0) {
                                JOptionPane.showMessageDialog(null,
                                            "No color has been selected",
                                            "Error",
                                            JOptionPane.WARNING_MESSAGE);
                            }
                            if (fillIn) {
                                fillInBaby(i, j, panel.getKey());
                                return;
                            }
                            if (panel.getKey() != activeKey) {
                                panel.setStats(activeKey);
                            } else {
                                panel.setStats(1);
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

        double zoomRatio = ((double) zoomLevel / previousZoomLevel);

        // View (local) coordiates of the cursor
        double cursorX = cursorPoint.getX();
        double cursorY = cursorPoint.getY();

        // Grid (global) coordinates of the cursor
        double cursorXInGrid = scrollPane.getHorizontalScrollBar().getValue() + cursorX;
        double cursorYInGrid = scrollPane.getVerticalScrollBar().getValue() + cursorY;

        int newX = (int) ((cursorXInGrid * zoomRatio) - cursorX);
        int newY = (int) ((cursorYInGrid * zoomRatio) - cursorY);

        scrollPane.getHorizontalScrollBar().setValue(totalWidth / 2 - viewport.getWidth() / 2);
        scrollPane.getVerticalScrollBar().setValue(totalHeight / 2 - viewport.getHeight() / 2);

        Point corner = new Point(newX, newY);
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
                if (!Palette.getBiglyMap().keySet().contains(key)) {
                    key = 1;
                }
                panel.setStats(key);
                panel.revalidate();
                panel.repaint();
            }
        }
        return;
    }

    public static void resetGrid() {
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                ColorPanel panel = gridPanels[i][j];
                panel.setStats(1);  
                panel.revalidate();
                panel.repaint();    
            }
        }
    }

    public static void fillInBaby(int i, int j, int key) {
        // recursively go outwards from i,j and fill in the grid
        // for all pixels of the same color
        // if the pixel is not filled in yet
        // if the pixel is already filled in, do nothing
        
        for (int k = -1; k <= 1; k = k + 2) {
            if (i + k >= 0 && i + k < gridX) {
                ColorPanel panel = gridPanels[i + k][j];
                int activeKey = Palette.getActiveKey();
                if (panel.getKey() == key) {
                    panel.setStats(activeKey);
                    panel.revalidate();
                    panel.repaint();
                    fillInBaby(i + k, j, key);
                }
            }
        }
        for (int l = -1; l <= 1; l = l + 2) {
            if (j + l >= 0 && j + l < gridY) {
                ColorPanel panel = gridPanels[i][j + l];
                int activeKey = Palette.getActiveKey();
                if (panel.getKey() == key) {
                    panel.setStats(activeKey);
                    panel.revalidate();
                    panel.repaint();
                    fillInBaby(i, j + l, key);
                }
            }
        }
    }

    public static void changeFillIn() {
        fillIn = !fillIn;
    }

    public static boolean getFillIn() { 
        return fillIn;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ClickableGridPanel panel = new ClickableGridPanel(100, 100);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}