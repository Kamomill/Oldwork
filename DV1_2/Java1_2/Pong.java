package exercises.products;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.event.KeyEvent;
import java.util.Random;

import static java.awt.Color.*;
import static java.lang.Math.*;

/**
 * Pong-like game (only one player)
 * NOTE: This is a basic version
 * You're encouraged to add features, have fun!
 */
public class Pong extends JPanel implements ActionListener {

    public static void main(String[] args) {
        new Pong().program();
    }

    final int width = 600;
    final int height = 400;
    final int interval = 10;
    final int delay = 1000;
    final int paddleSpeed = 5;
    final int paddleHeight = 60;
    final int paddleWidth = 5;

    Ball ball = new Ball(new double[]{1, randangle()}, width / 4 , height / 2 ); //-7 is to compensate for it's size (start=width/2-7), randangle() randomizes tha angle
    final Paddle paddle = new Paddle(paddleHeight, paddleWidth, paddleSpeed);

    int points;
    int highScore=0;
    int bouncesafteytick=0;

    void program() {
        initGraphics();
        initEvents();
    }

    // Update game state (the logic, i.e. move, bounce, etc.)
    void update() {
        if (((0 < ball.posx && ball.posx < width) && 0 < ball.posy && //bounds of window
                ball.posy < height)){
            ball.move();
        }else {
            ball= new Ball(new double[]{1, randangle()}, width / 4 , height / 2 ); //randangle() randomizes tha angle
            points=0;
        }

            bounce(ball); //bounce always runs, but the function only do something when the ball is close the the pad or the wall
    }

    boolean checkCollision (Ball ball, Paddle paddle){ //used in bounce
        if (((ball.posx-paddle.posx)+7)>2 && ball.posx<=paddle.posx &&((ball.posy-paddle.posy<33))) { //to test if ball is under padd
            if ((ball.posx - paddle.posx + 7) > 2 && ((ball.posy - paddle.posy > -33))) { //to test if ball is above padd
                return true;
            }
        }// OLD CODE ((ball.posx-paddle.posx+7)>2)&&(((ball.posy-paddle.posy+33)>2)||(ball.posy-paddle.posy-33>2)
        return false;

    }

    // Called by timers
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
        currenthighscore();
        bouncesafteytick+=1; // Makes sure that the ball don't "double bouce" and get stuck in a wall
        //System.out.println(bouncesafteytick); // test numbers
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(WHITE);
        g2.drawString("Points: " + points, 10, 10);
        g2.drawString("High score: " + highScore, 10, 20);
        g2.drawString("Speed: "+ currentspeed(), 10, 30);

        g2.drawLine(0, height / 2, width, height / 2);

        drawBall(g2, ball);
        drawPaddle(g2, paddle);

        Toolkit.getDefaultToolkit().sync();
    }
    double currentspeed (){
        double x;
        int y;
        x=sqrt(pow(ball.vel[0],2)+pow(ball.vel[1],2));
        x*=1;//Could be nicer (aka shorter by not showing so manny decimals)
        return x;
    }



    private void drawBall(Graphics2D g2, Ball ball) {
        Color old = g2.getColor();
        g2.setColor(ball.c);
        g2.fillOval((int )ball.posx-7, (int) ball.posy-7, ball.size, ball.size); //Force them t obe int because we can't print a half of pixel
        g2.setColor(old);
    }

    private void drawPaddle(Graphics2D g2, Paddle paddle) {
        Color old = g2.getColor();
        g2.setColor(paddle.c);
        g2.fillRect(paddle.posx, paddle.posy-30, paddle.paddlewidth, paddle.paddleheight);// -30 is to compensate for height of paddle
        g2.setColor(old);
    }

    void initGraphics() {
        setPreferredSize(new Dimension(width, height));
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.addKeyListener(kl);  // Extra row: Set listener on Window
    }

    void initEvents() {
        Timer timer = new Timer(interval, this);
        timer.setInitialDelay(delay);
        timer.start();
    }

    void bounce(Ball ball){
        if(checkCollision(ball,paddle) && bouncesafteytick>2){
            ball.normalize(ball.vel); // Also increases the speed
            ball.vel[0] = ball.vel[0] * (-1);

            points+=1;
            bouncesafteytick=0;
            System.out.println(ball.vel[0]+" "+ball.vel[1]);
        }
        //TEST (ball.posx-paddle.posx)+7)>2 && ball.posx<=paddle.posx
        if (ball.posx <= 7 && bouncesafteytick>2) { //THINK THE ERROR IS HERE
            ball.vel[0] = ball.vel[0] * (-1);
            points+=1;
            bouncesafteytick=0;
            System.out.println(ball.vel[0]+" "+ball.vel[1]);
            ball.move();

        }
        if (ball.posy <= 7 && bouncesafteytick>2) {
            ball.vel[1] = ball.vel[1] * (-1);
            points+=1;
            bouncesafteytick=0;
         //   ball.move();
        }

        if (ball.posy >= height-8 && bouncesafteytick>2) { //weird
            ball.vel[1] = ball.vel[1] * (-1);
            points+=1;
            bouncesafteytick=0;
          //  ball.move();
        }


}
    void currenthighscore (){
        if (highScore<points)
        highScore=points;
    }
double randangle () {
    Random rand = new Random();// used to randomize angle
    boolean updown = rand.nextBoolean(); // used to randomize up or down
    double angle;
    if (updown) {
         angle = (30 + rand.nextInt(15)) * (3.1415 / 180)*1;
    }else{
         angle = (30 + rand.nextInt(15)) * (3.1415 / 180)*-1;
    }

    return angle;
}
    KeyListener kl = new KeyAdapter() {

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                paddle.up();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                paddle.down();
            }
            //System.out.println(paddle.posy);
        }
    };


    //----- Classes  --------------------------------

    class Ball {
        final int size = 14;//cross section of the ball, used for both x and y in drawball
        final double maxSpeed = 20; // Maximum allowed speed
        final double[] vel;
        final Color c = MAGENTA;

        double posx;
        double posy;

        // If |velocity| > maxSpeed normalize
        double[] normalize(double[] vel) {
            vel[0]=vel[0]+1; //Coment this line if you want to turn of increasing speed for x-axis
            double length = sqrt(vel[0] * vel[0] + vel[1] * vel[1]);
            if (length > maxSpeed) {
                vel[0] = maxSpeed * vel[0] / length;
                vel[1] = maxSpeed * vel[1] / length;
            }
            return vel;
        }

        // Move using velocity
        void move() {
            posx += vel[0];     // posx = posx + vel[0];
            posy += vel[1];
        }


        public Ball(double[] vel, int posx, int posy) {
            this.vel = vel;
            this.posx = posx;
            this.posy = posy;
        }
    }

    class Paddle {
        final int paddleheight;
        final int paddlewidth;
        final int paddlespeed;
        final int posx = (width / 10) * 9;

        int posy = (height / 2);

        final Color c = RED;

        void up() {
            if (posy - paddlespeed >= 30)
                posy -= paddlespeed;
        }

        void down() {
            if (posy + paddlespeed <= 370) {
                posy += paddlespeed;
            }
        }

        public Paddle(int paddleheight, int paddlewidth, int paddlespeed) {
            this.paddleheight = paddleheight;
            this.paddlewidth = paddlewidth;
            this.paddlespeed = paddlespeed;
        }
    }
}
