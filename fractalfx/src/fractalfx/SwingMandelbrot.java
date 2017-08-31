package fractalfx;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Graphics;

import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Mandelbrot explorer using Swing. Once working transfer to JavaFX
 * 
 * 
 * @author srupik
 */
public class SwingMandelbrot extends JFrame {
    private final int MAX_ITER = 570;
    private final double ZOOM = 150;
    
 
    public SwingMandelbrot() {
        super("Mandelbrot Set");
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        add(new mandPanel(MAX_ITER, ZOOM));
    }
 
    public static void main(String[] args) {
        new SwingMandelbrot().setVisible(true);
    }
    
    
    class mandPanel extends JPanel {
        private final int MAX_ITER;
        private double ZOOM;
        private BufferedImage I;
        private double zx, zy, c_re, c_im, tmp;
        private int iter, height, width;
        private final int[] pallete;
    
        mandPanel(int MAX_ITER, double ZOOM) {
            this.MAX_ITER = MAX_ITER;
            this.ZOOM = ZOOM;
            
            pallete = new int[MAX_ITER];
            for (int i = 0; i<pallete.length; i++) {
                pallete[i] = java.awt.Color.HSBtoRGB(i/256f, 1, i/(i+8f));
                System.out.println(pallete[i]);
            } 
            
            addMouseListener( new MouseListener() {
                @Override public void mouseClicked(MouseEvent e) {
                    System.out.println(e.getPoint().x+" "+e.getPoint().y);
                    
                    zoomIn(e.getPoint().x, e.getPoint().y);
                }
                @Override public void mousePressed(MouseEvent e) {}
                @Override public void mouseReleased(MouseEvent e) {}
                @Override public void mouseEntered(MouseEvent e) {}
                @Override public void mouseExited(MouseEvent e) {}
            });
        }
        
        private void zoomIn(int xCor, int yCor) {
            ZOOM = ZOOM*2;
            
            runCalculation((xCor*2), (yCor*2));
            
            this.repaint();
        }
        
        private void runCalculation(int xVal, int yVal) {
            System.out.println("runCalculation");
            I = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            int xory = Math.min(width, height);
            
            for (int x = xVal; x < width; x++) {
                for (int y = yVal; y < height; y++) {
                    
                    c_re = (x - width/2) *4.0/ xory;
                    c_im = (y - height/2) *4.0/ xory;
                    
                    I.setRGB(x, y, pallete[mandelbrot(c_re, c_im)]);
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
        
        @Override public void paint(Graphics g) {
            if(height != getHeight() | width != getWidth()) {
                height = getHeight();
                width = getWidth();
                runCalculation(0,0);
            }
            g.drawImage(I, 0, 0, this);
        }
        
    }
}

