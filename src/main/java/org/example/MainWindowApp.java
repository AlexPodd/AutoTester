package org.example;

import javax.swing.*;

public class MainWindowApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame window = new MainFrame();
                window.setVisible(true);
            }
        });
    }
}
