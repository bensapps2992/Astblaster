package com.bensapps.android.astblaster;

import java.util.Random;

import android.opengl.Matrix;

public class ParticleSystem {
	
	//define variables for the emitters
	private int NO_PART = 20;
	private float  x,y;
	private Particle[] particles;
	private boolean active;
	public float radius;
	private float[][] color=new float[NO_PART][4];
	
	public static void drawExplosions(float[] mMVPMatrix, ParticleSystem[] explosion){
    	for(int iii=0;iii<20;iii++){
    		explosion[iii].draw(mMVPMatrix, explosion[iii].radius);
    	}
    }
	
	public ParticleSystem(float xstart, float ystart){
		particles=new Particle[1];//init 1 particles
		for(int iii=0;iii<1;iii++){
			particles[iii]=new Particle();
		}
		Random r = new Random(System.nanoTime());
		for(int iii=0;iii<NO_PART;iii++){
			color[iii][0]=r.nextFloat();
			color[iii][1]=r.nextFloat();
			color[iii][2]=r.nextFloat();
			color[iii][3]=0f;
		}
		x=xstart;
		y=ystart;
		active=false;
		radius=0.5f;
	}
	
	public boolean getActive(){
		return active;
	}
	
	public void setActive(boolean mActive){
		active=mActive;
	}
	
	public void setXY(float mx, float my){
		x=mx;
		y=my;
	}
	
	public void draw(float[] mvpMatrix,float radius1){
		//mvpMatrix stores the centre of the system... lets draw a circle at radius 0.1 around it
		for(int iii=0;iii<NO_PART;iii++){
			if(active){
			float[] scratch2 = new float[16];
			float[] scratch3 = new float[16];
			float theta=iii*2*(float)(Math.PI)/(float)(NO_PART);
			float x1=x+radius1*(float)(Math.cos(theta));
			float y1=y+radius1*(float)(Math.sin(theta));
			Matrix.setIdentityM(scratch2, 0);
			Matrix.translateM(scratch2, 0, x1, y1, 0);
			Matrix.multiplyMM(scratch3, 0, mvpMatrix, 0, scratch2, 0);
			particles[0].draw(scratch3, color[iii]);
		}
		
	}}
	

}
