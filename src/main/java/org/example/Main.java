package org.example;


import org.example.NonWindowApp.ExploitsWithoutWindow;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ExploitsWithoutWindow exploitsWithoutWindow = new ExploitsWithoutWindow();
        try {
            exploitsWithoutWindow.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}