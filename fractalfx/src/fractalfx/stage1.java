package fractalfx;
 
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.scene.image.ImageView;
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
    private final int MAX_ITER = 50;
    private double zx, zy, c_re, c_im, tmp;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override public void start(Stage primaryStage) {
        primaryStage.setTitle("Mandelbrot stage 1");
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        
        primaryStage.setScene(scene);
        
        currentImageView = createImageView(scene);
        
        root.getChildren().add(currentImageView);
        primaryStage.show();
    }
    
    private ImageView createImageView(Scene scene) {
        ImageView imageView = new ImageView();  
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(scene.widthProperty());
        
        imageView.setImage(createMandelbrotImage((int)scene.getWidth(), (int)scene.getHeight()));
        
        return imageView;
    }
    
    private WritableImage createMandelbrotImage(int width, int height) {
        WritableImage WI = new WritableImage(width, height);
        int xory = Math.min(width, height);
        
        int cValue;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                c_re = (x - width/2) *4.0/ xory;
                c_im = (y - height/2) *4.0/ xory;

                cValue = mandelbrot(c_re, c_im) * 255/MAX_ITER;
                WI.getPixelWriter().setColor(x, y, Color.rgb(cValue, cValue, cValue, 1.0));
            }
        }
        return WI;
    }

    private int mandelbrot(double c_re, double c_im) {
        zx = zy = 0.0;
        int iter = 0;
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
}