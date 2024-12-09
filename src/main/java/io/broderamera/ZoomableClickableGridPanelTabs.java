package io.broderamera;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *  Fix assignment on line 1215 in createCloseTabButton - the index that 
 *  the close operation is performed on becomes incorrect when tabs are removed 
 *  and added again 
 */

public class ZoomableClickableGridPanelTabs extends JTabbedPane{    
    private static final String ADD_TAB_TITLE = "Adding";
    private static final String UNNAMED_TAB_TITLE = "unnamed";

    private static ImageManager imageManager;
    private static Icon closeIcon;
    private static Icon addIcon;
    
    private static int tabIndex; 
    private static List<String> tabNames;
    private static HashMap<Integer, ZoomableClickableGridPanel> zoomableClickableGridPanels;
    
    public ZoomableClickableGridPanelTabs() {
        tabIndex = 0;
        tabNames = new ArrayList<>();
        zoomableClickableGridPanels = new HashMap<>();
        
        // Initialize SvgStack and ImageManager to get x and + icons
        imageManager = new ImageManager();
        closeIcon = new ImageIcon(imageManager.getImage("X.svg"));
        addIcon = new ImageIcon(imageManager.getImage("plus.svg"));
        
        //addStartTab();
        addAddTabButton();
        
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Get the currently selected tab index
                int selectedIndex = getSelectedIndex();

                printState(selectedIndex);

                try {
                    Palette.setActiveClickableGridPanel(selectedIndex);
                    getPanel(selectedIndex).updateGrid();
                } catch (Exception ex) {
                    System.err.println("Error changing tab: " + ex.getMessage());
                }      
            }
        });
    }

    // To be implemented
    private void addStartTab() {
        addTab(String.valueOf(getTabCount()), null);
        // Tab name label
        JLabel label = new JLabel("Welcome");
        // Close button
        JButton closeButton = createCloseTabButton(tabIndex);
        // Tab component
        JPanel tabComponent = createTabComponent(label, closeButton);
        setTabComponentAt(getTabCount() - 1, tabComponent);
        setTitleAt(getTabCount() - 1, "Welcome");
        updateTabIndex();
    }
    
    // Add new tab at the last index
    private void addNewTab() {
        ZoomableClickableGridPanel panel = new ZoomableClickableGridPanel(100, 100);
        zoomableClickableGridPanels.put(getTabIndex(), panel);
        
        // Tab name string
        String tabTitle = getTabTitle();
        // Add to tablist
        tabNames.add(tabTitle);
        // Tab name label
        JLabel label = new JLabel(tabTitle);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // Close button
        JButton closeButton = createCloseTabButton(tabIndex);
        // Tab component
        JPanel tabComponent = createTabComponent(label, closeButton);
        tabComponent.putClientProperty("tabName", tabTitle);
        // Set the custom tab component
        addTab(String.valueOf(getTabCount()), panel);
        setTabComponentAt(getTabCount() - 1, tabComponent);
        setTitleAt(getTabCount() - 1, getTabIndex() + "");
        updateTabIndex();
    }

    // Add new tab with button always at last index
    private void addAddTabButton() {
        JButton addButton = createAddTabButton();
        JPanel addTabComponent = createTabComponent(null, addButton);
        addTab(ADD_TAB_TITLE, null);
        setTabComponentAt(getTabCount() - 1, addTabComponent);
    }

    private JButton createAddTabButton() {
        JButton addButton = new JButton();
        addButton.setIcon(addIcon);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewTab();
                removeTabAt(getTabCount() - 2);
                addAddTabButton();
            }
        });
        return addButton;
    }
    
    private JButton createCloseTabButton(int tabIndex) {
        JButton closeButton = new JButton();
        closeButton.setIcon(closeIcon);
        // Refrence to this closing tab index has to change when other tabs are closed
        closeButton.putClientProperty("tabIndex", tabIndex);
        closeButton.setPreferredSize(new Dimension(20, 20));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int thisTabIndex = (int) closeButton.getClientProperty("tabIndex");

                for (int i = 0; i < tabNames.size(); i++) {
                    JPanel thisTab = (JPanel) getTabComponentAt(thisTabIndex);
                    String thisTabName = (String) thisTab.getClientProperty("tabName");

                    if (tabNames.get(i).equals(thisTabName)) {
                        tabNames.remove(i);
                        break;
                    }
                }

                zoomableClickableGridPanels.remove(thisTabIndex);
                removeTabAt(thisTabIndex);
                updateTabIndex();

                for (int i = thisTabIndex; i < getTabCount() - 1; i++) {
                    setTitleAt(i, i + "");
                    zoomableClickableGridPanels.put(i, getPanel(i + 1));
                    zoomableClickableGridPanels.remove(i + 1);
                }

                try {
                    Palette.setActiveClickableGridPanel(thisTabIndex);
                    getPanel(thisTabIndex).updateGrid();
                } catch (Exception ex) {
                    System.err.println("Error changing tab: " + ex.getMessage());
                }     
            }
        });
        return closeButton;
    }
    
    private JPanel createTabComponent(JLabel label, JButton closeButton) {
        JPanel tabComponent = new JPanel();
        tabComponent.setLayout(new BorderLayout(10, 0));
        if (label != null) {
            tabComponent.add(label, BorderLayout.CENTER);
        }
        tabComponent.add(closeButton, BorderLayout.EAST);
        return tabComponent;
    }

    public static ZoomableClickableGridPanel getPanel(int index) {
        return zoomableClickableGridPanels.get(index);
    }

    private String getTabTitle() {
        int namingIndex = 0;
        for (int i = 0; i < getTabCount(); i++) {
            String tabName;
            if (i == 0) {
                tabName = UNNAMED_TAB_TITLE;
            } else {
                tabName = UNNAMED_TAB_TITLE + "-" + namingIndex; 
            }

            if (tabNames.contains(tabName)) {
                namingIndex++;
            } else {
                return tabName;
            }
        }
        return UNNAMED_TAB_TITLE;
    }

    private int getTabIndex() {
        int namingIndex = 0;
        for (int i = 0; i < getTabCount(); i++) {
            if (!getTitleAt(i).equals(i + "")) {
                namingIndex = i;
                break;
            }
        }
        return namingIndex;
    }

    private void updateTabIndex() {
        tabIndex = getTabCount() - 1;
    }

    private void printState(int index) {
        // Print current system
        System.out.print("[");
        for (int i = 0; i < getTabCount(); i++) {
            System.out.print(indexOfTab(getTitleAt(i)) + " ");
        }
        System.out.print("], [");
        for (int i = 0; i < getTabCount(); i++) {
            System.out.print(getTitleAt(i) + " ");
        }
        System.out.print("], " + zoomableClickableGridPanels.keySet() + ", ");
        System.out.print(tabNames);
        System.out.println(" Changing tab to: " + index);
    }
}