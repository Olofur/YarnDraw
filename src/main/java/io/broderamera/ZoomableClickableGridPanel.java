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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionAdapter;

/**
* @author Olofur
*/
public class ZoomableClickableGridPanel extends JScrollPane {
    private int zoomLevel = 1;
    private int previousZoomLevel = 1;
    private int zoomMax = 20;
    
    private int gridX;
    private int gridY;
    private int[] keyGrid;
    
    private JPanel clickableGridPanel;
    private ColorPanel[] gridPanels;
    
    public ZoomableClickableGridPanel(int x, int y) {
        setGridSize(x, y);
        initializeGrid();
        
        clickableGridPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Convert the cursor position to the coordinates of the JViewport
                Point cursorPoint = e.getPoint();
                SwingUtilities.convertPointToScreen(cursorPoint, e.getComponent());
                SwingUtilities.convertPointFromScreen(cursorPoint, clickableGridPanel);
                
                int x = cursorPoint.x;
                int y = cursorPoint.y;
                Component component = clickableGridPanel.getComponentAt(x, y);
                ColorPanel panel;
                if (component instanceof ColorPanel) {
                    panel = (ColorPanel) component;
                } else {
                    return;
                }
                int activeKey = Palette.getActiveKey();
                if (panel.key() != activeKey) {
                    panel.setStats(activeKey);
                    panel.revalidate();
                    panel.repaint();
                }
            }
        });
        
        clickableGridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Convert the cursor position to the coordinates of the JViewport
                Point cursorPoint = e.getPoint();
                SwingUtilities.convertPointToScreen(cursorPoint, e.getComponent());
                SwingUtilities.convertPointFromScreen(cursorPoint, clickableGridPanel);
                
                int x = cursorPoint.x;
                int y = cursorPoint.y;
                Component component = clickableGridPanel.getComponentAt(x, y);
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
                if (ControlPanel.isFillInActive()) {
                    int[] indices = getGridPosition(panel, x, y);
                    fillInColor(indices[0], indices[1], panel.key());
                    return;
                }
                if (panel.key() != activeKey) {
                    panel.setStats(activeKey);
                } else {
                    panel.setStats(1);
                }
                panel.revalidate();
                panel.repaint();
            }
        });
        
        clickableGridPanel.addMouseWheelListener(new MouseAdapter() {
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
        clickableGridPanel.revalidate();
        clickableGridPanel.repaint();
        revalidate();
        repaint();
        
        // Get the JViewport
        JViewport viewport = getViewport();
        
        // Convert the cursor position to the coordinates of the JViewport
        Point cursorPoint = e.getPoint();
        SwingUtilities.convertPointToScreen(cursorPoint, e.getComponent());
        SwingUtilities.convertPointFromScreen(cursorPoint, viewport);
        
        int totalWidth = 5 * zoomLevel * gridX;
        int totalHeight = 5 * zoomLevel * gridY;
        double zoomRatio = ((double) zoomLevel / previousZoomLevel);
        
        // Get (local) coordiates of the cursor
        double cursorX = cursorPoint.getX();
        double cursorY = cursorPoint.getY();
        
        // Grid (global) coordinates of the cursor
        double cursorXInGrid = getHorizontalScrollBar().getValue() + cursorX;
        double cursorYInGrid = getVerticalScrollBar().getValue() + cursorY;
        
        int newX = (int) ((cursorXInGrid * zoomRatio) - cursorX);
        int newY = (int) ((cursorYInGrid * zoomRatio) - cursorY);
        
        getHorizontalScrollBar().setValue(totalWidth / 2- viewport.getWidth() / 2);
        getVerticalScrollBar().setValue(totalHeight / 2 - viewport.getHeight() / 2);
        
        Point corner = new Point(newX, newY);
        viewport.setViewPosition(corner);
        
        previousZoomLevel = zoomLevel;
    }
    
    public void initializeGrid() {
        if (clickableGridPanel != null) {
            clickableGridPanel.removeAll();
        }
        
        clickableGridPanel = new JPanel();
        clickableGridPanel.setLayout(new GridLayout(gridX, gridY));
        clickableGridPanel.setVisible(true);
        
        gridPanels = new ColorPanel[gridX * gridY];
        keyGrid = new int[gridX * gridY];
        
        Color borderColor = Palette.getBorderColor();
        gridPanels = new ColorPanel[gridX * gridY];
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = new ColorPanel();
            panel.setStats(1);
            panel.setVisible(true);
            panel.setPreferredSize(new Dimension(10, 10));
            panel.setBorder(BorderFactory.createLineBorder(borderColor));
            gridPanels[index] = panel;
            
            clickableGridPanel.add(gridPanels[index]);
        }
        setViewportView(clickableGridPanel);
    }
    
    public void reinitializeGrid() {
        clickableGridPanel.removeAll();
        clickableGridPanel.setLayout(new GridLayout(gridX, gridY));
        clickableGridPanel.setVisible(true);
        
        gridPanels = new ColorPanel[gridX * gridY];
        keyGrid = new int[gridX * gridY];
        
        Color border = Palette.getBorderColor();
        gridPanels = new ColorPanel[gridX * gridY];
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = new ColorPanel();
            panel.setStats(1);
            panel.setVisible(true);
            panel.setBorder(BorderFactory.createLineBorder(border));
            gridPanels[index] = panel;
            
            clickableGridPanel.add(gridPanels[index]);
        }
    }
    
    public void updateGrid() {
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = gridPanels[index];
            // If key is not in bigly map, change back to background color
            if (!Palette.getcolorSymbolMap().keySet().contains(panel.key())) {
                panel.setStats(1);
                panel.revalidate();
                panel.repaint();
            }
            // If panel is not shown with correct color or symbol representation, correct it
            if (panel.isRepresentedWrongly()) {
                panel.setStats(panel.key());
                panel.revalidate();
                panel.repaint();
            }
        }
        return;
    }
    
    public void resetGrid() {
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = gridPanels[index];
            panel.setStats(1);  
            panel.revalidate();
            panel.repaint();    
        }
    }
    
    public void loadKeyGrid(int[] grid) {
        for (int index = 0; index < gridX * gridY; index++) {
            ColorPanel panel = gridPanels[index];
            panel.setStats(grid[index]);
            panel.revalidate();
            panel.repaint();
        }
    }
    
    public int[] getGridPosition(ColorPanel panel, int x, int y) {
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
    
    public void fillInColor(int i, int j, int key) {
        // Recursively go outwards from i,j and fill in the grid
        // for all pixels of the same color
        // if the pixel is not filled in yet
        // if the pixel is already filled in, do nothing
        int index = i * gridY + j;
        ColorPanel panel = gridPanels[index];
        if (panel.key() == key) {
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

    public JPanel getClickableGridPanel() {
        return clickableGridPanel;
    }
    
    public int[] getGridSize() {
        return new int[]{gridX, gridY};
    }
    
    public void setGridSize(int x, int y) {
        gridX = x;
        gridY = y;
    }
    
    public int[] getKeyGrid() { 
        for (int index = 0; index < gridX * gridY; index++) {
            keyGrid[index] = gridPanels[index].key();
        }
        return keyGrid;
    }
    
    public static void main(String[] args) {
        ZoomableClickableGridPanel panel = new ZoomableClickableGridPanel(100, 100);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}