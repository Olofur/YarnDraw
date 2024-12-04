package io.broderamera;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.Dimension;

public class MainFrame {
    public MainFrame(int x, int y) {
        init(x, y);
    }

    public MainFrame() {
        init(100, 100);
    }

    public void init(int x, int y) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane menuPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane optionPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Create color wheel panel
        ColorWheel colorWheel = new ColorWheel();
        menuPane.setTopComponent(colorWheel);

        // Create color palette panel
        Palette colorPalette = new Palette();
        optionPane.setTopComponent(colorPalette);

        ControlPanel controlPanel = new ControlPanel();
        optionPane.setBottomComponent(controlPanel);

        menuPane.setBottomComponent(optionPane);
        mainPane.setRightComponent(menuPane);

        ClickableGridPanel grid = new ClickableGridPanel(x, y);
        JScrollPane scrollGrid = new JScrollPane(grid);
        mainPane.setLeftComponent(scrollGrid);

        // Set the split pane proportions
        mainPane.setDividerLocation(.99);
        mainPane.setResizeWeight(.99);
        colorWheel.setMinimumSize(new Dimension(300, 300));

        // Menu bar options
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem printItem = new JMenuItem("Print");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.add(printItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // Add the split pane to the frame
        frame.getContentPane().add(mainPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame(50, 50);
    }
}