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
public class stage2 extends Application {
    private ImageView currentImageView;
    private final int MAX_ITER = 750;
    private double ZOOM = 0.02;
    private double offsetX = 0.0;
    private double offsetY = 0.0;
    private WritableImage WI;
    private double zx, zy, c_re, c_im, tmp;
    private int iter, height, width;
    private int[] palette;
    private final AtomicBoolean calculationInProgress = new AtomicBoolean();
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override public void start(Stage primaryStage) {
        primaryStage.setTitle("Mandelbrot stage 2");
        
        palette = new int[MAX_ITER];
        for (int i = 0; i<palette.length; i++)
            palette[i] = java.awt.Color.HSBtoRGB(i/256f, 1, i/(i+8f));
        
        Group root = new Group();
        Scene scene = new Scene(root, 400, 300, Color.BLACK);
        
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
        
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
           if(mouseEvent.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
               ZOOM = ZOOM * 0.5;
           }
           if(mouseEvent.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
               ZOOM = ZOOM / 0.5;
           }
           offsetX = offsetX+ ZOOM *(mouseEvent.getX()-(width/2));
           offsetY = offsetY+ ZOOM *(mouseEvent.getY()-(height/2));
           System.out.printf("offset (x/y): (%f / %f)", offsetX, offsetY);
           
           calculate();
        });
        
        return imageView;
    }
    
    private WritableImage createMandelbrotImage() {
        System.out.println("runCalculation");
        WI = new WritableImage(width, height);
        
        for (int x = 0; x < width; x++) {
            c_re = (x - width/2) * ZOOM + offsetX;
            
            for (int y = 0; y < height; y++) {
                c_im = (y - height/2) * ZOOM + offsetY;
                
                WI.getPixelWriter().setArgb(x, y, palette[mandelbrot(c_re, c_im)]);
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
                    currentImageView.setImage(createMandelbrotImage());
                    calculationInProgress.set(false);
                });
                return true;
            }
        };
    }
}