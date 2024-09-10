package org.example;

import org.example.Exploit.Exploit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MainFrame extends JFrame {

    private DefaultListModel<String> listModel;
    private ArrayList<String> CVEList;

    private JProgressBar progressBar;
    private JTextArea process;
    private JPanel mainPanel;

    private final String testCommand = "sudo touch /root/superuser_flag";
    private Exploits exploits;
    public MainFrame() {
        super("Exploit Tester");

        CVEList = new ArrayList<>();
        process = new JTextArea();
        exploits = new Exploits(this);

        listModel = new DefaultListModel<>();
        for (Exploit cve: exploits.getExploits()){
            listModel.addElement(cve.getName());
            CVEList.add(cve.getName());
        }

        // Создаем панель для списка атрибутов
        JPanel attributesPanel = new JPanel(new GridBagLayout());

        // Добавляем каждый атрибут как элемент с переключателем
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < listModel.size(); i++) {
            String attribute = listModel.getElementAt(i);
            JCheckBox checkBox = new JCheckBox(attribute);
            checkBox.setSelected(true);
            gbc.gridx = 0;
            gbc.gridy = i;
            attributesPanel.add(checkBox, gbc);

            JLabel label = new JLabel("(Enabled)");
            gbc.gridx = 1;
            attributesPanel.add(label, gbc);

            checkBox.addItemListener(e -> {
                if (checkBox.isSelected()) {
                    label.setText("(Enabled)");
                    exploits.getExploits().get(CVEList.indexOf(checkBox.getActionCommand())).setNeed(true);
                } else {
                    exploits.getExploits().get(CVEList.indexOf(checkBox.getActionCommand())).setNeed(false);
                    label.setText("(Disabled)");
                }
            });
        }

        // Создаем прокручиваемую панель для списка атрибутов
        JScrollPane attributesScrollPane = new JScrollPane(attributesPanel);
        attributesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Панель для кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton toggleButton = new JButton("Run tests");

        JButton CreateButton = new JButton("Add exploit");

        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showProgressBar();

                exploits.execute();
            }
        });
        buttonPanel.add(toggleButton);
        buttonPanel.add(CreateButton);
        CreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                        showAddExploitDialog(exploits);

            }
        });
        // Размещаем компоненты в окне
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(attributesScrollPane, BorderLayout.CENTER); // Добавляем прокручиваемую панель с атрибутами
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Добавляем кнопку внизу

        // Настройки окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(400, 300);
        setLocationRelativeTo(null); // Центрируем окно на экране
    }


    private void showAddExploitDialog(Exploits exploits) {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField nameField = new JTextField();
        JTextField numberField = new JTextField();
        JTextArea textArea = new JTextArea();
        JButton fileButton = new JButton("Select file");
        JLabel fileLabel = new JLabel("No file selected");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("CVSS:"));
        panel.add(numberField);
        panel.add(new JLabel("Fix Patch:"));
        panel.add(new JScrollPane(textArea));
        panel.add(new JLabel("Exploit:"));
        panel.add(fileButton);
        panel.add(fileLabel);

        AtomicReference<String> filepath = new AtomicReference<>(null);
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filepath.set(selectedFile.getAbsolutePath());
                fileLabel.setText(selectedFile.getName());
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Exploit",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String number = numberField.getText();
            String text = textArea.getText();

            // Create and add the new exploit to the list
            Thread thread = new Thread(() -> {
                exploits.AddExploit(name, number, text, filepath.get());
            });
            thread.start();

            listModel.addElement(name);
            CVEList.add(name);
        }
    }

    // Метод для отображения прогресс-бара
    private void showProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            mainPanel.add(progressBar, BorderLayout.NORTH);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
    public void UpdateProgressBar(int progress, int maxvalue){

        SwingUtilities.invokeLater(() -> progressBar.setValue(progress*100/maxvalue));
    }

    public void IsDone(){
        SwingUtilities.invokeLater(() -> progressBar.setValue(100));
    }

    public void ShowErrorMessage(String message){
        JOptionPane.showMessageDialog(this,
                message, // Текст сообщения
                "Error",            // Заголовок окна
                JOptionPane.ERROR_MESSAGE); // Тип сообщения (ошибка)
    }
    public void ShowInfoMessage(String message){
        JOptionPane.showMessageDialog(this,
                message, // Текст сообщения
                "INFO",            // Заголовок окна
                JOptionPane.INFORMATION_MESSAGE); // Тип сообщения (ошибка)
    }


}
