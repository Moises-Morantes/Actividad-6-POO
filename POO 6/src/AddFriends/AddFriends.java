package AddFriends;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.NumberFormatException;
import java.util.Scanner;

class Addfriend {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Request the friend's name
        System.out.print("Enter the friend's name: ");
        String newName = scanner.nextLine().trim();

        // Request the friend's phone number
        System.out.print("Enter the friend's phone number: ");
        long newNumber = 0;
        try {
            newNumber = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid phone number.");
            scanner.close();
            return;
        }

        try {
            String nameNumberString;
            String name;
            long number;

            // Create or open the file for reading/writing
            File file = new File("friendsContact.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            boolean found = false;

            // Check if a contact with the same name or number already exists
            while (raf.getFilePointer() < raf.length()) {
                nameNumberString = raf.readLine();
                String[] lineSplit = nameNumberString.split("!");

                if (lineSplit.length != 2) {
                    continue; // Skip malformed lines
                }

                name = lineSplit[0].trim();
                number = Long.parseLong(lineSplit[1].trim());

                if (name.equals(newName) || number == newNumber) {
                    found = true;
                    break;
                }
            }

            // If the contact is not found, add it to the file
            if (!found) {
                nameNumberString = newName + "!" + newNumber;
                raf.writeBytes(nameNumberString);
                raf.writeBytes(System.lineSeparator());
                System.out.println("Friend added.");
            } else {
                System.out.println("The contact already exists.");
            }

            // Close the file
            raf.close();
            scanner.close();

        } catch (IOException ioe) {
            System.out.println("I/O Error: " + ioe.getMessage());
        }
    }
}
