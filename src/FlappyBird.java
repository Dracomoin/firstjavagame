import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener,KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdwidth = 34;
    int birdheight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdwidth;
        int height = birdheight;
        Image img;

        Bird(Image img){
            this.img = img;

        }
    }

        //pipes

        int pipeX=boardWidth;
        int pipeY=0;
        int pipewidth=64;
        int pipeHeight=512; //scaled by 1/6

        class Pipe{
            int x = pipeX;
            int y = pipeY;
            int width = pipewidth;
            int height = pipeHeight;
            Image img;
            boolean passed=false;

            Pipe(Image img){
                this.img = img;
    
        }
    }
    //logic
    Bird bird;
    int velocityX=-4;// moves pipes to the left(stimulates  the bird moving right)
    int velocityY=0;
    int gravity=1;

    ArrayList<Pipe> pipes ;
    Random random=  new Random();
    


    Timer gameloop;
    Timer placePipesTimer;
    boolean gameOver=false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this); 
        //setBackground(Color.blue);

        //loading images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg =new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes=new ArrayList<Pipe>();

        //placepipestimer
        placePipesTimer=new Timer(1500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //logic
        gameloop = new Timer(1000/60, this);
        gameloop.start();

    }

    public void placePipes(){
        //0-1 * pipeheight/2 ->0-256
        //128
        //0-128 - (0-256)--> 1/4 pipeheight -> 3/4 pipeheight


        int randomPipeY= (int) (pipeY - pipeHeight/4-Math.random()*(pipeHeight/2));
        int openingspace= boardHeight/4;

        Pipe topPipe=new Pipe (topPipeImg);
        topPipe.y = randomPipeY;             
        pipes.add(topPipe);

        Pipe bottomPipe=new Pipe (bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingspace;
        pipes.add(bottomPipe);

    }


    public void paintComponent(Graphics g)  {
        super.paintComponent(g);
        draw (g);
    }

    public void draw(Graphics g) {
    
        //draw background
        g.drawImage(backgroundImg, 0,0,boardWidth,boardHeight,null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i=0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over!"+ String.valueOf((int)score),10,35);
        }
        else{
            g.drawString(String.valueOf((int)score),10,35); 
        }

    }

    public void move(){
        //bird
        velocityY+=gravity;
        bird.y+=velocityY;
        bird.y=Math.max(bird.y,0);

        //pipes
        for(int i=0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x +=velocityX;

            if(!pipe.passed&& bird.x > pipe.x + pipe.width){
                pipe.passed=true;
                score+=0.5;
            }

            if(collision(bird, pipe)){
                gameOver=true;
            }
        }

        if(bird.y > boardHeight){
            gameOver=true;
        }
    }

    public boolean  collision(Bird a,Pipe b) {
        return a.x < b.x + b.width && 
        a.x +a.width > b.x &&
        a.y < b.y + b.height &&
        a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
       repaint();
       if (gameOver)                     {
        placePipesTimer.stop();
        gameloop.stop();
       }        
    }

    
    @Override
    public void keyPressed(KeyEvent e) {
       if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        velocityY=-9;
        if(gameOver){
            //reset game
            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            score=0;
            gameOver=false;
            gameloop.start();  //restart gameloop timer
            placePipesTimer.start();
       }
    }

    }

    @Override
    public void keyTyped(KeyEvent e) {}


    @Override
    public void keyReleased(KeyEvent e) {}

    }

