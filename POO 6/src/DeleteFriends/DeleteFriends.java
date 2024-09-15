package DeleteFriends;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

class DeleteFriends {

    public static void main(String[] data) {

        try {
            // Use Scanner to read data from the keyboard
            Scanner scanner = new Scanner(System.in);

            // Ask for the name of the contact to delete
            System.out.print("Enter the name of the contact to delete: ");
            String inputName = scanner.nextLine();

            String nameNumberString;
            String name;
            long number;
            int index;

            // Create or open the file
            File file = new File("friendsContact.txt");

            if (!file.exists()) {
                System.out.println("The file 'friendsContact.txt' does not exist.");
                return;
            }

            // Open the file for reading/writing
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            // Search if the contact exists
            while (raf.getFilePointer() < raf.length()) {
                nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");

                if (lineSplit.length != 2) {
                    continue; // Skip malformed lines
                }

                name = lineSplit[0].trim();
                number = Long.parseLong(lineSplit[1].trim());

                // Check if the name matches
                if (name.equals(inputName)) {
                    found = true;
                    break;
                }
            }

            // Delete the contact if found
            if (found) {

                // Create a temporary file
                File tmpFile = new File("temp.txt");
                RandomAccessFile tmpraf = new RandomAccessFile(tmpFile, "rw");

                // Go back to the beginning of the original file
                raf.seek(0);

                // Copy all contacts except the one to delete
                while (raf.getFilePointer() < raf.length()) {
                    nameNumberString = raf.readLine();
                    index = nameNumberString.indexOf('!');
                    name = nameNumberString.substring(0, index);

                    // If it's the contact to delete, skip it
                    if (name.equals(inputName)) {
                        continue;
                    }

                    // Write to the temporary file
                    tmpraf.writeBytes(nameNumberString);
                    tmpraf.writeBytes(System.lineSeparator());
                }

                // Copy the content from the temporary file to the original file
                raf.seek(0);
                tmpraf.seek(0);

                while (tmpraf.getFilePointer() < tmpraf.length()) {
                    raf.writeBytes(tmpraf.readLine());
                    raf.writeBytes(System.lineSeparator());
                }

                // Adjust the size of the original file
                raf.setLength(tmpraf.length());

                // Close both files
                tmpraf.close();
                raf.close();

                // Delete the temporary file
                tmpFile.delete();

                System.out.println("Friend deleted.");
            } else {
                // If the contact is not found
                raf.close();
                System.out.println("The contact does not exist.");
            }

        } catch (IOException ioe) {
            System.out.println("I/O Error: " + ioe.getMessage());
        }
    }
}
