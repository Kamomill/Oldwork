package exercises.products.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * Main program, event handling and all graphics here
 *
 * See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 */
public class Simulation extends JPanel implements ActionListener {

    public static void main(String[] args) {
        new Simulation().program();
    }

    public static final int WIDTH = 400;    // This is a constant!
    public static final int HEIGHT = 400;
    public static final int INTERVAL = 500;//= 1000;
    public static final int DELAY = 1000;

    private Neighbourhood nh;   // The logic is in nh

    private void program() {
        // NOTE: Result is very depending on % empty and threshold (50% and 0.8 did terminate)
        nh = new Neighbourhood(50, 50, 0.6);//std is = 30,30,(0.5 to 0.7)
        double[] distribution = {0.25, 0.25, 0.50};   // Percent of RED, BLUE and NONE
        // Send in prototype objects and distribution of
        nh.setData(new Agent[]{new Agent(Type.RED), new Agent(Type.BLUE), new Agent(Type.NONE)}, distribution);
        initGraphics();
        initEvents();
    }

    private boolean doFind = true;

    public void actionPerformed(ActionEvent e) {
        if (doFind) {
            nh.setSatisfied();
        } else {
            nh.moveDissatisfied();
        }
        doFind = !doFind;
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int row = 0; row < nh.getNRows(); row++) {
            for (int col = 0; col < nh.getNCols(); col++) {
                Agent a = nh.getAgent(row, col);
                int x = 10 * col + 50;
                int y = 10 * row + 50;

                if (a.getType() == Type.RED) {
                    g2.setColor(Color.RED);
                } else if (a.getType() == Type.BLUE) {
                    g2.setColor(Color.BLUE);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillOval(x, y, 10, 10);
                if (!a.isSatisfied()) {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x, y, 4, 4);
                }
            }
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void initGraphics() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void initEvents() {
        Timer timer = new Timer(INTERVAL, this);
        timer.setInitialDelay(DELAY);
        timer.start();
    }
}
