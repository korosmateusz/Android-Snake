package mateusz.snake;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import mateusz.snake.StartGameActivity.Difficulty;

public class MainActivity extends AppCompatActivity {

    private int color = Color.GREEN;
    private Difficulty difficulty = Difficulty.EASY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            getSupportActionBar().hide();
        }
        catch(Exception e)
        {
            Log.e("Action bar error", "Exception thrown during hiding of notification bar");
        }
        setContentView(R.layout.activity_main);
    }

    /**
     * Invoke StartGameActivity and send color and difficulty parameters
     * @param view
     */
    public void startGame(View view)
    {
        Intent intent = new Intent(this, StartGameActivity.class);
        intent.putExtra("chosenColor", color);
        intent.putExtra("chosenDifficulty", difficulty);
        startActivity(intent);
    }

    /**
     * Set game parameters according to radiobuttons checked
     * @param view
     */
    public void onRadioButtonClicked(View view)
    {
        boolean isChecked = ((RadioButton)view).isChecked();
        switch (view.getId())
        {
            case R.id.button_green:
                if(isChecked)
                {
                    color = Color.GREEN;
                }
                break;
            case R.id.button_red:
                if(isChecked)
                {
                    color = Color.RED;
                }
                break;
            case R.id.button_blue:
                if(isChecked)
                {
                    color = Color.BLUE;
                }
                break;
            case R.id.button_easy:
                if(isChecked)
                {
                    difficulty = Difficulty.EASY;
                }
                break;
            case R.id.button_medium:
                if(isChecked)
                {
                    difficulty = Difficulty.MEDIUM;
                }
                break;
            case R.id.button_hard:
                if(isChecked)
                {
                    difficulty = Difficulty.HARD;
                }
                break;
        }
    }
}
