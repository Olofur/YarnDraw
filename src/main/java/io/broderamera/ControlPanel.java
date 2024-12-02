package io.broderamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {
    private static boolean color;
    private static boolean symbol;
    
    public ControlPanel() {
        color = true;
        symbol = false;

        Palette.updatePalette();

        JButton buttonA = new JButton("Colors");
        JButton buttonB = new JButton("Symbols");
        JButton buttonC = new JButton("Swap color");
        JButton buttonD = new JButton("Fill in");
        JButton buttonE = new JButton("Reset");

        // JButton buttonF = new JButton("Save");
        // JButton buttonG = new JButton("Load");
        // JButton buttonH = new JButton("Print");

        // JButton buttonI = new JButton("Undo");
        // JButton buttonJ = new JButton("Redo");

        buttonA.addActionListener(new ActionListener() {
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

        buttonD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClickableGridPanel.changeFillIn();
                if (ClickableGridPanel.getFillIn()) {
                    buttonD.setBackground(Color.LIGHT_GRAY);
                } else {
                    buttonD.setBackground(Color.WHITE);
                }
            }
        });

        buttonE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the grid?");
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }
                ClickableGridPanel.resetGrid();
            }
        });

        add(buttonA);
        add(buttonB);
        add(buttonC);
        add(buttonD);        
        add(buttonE);
        // this.add(buttonF);        
        // this.add(buttonG);
        // this.add(buttonH);  
        // this.add(buttonI);
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

    public void main(String[] args) {
        JFrame frame = new JFrame();
        ControlPanel panel = new ControlPanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
