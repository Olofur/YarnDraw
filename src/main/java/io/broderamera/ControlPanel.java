package io.broderamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {
    private String represent = "Color";
    
    public ControlPanel() {
        JButton buttonA = new JButton("Colors");
        JButton buttonB = new JButton("Symbols");

        buttonA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonA.setBackground(Color.LIGHT_GRAY);
                buttonB.setBackground(Color.WHITE);
                setRepresent("Colors");
                // update grid panel
                // update palette color buttons
                System.out.println("You chose color representation");
            }
        });

        buttonB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonA.setBackground(Color.WHITE);
                buttonB.setBackground(Color.LIGHT_GRAY);
                setRepresent("Symbols");
                // update grid panel
                // update palette color buttons
                System.out.println("You chose symbol representation");
            }
        });

        buttonA.setBackground(Color.LIGHT_GRAY);
        buttonB.setBackground(Color.WHITE);

        this.add(buttonA);
        this.add(buttonB);
    }

    public void setRepresent(String represent) {
        this.represent = represent;
    }

    public String getRepresent() {
        return represent;
    }

    public void main(String[] args) {
        JFrame frame = new JFrame();
        ControlPanel panel = new ControlPanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
