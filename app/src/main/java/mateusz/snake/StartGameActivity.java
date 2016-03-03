package mateusz.snake;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class StartGameActivity extends AppCompatActivity
{

    /*Enum to specify the direction in which snake is moving*/
    enum Direction
    {
        LEFT, RIGHT, UP, DOWN, NONE
    }

    /*Enum to specify the game difficulty*/
    enum Difficulty
    {
        EASY, MEDIUM, HARD
    }

    /***Snake attributes***/
    /*Starting coordinates*/
    private int xAtStart;
    private int yAtStart;
    /*Snake size*/
    private final int SIZE = 50;
    /*Direction in which snake is moving*/
    private Direction direction = Direction.NONE;
    /*Default snake speed*/
    private long speed = 300;
    /*Maximum snake speed*/
    private final long MAX_SPEED = 50;
    /*Game speeds depending on chosen difficulty*/
    private final int EASY_SPEED = 300;
    private final int MEDIUM_SPEED = 200;
    private final int HARD_SPEED = 100;
    /*Starting length of a snake*/
    private int length = 1;
    /*Arrays for holding snake coordinates*/
    private int[] xCoordinates ;
    private int[] yCoordinates;
    /*Snake color*/
    private int color;
    /*Snake speed(game difficulty)*/
    private Difficulty difficulty;
    /*Flag that tells whether snake has eaten an enhanced apple recently*/
    private boolean isEnhancedAppleEaten = false;

    /***Board attributes***/
    /*Size of borders*/
    private int borderWidth;
    private int borderHeight;
    /*Size of phone*/
    private int gameWidth;
    private int gameHeight;
    /*Size of playable field*/
    private int boardWidth;
    private int boardHeight;
    /*Apple coordinates*/
    private int xAppleCoordinate;
    private int yAppleCoordinate;
    /*Flag telling whether game is over
    When it equals true, then game loop is stopped
     */
    private boolean isGameOver = false;
    /*Time at which enhanced apple was eaten, if it equals -1 then it wasn't
    that time is needed to calculate how long the snake should blink
     */
    private long enhancedAppleEatenTime = -1;
    /*Time at which enhanced apple was spawned*/
    private long enhancedAppleStartTime;
    /*How long enhanced apple will be on board*/
    private long enhancedAppleTime = 5;
    /*Time of blinking after eating enhanced apple*/
    private final int BLINKING_TIME = 5;
    /*Enhanced apple coordinates*/
    private int xEnhancedAppleCoordinate;
    private int yEnhancedAppleCoordinate;
    private final double CHANCE_OF_ENHANCED_APPLE = 0.1;
    private boolean isEnhancedApple = false;
    private int score = 0;

    /*Reference to the view*/
    private StartGameView startGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Prevent screen from rotating during game*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        /*Hide status bar so that it doesn't unnecessarily take space*/
        try
        {
            getSupportActionBar().hide();
        }
        catch(Exception e)
        {
            Log.e("Action bar error", "Exception thrown during hiding a notification bar");
        }
        /*Get difficulty and color added to intent*/
        getExtras();
        startGameView = new StartGameView(this, this, color);
        setParameters();
        setContentView(startGameView);
        beginPlaying();
    }

    /*Getters, setters and methods to change snake location*/
    public void changeX(int diff)
    {
        xCoordinates[0] += diff;
    }

    public void changeY(int diff)
    {
        yCoordinates[0] += diff;
    }

    public void setxAtStart(int newX)
    {
        xAtStart = newX;
    }

    public void setyAtStart(int newY)
    {
        yAtStart = newY;
    }

    public void setDirection(Direction dir)
    {
        direction = dir;
    }

    public int getSize()
    {
        return SIZE;
    }

    public Direction getDirection()
    {
        return direction;
    }

    public int getBorderWidth()
    {
        return borderWidth;
    }

    public int getBorderHeight()
    {
        return borderHeight;
    }

    public int getxAppleCoordinate()
    {
        return xAppleCoordinate;
    }

    public int getyAppleCoordinate()
    {
        return yAppleCoordinate;
    }

    public int getLength()
    {
        return length;
    }

    public int[] getXCoordinates()
    {
        return xCoordinates;
    }

    public int[] getYCoordinates()
    {
        return yCoordinates;
    }

    public int getxEnhancedAppleCoordinate()
    {
        return xEnhancedAppleCoordinate;
    }

    public int getyEnhancedAppleCoordinate()
    {
        return yEnhancedAppleCoordinate;
    }

    public boolean isEnhancedApple()
    {
        return isEnhancedApple;
    }

    public boolean isEnhancedAppleEaten()
    {
        return isEnhancedAppleEaten;
    }

    public int getScore()
    {
        return score;
    }


    /**
     * Move based on the direction the player chose
     */
    public void move()
    {
        /*every part of the body follows the head*/
        for (int i = length; i > 0; i--)
        {
            xCoordinates[i] = xCoordinates[i-1];
            yCoordinates[i] = yCoordinates[i-1];
        }
        /*move the head*/
        if (direction == Direction.LEFT)
            changeX(-1*SIZE);
        else if (direction == Direction.RIGHT)
            changeX(SIZE);
        else if (direction == Direction.UP)
            changeY(-1*SIZE);
        else if (direction == Direction.DOWN)
            changeY(SIZE);
    }

    /**
     * Determine size of borders based on screen size and set starting location.
     * Also adjust game speed according to chosen difficulty.
     */
    private void setParameters()
    {
        /*bar height is needed to set starting coordinates properly*/
        int barHeight = getStatusBarHeight();
        /*get display size*/
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        gameWidth = metrics.widthPixels;
        gameHeight = metrics.heightPixels - barHeight;
        /*get border size*/
        borderWidth = (metrics.widthPixels%SIZE)/2;
        borderHeight = ((metrics.heightPixels - barHeight)%SIZE)/2;
        /*if borders are too small (due to screen resolution), make them wider*/
        if (borderWidth < SIZE)
            borderWidth += SIZE/2;
        if (borderHeight < SIZE)
            borderHeight += SIZE/2;

        /*exact board size*/
        boardWidth = gameWidth - 2*borderWidth;
        boardHeight = gameHeight - 2*borderHeight;
        int tmp = (boardWidth) % SIZE;
        boardWidth -= tmp;
        tmp = (boardHeight) % SIZE;
        boardHeight -= tmp;

        /*set starting coordinates of snake*/
        setxAtStart((gameWidth - 2* borderWidth)/2);
        setyAtStart((gameHeight - 2* borderHeight)/2);

        /*makes coordinates a multiple of SIZE*/
        tmp = (xAtStart - borderWidth) % SIZE;
        xAtStart -= tmp;
        tmp = (yAtStart - borderHeight) % SIZE;
        yAtStart -= tmp;

        /*set game speed basing on chosen difficulty*/
        switch(difficulty)
        {
            case EASY:
                speed = EASY_SPEED;
                break;
            case MEDIUM:
                speed = MEDIUM_SPEED;
                break;
            case HARD:
                speed = HARD_SPEED;
                break;
        }
        /*set enhanced apple time, based on game speed*/
        enhancedAppleTime = speed/20;
    }

    /**
     * Tell the height of the notification bar in order to include it in calculation
     * of coordinates
     * @return bar height
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Check the events related to snake.
     * Inform whether snake has eaten an apple, hit a wall and so on.
     */
    private void checkStatus()
    {
        checkIfBordersPassed();
        checkIfAppleEaten();
        checkIfGameOver();
        checkIfEnhancedAppleTimePassed();
        checkIfSnakeBoosted();
    }

    /**
     * Actual gaming phase.
     * Move snake, make view repaint and call function checking location.
     */
    private void beginPlaying()
    {
        new Thread()
        {
            public void run()
            {
                xCoordinates = new int[gameWidth];
                yCoordinates = new int[gameHeight];
                xCoordinates[0] = xAtStart;
                yCoordinates[0] = yAtStart;
                spawnApple();
                while(!isGameOver)
                {
                    move();
                    startGameView.setWaitingForResponse(false); //notify the view that snake has
                    // moved and another command can be accepted
                    checkStatus();
                    try
                    {
                        Thread.sleep(speed);
                    } catch (Exception e)
                    {
                        Log.e("Sleep error", "Exception thrown during Thread.sleep");
                    }
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                           startGameView.invalidate();
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * Create random apple coordinates.
     * If an apple is spawned on a snake or outside of a board, find a new location
     */
    private void spawnApple()
    {
        /*sets the range of locations at which apple can spawn*/
        int xRange = gameWidth - 2*borderWidth + 1;
        int yRange = gameHeight - 2*borderHeight + 1;

		/*sets random x and y coordinates of apple*/
        xAppleCoordinate = (int)(Math.random() * xRange) + borderWidth;
        yAppleCoordinate = (int)(Math.random() * yRange) + borderHeight;

		/*makes coordinates a multiple of SIZE*/
        int tmp = (xAppleCoordinate - borderWidth) % SIZE;
        xAppleCoordinate -= tmp;
        tmp = (yAppleCoordinate - borderHeight) % SIZE;
        yAppleCoordinate -= tmp;

		/*ensures that apple does not spawn out of board or on a snake*/
        if (xAppleCoordinate < borderWidth) xAppleCoordinate += SIZE;
        if (yAppleCoordinate < borderHeight) yAppleCoordinate += SIZE;
        if (xAppleCoordinate >= gameWidth - borderWidth) xAppleCoordinate -= (2*SIZE);
        if (yAppleCoordinate >= gameHeight- borderHeight) yAppleCoordinate -= (2*SIZE);

        /*if apple spawns on a snake, another location is found*/
        for (int i = 0; i < length; i++)
        {
            if (xAppleCoordinate == xCoordinates[i] && yAppleCoordinate == yCoordinates[i])
                spawnApple();
        }

    }


    /**
     * Function analogical to spawnApple, but spawns enhanced apple.
     */
    private void spawnEnhancedApple()
    {
        /*sets the range of locations at which apple can spawn*/
        int xRange = gameWidth - 2*borderWidth + 1;
        int yRange = gameHeight - 2*borderHeight + 1;

		/*sets random x and y coordinates of apple*/
        xEnhancedAppleCoordinate = (int)(Math.random() * xRange) + borderWidth;
        yEnhancedAppleCoordinate = (int)(Math.random() * yRange) + borderHeight;

		/*makes coordinates a multiple of SIZE*/
        int tmp = (xEnhancedAppleCoordinate - borderWidth) % SIZE;
        xEnhancedAppleCoordinate -= tmp;
        tmp = (yEnhancedAppleCoordinate - borderHeight) % SIZE;
        yEnhancedAppleCoordinate -= tmp;

		/*ensures that apple does not spawn out of board or on a snake*/
        if (xEnhancedAppleCoordinate < borderWidth)
            xEnhancedAppleCoordinate += SIZE;
        if (yEnhancedAppleCoordinate < borderHeight)
            yEnhancedAppleCoordinate += SIZE;
        if (xEnhancedAppleCoordinate >= gameWidth - borderWidth)
            xEnhancedAppleCoordinate -= (2*SIZE);
        if (yEnhancedAppleCoordinate >= gameHeight- borderHeight)
            yEnhancedAppleCoordinate -= (2*SIZE);

        /*if apple spawns on a snake, another location is found*/
        for (int i = 0; i < length; i++)
        {
            if (xEnhancedAppleCoordinate == xCoordinates[i]
                    && yEnhancedAppleCoordinate == yCoordinates[i])
                spawnEnhancedApple();
        }
        enhancedAppleStartTime = System.nanoTime();
        isEnhancedApple = true;

    }
    /**
     * Check is head's coordinates match apple coordinates.
     * If snake has eaten an apple, increments length and spawn another apple.
     */
    private void checkIfAppleEaten()
    {
        /*If snake's head collides with an apple, increments length,
        adds score and spawns another apple*/
        if (xAppleCoordinate == xCoordinates[0] && yAppleCoordinate == yCoordinates[0])
        {
            length++;
            spawnApple();
            score += 10;
            /*If speed does not exceed maximum speed, change it to make game harder*/
            if (speed > MAX_SPEED)
                speed -= 5;
            /*Check whether enhanced apple should be spawned*/
            if (Math.random() > (1 - CHANCE_OF_ENHANCED_APPLE))
                if (!isEnhancedApple)
                    spawnEnhancedApple();
        }
        /*Check whether enhanced apple was eaten*/
        if (xEnhancedAppleCoordinate == xCoordinates[0] &&
                yEnhancedAppleCoordinate == yCoordinates[0] &&
                isEnhancedApple) {
            length++;
            score += 50;
            /*Eating enhanced apple slows the game down*/
            speed += 20;
            enhancedAppleEatenTime = System.nanoTime();
            isEnhancedApple = false;
            isEnhancedAppleEaten = true;
        }
    }

    /**
     * Check if snake has eaten himself.
     * If player lost, create game over activity.
     */
    private void checkIfGameOver()
    {
        /*Check whether snake has hit itself*/
        for (int i = 1; i < length; i++)
            if (xCoordinates[0] == xCoordinates[i] && yCoordinates[0] == yCoordinates[i])
            {
                isGameOver = true;
                Intent intent = new Intent(this, GameOverActivity.class);
                intent.putExtra("finalScore", getScore());
                startActivity(intent);
            }
    }

    /**Check whether snake has encounter a wall.
     * If so, move it to the other side of the board
     */
    private void checkIfBordersPassed()
    {
        if (xCoordinates[0] < borderWidth)
            xCoordinates[0] = borderWidth + boardWidth - SIZE;
        if (xCoordinates[0] > gameWidth - borderWidth - SIZE)
            xCoordinates[0] = borderWidth;
        if(yCoordinates[0] < borderHeight)
            yCoordinates[0] = borderHeight + boardHeight- SIZE;
        if (yCoordinates[0] > gameHeight - borderHeight - SIZE)
            yCoordinates[0] = borderHeight;
    }

    /**
     * Get game attributes chosen by a player.
     * Difficulty and color are passed as extras carried by intent.
     */
    private void getExtras()
    {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            color = (int)bundle.get("chosenColor");
            difficulty = (Difficulty)bundle.get("chosenDifficulty");
        }
    }

    /**
     * Checks if it is time the enhanced apple vanished.
     */
    private void checkIfEnhancedAppleTimePassed()
    {
        /*How long enhanced apple was on board in seconds*/
        long duration = (System.nanoTime() - enhancedAppleStartTime) / 1000000000;
        /*After a set time enhanced apple vanishes*/
        if (duration > enhancedAppleTime)
            isEnhancedApple = false;
    }

    /**
     * Check how much time after eating enhanced apple has passed.
     * If it is greater than blinking time, stop the blinking.
     */
    private void checkIfSnakeBoosted()
    {
        /*Check how long snake was boosted*/
        long duration = (System.nanoTime() - enhancedAppleEatenTime) / 1000000000;
        /*If that time exceeds blinking time, stop the boost*/
        if (duration > BLINKING_TIME)
            isEnhancedAppleEaten = false;
    }

}
