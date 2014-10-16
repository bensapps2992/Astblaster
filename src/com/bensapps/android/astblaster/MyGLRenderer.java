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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
	
	
    private static final String TAG = "MyGLRenderer";
    public Ship mShip;
    public SquareButton mFire;
    public SquareButton mHyper;
    private MenuButton mMenuButton;
    public Direction mDirection;
    private Background mBackground;
    public Triangle bg;
    public Circle mCircle;
    public Coin mCoin;
    private Line mTrail;
    private TextManager mTextManager;
    public Ast[] astLarge=new Ast[Ast.NO_AST_LARGE];
    public Ast[] astMed=new Ast[Ast.NO_AST_MED];
    public Ast[] astSmall=new Ast[Ast.NO_AST_SMALL];
    public Bullet[] bullet=new Bullet[20];
    public ParticleSystem[] explosion = new ParticleSystem[20];//lets have 20 explosions at the most
    private Rect loader;
    public Upgrade upgradeScreen;
    
    public static float ratio;
    public static int MONEY=0;
    private float mWidth;
    private float mHeight;
    public boolean loaded;
    int counter;
    private float time=0;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    
    Context mContext;
    
    
    public int labels;
    private boolean gameLoop;
    private boolean menuLoop;
    private boolean upgradeLoop;
    private boolean reloadMenu;
    private boolean reloadGameText;
    public static int GAMELOOP=0;
    public static int MENULOOP=1;
    public static int UPGRADELOOP=2;
    public static int highscore;
    
    public static float[] getProjMat(){
    	float[] ProjectionMatrix = new float[16];
    	Matrix.frustumM(ProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    	return ProjectionMatrix;
    }
    
    public static float[] getViewMat(){
    	float[] ViewMatrix = new float[16];
    	Matrix.setLookAtM(ViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    	return ViewMatrix;
    }
    
    public int getLoop(){
    	if(gameLoop){
    		return GAMELOOP;
    	}
    	else if(menuLoop){
    		return MENULOOP;
    	}
    	else if(upgradeLoop){
    		return UPGRADELOOP;
    	}
    	else return 3;
    }
    
    public void setLoop(int loop){
    	if(loop==GAMELOOP){
    		gameLoop=true;
    		menuLoop=false;
    		upgradeLoop=false;
    		reloadGameText=true;
    	}
    	else if(loop==MENULOOP){
    		menuLoop=true;
    		gameLoop=false;
    		upgradeLoop=false;
    		reloadMenu=true;
    	}
    	else if(loop==UPGRADELOOP){
    		upgradeLoop=true;
    		menuLoop=false;
    		gameLoop=false;
    		upgradeScreen.updateTex=true;
    	}
    	
    }
    
    public MyGLRenderer(Context c, float r)
	{
		mContext = c;
		ratio=r;
		reloadMenu=true;
		reloadGameText=false;
		loaded=false;
	}
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            	loader=new Rect();
            	bg=new Triangle(mContext);
                mTextManager = new TextManager(0);

            	upgradeScreen = new Upgrade(mContext, ratio);
            	counter=-2;
    }

    
    void loadGame(int object){
    	if(object==0){
    	GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mShip = new Ship();
        mFire= new SquareButton();
        mHyper = new SquareButton();
        mMenuButton = new MenuButton();
        mBackground = new Background(ratio);
        mCoin = new Coin(0f,0f,mContext);
        mDirection=new Direction();
        mCircle = new Circle();
        mTrail = new Line();
        }
    	
    	if(object==1){
    	
        for(int iii=0;iii<Ast.NO_AST_LARGE;iii++){//max 6 asteroids on screen
        	astLarge[iii]=new Ast(Ast.LARGE);
        }
    	}
    	
    	if(object==2){
        for(int iii=0;iii<Ast.NO_AST_MED;iii++){//max 6 asteroids on screen
        	astMed[iii]=new Ast(Ast.MED);
        }
    	}
    	if(object==3){
        for(int iii=0;iii<Ast.NO_AST_SMALL;iii++){//max 6 asteroids on screen
        	astSmall[iii]=new Ast(Ast.SMALL);
        }
    	}
    	if(object==4){
        for(int iii=0;iii<20;iii++){//max of 5 bullets at once
        	bullet[iii]=new Bullet(mContext);
        }}
    	if(object==5){
        for(int iii=0;iii<20;iii++){
            explosion[iii]=new ParticleSystem(0,0);
            }}
        
    }

  private void drawButtons(){
    	Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mMVPMatrix, 0, -3*ratio/4, -0.75f, 0);
        mFire.draw(mMVPMatrix);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mMVPMatrix, 0, -3*ratio/4, -0.25f, 0);
        mHyper.draw(mMVPMatrix);
         Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
         Matrix.translateM(mMVPMatrix, 0, 3*ratio/4, -0.75f, 0);
         Matrix.scaleM(mMVPMatrix, 0, 3, 3, 3);
        mCircle.draw(mMVPMatrix);
         Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
         Matrix.translateM(mMVPMatrix, 0, 3*ratio/4+mDirection.getX(), -0.75f+mDirection.getY(), 0);
        mDirection.draw(mMVPMatrix);
         Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
         Matrix.translateM(mMVPMatrix,0,0,1,0);
        mMenuButton.draw(mMVPMatrix);
    }
    
    
    
    private void drawBackground(){
    	Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    	mBackground.draw(mMVPMatrix,mWidth,mHeight,time, mShip);
    	time=time+0.01f;;
    }
    
    private void drawLives()
    {
    	for(int iii = 0;iii<upgradeScreen.lives;iii++)
    	{
    	float[] scratch = new float[16];
    	Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    	Matrix.translateM(scratch, 0, -ratio + 0.15f + iii*0.1f, 0.65f, 0);
    	mShip.draw(scratch, mWidth, mHeight);
    	}
    }

    @Override
    public void onDrawFrame(GL10 unused) {   
    	 counter++;
    	 
    	 if(upgradeLoop && loaded && !menuLoop && !gameLoop){
    		 GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    		 Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    		 Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    		 upgradeScreen.draw();
    		 

    	 }
    	 else if(gameLoop && loaded){
    		
    		if(reloadGameText){
   			 reloadGameText=false;
   			mTextManager.tex.SetupTextureMap(0);
   			mTextManager.clearText();
   			mTextManager.addText("Score: "+Text.getScore(), new float[]{ratio/4,0.9f},new float[]{1,1,1,1});
   			mTextManager.addText("Fire", new float[]{-9*ratio/10,-0.5f}, new float[]{1,1,1,1});
   			mTextManager.addText("Hyper", new float[]{-9*ratio/10,-0.0f}, new float[]{1,1,1,1});
   			mTextManager.addText("Thrust", new float[]{ratio/2,-0.5f}, new float[]{1,1,1,1});
   			mTextManager.addText("Money: " + MONEY, new float[]{-9*ratio/10,0.9f},new float[]{1,1,1,1});
   			 bullet[0].updateTexture();	 
   		 }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        
        drawBackground();
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Ast.resetAsteroids1(astLarge, astMed, astSmall);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        if(labels<0){
        	mTextManager.updateText(" ", 1);
        	mTextManager.updateText(" ", 2);
        	mTextManager.updateText(" ", 3);
        }
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Ship.drawShip(mMVPMatrix, mShip, mTrail, mWidth, mHeight);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Ast.drawAsteroids(mMVPMatrix, astLarge, astMed, astSmall);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Bullet.drawBullets(mMVPMatrix, bullet);
        drawButtons();
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        ParticleSystem.drawExplosions(mMVPMatrix,explosion);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mMVPMatrix, 0, mCoin.mX, mCoin.mY, 0);
        mCoin.setupCoin();
        mCoin.draw(mMVPMatrix);
        mTextManager.updateText("Score: "+Text.getScore(), 0);
        mTextManager.updateText("Money: " + MONEY, 4);
        mTextManager.draw();
        drawLives();
        }
    	 else if(menuLoop){
    		
    		if(reloadMenu){
    			reloadMenu=false;
    			mTextManager.tex.SetupTextureMap(0);
       			mTextManager.clearText();
       			mTextManager.addText("Highscore: " + highscore, new float[]{-3*ratio/4,0},new float[]{1,1,1,1});
    			bg.SetupImage();
    		}
    		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix,0, mViewMatrix, 0);
    		
    		
    		if(loaded){
    			bg.draw(mMVPMatrix);
    			mTextManager.updateText("Highscore: " + highscore, 0);
    			mTextManager.draw();
    		}
    		
    		if(!loaded){
    			if(counter>-1){
    			loadGame(counter);
    			float[] scratch = new float[16];
    	        Matrix.scaleM(scratch, 0, mMVPMatrix, 0, (float)(counter)/5, 0.2f, 1);
    	        float color[] = { 1.0f-(float)(counter)/5, (float)(counter)/5, 0.0f, 0.0f };
    			loader.draw(scratch,color,mWidth,mHeight);
    			reloadMenu=true;
     	        if(counter>4)loaded=true;
    		}}
    	}
    	
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    	riGraphicTools.doShaders(mContext);//load all the shaders
        
    	GLES20.glViewport(0, 0, width, height);//set the viewport to the whole screen
        //save variables of screen dimensions
        mWidth=width;
        mHeight=height;
        MyGLRenderer.ratio = (float) width / height;
        
        
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //
		GLES20.glEnable(GL10.GL_BLEND);
	    GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    
   
    
    
    
    

}