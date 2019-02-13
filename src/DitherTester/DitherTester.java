package DitherTester;

import Dither.Dither;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Karl on 3/7/2017.
 */
public class DitherTester {

    static String inputDirectory = "img/parrot.bmp";
    static String outputDirectory = "img/dithered.gif";

    public static void main(String[] args){

        Dither imgDitherer = new Dither(new File(inputDirectory),Dither.GREYSCALE);

        BufferedImage img = imgDitherer.ditherImg();

        try{
            File outputFile = new File(outputDirectory);
            ImageIO.write(img,"gif",outputFile);
        }catch(IOException e){
            System.out.println("WRITE FAILURE");
            System.exit(1);
        }
    }
}
