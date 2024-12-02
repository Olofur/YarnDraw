package io.broderamera;

import javax.swing.*;
import java.awt.*;

public class MainFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane menuPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane optionPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Create grid panel
        int x = 100; //Integer.parseInt(JOptionPane.showInputDialog("Enter x dimension"));
        int y = 100; //Integer.parseInt(JOptionPane.showInputDialog("Enter y dimension"));

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

        // Add the split pane to the frame
        frame.getContentPane().add(mainPane);

        frame.pack();
        frame.setVisible(true);
    }
}