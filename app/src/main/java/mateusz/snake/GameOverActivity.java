package mateusz.snake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_game_over);
        checkScore();
        displayPlayerScore();
        displayHighScore();
    }

    /**
     * Return to main activity to start game from the beginning.
     * @param view
     */
    public void restartGame(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Display score player managed to get.
     */
    private void displayPlayerScore()
    {
         /*Textview displaying game score*/
        TextView finalScore = new TextView(this);
        /*String with score*/
        finalScore = (TextView)findViewById(R.id.finalScore);
        String score = "Your score: " + Integer.toString(getPlayerScore());
        finalScore.setText(score);
    }

    /**
     * Display current high score.
     */
    private void displayHighScore()
    {
        /*Textview displaying high score*/
        TextView highScore = new TextView(this);
        /*String with high score*/
        highScore = (TextView)findViewById(R.id.highScore);
        String score = "High score: " + Integer.toString(getHighScore());
        highScore.setText(score);
    }

    /**
     * Get player score stored in intent sent to activity.
     * @return
     */
    private int getPlayerScore()
    {
        int score = 0;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
            score = (int)bundle.get("finalScore");
        return score;
    }

    /**
     * Get high score stored in shared preferences.
     * @return
     */
    private int getHighScore()
    {
        SharedPreferences preferences = getSharedPreferences("snake_preferences",
                Activity.MODE_PRIVATE);
        int highScore = preferences.getInt("snake_preferences", 0);
        return highScore;
    }

    /**
     * Check if current score is greater than high score.
     */
    private void checkScore()
    {
        int playerScore = getPlayerScore();
        int highScore = getHighScore();
        /*Change high score to current player score*/
        if (playerScore > highScore)
        {
            SharedPreferences preferences = getSharedPreferences("snake_preferences",
                    Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("snake_preferences", playerScore);
            editor.apply();
        }
    }
}
