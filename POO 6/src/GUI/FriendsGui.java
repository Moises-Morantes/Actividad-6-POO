package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FriendsGui extends JFrame {

    private JTextField nameField, numberField;
    private JTextArea displayArea;
    private JButton addButton, updateButton, deleteButton, displayButton, clearButton;

    public FriendsGui() {
        // Configuración de la ventana principal
        setTitle("Friend Manager");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear panel para los campos de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Number:"));
        numberField = new JTextField();
        inputPanel.add(numberField);

        add(inputPanel, BorderLayout.NORTH);

        // Crear área de texto para mostrar los contactos
        displayArea = new JTextArea();
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Crear panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5)); // Ahora 5 columnas para incluir el botón de limpiar

        addButton = new JButton("Add");
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        buttonPanel.add(deleteButton);

        displayButton = new JButton("Display");
        buttonPanel.add(displayButton);

        // Crear el botón "Clear" para limpiar los campos
        clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Configuración de eventos para los botones
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFriend();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFriend();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteFriend();
            }
        });

        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayFriends();
            }
        });

        // Configuración del evento para limpiar los campos de texto
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields(); // Llama al método que limpia los campos
            }
        });
    }

    private void addFriend() {
        String name = nameField.getText();
        long number = Long.parseLong(numberField.getText());

        try {
            File file = new File("friendsContact.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String existingName = lineSplit[0];
                long existingNumber = Long.parseLong(lineSplit[1]);

                if (existingName.equals(name) || existingNumber == number) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                String nameNumberString = name + "!" + number;
                raf.writeBytes(nameNumberString);
                raf.writeBytes(System.lineSeparator());
                displayArea.setText("Friend added.");
            } else {
                displayArea.setText("The friend already exists.");
            }

            raf.close();
        } catch (IOException | NumberFormatException e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void updateFriend() {
        String name = nameField.getText();
        long number = Long.parseLong(numberField.getText());

        try {
            File file = new File("friendsContact.txt");
            if (!file.exists()) {
                displayArea.setText("No contacts to update.");
                return;
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            File tmpFile = new File("temp.txt");
            RandomAccessFile tmpraf = new RandomAccessFile(tmpFile, "rw");

            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String existingName = lineSplit[0];

                if (existingName.equals(name)) {
                    nameNumberString = name + "!" + number;
                    found = true;
                }
                tmpraf.writeBytes(nameNumberString);
                tmpraf.writeBytes(System.lineSeparator());
            }

            if (found) {
                raf.seek(0);
                tmpraf.seek(0);

                while (tmpraf.getFilePointer() < tmpraf.length()) {
                    raf.writeBytes(tmpraf.readLine());
                    raf.writeBytes(System.lineSeparator());
                }

                raf.setLength(tmpraf.length());
                displayArea.setText("Friend updated.");
            } else {
                displayArea.setText("Friend not found.");
            }

            tmpraf.close();
            raf.close();
            tmpFile.delete();
        } catch (IOException | NumberFormatException e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void deleteFriend() {
        String name = nameField.getText();

        try {
            File file = new File("friendsContact.txt");
            if (!file.exists()) {
                displayArea.setText("No contacts to delete.");
                return;
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            File tmpFile = new File("temp.txt");
            RandomAccessFile tmpraf = new RandomAccessFile(tmpFile, "rw");

            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String existingName = lineSplit[0];

                if (!existingName.equals(name)) {
                    tmpraf.writeBytes(nameNumberString);
                    tmpraf.writeBytes(System.lineSeparator());
                } else {
                    found = true;
                }
            }

            if (found) {
                raf.seek(0);
                tmpraf.seek(0);

                while (tmpraf.getFilePointer() < tmpraf.length()) {
                    raf.writeBytes(tmpraf.readLine());
                    raf.writeBytes(System.lineSeparator());
                }

                raf.setLength(tmpraf.length());
                displayArea.setText("Friend deleted.");
            } else {
                displayArea.setText("Friend not found.");
            }

            tmpraf.close();
            raf.close();
            tmpFile.delete();
        } catch (IOException e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void displayFriends() {
        try {
            File file = new File("friendsContact.txt");
            if (!file.exists()) {
                displayArea.setText("No contacts to display.");
                return;
            }

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            StringBuilder contacts = new StringBuilder();

            while (raf.getFilePointer() < raf.length()) {
                String nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");
                String name = lineSplit[0];
                long number = Long.parseLong(lineSplit[1]);

                contacts.append("Name: ").append(name).append(", Number: ").append(number).append("\n");
            }

            displayArea.setText(contacts.toString());
            raf.close();
        } catch (IOException e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    // Método para limpiar los campos de texto
    private void clearFields() {
        nameField.setText("");
        numberField.setText("");
        displayArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FriendsGui app = new FriendsGui();
            app.setVisible(true);
        });
    }
}
