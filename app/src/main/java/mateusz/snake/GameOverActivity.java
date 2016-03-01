package mateusz.snake;

import android.content.Intent;
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
         /*Textview displaying game score*/
        TextView finalScore = new TextView(this);
        /*String with score*/
        String score = new String();

        finalScore = (TextView)findViewById(R.id.finalScore);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
            score = (String)bundle.get("finalScore");
        finalScore.setText(score);
    }

    public void restartGame(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
