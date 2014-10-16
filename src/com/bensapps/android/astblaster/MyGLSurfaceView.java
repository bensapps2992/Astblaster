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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

/*
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {
	
	
	//private MyGLSurfaceView test1;
	public MyGLRenderer mRenderer;
    private int pressed=0;
    private int shoot=0;
    private int hyper=0;//set to 1 to do a hyperspace jump
    
    private int directionPointer=-1;//variables to save the pointer number of the 2 fingers
    private int firePointer=-1;
    private int hyperPointer=-1;
    private int menuPointer=-1;
    
    Context mcontext;
    
   
    //-1=no touch
    //0=pointer id 1
    //1=pointer id 2
    
    public void setPressed(int x){//are we accelerating
    	pressed=x;
    }
    
    public int getPressed(){
    	return pressed;
    }
    public void setShoot(int x){//are we shooting
    	shoot=x;
    }
    
    public int getShoot(){
    	return shoot;
    }
    
    public int getHyper(){
    	return hyper;
    }
    
    public void setHyper(int test){
    	hyper=test;
    }


    public MyGLSurfaceView(Context context) {
        super(context);
        mcontext = context;
        pressed=0;
        shoot=0;
        hyper=0;

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        //float ratio = getWidth()/getHeight();
        mRenderer = new MyGLRenderer(context, 2f);
        //mRenderer.ratio=getWidth()/getHeight();
        setRenderer(mRenderer);
        

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    
    
    

    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	if(mRenderer.getLoop()==MyGLRenderer.UPGRADELOOP){//we are in the upgrade loop
    		if(e.getActionMasked()==MotionEvent.ACTION_DOWN || e.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN){
            	//a finger has landed on the screen
            int actionindex= e.getActionIndex();
            float width = getWidth();
            float height = getHeight();
    		if(e.getX(actionindex)<3*width/4 && e.getX(actionindex)>width/4 && e.getY(actionindex)<height/4 && e.getY(actionindex)>0){
            	mRenderer.setLoop(MyGLRenderer.MENULOOP);
            }
    		if(e.getX(actionindex)>3*width/4 && e.getY(actionindex)>3*height/4){
    			mRenderer.setLoop(MyGLRenderer.GAMELOOP);
    		}
    		if(e.getX(actionindex)<width/2 && e.getY(actionindex)>7*height/20 && e.getY(actionindex)<9*height/20){
    			Bullet.incBullets(mcontext);
    		}
    		if(e.getX(actionindex)<width/2 && e.getY(actionindex)>11*height/20 && e.getY(actionindex)<13*height/20){
    			if(MyGLRenderer.MONEY>1499)
    				{
    				mRenderer.upgradeScreen.lives++;
    				MyGLRenderer.MONEY = MyGLRenderer.MONEY-1500;
    				}
    			else
    			{
    				CharSequence text = "Cost: 1500";
    				int duration = Toast.LENGTH_SHORT;
    				Toast toast = Toast.makeText(mcontext, text, duration);
    				toast.show();
    				
    			}
    		}
    		
    	}
    	}
    	
    	
    	else if(mRenderer.getLoop()==MyGLRenderer.MENULOOP){//we are in the menu
    		if(e.getActionMasked()==MotionEvent.ACTION_DOWN){
    			if(mRenderer.loaded){
    				mRenderer.setLoop(MyGLRenderer.GAMELOOP);
    			}
    		}
    	}
    	
    	
    	else if(mRenderer.getLoop()==MyGLRenderer.GAMELOOP){//we are in the game
    	
    	float width = getWidth();
        float height = getHeight();
        
        if(e.getActionMasked()==MotionEvent.ACTION_DOWN||e.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN){
        	//a finger has landed on the screen
        int actionindex= e.getActionIndex();
        
        float x=e.getX(actionindex)-7*width/8;
        float y=e.getY(actionindex)-7*height/8;
        if(e.getX(actionindex)<width && e.getX(actionindex)>width/2 && e.getY(actionindex)<height && e.getY(actionindex)>height/2){
        	directionPointer=e.getPointerId(actionindex);
        	float newangle = (float) Math.atan(y/(-x));
        	newangle=(float) (newangle*180.0);
            newangle=(float) (newangle/Math.PI);
            if(x<0)newangle+=180;
            mRenderer.mShip.setAngle(newangle);
            float distance=(float)Math.sqrt((double)(x*x+y*y));
            if(distance>height/12)pressed=1;
            if(distance<height/12)pressed=0;
            if(x>width/8)x=width/8;
            if(x<-width/8)x=-width/8;
            if(y>height/8)y=height/8;
            if(y<-height/8)y=-height/8;
            float x1=x*mRenderer.ratio*2/width;
            float y1=-y*2/height;
            mRenderer.mDirection.setXY(x1,y1);
            
        }
        if(e.getX(actionindex)<width/4 && e.getX(actionindex)>0 && e.getY(actionindex)<height && e.getY(actionindex)>3*height/4){
        	firePointer=e.getPointerId(actionindex);
        	//what happens when we press the button
        	shoot=1;
        }
        if(e.getX(actionindex)<width/4 && e.getX(actionindex)>0 && e.getY(actionindex)<3*height/4 && e.getY(actionindex)>2*height/4){
        	hyperPointer=e.getPointerId(actionindex);
        	hyper=1;
        }
        if(e.getX(actionindex)<3*width/4 && e.getX(actionindex)>width/4 && e.getY(actionindex)<height/4 && e.getY(actionindex)>0){
        	menuPointer=e.getPointerId(actionindex);
        	mRenderer.setLoop(MyGLRenderer.MENULOOP);
        }
        }
        
        if(e.getActionMasked()==MotionEvent.ACTION_UP||e.getActionMasked()==MotionEvent.ACTION_POINTER_UP){
        	//if the pointer is lifted
        	int actionindex=e.getActionIndex();
        	int pointerid=e.getPointerId(actionindex);
        	if(pointerid==firePointer && firePointer!=-1 && pressed==1){
        		firePointer=-1;
        	}
        	if(pointerid==directionPointer){
        		directionPointer=-1;
        		pressed=0;
        	}
        	if(pointerid==hyperPointer){
        		hyperPointer=-1;
        		hyper=0;
        	}
        	if(pointerid==menuPointer){
        		menuPointer=-1;
        		
        	}
        }
        
        if(e.getActionMasked()==MotionEvent.ACTION_MOVE){
        	//the pointer has moved
        	int pointerCount=e.getPointerCount();
        	
        	for(int i=0;i<pointerCount;i++){
        		int pointerid=e.getPointerId(i);
        		if(pointerid==directionPointer){
            		float x=e.getX(i)-7*width/8;
                    float y=e.getY(i)-7*height/8;
            		float newangle = (float) Math.atan(y/(-x));
                	newangle=(float) (newangle*180.0);
                    newangle=(float) (newangle/Math.PI);
                    if(x<0)newangle+=180;
                    mRenderer.mShip.setAngle(newangle);
                    float distance=(float)Math.sqrt((double)(x*x+y*y));
                    if(distance>height/12)pressed=1;
                    if(distance<height/12)pressed=0;
                    if(x>width/8)x=width/8;
                    if(x<-width/8)x=-width/8;
                    if(y>height/8)y=height/8;
                    if(y<-height/8)y=-height/8;
                    float x1=x*mRenderer.ratio*2/width;
                    float y1=-y*2/height;
                    mRenderer.mDirection.setXY(x1,y1);
                    //Log.w("test","direction: " + Float.toString(distance));
            	}	
        	}
        }}
        return true;
    }
}
    
