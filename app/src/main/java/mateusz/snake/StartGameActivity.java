package mateusz.snake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StartGameActivity extends AppCompatActivity
{

    /*enum to specify the direction in which snake is moving*/
    enum Direction
    {
        LEFT, RIGHT, UP, DOWN, NONE
    }

    /*enum to specify the game difficulty*/
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
    /*Starting length of a snake*/
    private int length = 1;
    /*Arrays for holding snake coordinates*/
    int[] xCoordinates ;
    int[] yCoordinates;
    /*Snake color*/
    private int color;
    /*Snake speed(game difficulty)*/
    private Difficulty difficulty;

    /***Board attributes***/
    private int borderWidth;
    private int borderHeight;
    private int gameWidth;
    private int gameHeight;
    private int boardWidth;
    private int boardHeight;
    private int xAppleCoordinate;
    private int yAppleCoordinate;
    private boolean isGameOver = false;

    /*Reference to the view*/
    private StartGameView startGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            getSupportActionBar().hide();
        }
        catch(Exception e)
        {
            Log.e("Action bar error", "Exception thrown during hiding a notification bar");
        }
        getExtras();
        startGameView = new StartGameView(this, this, color);
        int barHeight = getStatusBarHeight();   //bar height is needed to set
                                            //starting coordinates properly
        setParameters(barHeight);
        setContentView(startGameView);
        beginPlaying();
    }

    /***Getters, setters and methods to change snake location*/
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
     * @param barHeight
     */
    private void setParameters(int barHeight)
    {
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
                speed = 300;
                break;
            case MEDIUM:
                speed = 200;
                break;
            case HARD:
                speed = 100;
                break;
        }
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
     * Check the events related to snake's location.
     * Inform whether snake has eaten an apple, hit a wall and so on.
     */
    private void checkLocation()
    {
        checkIfAppleEaten();
        checkIfGameOver();
        checkIfBordersPassed();
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
                    checkLocation();
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
     * Check is head's coordinates match apple coordinates.
     * If snake has eaten an apple, increments length and spawn another apple.
     */
    private void checkIfAppleEaten()
    {
        /*if snake's head collides with an apple, increments length,
        adds score and spawns another apple*/
        if (xAppleCoordinate == xCoordinates[0] && yAppleCoordinate == yCoordinates[0]) {
            length++;
            spawnApple();
        }
    }

    /**
     * Check is snake has eaten himself.
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
        if (xCoordinates[0] >= gameWidth - borderWidth)
            xCoordinates[0] = borderWidth;
        if(yCoordinates[0] < borderHeight)
            yCoordinates[0] = borderHeight + boardHeight- SIZE;
        if (yCoordinates[0] >= gameHeight - borderHeight)
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

}
