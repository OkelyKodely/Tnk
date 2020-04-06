package mount;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.Timer;
import javax.swing.*;

public class Tnnk extends JPanel implements KeyListener {

    JFrame j = new JFrame();
    Graphics g = null;    
    ArrayList<O> list = new ArrayList<>();
    Tnk tnk = new Tnk(100, 300);
    int power = 60;
    JLabel powerLbl = new JLabel();
    boolean starting = true;
    static final double G = 9.8;
    int animationSpeed = 1;
    static int size = 900, ballDiameter = 10;
    double startX, startY, ballX, ballY;
    double xSpeed, ySpeed, lastPointX, lastPointY;
    double time, deltaTime = 0.01 ;
    ArrayList<Point2D> curvePoints= new ArrayList<>();
    Timer timer;
    boolean s = true;
    int angle = 0;
    double aangle = 0;
    boolean shooting = false;

    class O {
        int x, y;
    }
    
    class Tnk {
        int x, y;
        int sqtop_x, sqtop_y;
        int sqbot_x, sqbot_y;
        int can_x, can_y;
        
        Tnk(int xx, int yy) {
            x = xx;
            y = yy;
            sqbot_x = x;
            sqbot_y = y;
            sqtop_x = sqbot_x + 20;
            sqtop_y = sqbot_y - 30;
            can_x = sqtop_x + 50;
            can_y = sqtop_y;
            angle = can_y + 10;
        }
        
        void drawMe() {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.green);
            g2.fillRect(sqbot_x, sqbot_y, 90, 20);
            g2.setColor(Color.black);
            g2.fillRect(sqbot_x, sqbot_y+20, 90, 15);
            g2.setColor(Color.green);
            g2.fillRect(sqtop_x, sqtop_y, 50, 30);
            g2.setColor(Color.black);
            g2.drawRect(sqbot_x-1, sqbot_y-1, 92, 22);
            g2.setColor(Color.black);
            g2.drawRect(sqbot_x-1, sqbot_y+20-1, 92, 17);
            g2.setColor(Color.black);
            g2.drawRect(sqtop_x-1, sqtop_y-1, 52, 32);
            g2.setStroke(new BasicStroke(10));
            if(s) {
                angle = can_y+10;
                s = false;
            }
            g2.drawLine(can_x, can_y+10, can_x + 30, angle);
        }
    }
    
    public Tnnk() {

        setGUI();

        Thread t = new Thread() {
            public void run() {
                while(true) {
                    tnk.drawMe();
                }
            }
        };
        
        t.start();
    }

    public void paint(Graphics g) {

        if(starting)
            list.clear();

        for(int i=0; i<801; i++) {
            g.setColor(new Color(100, 155, 55+i/4));
            g.drawLine(0, i, 1200, i);
        }
        Random rand = new Random();
        if(starting)
        for(int i=0; i<30; i++) {
            int v = 300 + rand.nextInt(100);
            O o = new O();
            o.x = i*45;
            o.y = v;
            list.add(o);
            
            if(i==3) {
                tnk = new Tnk(o.x, o.y);
            }
        }
        g.setColor(new Color(140, 250, 70));
        Graphics2D g2 = (Graphics2D) g;
        for(int j=0; j<800; j++){
            g2.setColor(new Color(0, j/4 + 55, 0));
            for(int i=0; i<list.size(); i++) {
                try {
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(list.get(i).x, list.get(i).y+j, list.get(i+1).x, list.get(i+1).y+j);
                } catch(Exception e) {}
            }
        }
        if(starting)
            starting = false;
        
        g.setColor(Color.GRAY);
        g.fillOval(1000, 50, 100, 100);
        g.setColor(new Color(150, 150, 150));
        g.fillOval(1030, 60, 20, 20);
        g.setColor(new Color(150, 150, 150));
        g.fillOval(1060, 80, 20, 20);
        g.setColor(new Color(150, 150, 150));
        g.fillOval(1030, 100, 20, 20);

        g.setColor(Color.BLACK);
        g.setFont(new Font("arial", Font.PLAIN, 25));
        
        g.drawString("POWER: " + power + "", 100, 40);

        g.drawString("power is left/right keys", 100, 60);
        g.drawString("angle is up/down keys", 100, 80);
        g.drawString("to shoot, press spacebar", 100, 100);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            angle -= 4;
            aangle += 7;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            angle += 4;
            aangle -= 7;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            power--;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            power++;
        }
        else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            if(!shooting) {
                shooting = true;
                time = 0;
                for(int i=0; i<(int)((double)power/(double)12); i++) {
                    ballX= lastPointX = startX = tnk.x + 90;
                    ballY = lastPointY = startY = tnk.y - 30;
                    getUserInput();

                    timer = new Timer(animationSpeed, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent event) {

                            if(time != -1) {
                                moveBall();

                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                                g2d.setColor(Color.RED);
                                g2d.fillOval((int)ballX,(int)ballY,ballDiameter,ballDiameter);

                                if((Math.abs(lastPointX - ballX)>=1) && (Math.abs(lastPointY - ballY)>=1) ) {
                                    curvePoints.add(new Point2D.Double(ballX, ballY));
                                    lastPointX = ballX; lastPointY = ballY;
                                }

                                repaint();

                                if(! inBounds()) {
                                    timer.stop();

                                    shooting = false;
                                }
                            }
                        }
                    });
                    timer.start();
                }
            }
        }
        repaint();
    }

    private void moveBall() {

        ballX = startX + (xSpeed * time);
        ballY = startY - ((ySpeed *time)-(1.1 *G * Math.pow(time, 2))) ;
        time += deltaTime;

        for(int i=0; i<list.size(); i++) {
            try {
                int y2, y1, x2, x1;
                y2 = list.get(i+1).y;
                y1 = list.get(i).y;
                x2 = list.get(i+1).x;
                x1 = list.get(i).x;
                double m = (double)(y2 - y1)/(double)(x2 - x1);
                double c = y1 - m*x1;
                
                if(Math.abs((ballY - c)/(ballX) - m) < 1.4 && (Math.abs(ballX - x1) < 15 && Math.abs(ballY - y1) < 15)) {
                    O l = new O();
                    l.x = (int) ballX + 2;
                    l.y = (int) ballY+30;
                    list.add(i+1, l);
                    l = new O();
                    l.x = (int) ballX + 20;
                    l.y = (int) ballY + 120;
                    list.add(i+2, l);

                    time = -1;
                    
                    timer.stop();
                                
                    shooting = false;
                    
                    return;
                }
            } catch(Exception e) {}
        }
        
        repaint();
    }

    private void getUserInput() {

        double speed = 50 + power;
        xSpeed = speed * Math.cos(aangle * (Math.PI / 180));
        ySpeed = speed * Math.sin(aangle * (Math.PI / 180));
    }

    private boolean inBounds() {

        if((ballX < 0) || (ballX > (getWidth()))
                || ( ballY  > (getHeight() - ballDiameter) ) ) {
            return false;
        }

        return true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    void setGraphics() {
        
        g = this.getGraphics();
    }
    
    void setGUI() {
 
        j.setTitle("Tank Digger Kim Jong Un");
        
        j.setLayout(null);
        j.setBounds(0, 0, 1200, 800);
        this.setBounds(j.getBounds());
        j.add(this);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setVisible(true);
        j.setExtendedState(j.getExtendedState() | JFrame.MAXIMIZED_BOTH);
  
        setGraphics();
 
        j.addKeyListener(this);
    }
    
    public static void main(String[] args) {

        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new Tnnk();
                }
            });
        } catch(Exception e) {}
    }
}