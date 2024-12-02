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

    private static ColorPanel[] gridPanels;
    private static int[][] keyMap;

    private static int gridX;
    private static int gridY;

    public ClickableGridPanel(int x, int y) {
        gridX = x;
        gridY = y;

        setLayout(new GridLayout(gridX, gridY));
        gridPanels = new ColorPanel[gridX * gridY];
        keyMap = new int[gridX][gridY];

        initializeGrid();

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Component component = getComponentAt(x, y);
                ColorPanel panel;
                if (component instanceof ColorPanel) {
                    panel = (ColorPanel) component;
                } else {
                    return;
                }
                int activeKey = Palette.getActiveKey();
                if (panel.getKey() != activeKey) {
                    panel.setStats(activeKey);
                    panel.revalidate();
                    panel.repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Component component = getComponentAt(x, y);
                ColorPanel panel;
                if (component instanceof ColorPanel) {
                    panel = (ColorPanel) component;
                } else {
                    return;
                }
                int activeKey = Palette.getActiveKey();
                if (activeKey <= 0) {
                    JOptionPane.showMessageDialog(null,
                                "No color has been selected",
                                "Error",
                                JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (ControlPanel.getFillIn()) {
                    int[] indices = getGridPosition(panel, x, y);
                    fillInColor(indices[0], indices[1], panel.getKey());
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
        for (int index = 0; index < gridX * gridY; index++) {
            gridPanels[index].setPreferredSize(new Dimension(5 * zoomLevel, 5 * zoomLevel));    
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

        // This could be separate function

        // Get the JViewport
        JViewport viewport = scrollPane.getViewport();

        // Convert the cursor position to the coordinates of the JViewport
        Point cursorPoint = e.getPoint();
        SwingUtilities.convertPointToScreen(cursorPoint, e.getComponent());
        SwingUtilities.convertPointFromScreen(cursorPoint, viewport);

        double zoomRatio = ((double) zoomLevel / previousZoomLevel);

        // Get (local) coordiates of the cursor
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
        Color border = Palette.getBorderColor();
        gridPanels = new ColorPanel[gridX * gridY];
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = new ColorPanel();
            panel.setStats(1);
            panel.setPreferredSize(new Dimension(10, 10));
            panel.setBorder(BorderFactory.createLineBorder(border));
            gridPanels[index] = panel;
            add(gridPanels[index]);
        }
    }

    public static void updateGrid() {
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = gridPanels[index];
            int key = panel.getKey();
            if (!Palette.getBiglyMap().keySet().contains(key)) {
                key = 1;
            }
            if (panel.getColor() != Palette.getBiglyMap().get(key).color() ||
                panel.getSymbol() != Palette.getBiglyMap().get(key).symbol()) {
                panel.setStats(key);
                panel.revalidate();
                panel.repaint();
            }
        }
        return;
    }

    public static void resetGrid() {
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = gridPanels[index];
            panel.setStats(1);  
            panel.revalidate();
            panel.repaint();    
        }
    }

    public static int[] getGridPosition(ColorPanel panel, int x, int y) {
        for (int index = 0; index < gridX * gridY; index++) {
            int i = index / gridY;
            int j = index % gridY;
            if (gridPanels[index] == panel) {
                x = i;
                y = j;
                break;
            }
        }
        return new int[]{x, y};
    }

    public static void fillInColor(int i, int j, int key) {
        // recursively go outwards from i,j and fill in the grid
        // for all pixels of the same color
        // if the pixel is not filled in yet
        // if the pixel is already filled in, do nothing
        int index = i * gridY + j;
        ColorPanel panel = gridPanels[index];
        if (panel.getKey() == key) {
            int activeKey = Palette.getActiveKey();
            panel.setStats(activeKey);
            panel.revalidate();
            panel.repaint();
            // Visit all direct neightbors
            fillInColor(i + 1, j, key);
            fillInColor(i - 1, j, key);
            fillInColor(i, j + 1, key);
            fillInColor(i, j - 1, key);
        }
    }

    public static int[][] getKeyMap() { 
        for (int index = 0; index < gridX * gridY; index++) {
            int i = index / gridY;
            int j = index % gridY;
            keyMap[i][j] = gridPanels[index].getKey();
        }
        return keyMap;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ClickableGridPanel panel = new ClickableGridPanel(100, 100);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}