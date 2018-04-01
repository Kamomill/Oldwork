package exercises.products;

import javax.swing.*;
import java.awt.*;

import static java.awt.Color.*;

/**
 * Draw the Mandelbrot fractal
 * https://en.wikipedia.org/wiki/Mandelbrot_set
 */
public class Mandelbrot extends JPanel {

    public static void main(String[] args) {
        new Mandelbrot().program();
    }


    final int width = 800;
    final int height = 600;

    void program() {
        initGraphics();
    }


    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;


        for (double wpix = 0; wpix <= width; wpix++) {   // To get to every  pixel on the screen
            for (double hpix = 0; hpix <= height; hpix++) {

                int i = getNIterations(wpix, hpix);
                if (i < 4) {
                    g2.setColor(WHITE);
                } else if (i < 12) {
                    g2.setColor(YELLOW);
                } else if (i < 20) {
                    g2.setColor(RED);
                } else if (i < 50) {
                    g2.setColor(GREEN);
                } else if (i < 100) {
                    g2.setColor(MAGENTA);
                } else if (i < 200) {
                    g2.setColor(BLUE);
                } else {
                    g2.setColor(BLACK);
                }
                drawPoint(g2, wpix, hpix);
            }
        }
        // Coordinate axis
        g2.setColor(LIGHT_GRAY);
        g2.drawLine(0, height / 2, width, height / 2);
        g2.drawLine(2 * width / 3, 0, 2 * width / 3, height);
    }

    void drawPoint(Graphics2D g2, double x, double y) {
        // Fit to draw area
        int x1 = (int) x; //(200 * x + 2 * width / 3);
        int y1 = (int) y; //(200 * y + height / 2);
        g2.drawLine(x1, height - y1, x1, height - y1);
    }

    // Return number of iteration before point diverges
    // Max iterations = 255 (if so point doesn't diverge )
    //our limits are -3 <= x <= 1,5 och -1,5 <= y <= 1.5
    int getNIterations(double px, double py) {
        double xO = (px * (1/(width/4.5))-3);   //Adjust x and y for scale relative to pixel
        double yO = (py * 0.005 - 1.5);         //xO = x's start value && yO = y's start value
        int iteration = 0;
        double x=0;
        double y=0;
        double xtemp;
        // z= x+iy      //The Formula
        // z^2 = x^2 +i2xy - y^2
        while ((x * x + y * y) < 2 && iteration < 255) { // Tests to see if the number is grounded
            xtemp=(x*x) - (y*y)+xO; //(x*x)-(y*y)+xO <=> x^2 -y^2 + the reel part of C, this is the reel part of Z^2
            y = 2 * x * y + yO;     //2*x * y+yO <=> i2xy + the complex part of C,  this is the complex part of Z^2
            x = xtemp;              // Uses a temporary x so we can treat the reel part and complex part separately
            iteration++;
        }
        return iteration;
    }

    void initGraphics() {
        setPreferredSize(new Dimension(width, height));
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
