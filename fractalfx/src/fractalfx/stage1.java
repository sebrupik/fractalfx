/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fractalfx;
 
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.WritableImage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author snr
 */
public class stage1 extends Application {
    private ImageView currentImageView;
    private final int MAX_ITER = 750;
    private double ZOOM;
    private WritableImage WI;
    private double zx, zy, c_re, c_im, tmp;
    private int iter, height, width;
    private int[] pallete;
    private final AtomicBoolean calculationInProgress = new AtomicBoolean();
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override public void start(Stage primaryStage) {
        primaryStage.setTitle("Mandelbrot stage 1");
        
        pallete = new int[MAX_ITER];
        for (int i = 0; i<pallete.length; i++)
            pallete[i] = java.awt.Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        
        
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        
        scene.widthProperty().addListener((changed, oldVal, newVal) -> { width = (int)scene.getWidth(); calculate(); } );
        scene.heightProperty().addListener((changed, oldVal, newVal) -> { height = (int)scene.getHeight(); calculate(); } );
        
        primaryStage.setScene(scene);
        
        currentImageView = createImageView(scene);
        
        root.getChildren().add(currentImageView);
        primaryStage.show();
    }
    
    private ImageView createImageView(Scene scene) {
        ImageView imageView = new ImageView();  
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(scene.widthProperty());
        
        imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
           System.out.printf("Mouse pressed in imageView (%f : %f", mouseEvent.getX(), mouseEvent.getY());
           
        });
        
        return imageView;
    }
    
    private WritableImage createMandelbrotImage(int xVal, int yVal) {
        System.out.println("runCalculation");
        WI = new WritableImage(width, height);
        int xory = Math.min(width, height);
        
        for (int x = xVal; x < width; x++) {
            for (int y = yVal; y < height; y++) {
                c_re = (x - width/2) *4.0/ xory;
                c_im = (y - height/2) *4.0/ xory;

                WI.getPixelWriter().setArgb(x, y, pallete[mandelbrot(c_re, c_im)]);
            }
        }
        return WI;
    }

    private int mandelbrot(double c_re, double c_im) {
        zx = zy = 0.0;
        iter = 0;
        double xSqr = zx * zx;
        double ySqr = zy * zy;
        
        while (xSqr + ySqr < 4 && iter < MAX_ITER) {
            tmp = xSqr - ySqr + c_re;
            zy = 2.0 * zx * zy + c_im;
            zx = tmp;
            
            xSqr = zx * zx;
            ySqr = zy * zy;
            iter++;
        }
        return (iter < MAX_ITER) ? iter : 0;
    }
    
    private void calculate() {
        if (!calculationInProgress.getAndSet(true)) {
            System.out.println("calculate spawned");
            Task calculate = createWorker();
            
            new Thread(calculate).start();
        } else {
            System.out.println("someone beat us to it!");
        }
    }
    
    private Task createWorker() {
        return new Task() {
            @Override protected Object call() throws Exception {
                Platform.runLater(() -> {
                    currentImageView.setImage(createMandelbrotImage(0,0));
                    calculationInProgress.set(false);
                });
                return true;
            }
        };
    }
}