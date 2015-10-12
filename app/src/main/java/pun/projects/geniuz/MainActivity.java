package pun.projects.geniuz;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.SoundPool;
import android.media.AudioManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Integer RED = 0;
    private Integer GREEN = 1;
    private Integer BLUE = 2;
    private Integer YELLOW = 3;
    private Integer MAX_DIFFICULTY = 7;

    private TextView scoreView = null;
    private Button redButton = null;
    private Button greenButton = null;
    private Button blueButton = null;
    private Button yellowButton = null;
    private Button goButton = null;
    private Button resetButton = null;

    private Random rand = new Random();
    private Boolean showingSequence = false;
    private Boolean blinkingButton = false;
    private Boolean waitingPlayer = false;
    private List<Integer> colorSequence = new ArrayList<Integer>();
    private Integer difficulty = 1;
    private Integer score = 0;
    private Integer currentColor = 0;
    private Integer currentColorIndex = 0;

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    private Handler handler = new Handler();
    private Runnable colorRunnable = new Runnable(){
        public void run() {
            Integer color = currentColor;
            setButtonAlpha(color, 255);
            blinkingButton = false;

        }
    };
    private Runnable sequenceRunnable = new Runnable(){
        public void run() {
            Integer color = colorSequence.get(currentColorIndex);
            setButtonAlpha(color, 100);
            soundPool.play((int) soundPoolMap.get(color), 1, 1, 1, 0, 1f);
            handler.postDelayed(sequence2Runnable, 500);

        }
    };
    private Runnable sequence2Runnable = new Runnable(){
        public void run() {
            Integer color = colorSequence.get(currentColorIndex);
            setButtonAlpha(color, 255);
            currentColorIndex++;
            if (currentColorIndex < colorSequence.size())
                handler.postDelayed(sequenceRunnable, 500);
            else
            {
                showingSequence = false;
                currentColorIndex = 0;
                waitingPlayer = true;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rand.setSeed(Calendar.getInstance().get(Calendar.SECOND));
        scoreView = (TextView) findViewById(R.id.scoreView);
        redButton = (Button) findViewById(R.id.redButton);
        greenButton = (Button) findViewById(R.id.greenButton);
        blueButton = (Button) findViewById(R.id.blueButton);
        yellowButton = (Button) findViewById(R.id.yellowButton);
        goButton = (Button) findViewById(R.id.goButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap(4);
        soundPoolMap.put(RED, soundPool.load(this, R.raw.a, 1));
        soundPoolMap.put(GREEN, soundPool.load(this, R.raw.c, 2));
        soundPoolMap.put(BLUE, soundPool.load(this, R.raw.e, 3));
        soundPoolMap.put(YELLOW, soundPool.load(this, R.raw.g, 4));

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingSequence) return;
                if (waitingPlayer) return;

                Integer color = rand.nextInt(4);
                colorSequence.add(color);

                currentColorIndex = 0;
                showingSequence = true;
                handler.postDelayed(sequenceRunnable, 100);


            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               gameOver();

            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtonClickEvent(RED);
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtonClickEvent(GREEN);

            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtonClickEvent(BLUE);

            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorButtonClickEvent(YELLOW);
            }
        });

    }

    public void addColorToSequence()
    {
        Integer color = rand.nextInt(4);
        colorSequence.add(color);

        currentColorIndex = 0;
        handler.postDelayed(sequenceRunnable, 1000);
    }

    public void pressColor(Integer pressedColor)
    {
        Integer rightColor = colorSequence.get(currentColorIndex);
        if (pressedColor == rightColor)
        {
            currentColorIndex++;
            if (currentColorIndex >= colorSequence.size())
            {
                waitingPlayer = false;
                currentColorIndex = 0;
                difficulty++;
                score++;
                showingSequence = true;
                addColorToSequence();
                scoreView.setText("Score: " + score.toString());
            }
        }
        else
        {
            gameOver();
        }


    }

    public void colorButtonClickEvent(Integer color)
    {
        if (showingSequence) return;
        if (!waitingPlayer) return;
        currentColor = color;
        blinkingButton = true;

        if (color == RED) redButton.getBackground().setAlpha(100);
        else if (color == GREEN) greenButton.getBackground().setAlpha(100);
        else if (color == BLUE) blueButton.getBackground().setAlpha(100);
        else yellowButton.getBackground().setAlpha(100);

        handler.postDelayed(colorRunnable, 250);
        soundPool.play((int) soundPoolMap.get(color), 1, 1, 1, 0, 1f);
        pressColor(color);


    }

    public void gameOver()
    {
        score = 0;
        handler.removeCallbacks(colorRunnable);
        handler.removeCallbacks(sequenceRunnable);
        handler.removeCallbacks(sequence2Runnable);
        for (int i = 0; i < 4; i++) setButtonAlpha(i, 255);
        difficulty = 1;
        showingSequence = false;
        waitingPlayer = false;
        currentColorIndex = 0;
        scoreView.setText("Score: 0");
        colorSequence.clear();

    }

    public void setButtonAlpha(Integer buttonColor, Integer alpha)
    {
        if (buttonColor == RED) redButton.getBackground().setAlpha(alpha);
        else if (buttonColor == GREEN) greenButton.getBackground().setAlpha(alpha);
        else if (buttonColor == BLUE) blueButton.getBackground().setAlpha(alpha);
        else yellowButton.getBackground().setAlpha(alpha);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
