package io.broderamera;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionAdapter;

public class ClickableGridPanel extends JPanel {
    private int zoomLevel = 1;
    private int previousZoomLevel = 1;
    private int zoomMax = 20;

    private static JPanel megaBoy;
    private static ColorPanel[] gridPanels;
    private static int[] keyGrid;

    private static int gridX;
    private static int gridY;

    public ClickableGridPanel(int x, int y) {
        setGridSize(x, y);
        initializeGrid();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Convert the cursor position to the coordinates of the JViewport
                Point cursorPoint = e.getPoint();
                SwingUtilities.convertPointToScreen(cursorPoint, e.getComponent());
                SwingUtilities.convertPointFromScreen(cursorPoint, megaBoy);
 
                int x = cursorPoint.x;
                int y = cursorPoint.y;
                Component component = megaBoy.getComponentAt(x, y);
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
                 // Convert the cursor position to the coordinates of the JViewport
                Point cursorPoint = e.getPoint();
                SwingUtilities.convertPointToScreen(cursorPoint, e.getComponent());
                SwingUtilities.convertPointFromScreen(cursorPoint, megaBoy);

                int x = cursorPoint.x;
                int y = cursorPoint.y;
                Component component = megaBoy.getComponentAt(x, y);
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
                if (rotation > 0 && zoomLevel > 1) {
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

        megaBoy.revalidate();
        megaBoy.repaint();

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
            return;
        }

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
        if (megaBoy != null) {
            removeAll();
            megaBoy.removeAll();
        }

        megaBoy = new JPanel();
        megaBoy.setLayout(new GridLayout(gridX, gridY));
        megaBoy.setVisible(true);
        
        gridPanels = new ColorPanel[gridX * gridY];
        keyGrid = new int[gridX * gridY];

        Color border = Palette.getBorderColor();
        gridPanels = new ColorPanel[gridX * gridY];
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = new ColorPanel();
            panel.setStats(1);
            panel.setVisible(true);
            panel.setPreferredSize(new Dimension(10, 10));
            panel.setBorder(BorderFactory.createLineBorder(border));
            gridPanels[index] = panel;

            megaBoy.add(gridPanels[index]);
        }
        add(megaBoy);

        revalidate();
        repaint();
    }

    public static void reinitializeGrid() {
        megaBoy.removeAll();
        megaBoy.setLayout(new GridLayout(gridX, gridY));
        megaBoy.setVisible(true);
        
        gridPanels = new ColorPanel[gridX * gridY];
        keyGrid = new int[gridX * gridY];

        Color border = Palette.getBorderColor();
        gridPanels = new ColorPanel[gridX * gridY];
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = new ColorPanel();
            panel.setStats(1);
            panel.setVisible(true);
            panel.setPreferredSize(new Dimension(10, 10));
            panel.setBorder(BorderFactory.createLineBorder(border));
            gridPanels[index] = panel;

            megaBoy.add(gridPanels[index]);
        }
    }

    public static void updateGrid() {
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = gridPanels[index];
            int key = panel.getKey();
            if (!Palette.getBiglyMap().keySet().contains(key)) {
                key = 1;
            }
            if (panel.isRepresentedWrongly()) {
                panel.setStats(key);
                panel.revalidate();
                panel.repaint();
            }
        }
        return;
    }

    public static void loadKeys(int[] grid) {
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = gridPanels[index];
            panel.setStats(grid[index]);
            panel.revalidate();
            panel.repaint();
        }
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

    public static int[] getGridSize() {
        return new int[]{gridX, gridY};
    }

    public static void setGridSize(int x, int y) {
        gridX = x;
        gridY = y;
    }

    public static int[] getKeyGrid() { 
        for (int index = 0; index < gridX * gridY; index++) {
            keyGrid[index] = gridPanels[index].getKey();
        }
        return keyGrid;
    }

    public static void main(String[] args) {
        ClickableGridPanel panel = new ClickableGridPanel(100, 100);

        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}