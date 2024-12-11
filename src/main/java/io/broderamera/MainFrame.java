package io.broderamera;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import java.awt.Dimension;
import java.awt.event.ActionListener;

/**
 * The main frame of the application. Contains the color wheel, palette and
 * grid panels.
 * 
 * @version 1.0
 * @author Olofur
 */
public class MainFrame {
    int width; 

    /**
     * Default constructor
     */
    public MainFrame() {
        init(100, 100);
    }

    /**
     * Grid constructor, which sets the dimensions of the grid
     * 
     * @param x
     * @param y
     */
    public MainFrame(int x, int y) {
        init(x, y);
    }

    /**
     * Initialize the main frame with the given dimensions
     * 
     * @param x
     * @param y
     */
    public void init(int x, int y) {
        width = 350;

        JFrame frame = new JFrame();

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane menuPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane optionPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Create color palette panel with control panel
        Palette palette = new Palette(width);
        ControlPanel controlPanel = new ControlPanel(width);
        optionPane.setTopComponent(palette);
        optionPane.setBottomComponent(controlPanel);

        // Create color wheel panel
        ColorWheel colorWheel = new ColorWheel(width);
        menuPane.setTopComponent(colorWheel);
        menuPane.setBottomComponent(optionPane);

        // Create grid panel inside scroll pane
        ZoomableClickableGridPanelTabs gridTabs = new ZoomableClickableGridPanelTabs();
        mainPane.setLeftComponent(gridTabs);
        mainPane.setRightComponent(menuPane);

        // Set the split pane proportions
        mainPane.setDividerLocation(.99);
        mainPane.setResizeWeight(.99);

        colorWheel.setMinimumSize(new Dimension(width, width + 20));
        palette.setMinimumSize(new Dimension(width, 500));

        // Add menu bar options
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controlPanel.savePatternToFile();
            }
        });
        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controlPanel.loadPatternFromFile();
            }
        });
        JMenuItem printItem = new JMenuItem("Print");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.add(printItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPane);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame(50, 50);
    }
}