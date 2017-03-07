package Dither;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.lang.ArrayIndexOutOfBoundsException;

public class Main {

    private static String fileDirectory = "img/lena.bmp";
    private static String outputDirectory = "img/dithered.gif";

    private static BufferedImage rawImg = null;

    public static BufferedImage redPart = null;
    public static BufferedImage bluePart = null;
    public static BufferedImage greenPart = null;

    private static boolean enableDithering = true;

    private static int successes = 0;
    private static int failures = 0;

    public static void main(String[] args){
        rawImg = fetchImage();
        rawImg = ditherImageGreyscale(rawImg);
        //ditherImageColored();
        publishImage(rawImg);

        System.out.println("Successes "+ successes);
        System.out.println("Failures "+ failures);


    }

    private static BufferedImage fetchImage(){
        BufferedImage img = null;

        try{
            img = ImageIO.read(new File(fileDirectory));
        } catch (IOException e){
            System.out.println("INVALID FILE");
            System.exit(1);
        }

        System.out.println(img.getType());
        img = convertImg(img,BufferedImage.TYPE_3BYTE_BGR);
        System.out.println(img.getType());
        System.out.println("Width "+img.getWidth());
        System.out.println("Height "+img.getHeight());

        return img;
    }

    public static BufferedImage convertImg(BufferedImage src, int bufImgType) {
        BufferedImage img= new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
        Graphics2D g2d= img.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return img;
    }

    private static void publishImage(BufferedImage img){
        try{
            File outputFile = new File(outputDirectory);
            ImageIO.write(img,"gif",outputFile);
        }catch(IOException e){
            System.out.println("WRITE FAILURE");
            System.exit(1);
        }

        System.out.println("Dithering Finished");
    }

    private static int getClosestGreyscale(int rgb){
        int x;


        if(rgb<=-10000001 && rgb>=-16777777){
            x = -16777216;
        }else if(rgb<=-6000000 && rgb>=-10000000){
            x = -8355712;
        }else if(rgb<=-2000000 && rgb>=-5999999){
            x = -4144960;
        }else{
            x = -1;
        }

        return x;
    }





    private static BufferedImage ditherImageGreyscale(BufferedImage img){

        for(int y = 0; y< img.getHeight(); y++){
            for(int x = 0; x< img.getWidth(); x++){
                int oldPixel = img.getRGB(x,y);
                int newPixel = getClosestGreyscale(oldPixel);
                int quantError = oldPixel - newPixel;

                img.setRGB(x,y,newPixel);
                successes++;

                if(enableDithering){

                    try{
                        if(img.getRGB(x+1,y  )+quantError*7/16 > -1){
                            img.setRGB(x+1,y  ,-1);
                            successes++;
                        }else{
                            img.setRGB(x+1,y  , img.getRGB(x+1,y  )+quantError*7/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                    try{
                        if(img.getRGB(x+1,y+1)+quantError/16 > -1){
                            img.setRGB(x+1,y+1,-1);
                            successes++;
                        }else{
                            img.setRGB(x+1,y+1, img.getRGB(x+1,y+1)+quantError/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                    try{
                        if(img.getRGB(x  ,y+1)+quantError*5/16 > -1){
                            img.setRGB(x  ,y+1,-1);
                            successes++;
                        }else{
                            img.setRGB(x  ,y+1, img.getRGB(x  ,y+1)+quantError*5/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                    try{
                        if(img.getRGB(x-1,y+1)+quantError*3/16 > -1){
                            img.setRGB(x-1,y+1,-1);
                            successes++;
                        }else{
                            img.setRGB(x-1,y+1, img.getRGB(x-1,y+1)+quantError*3/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                }

            }

        }

        return img;

    }


}
