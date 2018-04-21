package minesveiper;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A small class for fileHandling.
 * Created by Elise Haram Vannes on 30.07.2017.
 */
public class FileHandling {

    /**
     * Initializes an image from a file.
     * @param location  the location of the file
     * @return          the image that has been found, or null if it was not found
     */
    public static Image initImage(String location){
        try {
            return new Image(new FileInputStream(location));
        } catch(FileNotFoundException e){
            System.out.println("Could not find picture.");
        }
        return null;
    }
}
