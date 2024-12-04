package io.broderamera;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ControlPanel extends JPanel {
    private static boolean color;
    private static boolean symbol;
    private static boolean fillIn;
    
    public ControlPanel() {
        color = true;
        symbol = false;
        fillIn = false;

        Palette.updatePalette();

        JButton buttonA = new JButton("Colors");
        JButton buttonB = new JButton("Symbols");
        JButton buttonC = new JButton("Swap color");
        JButton buttonD = new JButton("Fill in");
        JButton buttonE = new JButton("Reset");

        JButton buttonF = new JButton("Save");
        JButton buttonG = new JButton("Load");
        // JButton buttonH = new JButton("Print");

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
                setColor();
                Palette.updatePalette();
                Palette.updateActive();
                ClickableGridPanel.updateGrid();
                System.out.println("Color representation is: " + color);
            }
        });
        buttonA.setBackground(Color.LIGHT_GRAY);

        buttonB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (symbol) {
                    buttonB.setBackground(Color.WHITE);
                } else {
                    buttonB.setBackground(Color.LIGHT_GRAY);
                }
                setSymbol();
                Palette.updatePalette();
                Palette.updateActive();
                ClickableGridPanel.updateGrid();
                System.out.println("Symbol representation is: " + symbol);
            }
        });
        buttonB.setBackground(Color.WHITE);

        buttonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                ColorWheel colorWheel = new ColorWheel();

                JButton changeButton = new JButton("Change Color");
                changeButton.setPreferredSize(new Dimension(136, 20));
                changeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String colorString = ColorWheel.getColorFieldText();
                        try {
                            Color color = Color.decode(colorString);
                            if (Palette.getActiveColor().equals(null)) {
                                System.out.println("Can not change color because no color has been chosen");
                                return;
                            }
                            if (Palette.getKeyForColor(color) == -1) {
                                // Change active color for new color
                                Palette.getBiglyMap().get(Palette.getActiveKey()).setColor(color);
                                Palette.updatePalette();
                                Palette.updateActive();
                                ClickableGridPanel.updateGrid();
                                frame.dispose();
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
                        frame.dispose();
                    }
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(changeButton);
                buttonPanel.add(exitButton);

                frame.add(colorWheel);
                frame.add(buttonPanel, BorderLayout.SOUTH);
                frame.pack();
                frame.setVisible(true);
            }
        });
        buttonC.setBackground(Color.WHITE);

        buttonD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeFillIn();
                if (getFillIn()) {
                    buttonD.setBackground(Color.LIGHT_GRAY);
                } else {
                    buttonD.setBackground(Color.WHITE);
                }
            }
        });
        buttonD.setBackground(Color.WHITE);

        buttonE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the grid?");
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }
                ClickableGridPanel.resetGrid();
            }
        });
        buttonE.setBackground(Color.WHITE);

        buttonF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] dimensions = ClickableGridPanel.getGridSize();
                int[] keyGrid = ClickableGridPanel.getKeyGrid();
                HashMap<Integer, ColorSymbol> biglyMap = Palette.getBiglyMap();

                String filename = "saving.csv";
                try (FileWriter writer = new FileWriter(filename)) {
                    // Save dimensions at the top of the file
                    writer.write(dimensions[0] + ", " + dimensions[1] + "\n");
                    // Save color code mappings at the top of the file
                    for (Map.Entry<Integer, ColorSymbol> entry : biglyMap.entrySet()) {
                        int key = entry.getKey();
                        Color color = entry.getValue().color();
                        String hexColor = String.format("#%06X", (0xFFFFFF & color.getRGB()));
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
        });
        buttonF.setBackground(Color.WHITE);

        buttonG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<Integer, ColorSymbol> loadBiglyMap  = new HashMap<Integer, ColorSymbol>();

                SvgStack loadStack = Palette.initializeNewStack(); 
                ImageManager loadManager = new ImageManager();

                String filename = "saving.csv";
                try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                    String firstLine = reader.readLine();
                    int[] loadDimensions = Arrays.stream(firstLine.split(", ")).mapToInt(Integer::parseInt).toArray();
                    int[] loadKeyGrid = new int[loadDimensions[0] * loadDimensions[1]];

                    ColorSymbol.resetKeyCount();
                    int row = 0;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("#")) {
                            String colorString = line.split(", ")[1];
                            Color color = Color.decode(colorString);
                            String item = loadStack.popItem();
                            BufferedImage symbol = loadManager.getImage(item);
                            ColorSymbol cs =new ColorSymbol(color, symbol, item);

                            loadBiglyMap.put(cs.getKey(), cs);
                        } else {
                            int[] lineValues = Arrays.stream(line.split(", ")).mapToInt(Integer::parseInt).toArray();
        
                            // copy the elements to the desired part of the original array
                            System.arraycopy(lineValues, 0, loadKeyGrid, row, lineValues.length);
                            
                            // or use IntStream.forEach
                            int thisRow = row;
                            Arrays.stream(line.split(", ")).mapToInt(Integer::parseInt)
                                .forEach(value -> loadKeyGrid[thisRow] = value);
                            
                            row += lineValues.length;
                        }
                    }
                    // two possible approaches ; 
                    // redraw all components, grid, palette 
                    // make new instance of the program for new components

                    //1
                    Palette.clearPalette();
                    for (Map.Entry<Integer, ColorSymbol> entry : loadBiglyMap.entrySet()) {
                        int key = entry.getKey();
                        ColorSymbol cs = entry.getValue();
                        
                        Palette.addToBiglyMap(key, cs);
                    }
                    Palette.updatePalette();
                    ClickableGridPanel.setGridSize(loadDimensions[0], loadDimensions[1]);
                    ClickableGridPanel.reinitializeGrid();
                    ClickableGridPanel.loadKeys(loadKeyGrid);

                } catch (IOException ioe) {
                    System.err.println("Error reading file: " + ioe.getMessage());
                }
               // clear biglyMap
               // read file into keyGrid and biglyMap
               // paint grid with keyGrid
               // update palette
               // update active
               // update grid
            }
        });
        buttonG.setBackground(Color.WHITE);

        add(buttonA);
        add(buttonB);
        add(buttonC);
        add(buttonD);        
        add(buttonE);
        add(buttonF);        
        add(buttonG);
        // add(buttonH);  
        // add(buttonI);
        // add(buttonJ);
    }

    public static void setColor() {
        ControlPanel.color = !ControlPanel.color;
    }

    public static boolean showColor() {
        return color;
    }
    
    public static void setSymbol() {
        ControlPanel.symbol = !ControlPanel.symbol;
    }

    public static boolean showSymbol() {
        return symbol;
    }

    public static void changeFillIn() {
        ControlPanel.fillIn = !ControlPanel.fillIn;
    }

    public static boolean getFillIn() {
        return fillIn;
    }

    public void main(String[] args) {
        JFrame frame = new JFrame();
        ControlPanel panel = new ControlPanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
