package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.TextInputListener;



public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	float screenWidth, screenHeight, bulldogX, bulldogY, bulldogWidth, bulldogHeight;
	Texture [] bulldog;
	int whichDog = 0;
	int gameState = 0;
	int pause  = 0;
	Random random;

	Rectangle dogRectangle;
	BitmapFont font;
	int score = 0;

	ArrayList<Integer> ballX = new ArrayList<Integer>();
	ArrayList<Integer> ballY = new ArrayList<Integer>();
	ArrayList<Rectangle> ballRects =  new ArrayList<Rectangle>();
	Texture ball;
	int ballCount; //have proper spacing between coins
	float ballWidth;
	float ballHeight;
	float jump = 10;
	float dogVelocity;
	float destination;
	OrthographicCamera camera;

	Texture [] enemies;
	float[] enemyX;
	float [] enemyY;
	Rectangle [] enemyRect;
	float enemyWidth;
	float enemyHeight;
	Texture gameOver;
	int enemyCount = 6;
	float enemyVelocity = 8;
	boolean beginning = true;

    public class MyTextInputListener implements TextInputListener {
        public String newText;

        @Override
        public void input (String text) {
            newText = text;
        }
        @Override
        public void canceled () {

        }

        public String getText(){
            return newText;
        }
    }

    MyTextInputListener listener;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		background = new Texture("background.jpg");
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		random = new Random();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);
        listener = new MyTextInputListener();

        bulldog = new Texture[5];
		bulldog[0] = new Texture("dog1.png");
		bulldog[1] = new Texture("dog2.png");
		bulldog[2] = new Texture("dog3.png");
		bulldog[3] = new Texture("dog4.png");
		bulldog[4] = new Texture("dog5.png");

		ball = new Texture("basketball.png");
		ballWidth = ball.getWidth();
		ballHeight = ball.getHeight();

		enemies = new Texture[enemyCount];
		enemies[0] = new Texture("villanova.png");
		enemies[1] = new Texture("syracuse.png");
		enemies[2] = new Texture("Marquette.png");
		enemies[3] = new Texture("creighton.png");
		enemies[4] = new Texture("snaps.png");
		enemies[5] = new Texture("rat.png");
		enemyX = new float[enemyCount];
		enemyY = new float[enemyCount];
		enemyRect = new Rectangle[enemyCount];

		gameOver = new Texture("gameover.png");

		font = new BitmapFont();
		font.setColor(Color.GOLD);
		font.getData().setScale(10);

		initialize();
	}

	public void initialize(){

		bulldogX = screenWidth/2 - bulldog[0].getWidth()/2;
		bulldogY = screenHeight/2;
		bulldogWidth = bulldog[0].getWidth();
		bulldogHeight = bulldog[0].getHeight();
		dogVelocity = 0;

		enemyWidth = enemies[0].getWidth();
		enemyHeight = enemies[0].getHeight();

		ballX.clear();
		ballY.clear();
		ballRects.clear();
		ballCount = 0;
		score = 0;

		for( int i = 0 ; i < enemyCount; i++){
			enemyX[i] = screenWidth + i*screenWidth*3/4;
			enemyY[i] = random.nextFloat() * ( screenHeight - enemyHeight );
			enemyRect[i] = new Rectangle();
		}
	}

	public void drawDogs(){
		batch.draw(bulldog[whichDog], bulldogX, bulldogY);
		bulldogWidth = bulldog[whichDog].getWidth();
		bulldogHeight = bulldog[whichDog].getHeight();
		if( pause < 8 ){
			pause++;
		} else{
			pause = 0;
			if( whichDog < 4 ){
				whichDog++;
			} else {
				whichDog = 0;
			}
		}
	}

	public void makeEnemy() {
		for (int i=0;i < enemyCount;i++) {
			if( enemyX[i] < -enemyWidth ) {
				enemyX[i] = 3*screenWidth;
				enemyY[i] = random.nextFloat() * (screenHeight-enemyHeight);
			}
			enemyWidth = enemies[i].getWidth();
			enemyHeight =  enemies[i].getHeight();
			batch.draw(enemies[i], enemyX[i], enemyY[i]);
			enemyX[i] -= enemyVelocity;
			enemyRect[i] = new Rectangle(enemyX[i], enemyY[i], enemyWidth, enemyHeight);
		}
	}

	public void makeBalls() {
		float height = random.nextFloat() * screenHeight;
		ballY.add((int)height);
		ballX.add((int)screenWidth);
	}
	public void setDestination(){
		destination = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).y;
		if( destination < bulldogY + bulldogHeight/2 ){
			dogVelocity = jump*-1;
		}
		else{
			dogVelocity = jump;
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if( beginning ){
            Gdx.input.getTextInput(listener, "Enter Cheat Code", "", "" );
            beginning = false;
        }
        if( listener.getText() != null ){
            if( listener.getText().toLowerCase().equals("hoyasaxa")) jump = 20;
            Gdx.app.debug("WHY", String.valueOf(jump) );

        }

        batch.begin();
		batch.draw(background, 0,0,screenWidth, screenHeight);


		if( gameState == 1 ){
			makeEnemy();

			ballCount = (ballCount + 1) % 100;
			if (ballCount == 0){
				makeBalls();
			}

			ballRects.clear();
			for (int i=0;i < ballX.size();i++) {
				batch.draw(ball, ballX.get(i), ballY.get(i));
				ballX.set(i, ballX.get(i) - 4);
				ballRects.add(new Rectangle(ballX.get(i), ballY.get(i), ball.getWidth(), ball.getHeight()));
				if( ballX.get(i) == 0 ) score--;
			}
			if( Gdx.input.justTouched() ) {
				setDestination();
			}
			bulldogY = bulldogY + dogVelocity;
			if( bulldogY + bulldogHeight >= screenHeight){
				dogVelocity = 0;
				bulldogY = screenHeight - bulldogHeight;
			}
			else if( bulldogY <= 0 ) {
				dogVelocity = 0;
			}
		} else if( gameState == 0 ) {
			if(Gdx.input.justTouched()){
				initialize();
				gameState = 1;
				setDestination();
			}
		}
		else{
			batch.draw(gameOver, screenWidth/2 - gameOver.getWidth()/2, screenHeight/2);
			if(Gdx.input.justTouched()){
				initialize();
				gameState = 1;
			}
		}
		drawDogs();

		dogRectangle = new Rectangle(screenWidth/2 - bulldogWidth/2, bulldogY, bulldogWidth, bulldogHeight);

		for (int i=0; i < ballRects.size();i++) {
			if (Intersector.overlaps(dogRectangle, ballRects.get(i))) {
				score++;
				ballRects.remove(i);
				ballX.remove(i);
				ballY.remove(i);
				break;
			}
		}
		if( enemyRect[0] != null ) {
			for (int i = 0; i < enemyRect.length; i++) {
				if (Intersector.overlaps(dogRectangle, enemyRect[i])) {
					gameState = 2;
				}
			}
		}

		font.draw(batch, String.valueOf(score),100,200);

		batch.end();
	}

}
