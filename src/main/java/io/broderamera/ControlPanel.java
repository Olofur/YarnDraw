package io.broderamera;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


/**
 * Represents the control panel for the application, which contains buttons for
 * different actions, including color and symbol representation, fill in and
 * reset the grid, save, load and print patterns as well as to undo, and redo
 * actions. The representation buttons relate to the same property, whereas
 * the other buttons perform different actions.
 * 
 * @author Olofur
 */
public class ControlPanel extends JPanel {
    private static boolean color;
    private static boolean symbol;
    private static boolean fillInActive;
    
    private String directoryPath = System.getProperty("user.dir");
    
    /**
     * Default constructor for the control panel with a width of 200
     */
    public ControlPanel() {
        this(200);
    }
    
    /**
     * Constructor for the control panel with a specified width
     * 
     * @param width The width of the control panel
     */
    public ControlPanel(int width) {
        color = true;
        symbol = false;
        fillInActive = false;
        
        Palette.updatePalette();
        
        JButton buttonA = new JButton("Colors");
        JButton buttonB = new JButton("Symbols");
        JButton buttonC = new JButton("Swap color");
        JButton buttonD = new JButton("Fill in");
        JButton buttonE = new JButton("Reset");
        JButton buttonF = new JButton("Save");
        JButton buttonG = new JButton("Load");
        JButton buttonH = new JButton("Print");
        // JButton buttonI = new JButton("Undo");
        // JButton buttonJ = new JButton("Redo");
        
        buttonA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (color) {
                    buttonA.setBackground(Color.WHITE);
                } else {
                    buttonA.setBackground(Color.LIGHT_GRAY);
                }
                int key = Palette.getActiveKey();
                flipDisplayColor();
                Palette.updatePalette();
                Palette.setActive(key);
                ZoomableClickableGridPanel activePanel = ZoomableClickableGridPanelTabs.getPanel(Palette.getActiveClickableGridPanel());
                activePanel.updateGrid();
                System.out.println("Color representation is: " + color);
            }
        });
        
        buttonB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (symbol) {
                    buttonB.setBackground(Color.WHITE);
                } else {
                    buttonB.setBackground(Color.LIGHT_GRAY);
                }
                int key = Palette.getActiveKey();
                flipDisplaySymbol();
                Palette.updatePalette();
                Palette.setActive(key);
                ZoomableClickableGridPanel activePanel = ZoomableClickableGridPanelTabs.getPanel(Palette.getActiveClickableGridPanel());
                activePanel.updateGrid();
                System.out.println("Symbol representation is: " + symbol);
            }
        });
        
        buttonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                swapColor();
            }
        });
        
        buttonD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flipFillInActive();
                if (isFillInActive()) {
                    buttonD.setBackground(Color.LIGHT_GRAY);
                } else {
                    buttonD.setBackground(Color.WHITE);
                }
            }
        });
        
        buttonE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the grid?");
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }
                ZoomableClickableGridPanel activePanel = ZoomableClickableGridPanelTabs.getPanel(Palette.getActiveClickableGridPanel());
                activePanel.resetGrid();
            }
        });
        
        buttonF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePatternToFile();
            }
        });
        
        buttonG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPatternFromFile();
            }
        });

        buttonH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printPattern();
            }
        });
        
        buttonA.setBackground(Color.LIGHT_GRAY);
        buttonB.setBackground(Color.WHITE);
        buttonC.setBackground(Color.WHITE);
        buttonD.setBackground(Color.WHITE);
        buttonE.setBackground(Color.WHITE);
        buttonF.setBackground(Color.WHITE);
        buttonG.setBackground(Color.WHITE);
        buttonH.setBackground(Color.WHITE);
        // buttonI.setBackground(Color.WHITE);
        // buttonJ.setBackground(Color.WHITE);
        
        add(buttonA);
        add(buttonB);
        add(buttonC);
        add(buttonD);        
        add(buttonE);
        add(buttonF);        
        add(buttonG);
        add(buttonH);  
        // add(buttonI);
        // add(buttonJ);
        
        setLayout(new GridLayout(3, 3, 5, 10));
    }
    
    /**
     * Method to swap the color of the active color panel
     */
    private void swapColor() {
        JFrame swapColorFrame = new JFrame();
        
        ColorWheel colorWheel = new ColorWheel(300);
        JButton changeButton = new JButton("Change Color");
        changeButton.setPreferredSize(new Dimension(136, 20));
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colorString = ColorWheel.getColorFieldText();
                try {
                    Color color = Color.decode(colorString);
                    int key = Palette.getKeyForColor(color);
                    if (Palette.getActiveColor().equals(null)) {
                        System.out.println("Can not change color because no color has been chosen");
                        return;
                    }
                    if (key == -1) {
                        // Change active color for new color
                        Palette.getcolorSymbolMap().get(Palette.getActiveKey()).setColor(color);
                        Palette.updatePalette();
                        Palette.setActive(key);
                        ZoomableClickableGridPanel activePanel = ZoomableClickableGridPanelTabs.getPanel(Palette.getActiveClickableGridPanel());
                        activePanel.updateGrid();
                        swapColorFrame.dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid color code");
                }
            }
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(136, 20));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                swapColorFrame.dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(changeButton);
        buttonPanel.add(exitButton);
        
        swapColorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        swapColorFrame.add(colorWheel);
        swapColorFrame.add(buttonPanel, BorderLayout.SOUTH);
        swapColorFrame.pack();
        swapColorFrame.setVisible(true);
    }
    
    /**
     * Method to save the current active pattern to a file
     */
    public void savePatternToFile() {
        String filename = JOptionPane.showInputDialog("Enter a filename:");

        if (filename == null) {
            return;
        }
        if (!filename.endsWith(".csv")) {
            filename += ".csv";
        }
        File directory = new File(directoryPath);
        File file = new File(directory, filename);
        if (file.exists()) {
            int option = JOptionPane.showConfirmDialog(null, "File already exists. Do you want to overwrite it?");
            if (option != JOptionPane.OK_OPTION) {
                return;
            }
        }
        filename = directoryPath + "/savefiles/" + filename;

        ZoomableClickableGridPanel activePanel = ZoomableClickableGridPanelTabs.getPanel(Palette.getActiveClickableGridPanel());
        int[] dimensions = activePanel.getGridSize();
        int[] keyGrid = activePanel.getKeyGrid();
        HashMap<Integer, ColorSymbol> colorSymbolMap = Palette.getcolorSymbolMap();
        
        try (FileWriter writer = new FileWriter(filename)) {
            // Save dimensions at the top of the file
            writer.write(dimensions[0] + ", " + dimensions[1] + "\n");
            // Save color code mappings at the top of the file
            for (Map.Entry<Integer, ColorSymbol> entry : colorSymbolMap.entrySet()) {
                int key = entry.getKey();
                Color color = entry.getValue().color();
                String hexColor = Palette.getHexFromColor(color);
                writer.write(key + ", " + hexColor + "\n");
            }
            // Save key map afterwards
            for (int index = 0; index < keyGrid.length; index++) {
                int column = index % dimensions[1];
                if (column == 0 && index != 0) {
                    writer.write("\n");
                }
                writer.write(keyGrid[index] + ", ");
            }
        } catch (IOException error) {
            System.err.println("Error writing to file: " + error.getMessage());
        }
        System.out.println("Saved to " + filename);
    }
    
    /**
     * Method to load a pattern from a file
     */
    public void loadPatternFromFile() {
        String filename = getLoadFile();
        if (filename == null) {
            return;
        }

        // SvgStack loadStack = Palette.initializeNewStack(); 
        // ImageManager loadManager = new ImageManager();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String firstLine = reader.readLine();
            int[] loadDimensions = Arrays.stream(firstLine.split(", ")).mapToInt(Integer::parseInt).toArray();
            int[] loadKeyGrid = new int[loadDimensions[0] * loadDimensions[1]];
            HashMap<Integer, ColorSymbol> loadcolorSymbolMap  = new HashMap<Integer, ColorSymbol>();
            HashMap<Integer, Integer> loadKeyMap = new HashMap<Integer, Integer>();
            
            int row = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                // First part containing color map
                if (line.contains("#")) {
                    int originalKey = Integer.parseInt(line.split(", ")[0]);
                    String colorString = line.split(", ")[1];
                    Color color = Color.decode(colorString);
                    String item = Palette.getStack().popItem();
                    BufferedImage symbol = Palette.getManager().getImage(item);
                    ColorSymbol colorSymbol = new ColorSymbol(color, symbol, item);
                    
                    loadcolorSymbolMap.put(colorSymbol.getKey(), colorSymbol);
                    loadKeyMap.put(originalKey, colorSymbol.getKey());

                    // Second part containing key grid
                } else {
                    int thisRow = row;
                    int[] keyGridValues = Arrays.stream(line.split(", ")).mapToInt(Integer::parseInt).toArray();
                    
                    // copy the elements to the desired part of the original array
                    System.arraycopy(keyGridValues, 0, loadKeyGrid, row, keyGridValues.length);
                    Arrays.stream(line.split(", ")).mapToInt(Integer::parseInt)
                    .forEach(value -> loadKeyGrid[thisRow] = value);
                    
                    row += keyGridValues.length;
                }
            }
            // Remap the loadKeyGrid according to the loadKeyMap - mapping save file keys to active keys
            for (int index = 0; index < loadKeyGrid.length; index++) {
                int key = loadKeyGrid[index];
                loadKeyGrid[index] = loadKeyMap.get(key);
            }

            for (Map.Entry<Integer, ColorSymbol> entry : loadcolorSymbolMap.entrySet()) {
                for (Map.Entry<Integer, ColorSymbol> matchingEntry : Palette.getcolorSymbolMap().entrySet()) {
                    if (matchingEntry.getValue().color().equals(entry.getValue().color())) {
                        continue;
                    }
                }
                int key = entry.getKey();
                ColorSymbol colorSymbol = entry.getValue();
                Palette.insertcolorSymbolMap(key, colorSymbol);
            }
            Palette.updatePalette();
            ZoomableClickableGridPanel activePanel = ZoomableClickableGridPanelTabs.getPanel(Palette.getActiveClickableGridPanel());
            activePanel.setGridSize(loadDimensions[0], loadDimensions[1]);
            activePanel.reinitializeGrid();
            activePanel.loadKeyGrid(loadKeyGrid);
            
        } catch (IOException ioe) {
            System.err.println("Error reading file: " + ioe.getMessage());
        }
    }
    
    /**
     * Method to get the path of the file to load
     * 
     * @return String
     */
    private String getLoadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to load");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        // Set the chooser to savefiles directory
        String startDirectory = directoryPath + "/savefiles";
        File directory = new File(startDirectory);
        if (directory.exists() && directory.isDirectory()) {
            fileChooser.setCurrentDirectory(directory);
        } else {
            System.out.println("Invalid start directory: " + startDirectory);
        }
        
        // Show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File selectedFile = fileChooser.getSelectedFile();
            
            // Check if the file exists
            if (selectedFile.exists()) {
                try {
                    return selectedFile.getAbsolutePath();
                } catch (Exception e) {
                    System.err.println("Error getting file path: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selected file does not exist.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "No file selected.");
        }
        return null;
    }

    /**
     * Method to print the current pattern to a printer
     */
    private void printPattern() {
        return;
    }

    /**
     * Method to flip the display color boolean
     */
    public static void flipDisplayColor() {
        ControlPanel.color = !ControlPanel.color;
    }
    
    /**
     * Method to get the display color boolean
     * 
     * @return boolean
     */
    public static boolean displayColor() {
        return color;
    }
    
    /**
     * Method to flip the display symbol boolean
     */
    public static void flipDisplaySymbol() {
        ControlPanel.symbol = !ControlPanel.symbol;
    }
    
    /**
     * Method to get the display symbol boolean
     * 
     * @return boolean
     */
    public static boolean displaySymbol() {
        return symbol;
    }
    
    /**
     * Method to flip the fill in active boolean
     */
    public static void flipFillInActive() {
        ControlPanel.fillInActive = !ControlPanel.fillInActive;
    }
    
    /**
     * Method to get the fill in active boolean
     * 
     * @return boolean
     */
    public static boolean isFillInActive() {
        return fillInActive;
    }
    
    public void main(String[] args) {
        ControlPanel panel = new ControlPanel();
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
