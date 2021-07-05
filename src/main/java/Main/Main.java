package Main;


import controller.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.jsquish.Squish;
import net.semanticmetadata.lire.utils.FileUtils;
import org.lwjgl.BufferUtils;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

import static model.jsquish.Squish.CompressionType.DXT1;

public class Main extends Application {
    public static Stage mainStage;
    static void staticImageTest(String loadPath,String savePath, String type,int count){
        long totalElapsedTime=0;
        long start;
        long end;

        try {
            ArrayList<String> images = FileUtils.getAllImages(new File(loadPath), true);
            ArrayList<BufferedImage> bufferedImages=new ArrayList<>();
            for (int i = 0; i < images.size(); ++i) {
                bufferedImages.add(ImageIO.read(new File(images.get(i))));
            }

            for(int j=0;j<count;++j) {
                System.out.println("j:"+j);
                try {
                    Files.walk(Paths.get(savePath))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
                catch(Exception ex){

                }

                new File(savePath).mkdirs();
                start = System.currentTimeMillis();
                for (int i = 0; i < bufferedImages.size(); ++i) {
                    ImageIO.write(bufferedImages.get(i), type, new File(savePath+"\\" + i + "."+type));
                }
                end=System.currentTimeMillis() - start;
                totalElapsedTime+=end;
            }
            System.out.println("-------------------------------");
            System.out.println("totalElapsedTime:"+totalElapsedTime/count);
            System.out.println("-------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    static void staticLoadTest(String loadPath,int count){

        ArrayList<String> images = null;
        long start;
        long end;
        long totalElapsedTime=0;
        try {
            //images = FileUtils.getAllImages(new File(loadPath), true);
            String[] filesInDir = new File(loadPath).list();
            System.out.println(loadPath);
            for ( int i=0; i<filesInDir.length; i++ )
            {
                System.out.println( "file: " + filesInDir[i] );
            }
            images=new ArrayList<>();
            for(int k=0;k<filesInDir.length;++k){
                images.add(loadPath+"\\"+filesInDir[k]);
            }
            for(int j=0;j<count;++j) {
                System.out.println("j:"+j);
                ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
                start = System.currentTimeMillis();
                for (int i = 0; i < images.size(); ++i) {
                    bufferedImages.add(ImageIO.read(new File(images.get(i))));
                }
                end=System.currentTimeMillis() - start;
                totalElapsedTime+=end;
            }
            System.out.println("-------------------------------");
            System.out.println("totalElapsedTime:"+totalElapsedTime/count);
            System.out.println("-------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    static void dxtWriteTest(String loadPath,String savePath,int count){
        long totalElapsedTime=0;
        long start;
        long end;

        try {
            ArrayList<String> images = FileUtils.getAllImages(new File(loadPath), true);
            ArrayList<BufferedImage> bufferedImages=new ArrayList<>();
            for (int i = 0; i < images.size(); ++i) {
                bufferedImages.add(ImageIO.read(new File(images.get(i))));
            }

            for(int j=0;j<count;++j) {
                System.out.println("j:"+j);
                try {
                    Files.walk(Paths.get(savePath))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
                catch(Exception ex){

                }

                new File(savePath).mkdirs();
                start = System.currentTimeMillis();
                for (int i = 0; i < bufferedImages.size(); ++i) {

                    /////

                    BufferedImage temp=bufferedImages.get(i);
                    int width=temp.getWidth();
                    int height=temp.getHeight();
                    int[] pixels = new int[width * height];
                    temp.getRGB(0, 0, width, height, pixels, 0, width);
                    ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4 );
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            /* Pixel as RGBA: 0xAARRGGBB */
                            int pixel = pixels[y * width + x];
                            /* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
                            buffer.put((byte) ((pixel >> 16) & 0xFF));
                            /* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
                            buffer.put((byte) ((pixel >> 8) & 0xFF));
                            /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                            buffer.put((byte) (pixel & 0xFF));
                            /* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
                            buffer.put((byte) ((pixel >> 24) & 0xFF));
                        }
                    }
                    buffer.flip();

                    byte[] b = new byte[buffer.remaining()];
                    buffer.get(b);
                    byte[] result=Squish.compressImage(b,width,height,null,DXT1);
                    Path path = Paths.get(savePath+"\\" + i + ".dxt1");
                    Files.write(path, result);

                    ////
                    //ImageIO.write(bufferedImages.get(i), type, new File(savePath+"\\" + Integer.toString(i) + "."+type));
                }
                end=System.currentTimeMillis() - start;
                totalElapsedTime+=end;
            }
            System.out.println("-------------------------------");
            System.out.println("totalElapsedTime:"+totalElapsedTime/count);
            System.out.println("-------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void readDxt1(String loadPath,int count){
        ArrayList<String> images = null;
        long start;
        long end;
        long totalElapsedTime=0;
        try {
            //images = FileUtils.getAllImages(new File(loadPath), true);
            String[] filesInDir = new File(loadPath).list();
            System.out.println(loadPath);
            for ( int i=0; i<filesInDir.length; i++ )
            {
                System.out.println( "file: " + filesInDir[i] );
            }
            images=new ArrayList<>();
            for(int k=0;k<filesInDir.length;++k){
                images.add(loadPath+"\\"+filesInDir[k]);
            }

            for(int j=0;j<count;++j) {
                System.out.println("j:"+j);
                ArrayList<byte[]> bufferedImages = new ArrayList<>();
                start = System.currentTimeMillis();
                for (int i = 0; i < images.size(); ++i) {
                    RandomAccessFile f = new RandomAccessFile(images.get(i), "r");
                    byte[] b = new byte[(int)f.length()];
                    f.readFully(b);
                    bufferedImages.add(b);
                }
                end=System.currentTimeMillis() - start;
                totalElapsedTime+=end;
            }
            System.out.println("-------------------------------");
            System.out.println("totalElapsedTime:"+totalElapsedTime/count);
            System.out.println("-------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {


System.out.println(Integer.MAX_VALUE);
    

        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage=primaryStage;
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/MainWindow.fxml"));
            root = loader.load();
            MainWindowController controller = loader.getController();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Allamvizsga");
            primaryStage.setScene(scene);
            primaryStage.setOnHidden(e ->{
                controller.shutdown();
                System.exit(0);
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
