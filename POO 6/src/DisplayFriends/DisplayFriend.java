package DisplayFriends;

// Java program to read from file "friendsContact.txt"
// and display the contacts

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.NumberFormatException;

class DisplayFriends {

    public static void main(String data[]) {

        try {

            String nameNumberString;
            String name;
            long number;

            // Create or open the file in read/write mode
            File file = new File("friendsContact.txt");

            if (!file.exists()) {
                System.out.println("The file 'friendsContact.txt' does not exist.");
                return;
            }

            // Open the file in read mode
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            // Traverse the file to read the contacts
            while (raf.getFilePointer() < raf.length()) {

                // Read line from the file
                nameNumberString = raf.readLine();

                // Split name and number
                String[] lineSplit = nameNumberString.split("!");

                if (lineSplit.length != 2) {
                    System.out.println("Incorrect format on line: " + nameNumberString);
                    continue; // Skip malformed lines
                }

                // Split name and number
                name = lineSplit[0].trim();
                number = Long.parseLong(lineSplit[1].trim());

                // Display contact information
                System.out.println("Friend's Name: " + name + "\n"
                        + "Contact Number: " + number + "\n");
            }

            // Close the file
            raf.close();

        } catch (IOException ioe) {
            System.out.println("I/O Error: " + ioe.getMessage());
        } catch (NumberFormatException nef) {
            System.out.println("Incorrect phone number format: " + nef.getMessage());
        }
    }
}
