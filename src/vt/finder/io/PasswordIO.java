package vt.finder.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * encrypts a passed in string and saves it to a default file specified in the
 * constructor
 * 
 * @author Ethan Gaebel (egaebel)
 * 
 */
public class PasswordIO {

    // ~Constants
    /**
     * key used to encrypt the username and password
     */
    private static final char KEY = '#';

    // ~Data Fields--------------------------------------------
    /**
     * file which this class will read from and write to
     */
    private File passwordFile;

    // ~Constructors--------------------------------------------
    /**
     * only constructor, takes a file to use as an argument
     * 
     * @param file
     *            , the file that this class is to write to
     */
    public PasswordIO(File file) {

        passwordFile = file;
    }

    // ~Methods--------------------------------------------
    /**
     * checks to see if the password file passed in exists, returns true if it
     * does
     * 
     * @return true if file exists, false otherwise
     */
    public boolean fileExists() {

        boolean value = false;

        if (passwordFile != null) {

            value = passwordFile.exists();
        }

        return value;
    }

    /**
     * front for main writeEncrypt method, takes two strings, turns them into a
     * 2 element string array, and passes the array to the main writeEncrypt
     * method
     * 
     * @param username the username
     * @param password the password
     */
    public void writeEncrypt(String username, String password) {

        String[] loginData = new String[2];
        loginData[0] = username;
        loginData[1] = password;

        writeEncrypt(loginData);
    }

    /**
     * takes in a String array and encrypts the two (or first two) members of
     * it, and writes the result to file
     * 
     * @param loginData
     *            , String array containing the username and password of the
     *            user
     */
    public void writeEncrypt(String[] loginData) {

        // encrypt data
        decoderRing(loginData);

        // write data to file
        try {

            // add a space to differentiate between strings in file
            loginData[0] += " ";

            // allocate resources to write username
            // gets the bytes from the text string, and creates an Input Stream
            InputStream is = new ByteArrayInputStream(loginData[0].getBytes());
            // creates an output stream for the FILEIO's myFile
            OutputStream os = new FileOutputStream(passwordFile);

            // creates a byte array the size of the available members in is
            // InputStream
            // used to copy data to file
            byte[] data = new byte[is.available()];

            // read bytes into data byte array
            is.read(data);
            // copy bytes into file
            os.write(data);

            // reallocate resources to write password
            is = new ByteArrayInputStream(loginData[1].getBytes());
            data = new byte[is.available()];

            // read bytes into data byte array
            is.read(data);
            // copy bytes into file
            os.write(data);

            // close down streams
            is.close();
            os.close();
        }
        catch (IOException e) {

            // Unable to create file, likely because external storage is
            // not currently mounted.
            e.printStackTrace();
        }

    }

    /**
     * reads from file (if it exists) the user data saved, decrypts it, and
     * returns the decrypted strings in a 2 element String array
     * 
     * @return loginData the user's unencrypted username and password, in that
     *         order
     */
    public String[] readDecrypt() {

        String[] loginData = new String[2];
        loginData[0] = "";
        loginData[1] = "";

        // read data from file
        if (fileExists()) {

            try {

                Scanner in = new Scanner(passwordFile);

                // loop through all space separated elements in passwordFile
                for (int i = 0; i < 2 && in.hasNext(); i++) {

                    loginData[i] = in.next();
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                loginData[0] = "";
                loginData[1] = "";
            }

            // decrypt data
            decoderRing(loginData);
        }

        return loginData;
    }
    
    /**
     * decrypts or encrypts user data, depends on what form it is in when its
     * passed in. alters the passed in String array
     * 
     * @param loginData
     *            the encrypted or regular string of loginData from the user
     */
    private void decoderRing(String[] loginData) {

        if (loginData.length == 2) {

            char[] temp = loginData[0].toCharArray();
            for (int i = 0; i < loginData[0].length(); i++) {

                temp[i] ^= KEY;
            }
            loginData[0] = String.valueOf(temp);
            temp = loginData[1].toCharArray();

            for (int i = 0; i < loginData[1].length(); i++) {

                temp[i] ^= KEY;
            }
            loginData[1] = String.valueOf(temp);
        }
    }
}
