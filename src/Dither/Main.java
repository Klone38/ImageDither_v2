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

    private static BufferedImage srcImg = null;
    private static BufferedImage rawImg = null;

    public static BufferedImage redPart = null;
    public static BufferedImage bluePart = null;
    public static BufferedImage greenPart = null;

    private static boolean enableDithering = true;

    private static int successes = 0;
    private static int failures = 0;

    public static void main(String[] args){
        fetchImage();
        ditherImageGreyscale();
        //ditherImageColored();
        publishImage();

        System.out.println("Successes "+ successes);
        System.out.println("Failures "+ failures);


    }

    private static void fetchImage(){
        try{
            srcImg = ImageIO.read(new File(fileDirectory));
            System.out.println(srcImg.getType());
        } catch (IOException e){
            System.out.println("INVALID FILE");
            System.exit(1);
        }

        rawImg = convertImg(srcImg,BufferedImage.TYPE_3BYTE_BGR);

        System.out.println(rawImg.getType());
        System.out.println("Width "+srcImg.getWidth());
        System.out.println("Height "+srcImg.getHeight());

    }

    public static BufferedImage convertImg(BufferedImage src, int bufImgType) {
        BufferedImage img= new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
        Graphics2D g2d= img.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return img;
    }

    private static void publishImage(){
        try{
            File outputFile = new File(outputDirectory);
            ImageIO.write(rawImg,"png",outputFile);
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





    private static void ditherImageGreyscale(){

        for(int y = 0;y<rawImg.getHeight();y++){
            for(int x = 0;x<rawImg.getWidth();x++){
                int oldPixel = rawImg.getRGB(x,y);
                int newPixel = getClosestGreyscale(oldPixel);
                int quantError = oldPixel - newPixel;

                rawImg.setRGB(x,y,newPixel);
                successes++;

                if(enableDithering){

                    try{
                        if(rawImg.getRGB(x+1,y  )+quantError*7/16 > -1){
                            rawImg.setRGB(x+1,y  ,-1);
                            successes++;
                        }else{
                            rawImg.setRGB(x+1,y  ,rawImg.getRGB(x+1,y  )+quantError*7/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                    try{
                        if(rawImg.getRGB(x+1,y+1)+quantError/16 > -1){
                            rawImg.setRGB(x+1,y+1,-1);
                            successes++;
                        }else{
                            rawImg.setRGB(x+1,y+1,rawImg.getRGB(x+1,y+1)+quantError/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                    try{
                        if(rawImg.getRGB(x  ,y+1)+quantError*5/16 > -1){
                            rawImg.setRGB(x  ,y+1,-1);
                            successes++;
                        }else{
                            rawImg.setRGB(x  ,y+1,rawImg.getRGB(x  ,y+1)+quantError*5/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                    try{
                        if(rawImg.getRGB(x-1,y+1)+quantError*3/16 > -1){
                            rawImg.setRGB(x-1,y+1,-1);
                            successes++;
                        }else{
                            rawImg.setRGB(x-1,y+1,rawImg.getRGB(x-1,y+1)+quantError*3/16);
                            successes++;
                        }
                    } catch(ArrayIndexOutOfBoundsException e){
                        failures++;
                    }

                }

            }

        }

    }


}
