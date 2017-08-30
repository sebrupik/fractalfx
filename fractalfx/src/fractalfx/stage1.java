/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fractalfx;

 
import java.awt.Color;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 *
 * @author snr
 */
public class stage1 extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override public void start(Stage primaryStage) {
        primaryStage.setTitle("Mandelbrot stage 1");
        
        Group root = new Group();
        final mandCanvas canvas = new mandCanvas(800,600);
        
        
        
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
    
    class mandCanvas extends Canvas {
        private final int MAX_ITER;
        private double ZOOM;
        private WritableImage WI;
        private double zx, zy, c_re, c_im, tmp;
        private int iter, height, width;
        private final int[] pallete;
    
        mandCanvas(int MAX_ITER, double ZOOM) {
            this.MAX_ITER = MAX_ITER;
            this.ZOOM = ZOOM;
            
            width = 800;
            height = 600;
            
            pallete = new int[MAX_ITER];
            for (int i = 0; i<pallete.length; i++) 
                pallete[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
            
            
            runCalculation(0, 0);
            System.out.println(WI.toString());
            GraphicsContext gc = this.getGraphicsContext2D();
            
            gc.drawImage(WI, 0,0);
        }
        
        private void runCalculation(int xVal, int yVal) {
            System.out.println("runCalculation");
            WI = new WritableImage(width, height);
            System.out.println(WI.toString());
            int xory = Math.min(width, height);
            
            for (int x = xVal; x < width; x++) {
                for (int y = yVal; y < height; y++) {
                    
                    c_re = (x - width/2) *4.0/ xory;
                    c_im = (y - height/2) *4.0/ xory;
                    
                    //WI.setRGB(x, y, pallete[mandelbrot(c_re, c_im)]);
                    int m = mandelbrot(c_re, c_im);
                    //WI.getPixelWriter().setArgb(x, y, pallete[m]);
                    WI.getPixelWriter().setColor(x, y, new Color(1.0, 1.0, m | m << 8, 0.0));
                }
            }
        }
        
        private int mandelbrot(double c_re, double c_im) {
            zx = zy = 0.0;
            iter = 0;
            while (zx * zx + zy * zy < 4 && iter < MAX_ITER) {
                tmp = zx * zx - zy * zy + c_re;
                zy = 2.0 * zx * zy + c_im;
                zx = tmp;
                iter++;
            }
            return (iter < MAX_ITER) ? iter : 0;
        }
        
    }
}