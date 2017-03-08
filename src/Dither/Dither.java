package Dither;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.lang.ArrayIndexOutOfBoundsException;

public class Dither {

    private File inputFile;
    private BufferedImage rawImg;

    private int type;

    private boolean enableDithering = true;

    public static final int GREYSCALE = 0;
    public static final int GREYSCALE_NO_DITHER = 1;
    public static final int COLOR = 2;              // NOT YET IMPLEMENTED
    public static final int COLOR_NO_DITHER = 3;    // NOT YET IMPLEMENTED


    public Dither(File inputFile,int type){
        this.type = type;
        this.inputFile = inputFile;
        this.rawImg = null;

        if(type == 1){
            enableDithering = false;
        }
    }

    public Dither(BufferedImage inputImage,int type){
        this.type = type;
        this.inputFile = null;
        this.rawImg = inputImage;

        if(type == 1){
            enableDithering = false;
        }
    }

    public BufferedImage ditherImg(){
        this.rawImg = prepImage();
        this.rawImg = ditherImageGreyscale(this.rawImg);
        //ditherImageColored();
        return this.rawImg;

    }

    private BufferedImage prepImage(){
        BufferedImage img = null;

        if(this.rawImg == null && this.inputFile != null) {
            try {
                img = ImageIO.read(inputFile);
            } catch (IOException e) {
                System.out.println("INVALID FILE");
                System.exit(1);
            }
        } else {
            img = this.rawImg;
        }

        System.out.println(img.getType());
        img = convertImg(img,BufferedImage.TYPE_3BYTE_BGR);
        System.out.println(img.getType());
        System.out.println("Width "+img.getWidth());
        System.out.println("Height "+img.getHeight());

        return img;
    }

    private BufferedImage convertImg(BufferedImage src, int bufImgType) {
        BufferedImage img= new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
        Graphics2D g2d= img.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return img;
    }

    /* Depreciated
    private void publishImage(BufferedImage img){
        try{
            File outputFile = new File(outputDirectory);
            ImageIO.write(img,"gif",outputFile);
        }catch(IOException e){
            System.out.println("WRITE FAILURE");
            System.exit(1);
        }

        System.out.println("Dithering Finished");
    }
    */

    private int getClosestGreyscale(int rgb){
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





    private BufferedImage ditherImageGreyscale(BufferedImage img){

        for(int y = 0; y< img.getHeight(); y++){
            for(int x = 0; x< img.getWidth(); x++){
                int oldPixel = img.getRGB(x,y);
                int newPixel = getClosestGreyscale(oldPixel);
                int quantError = oldPixel - newPixel;

                img.setRGB(x,y,newPixel);

                if(enableDithering){

                    try{
                        if(img.getRGB(x+1,y  )+quantError*7/16 > -1){
                            img.setRGB(x+1,y  ,-1);
                        }else{
                            img.setRGB(x+1,y  , img.getRGB(x+1,y  )+quantError*7/16);
                    }
                    } catch(ArrayIndexOutOfBoundsException e){

                    }

                    try{
                        if(img.getRGB(x+1,y+1)+quantError/16 > -1){
                            img.setRGB(x+1,y+1,-1);
                        }else{
                            img.setRGB(x+1,y+1, img.getRGB(x+1,y+1)+quantError/16);
                        }
                    } catch(ArrayIndexOutOfBoundsException e){

                    }

                    try{
                        if(img.getRGB(x  ,y+1)+quantError*5/16 > -1){
                            img.setRGB(x  ,y+1,-1);
                        }else{
                            img.setRGB(x  ,y+1, img.getRGB(x  ,y+1)+quantError*5/16);
                        }
                    } catch(ArrayIndexOutOfBoundsException e){

                    }

                    try{
                        if(img.getRGB(x-1,y+1)+quantError*3/16 > -1){
                            img.setRGB(x-1,y+1,-1);
                        }else{
                            img.setRGB(x-1,y+1, img.getRGB(x-1,y+1)+quantError*3/16);
                        }
                    } catch(ArrayIndexOutOfBoundsException e){

                    }

                }

            }

        }

        return img;

    }


}
