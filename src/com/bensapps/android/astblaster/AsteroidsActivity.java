/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bensapps.android.astblaster;

import java.util.Random;

import com.bensapps.android.astblaster.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;

public class AsteroidsActivity extends Activity {

    public MyGLSurfaceView mGLView;
    private Handler handler = new Handler();
    private float mX1,mY1,mVx1,mVy1,mAx1,mAy1,mAngle,life;
    private int running;
    private int level;
    long lasttime;
    float fps;
    boolean menu;
    SharedPreferences prefs;
    int highscore;
    
    SoundPool sp;
    private int soundid[] = new int[5];
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    MediaPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) { 		
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        Context context = getApplicationContext();
        //do sounds
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundid[0] = sp.load(this, R.raw.shoot, 1);
        soundid[1] = sp.load(this, R.raw.explosion,1);
        soundid[2] = sp.load(this, R.raw.coin,1);
        
        player = MediaPlayer.create(this, R.raw.deepspace);
        player.setLooping(true);
        player.setVolume(volume,volume);
        player.start();
        
        //code to commit the score
        
        
        prefs = getSharedPreferences("your_prefs",Activity.MODE_PRIVATE);
        MyGLRenderer.highscore = prefs.getInt("mscore"	, 0);
        
        //set the game loop to start in 2 seconds
        running=0;
        level=1;
        mGLView.mRenderer.labels=1000;
        Text.setScore(0);
        mGLView.mRenderer.setLoop(MyGLRenderer.MENULOOP);
        //this starts the game
        handler.postDelayed(menuLoop, 30); 
    }
    
   

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        player.stop();
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        mGLView.mRenderer.setLoop(MyGLRenderer.MENULOOP);
        /*player = MediaPlayer.create(this, R.raw.deepspace);
        player.setLooping(true);
        player.setVolume(volume,volume);
        player.start();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
       
        running=0;
        mGLView.mRenderer.labels=1000;
        //starttime=System.nanoTime();
        Text.setScore(0);
        mGLView.mRenderer.setLoop(MyGLRenderer.MENULOOP);
        mGLView.mRenderer.loaded=false;*/
    }
    
    private Runnable menuLoop=new Runnable(){
    	@Override
    	public void run(){
    		actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = actVolume / maxVolume;
            player.setVolume(volume,volume);
    		if(mGLView.mRenderer.getLoop()==MyGLRenderer.MENULOOP){
    			handler.postDelayed(menuLoop, 30);
    		}
    		if(mGLView.mRenderer.getLoop()==MyGLRenderer.GAMELOOP){
    			handler.postDelayed(gameLoop, 30);
    		}
    		if(mGLView.mRenderer.getLoop()==MyGLRenderer.UPGRADELOOP){
    			handler.postDelayed(upgradeLoop, 30);
    		}
    	}
    };
    //gets displayed after every level
    private Runnable upgradeLoop=new Runnable(){
    	@Override
    	public void run(){
    		actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = actVolume / maxVolume;
            player.setVolume(volume,volume);
            //do stuff for the upgrade screen
            if(mGLView.mRenderer.getLoop()==MyGLRenderer.MENULOOP){
    			handler.postDelayed(menuLoop, 30);
    		}
    		if(mGLView.mRenderer.getLoop()==MyGLRenderer.GAMELOOP){
    			handler.postDelayed(gameLoop, 30);
    		}
    		if(mGLView.mRenderer.getLoop()==MyGLRenderer.UPGRADELOOP){
    			handler.postDelayed(upgradeLoop, 30);
    		}
    	}
    };
    
    private Runnable gameLoop = new Runnable(){
    	  @Override
    	  public void run() {
    		  actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	      maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	      volume = actVolume / maxVolume;
    	      player.setVolume(volume,volume);
    		  lasttime=System.nanoTime();
    		  Random r = new Random(System.nanoTime());//a random number generator
    		  if(running==0){
    			  running=1; 
    			  for(int iii=0;iii<Ast.NO_AST_LARGE;iii++){
    				  mGLView.mRenderer.astLarge[iii].setActive(0);
    			  }
    			  for(int iii=0;iii<level;iii++){
        			  mGLView.mRenderer.astLarge[iii].setActive(1);
        			  float test[]=getFreeXY(iii);
        			  mGLView.mRenderer.astLarge[iii].setXY(test[0],test[1]);
        			  mGLView.mRenderer.astLarge[iii].setVXY(r.nextFloat()*0.003f-0.0015f, r.nextFloat()*0.003f-0.0015f);//0.002
        			  mGLView.mRenderer.astLarge[iii].setVangle(1f-0.5f+r.nextFloat());
        			  }
    			  mGLView.mRenderer.mShip.setXY(0,0);
    			  mGLView.mRenderer.mShip.setVxy(0,0);
    			  mGLView.mRenderer.mShip.setAxy(0,0);
    			  mGLView.mRenderer.mShip.setAngle(90);
    			  mGLView.mRenderer.mShip.clearHist();
    			  for(int iii=0;iii<Bullet.MAX_BULLETS;iii++){
    				  mGLView.mRenderer.bullet[iii].setActive(0);
    			  }
    		  }
    		  if(mGLView.mRenderer.getLoop()==MyGLRenderer.MENULOOP){//go into the menu
        		  handler.postDelayed(menuLoop,30);
				  			  }
    		  else if(mGLView.mRenderer.getLoop()==MyGLRenderer.UPGRADELOOP){
    			  handler.postDelayed(upgradeLoop, 30);
    		  }
    		  else{//stay in the game
    		  updateShip();
    		  updateAsteroids();
    		  updateBullets();
    		  updateExplosions();
    		  shipCollision();
    		  bulletCollision();
    		  coinCollision();
    		  updateLevel();
    		  
    		  mGLView.mRenderer.labels--;
    		  handler.postDelayed(gameLoop,30);//roughly 60 fps
    		  }
    	  }
};

private float[] getFreeXY(int iii)
{
	Random r = new Random(System.nanoTime());
	float radius=r.nextFloat()/2+0.5f;
	float[] test={0,0};
	test[0]=radius*(float)(Math.cos((float)(((float)(iii)*2f*Math.PI/10f))));
	test[1]=radius*(float)(Math.sin((float)(((float)(iii)*2f*Math.PI/10f))));
	return test;
}

private void updateLevel()
{
	int noAst=0;
	//check if all the asteroids are inactive
	for(int iii=0;iii<Ast.NO_AST_LARGE;iii++){
		if(mGLView.mRenderer.astLarge[iii].getActive()==1)noAst++;//for every active one add someting to test
	}
	for(int iii=0;iii<Ast.NO_AST_MED;iii++){
		if(mGLView.mRenderer.astMed[iii].getActive()==1)noAst++;//for every active one add someting to test
	}
	for(int iii=0;iii<Ast.NO_AST_SMALL;iii++){
		if(mGLView.mRenderer.astSmall[iii].getActive()==1)noAst++;//for every active one add someting to test
	}
	if(noAst==0){
		//there are no asteroids so we can change the level etc
		Ast.resetAsteroids();
		running=0;
		level++;
		if(level>10)level=10;
		mGLView.mRenderer.setLoop(MyGLRenderer.UPGRADELOOP);
	}
}


private void updateExplosions(){
	for(int iii=0;iii<20;iii++){
		if(mGLView.mRenderer.explosion[iii].getActive()){
			mGLView.mRenderer.explosion[iii].radius=mGLView.mRenderer.explosion[iii].radius+0.015f;
			if(mGLView.mRenderer.explosion[iii].radius>0.5)mGLView.mRenderer.explosion[iii].setActive(false);
		}
	}
}


private void updateShip(){
	  mX1=mGLView.mRenderer.mShip.getX();
	  mY1=mGLView.mRenderer.mShip.getY();
	  mVx1=mGLView.mRenderer.mShip.getVx();
	  mVy1=mGLView.mRenderer.mShip.getVy();
	  mAngle=mGLView.mRenderer.mShip.getAngle();
	  float ratio=mGLView.mRenderer.ratio;
	  Random r = new Random();
	  if(mGLView.getHyper()==1){//hyper is pressed
		  mGLView.setHyper(0);
		  mX1=2f*r.nextFloat()-1f;//a random number between -1 and 1
		  mY1=2f*ratio*r.nextFloat()-ratio;//a random number between -ratio and ratio
		  mVx1=0;
		  mVy1=0;
		  mGLView.mRenderer.mShip.setXY(mX1, mY1);
		  mGLView.mRenderer.mShip.saveCoords(mX1, mY1);
		  mGLView.mRenderer.mShip.setVxy(mVx1, mVy1);
		  mGLView.mRenderer.mShip.setAxy(mAx1,mAy1);
		  mGLView.mRenderer.mShip.clearHist();
	  }
	  if(mGLView.getPressed()==1){//thrust is pressed
		  //set the accelerations
		  mAx1=Ship.ACC*(float)(Math.cos(Math.toRadians(mAngle)));
		  mAy1=Ship.ACC*(float)(Math.sin(Math.toRadians(mAngle)));
		  mVx1=mVx1+mAx1;
		  mVy1=mVy1+mAy1;
	  }
	  if(mGLView.getPressed()==0){//thrust isnt pressed
		  //stop the accelerations
		  mAx1=0;
		  mAy1=0;
		  if((float)(Math.sqrt((double)(mVx1*mVx1+mVy1*mVy1)))>0){
		  mVx1=mVx1*0.95f;
		  mVy1=mVy1*0.95f;}
	  }
	  //add acceleration to velocity
	  
	  float mag = (float)(Math.sqrt((double)(mVx1*mVx1+mVy1*mVy1)));
	  if(mag>Ship.MAX_SPEED){
		  float scale=mag/Ship.MAX_SPEED;
		  mVx1=mVx1/scale;
		  mVy1=mVy1/scale;
	  }
	  //limit velocity
	  //add velocity to position
	  mX1=mX1+mVx1;
	  mY1=mY1+mVy1;//add the velocities to the positions
	  if(mX1>mGLView.mRenderer.ratio){
		  mX1=-mGLView.mRenderer.ratio;
		  mGLView.mRenderer.mShip.setXY(mX1, mY1);
		  mGLView.mRenderer.mShip.saveCoords(mX1, mY1);
		  mGLView.mRenderer.mShip.setVxy(mVx1, mVy1);
		  mGLView.mRenderer.mShip.setAxy(mAx1,mAy1);
		  mGLView.mRenderer.mShip.clearHist();
	  }
	  if(mX1<-mGLView.mRenderer.ratio){
		  mX1=mGLView.mRenderer.ratio;
		  mGLView.mRenderer.mShip.setXY(mX1, mY1);
		  mGLView.mRenderer.mShip.saveCoords(mX1, mY1);
		  mGLView.mRenderer.mShip.setVxy(mVx1, mVy1);
		  mGLView.mRenderer.mShip.setAxy(mAx1,mAy1);
		  mGLView.mRenderer.mShip.clearHist();
	  }
	  if(mY1<-1f){
		  mY1=1f;
		  mGLView.mRenderer.mShip.setXY(mX1, mY1);
		  mGLView.mRenderer.mShip.saveCoords(mX1, mY1);
		  mGLView.mRenderer.mShip.setVxy(mVx1, mVy1);
		  mGLView.mRenderer.mShip.setAxy(mAx1,mAy1);
		  mGLView.mRenderer.mShip.clearHist();
	  }
	  if(mY1>1f){
		  mY1=-1f;
		  mGLView.mRenderer.mShip.setXY(mX1, mY1);
		  mGLView.mRenderer.mShip.saveCoords(mX1, mY1);
		  mGLView.mRenderer.mShip.setVxy(mVx1, mVy1);
		  mGLView.mRenderer.mShip.setAxy(mAx1,mAy1);
		  mGLView.mRenderer.mShip.clearHist();
	  }
	  mGLView.mRenderer.mShip.setXY(mX1, mY1);
	  mGLView.mRenderer.mShip.saveCoords(mX1, mY1);
	  mGLView.mRenderer.mShip.setVxy(mVx1, mVy1);
	  mGLView.mRenderer.mShip.setAxy(mAx1,mAy1);
}

private void updateAsteroids(){
	int iii=0;
	for(iii=0;iii<Ast.NO_AST_LARGE;iii++){
		if(mGLView.mRenderer.astLarge[iii].getActive()==1){//only if they are active
			 mX1=mGLView.mRenderer.astLarge[iii].getX();
			 mY1=mGLView.mRenderer.astLarge[iii].getY();
			 mVx1=mGLView.mRenderer.astLarge[iii].getVX();
			 mVy1=mGLView.mRenderer.astLarge[iii].getVY();
			 mAngle=mGLView.mRenderer.astLarge[iii].getAngle();
			 mX1=mX1+mVx1;
			 mY1=mY1+mVy1;//add the velocities to the positions
			 mAngle=mAngle+mGLView.mRenderer.astLarge[iii].getVangle();
			 if(mX1>mGLView.mRenderer.ratio)mX1=-mGLView.mRenderer.ratio;
			 if(mX1<-mGLView.mRenderer.ratio)mX1=mGLView.mRenderer.ratio;
			 if(mY1<-1f)mY1=1f;
			 if(mY1>1f)mY1=-1f;
			  mGLView.mRenderer.astLarge[iii].setXY(mX1, mY1);
			  mGLView.mRenderer.astLarge[iii].setAngle(mAngle);
		}
	}
	
	for(iii=0;iii<Ast.NO_AST_MED;iii++){
		if(mGLView.mRenderer.astMed[iii].getActive()==1){//only if they are active
			 mX1=mGLView.mRenderer.astMed[iii].getX();
			 mY1=mGLView.mRenderer.astMed[iii].getY();
			 mVx1=mGLView.mRenderer.astMed[iii].getVX();
			 mVy1=mGLView.mRenderer.astMed[iii].getVY();
			 mAngle=mGLView.mRenderer.astMed[iii].getAngle();
			 mX1=mX1+mVx1;
			 mY1=mY1+mVy1;//add the velocities to the positions
			 mAngle=mAngle+mGLView.mRenderer.astMed[iii].getVangle();
			 if(mX1>mGLView.mRenderer.ratio)mX1=-mGLView.mRenderer.ratio;
			 if(mX1<-mGLView.mRenderer.ratio)mX1=mGLView.mRenderer.ratio;
			 if(mY1<-1f)mY1=1f;
			 if(mY1>1f)mY1=-1f;
			  mGLView.mRenderer.astMed[iii].setXY(mX1, mY1);
			  mGLView.mRenderer.astMed[iii].setAngle(mAngle);
		}
	}
	
	for(iii=0;iii<Ast.NO_AST_SMALL;iii++){
		if(mGLView.mRenderer.astSmall[iii].getActive()==1){//only if they are active
			 mX1=mGLView.mRenderer.astSmall[iii].getX();
			 mY1=mGLView.mRenderer.astSmall[iii].getY();
			 mVx1=mGLView.mRenderer.astSmall[iii].getVX();
			 mVy1=mGLView.mRenderer.astSmall[iii].getVY();
			 mAngle=mGLView.mRenderer.astSmall[iii].getAngle();
			 mX1=mX1+mVx1;
			 mY1=mY1+mVy1;//add the velocities to the positions
			 mAngle=mAngle+mGLView.mRenderer.astSmall[iii].getVangle();
			 if(mX1>mGLView.mRenderer.ratio)mX1=-mGLView.mRenderer.ratio;
			 if(mX1<-mGLView.mRenderer.ratio)mX1=mGLView.mRenderer.ratio;
			 if(mY1<-1f)mY1=1f;
			 if(mY1>1f)mY1=-1f;
			  mGLView.mRenderer.astSmall[iii].setXY(mX1, mY1);
			  mGLView.mRenderer.astSmall[iii].setAngle(mAngle);
		}
	}
	
	
	
}

private void updateBullets(){
	int iii=0;
	//check if we need to add any more bullets
	if(mGLView.getShoot()==1){//we need to add a bullet
		
		mGLView.setShoot(0);
		//find the next inactive id
		for(iii=0;iii<Bullet.MAX_BULLETS;iii++){
			if(mGLView.mRenderer.bullet[iii].getActive()==0)break;//if we find a non-active one then break
		}
		if(iii<Bullet.MAX_BULLETS){//if its valid
		sp.play(soundid[0],volume,volume,1,0,1.0f);
		mGLView.mRenderer.bullet[iii].setActive(1);//then generate it and set it off
		mGLView.mRenderer.bullet[iii].setLife(70);
		mGLView.mRenderer.bullet[iii].setXY(mGLView.mRenderer.mShip.getX(), mGLView.mRenderer.mShip.getY());
		mGLView.mRenderer.bullet[iii].setAngle(mGLView.mRenderer.mShip.getAngle()+90);
		mGLView.mRenderer.bullet[iii].setVxy((float)(Bullet.SPEED*(double)(Math.cos(Math.toRadians(mGLView.mRenderer.mShip.getAngle())))), (float)(Bullet.SPEED*(double)(Math.sin(Math.toRadians(mGLView.mRenderer.mShip.getAngle())))));
	}}
	for(iii=0;iii<Bullet.MAX_BULLETS;iii++){
		if(mGLView.mRenderer.bullet[iii].getActive()==1){//update currently active bullets
			mX1=mGLView.mRenderer.bullet[iii].getX();
			mY1=mGLView.mRenderer.bullet[iii].getY();
			mVx1=mGLView.mRenderer.bullet[iii].getVx();
			mVy1=mGLView.mRenderer.bullet[iii].getVy();
			life=mGLView.mRenderer.bullet[iii].getLife();
			life--;
			mX1=mX1+mVx1;
			mY1=mY1+mVy1;
			if(mX1>mGLView.mRenderer.ratio)mX1=-mGLView.mRenderer.ratio;
			if(mX1<-mGLView.mRenderer.ratio)mX1=mGLView.mRenderer.ratio;
			if(mY1<-1f)mY1=1f;
			if(mY1>1f)mY1=-1f;
			mGLView.mRenderer.bullet[iii].setXY(mX1, mY1);
			mGLView.mRenderer.bullet[iii].setLife(life);
			if(life<0f)mGLView.mRenderer.bullet[iii].setActive(0);
		}
	}
	
	
}
private void shipCollision(){
	int collision=0;
	int iii=0;
	mX1=mGLView.mRenderer.mShip.getX();
	mY1=mGLView.mRenderer.mShip.getY();
	mAngle=(float)Math.toRadians(mGLView.mRenderer.mShip.getAngle());
	float p1[]={mX1+0.1f*(float)(Math.cos(mAngle)),mY1+0.1f*(float)(Math.sin(mAngle))};
	float p2[]={mX1+0.07f*(float)(Math.cos(Math.toRadians(135)-mAngle)),mY1-0.07f*(float)(Math.sin(Math.toRadians(135)-mAngle))};
	float p3[]={mX1-0.07f*(float)(Math.cos(mAngle-Math.toRadians(45))),mY1-0.07f*(float)(Math.sin(mAngle-Math.toRadians(45)))};//calculate the ship bounding vertexes
	//model the ship as a circle and the asteroids as circles
	for(iii=0;iii<Ast.NO_AST_LARGE;iii++){//there are 6 asteroids max
		if(mGLView.mRenderer.astLarge[iii].getActive()==1){
			// check p1, p2, p3 with result1
			float result1[]=Ast.getCoords(mGLView.mRenderer.astLarge[iii]);
	if(pointInPolygon(p1[0],p1[1],result1) || pointInPolygon(p2[0],p2[1],result1) || pointInPolygon(p3[0],p3[1],result1))collision=1;
	}}
	for(iii=0;iii<Ast.NO_AST_MED;iii++){//there are 6 asteroids max
		if(mGLView.mRenderer.astMed[iii].getActive()==1){
			// check p1, p2, p3 with result1
			float result1[]=Ast.getCoords(mGLView.mRenderer.astMed[iii]);
	if(pointInPolygon(p1[0],p1[1],result1) || pointInPolygon(p2[0],p2[1],result1) || pointInPolygon(p3[0],p3[1],result1))collision=1;
	}}
	for(iii=0;iii<Ast.NO_AST_SMALL;iii++){//there are 6 asteroids max
		if(mGLView.mRenderer.astSmall[iii].getActive()==1){
			// check p1, p2, p3 with result1
			float result1[]=Ast.getCoords(mGLView.mRenderer.astSmall[iii]);
	if(pointInPolygon(p1[0],p1[1],result1) || pointInPolygon(p2[0],p2[1],result1) || pointInPolygon(p3[0],p3[1],result1))collision=1;
	}}
	if(collision==1 && mGLView.mRenderer.upgradeScreen.lives == 0){
		level=1;
		 mGLView.mRenderer.upgradeScreen.lives = 3;
		Ast.resetAsteroids();//reset the asteroids
		running=0;//if there has been a collision reset up the level
		mGLView.mRenderer.setLoop(MyGLRenderer.MENULOOP);
		if(Text.getScore()>MyGLRenderer.highscore)MyGLRenderer.highscore=Text.getScore();
		prefs = getSharedPreferences("your_prefs",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("mscore",MyGLRenderer.highscore);
        editor.commit();
		Text.setScore(0);
		MyGLRenderer.MONEY=0;
		Text.updateScore();
	}
	else if(collision == 1 && mGLView.mRenderer.upgradeScreen.lives != 0)
	{
		Ast.resetAsteroids();
		running = 0;
		mGLView.mRenderer.upgradeScreen.lives--;
		
	}
}

private void coinCollision(){
	//check for collisions
	boolean collision = false;
	mX1=mGLView.mRenderer.mShip.getX();
	mY1=mGLView.mRenderer.mShip.getY();
	mAngle=(float)Math.toRadians(mGLView.mRenderer.mShip.getAngle());
	float p1[]={mX1+0.1f*(float)(Math.cos(mAngle)),mY1+0.1f*(float)(Math.sin(mAngle))};
	float p2[]={mX1+0.07f*(float)(Math.cos(Math.toRadians(135)-mAngle)),mY1-0.07f*(float)(Math.sin(Math.toRadians(135)-mAngle))};
	float p3[]={mX1-0.07f*(float)(Math.cos(mAngle-Math.toRadians(45))),mY1-0.07f*(float)(Math.sin(mAngle-Math.toRadians(45)))};//calculate the ship bounding vertexes
	mX1 = mGLView.mRenderer.mCoin.mX;
	mY1 = mGLView.mRenderer.mCoin.mY;
	float p1distance2 = (mX1-p1[0])*(mX1-p1[0])+(mY1-p1[1])*(mY1-p1[1]);//distance between p1 and centre of circle
	float p2distance2 = (mX1-p2[0])*(mX1-p2[0])+(mY1-p2[1])*(mY1-p2[1]);
	float p3distance2 = (mX1-p3[0])*(mX1-p3[0])+(mY1-p3[1])*(mY1-p3[1]);
	if(p1distance2<Coin.RADIUS2 || p2distance2<Coin.RADIUS2 || p3distance2<Coin.RADIUS2){
		collision=true;
		Random r = new Random(System.nanoTime());
		mGLView.mRenderer.mCoin.mX=2*mGLView.mRenderer.ratio*r.nextFloat()-mGLView.mRenderer.ratio;//random number between -ratio and ratio
		mGLView.mRenderer.mCoin.mY=2*r.nextFloat()-1;//random number between -1 and 1
		mGLView.mRenderer.MONEY=mGLView.mRenderer.MONEY+50;
		sp.play(soundid[2],volume,volume,1,0,1.0f);
	}
}


private void bulletCollision(){
	//int collision=0;
	int iii=0;
	int jjj=0;
	int kkk=0;
	
	for(iii=0;iii<Ast.NO_AST_LARGE;iii++){//loop through all the asteroids
		for(jjj=0;jjj<Bullet.MAX_BULLETS;jjj++){//loop through all the bullets
			if(mGLView.mRenderer.bullet[jjj].getActive()==1 && mGLView.mRenderer.astLarge[iii].getActive()==1){//only check the active bullets
				//we need to get an array with all the points of the asteroid in order
				float bulletx=mGLView.mRenderer.bullet[jjj].getX();
				float bullety=mGLView.mRenderer.bullet[jjj].getY();
				float bulleta=mGLView.mRenderer.bullet[jjj].getAngle();
				float astx=mGLView.mRenderer.astLarge[iii].getX();
				float asty=mGLView.mRenderer.astLarge[iii].getY();
				float distance2=(astx-bulletx)*(astx-bulletx)+(asty-bullety)*(asty-bullety);
				if(distance2<Ast.LARGE_R2){
				float radius=Bullet.RADIUS;
				float p1[]={bulletx-radius*(float)(Math.sin(Math.toRadians(bulleta-Bullet.ANGLE))),bullety+radius*(float)(Math.cos(Math.toRadians(bulleta-Bullet.ANGLE)))};//the vertices of the bullet
				float p2[]={bulletx-radius*(float)(Math.cos(Math.toRadians(90-Bullet.ANGLE-bulleta))),bullety+radius*(float)(Math.sin(Math.toRadians(90-Bullet.ANGLE-bulleta)))};
				float p3[]={bulletx+radius*(float)(Math.sin(Math.toRadians(bulleta-Bullet.ANGLE))),bullety-radius*(float)(Math.cos(Math.toRadians(bulleta-Bullet.ANGLE)))};
				float p4[]={bulletx+radius*(float)(Math.cos(Math.toRadians(90-Bullet.ANGLE-bulleta))),bullety-radius*(float)(Math.sin(Math.toRadians(90-Bullet.ANGLE-bulleta)))};
				if( pointInPolygon(p1[0],p1[1],Ast.getCoords(mGLView.mRenderer.astLarge[iii])) || 
						pointInPolygon(p2[0],p2[1],Ast.getCoords(mGLView.mRenderer.astLarge[iii])) ||
						pointInPolygon(p3[0],p3[1],Ast.getCoords(mGLView.mRenderer.astLarge[iii])) ||
						pointInPolygon(p4[0],p4[1],Ast.getCoords(mGLView.mRenderer.astLarge[iii]))){
				mGLView.mRenderer.bullet[jjj].setActive(0);
				mGLView.mRenderer.astLarge[iii].setActive(0);
				int mScore=Text.getScore();
				Text.setScore(mScore+50);
				
				sp.play(soundid[1],volume,volume,1,0,1.0f);
				Text.updateScore();
				//first get an inactive explosion
				for(kkk=0;kkk<20;kkk++){
					if(mGLView.mRenderer.explosion[kkk].getActive()==false)break;
				}
				mGLView.mRenderer.explosion[kkk].setActive(true);
				mGLView.mRenderer.explosion[kkk].setXY(astx,asty);
				mGLView.mRenderer.explosion[kkk].radius=0.0f;
				//need to put 2 medium asteroids in place of the large asteroid
				//first get an avaliable "astmed"
				for(kkk=0;kkk<Ast.NO_AST_MED;kkk++){
					if(mGLView.mRenderer.astMed[kkk].getActive()==0)break;
				}
				for(int lll=0;lll<2;lll++){
					Random r = new Random(System.nanoTime());
					mGLView.mRenderer.astMed[kkk].setActive(1);
					mGLView.mRenderer.astMed[kkk].setXY(mGLView.mRenderer.astLarge[iii].getX(),mGLView.mRenderer.astLarge[iii].getY());
					mGLView.mRenderer.astMed[kkk].setVXY(r.nextFloat()*0.006f-0.003f, r.nextFloat()*0.006f-0.003f);//0.002
					mGLView.mRenderer.astMed[kkk].setVangle(1f-0.5f+r.nextFloat());
					kkk++;
				}
			}
		}
	}}
	}
	
	for(iii=0;iii<Ast.NO_AST_MED;iii++){//loop through all the asteroids
		for(jjj=0;jjj<Bullet.MAX_BULLETS;jjj++){//loop through all the bullets
			if(mGLView.mRenderer.bullet[jjj].getActive()==1 && mGLView.mRenderer.astMed[iii].getActive()==1){//only check the active bullets
				//we need to get an array with all the points of the asteroid in order
				float bulletx=mGLView.mRenderer.bullet[jjj].getX();
				float bullety=mGLView.mRenderer.bullet[jjj].getY();
				float bulleta=mGLView.mRenderer.bullet[jjj].getAngle();
				float astx=mGLView.mRenderer.astMed[iii].getX();
				float asty=mGLView.mRenderer.astMed[iii].getY();
				float distance2=(astx-bulletx)*(astx-bulletx)+(asty-bullety)*(asty-bullety);
				if(distance2<Ast.MED_R2){
					float radius=Bullet.RADIUS;
					float p1[]={bulletx-radius*(float)(Math.sin(Math.toRadians(bulleta-Bullet.ANGLE))),bullety+radius*(float)(Math.cos(Math.toRadians(bulleta-Bullet.ANGLE)))};//the vertices of the bullet
					float p2[]={bulletx-radius*(float)(Math.cos(Math.toRadians(90-Bullet.ANGLE-bulleta))),bullety+radius*(float)(Math.sin(Math.toRadians(90-Bullet.ANGLE-bulleta)))};
					float p3[]={bulletx+radius*(float)(Math.sin(Math.toRadians(bulleta-Bullet.ANGLE))),bullety-radius*(float)(Math.cos(Math.toRadians(bulleta-Bullet.ANGLE)))};
					float p4[]={bulletx+radius*(float)(Math.cos(Math.toRadians(90-Bullet.ANGLE-bulleta))),bullety-radius*(float)(Math.sin(Math.toRadians(90-Bullet.ANGLE-bulleta)))};				if( pointInPolygon(p1[0],p1[1],Ast.getCoords(mGLView.mRenderer.astMed[iii])) || 
						pointInPolygon(p2[0],p2[1],Ast.getCoords(mGLView.mRenderer.astMed[iii])) ||
						pointInPolygon(p3[0],p3[1],Ast.getCoords(mGLView.mRenderer.astMed[iii])) ||
						pointInPolygon(p4[0],p4[1],Ast.getCoords(mGLView.mRenderer.astMed[iii]))){
				mGLView.mRenderer.bullet[jjj].setActive(0);
				mGLView.mRenderer.astMed[iii].setActive(0);
				int mScore=Text.getScore();
				Text.setScore(mScore+100);
				Text.updateScore();
				sp.play(soundid[1],volume,volume,1,1,1.0f);
				for(kkk=0;kkk<20;kkk++){
					if(mGLView.mRenderer.explosion[kkk].getActive()==false)break;
				}
				mGLView.mRenderer.explosion[kkk].setActive(true);
				mGLView.mRenderer.explosion[kkk].setXY(astx,asty);
				mGLView.mRenderer.explosion[kkk].radius=0.0f;
				//need to put 2 medium asteroids in place of the large asteroid
				//first get an avaliable "astmed"
				for(kkk=0;kkk<Ast.NO_AST_SMALL;kkk++){
					if(mGLView.mRenderer.astSmall[kkk].getActive()==0)break;
				}
				for(int lll=0;lll<2;lll++){
					Random r = new Random(System.nanoTime());
					mGLView.mRenderer.astSmall[kkk].setActive(1);
					mGLView.mRenderer.astSmall[kkk].setXY(mGLView.mRenderer.astMed[iii].getX(),mGLView.mRenderer.astMed[iii].getY());
					mGLView.mRenderer.astSmall[kkk].setVXY(r.nextFloat()*0.009f-0.0045f, r.nextFloat()*0.009f-0.0045f);//0.002
					mGLView.mRenderer.astSmall[kkk].setVangle(1f-0.5f+r.nextFloat());
					kkk++;
				}
			}
		}}
	}
	}
	
	for(iii=0;iii<Ast.NO_AST_SMALL;iii++){//loop through all the asteroids
		for(jjj=0;jjj<Bullet.MAX_BULLETS;jjj++){//loop through all the bullets
			if(mGLView.mRenderer.bullet[jjj].getActive()==1 && mGLView.mRenderer.astSmall[iii].getActive()==1){//only check the active bullets
				//we need to get an array with all the points of the asteroid in order
				float bulletx=mGLView.mRenderer.bullet[jjj].getX();
				float bullety=mGLView.mRenderer.bullet[jjj].getY();
				float bulleta=mGLView.mRenderer.bullet[jjj].getAngle();
				float astx=mGLView.mRenderer.astSmall[iii].getX();
				float asty=mGLView.mRenderer.astSmall[iii].getY();
				float distance2=(astx-bulletx)*(astx-bulletx)+(asty-bullety)*(asty-bullety);
				if(distance2<Ast.SMALL_R2){
					float radius=Bullet.RADIUS;
					float p1[]={bulletx-radius*(float)(Math.sin(Math.toRadians(bulleta-Bullet.ANGLE))),bullety+radius*(float)(Math.cos(Math.toRadians(bulleta-Bullet.ANGLE)))};//the vertices of the bullet
					float p2[]={bulletx-radius*(float)(Math.cos(Math.toRadians(90-Bullet.ANGLE-bulleta))),bullety+radius*(float)(Math.sin(Math.toRadians(90-Bullet.ANGLE-bulleta)))};
					float p3[]={bulletx+radius*(float)(Math.sin(Math.toRadians(bulleta-Bullet.ANGLE))),bullety-radius*(float)(Math.cos(Math.toRadians(bulleta-Bullet.ANGLE)))};
					float p4[]={bulletx+radius*(float)(Math.cos(Math.toRadians(90-Bullet.ANGLE-bulleta))),bullety-radius*(float)(Math.sin(Math.toRadians(90-Bullet.ANGLE-bulleta)))};				if( pointInPolygon(p1[0],p1[1],Ast.getCoords(mGLView.mRenderer.astSmall[iii])) || 
					pointInPolygon(p2[0],p2[1],Ast.getCoords(mGLView.mRenderer.astSmall[iii])) ||
					pointInPolygon(p3[0],p3[1],Ast.getCoords(mGLView.mRenderer.astSmall[iii])) ||
					pointInPolygon(p4[0],p4[1],Ast.getCoords(mGLView.mRenderer.astSmall[iii]))){
				mGLView.mRenderer.bullet[jjj].setActive(0);
				mGLView.mRenderer.astSmall[iii].setActive(0);
				int mScore=Text.getScore();
				Text.setScore(mScore+200);
				Text.updateScore();
				sp.play(soundid[1],volume,volume,1,1,1.0f);
				for(kkk=0;kkk<20;kkk++){
					if(mGLView.mRenderer.explosion[kkk].getActive()==false)break;
				}
				mGLView.mRenderer.explosion[kkk].setActive(true);
				mGLView.mRenderer.explosion[kkk].setXY(astx,asty);
				mGLView.mRenderer.explosion[kkk].radius=0.0f;
			}
		}
	}}
	}
	
}
//for polys more square or more
private boolean pointInPolygon(float px, float py, float[] poly){

	float p[] = {px,py};
	float a[] = {poly[0],poly[1]};
	float b[] = {poly[3],poly[4]};
	float c[] = {poly[6],poly[7]};
	float d[] = {poly[9],poly[10]};
	float e[] = {poly[12],poly[13]};
	//first do a bounding box check

	
	if(sameSide(p,c,a,b) && sameSide(p,a,b,c) && sameSide(p,a,c,d) && sameSide(p,a,d,e) && sameSide(p,b,e,a))return true;
	else return false;


}
//a function to tell us if two points are on the same side of a line
//take 4 arrays as vectors
private boolean sameSide(float[] p1, float[] p2, float[] a, float[] b){
	//first calculate the cross product of (b-a)*(p1-a)
	float x1,x2,y1,y2,cp1,cp2;
	x1=b[0]-a[0];
	y1=b[1]-a[1];//store (b-a)
	x2=p1[0]-a[0];
	y2=p1[1]-a[1];//store p1-a
	cp1=x1*y2-x2*y1;
	x2=p2[0]-a[0];
	y2=p2[1]-a[1];//store p2-a
	cp2=x1*y2-x2*y1;
	if((cp1*cp2)>0)return true;
	else return false;
}
}



