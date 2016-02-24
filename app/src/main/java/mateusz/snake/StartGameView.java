package mateusz.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import mateusz.snake.StartGameActivity.Direction;


/**
 * Created by mateusz on 16.02.16.
 */
public class StartGameView extends View
{

    /*Coordinates needed to determine swipe direction*/
    private float initialX = 0f;
    private float initialY = 0f;
    private float finalX = 0f;
    private float finalY = 0f;

    /*Flag that checks whether a snake is about to move, prevents the snake from turning
    backwards when two swipes are made too fast
     */
    private boolean isWaitingForResponse = false;

    /*Screen size*/
    private int mWidth;
    private int mHeight;

    /*View's activity*/
    private StartGameActivity activity;

    /*Color chosen by player on game start*/
    private int color;

    /*View constructors*/
    public StartGameView(Context context, StartGameActivity givenActivity, int chosenColor)
    {
        super(context);
        activity = givenActivity;
        color = chosenColor;
    }

    public StartGameView(Context context, AttributeSet attributes, StartGameActivity givenActivity,
                         int chosenColor)
    {
        super(context, attributes);
        activity = givenActivity;
        color = chosenColor;
    }

    public StartGameView(Context context, AttributeSet attributes,
                         int defStyleAttr, StartGameActivity givenActivity, int chosenColor)
    {
        super(context, attributes, defStyleAttr);
        activity = givenActivity;
        color = chosenColor;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint paint = new Paint();

        /*paint borders*/
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        /*paint board*/
        paint.setColor(Color.BLACK);
        Rect filling = new Rect(activity.getBorderWidth(), activity.getBorderHeight(),
                mWidth - activity.getBorderWidth(), mHeight - activity.getBorderHeight());
        canvas.drawRect(filling, paint);

        int[] xCoordinates = activity.getXCoordinates();
        int[] yCoordinates = activity.getYCoordinates();

        /*paints snake's body*/
        for (int i = 1; i < activity.getLength(); i++)
        {
            Rect body = new Rect(xCoordinates[i], yCoordinates[i],
                    xCoordinates[i] + activity.getSize(), yCoordinates[i] + activity.getSize());
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(body, paint);   //draw filling
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(body, paint);   //draw borders
        }

        /*paint snake's head*/
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        Rect rectangle = new Rect(xCoordinates[0], yCoordinates[0],
                xCoordinates[0]+activity.getSize(), yCoordinates[0]+activity.getSize());
        canvas.drawRect(rectangle, paint);

        /*paint an apple*/
        /*default coordinates tell the location of the upper-left corner of an apple but default
        draw function puts circle's center in there so we have to relocate the apple by size/2
         */
        paint.setColor(Color.RED);
        canvas.drawCircle(activity.getxAppleCoordinate() + activity.getSize()/2,
                activity.getyAppleCoordinate() + activity.getSize()/2,
                activity.getSize()/2, paint);

        canvas.save();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (!isWaitingForResponse)
        {
            int action = e.getActionMasked();
        /*switch which chooses action depending on event*/
            switch (action) {
            /*when screen is being touched*/
                case MotionEvent.ACTION_DOWN:
                    initialX = e.getX();
                    initialY = e.getY();
                    break;

            /*when the user stops touching the screen*/
                case MotionEvent.ACTION_UP:
                    finalX = e.getX();
                    finalY = e.getY();
                /*determine the direction of swipe based on beginning
                and end coordinates of period when screen was being touched
                 */
                /*when screen was only touched, no swipe happened*/
                    if (finalX == initialX && finalY == initialY) {
                        //do nothing
                    }
                /*when horizontal swipe was more meaningful than horizontal*/
                    else if (Math.abs(finalX - initialX) > Math.abs(finalY - initialY)) {
                        if (finalX > initialX && activity.getDirection() != Direction.LEFT)
                            activity.setDirection(Direction.RIGHT);
                        else if (activity.getDirection() != Direction.RIGHT)
                            activity.setDirection(Direction.LEFT);
                    }
               /*when vertical swipe was more meaningful than horizontal*/
                    else {
                        if (finalY < initialY && activity.getDirection() != Direction.DOWN)
                            activity.setDirection(Direction.UP);
                        else if (activity.getDirection() != Direction.UP)
                            activity.setDirection(Direction.DOWN);
                    }
                    isWaitingForResponse = true;
                    break;
            }   //end of switch
            return true;
        }
        else return false;
    }

    /**
     * Tell the view that command has been executed.
     * @param state
     */
    public void setWaitingForResponse(boolean state)
    {
        isWaitingForResponse = state;
    }

}
