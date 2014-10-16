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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Ast {

    private  FloatBuffer vertexBuffer;
    private  ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private float x,y,vx,vy,angle,active,vangle;
    
    public static final int NO_AST_LARGE=10;
    public static final int NO_AST_MED=20;
    public static final int NO_AST_SMALL=40;
    
    public static final int LARGE=0;
    public static final int MED=1;
    public static final int SMALL=2;
    
    public static final float LARGE_R2=0.023f;//max 0.15 min 0.10
    public static final float MED_R2=0.02f;//max 0.12 min 0,08
    public static final float SMALL_R2=0.007f;//max 0.08 min 0.04
    

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    short drawOrder[]={1,2,3,4,5,1};
    //short drawOrder[] = { 0,1,2, 0,2,3, 0,3,4, 0,4,5,0,5,1 }; // order to draw vertices
    //private final int vertexCount = shipCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    
    Random r=new Random(System.nanoTime());
    float color[] = { r.nextFloat(), r.nextFloat(), r.nextFloat(), 0.0f };
    
    //statics
    private static int resetAsteroids;
    
    public static void resetAsteroids(){
    	resetAsteroids=1;
    }
    
    public static void resetAsteroids1(Ast[] astLarge, Ast[] astMed, Ast[] astSmall){
    	if(resetAsteroids==1){
    		for(int iii=0;iii<Ast.NO_AST_LARGE;iii++){
    			//astLarge[iii].resetAst(Ast.LARGE);
    			//astLarge[iii].setActive(0);
    			resetAsteroids=0;
    		}
    		for(int iii=0;iii<Ast.NO_AST_MED;iii++){
    			//astMed[iii].resetAst(Ast.MED);
    			astMed[iii].setActive(0);
    			resetAsteroids=0;
    		}
    		for(int iii=0;iii<Ast.NO_AST_SMALL;iii++){
    			//astSmall[iii].resetAst(Ast.SMALL);
    			astSmall[iii].setActive(0);
    			resetAsteroids=0;
    		}
    	}
    }
    
    public static void drawAsteroids(float[] mMVPMatrix, Ast[] astLarge, Ast[] astMed, Ast[] astSmall){
    	int iii=0;
    	for(iii=0;iii<Ast.NO_AST_LARGE;iii++){//loop through all the asteroids
    		if(astLarge[iii].getActive()==1){//only draw an asteroids if its active
    			float[] scratch = new float[16];
    			float[] mRotationMatrix = new float[16];
    			float[] mMVPMatrixTran = new float[16];
    			Matrix.translateM(mMVPMatrixTran, 0, mMVPMatrix, 0, astLarge[iii].getX(), astLarge[iii].getY(), 0);
    			Matrix.setRotateM(mRotationMatrix, 0, astLarge[iii].getAngle(), 0f, 0f, 1f);
    			Matrix.multiplyMM(scratch, 0, mMVPMatrixTran, 0, mRotationMatrix, 0);
    			astLarge[iii].draw(scratch);
    		}
    }
    	
    	for(iii=0;iii<Ast.NO_AST_MED;iii++){//loop through all the asteroids
    		if(astMed[iii].getActive()==1){//only draw an asteroids if its active
    	float[] scratch = new float[16];
    	float[] mRotationMatrix = new float[16];
    	float[] mMVPMatrixTran = new float[16];
		Matrix.translateM(mMVPMatrixTran, 0, mMVPMatrix, 0, astMed[iii].getX(), astMed[iii].getY(), 0);
    	Matrix.setRotateM(mRotationMatrix, 0, astMed[iii].getAngle(), 0f, 0f, 1f);
    	Matrix.multiplyMM(scratch, 0, mMVPMatrixTran, 0, mRotationMatrix, 0);
    	astMed[iii].draw(scratch);
    		}
    }
    	
    	for(iii=0;iii<Ast.NO_AST_SMALL;iii++){//loop through all the asteroids
    		if(astSmall[iii].getActive()==1){//only draw an asteroids if its active
    	float[] scratch = new float[16];
    	float[] mRotationMatrix = new float[16];
    	float[] mMVPMatrixTran = new float[16];
		Matrix.translateM(mMVPMatrixTran, 0, mMVPMatrix, 0, astSmall[iii].getX(), astSmall[iii].getY(), 0);
    	Matrix.setRotateM(mRotationMatrix, 0, astSmall[iii].getAngle(), 0f, 0f, 1f);
    	Matrix.multiplyMM(scratch, 0, mMVPMatrixTran, 0, mRotationMatrix, 0);
    	astSmall[iii].draw(scratch);
    		}
    }
    }
    
    //getters and setters
    public float getX(){
    	return x;
    }
    public float getY(){
    	return y;
    }
    public void setXY(float mX,float mY){
    	x=mX;
    	y=mY;
    }
    public float getVX(){
    	return vx;
    }
    public float getVY(){
    	return vy;
    }
    public void setVXY(float mVX,float mVY){
    	vx=mVX;
    	vy=mVY;
    }
    public float getAngle(){
    	return angle;
    }
    public void setAngle(float mAngle){
    	angle=mAngle;
    }
    
    public void setActive(float mActive){
    	active=mActive;
    }
    public float getActive(){
    	return active;
    }
    public float getVangle(){
    	return vangle;
    }
    public void setVangle(float mVangle){
    	vangle=mVangle;
    }
    
    public static float[] getCoords(Ast ast){
    	float mAngle=ast.getAngle();//the current angle of the asteroid
		float mX1=ast.getX();
		float mY1=ast.getY();
		float poly[]={
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f
		};
		float result[]={
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f
		};    	
    	float result1[]={
				0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f
		};
    	for(int kkk=0;kkk<5;kkk++){
			poly[4*kkk]=ast.astCoords[3*(kkk+1)];//fill poly with the asteroid coordinates
			poly[4*kkk+1]=ast.astCoords[3*(kkk+1)+1];
			poly[4*kkk+2]=0.0f;//we are in 2d
			poly[4*kkk+3]=1f;
		}
    	float[] mTranslate = new float[16];//a matrix to help with translations
		float[] mRotate = new float[16];
		float[] m=new float[16];
		Matrix.setIdentityM(mTranslate, 0);
		Matrix.setIdentityM(mRotate, 0);
		Matrix.setIdentityM(m, 0);
		Matrix.setRotateM(mRotate, 0, mAngle, 0, 0, 1);
		Matrix.translateM(mTranslate, 0, mX1, mY1, 0);
		Matrix.multiplyMM(m, 0, mTranslate, 0, mRotate, 0);
		for(int kkk=0;kkk<5;kkk++){
			Matrix.multiplyMV(result, kkk*4, m, 0, poly, kkk*4);
		}
		for(int kkk=0;kkk<3;kkk++){
			result1[kkk]=result[kkk];
			result1[kkk+3]=result[kkk+4];
			result1[kkk+6]=result[kkk+8];
			result1[kkk+9]=result[kkk+12];
			result1[kkk+12]=result[kkk+16];
		}
    	return result1;
    }
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public float astCoords[] = {
            // in counterclockwise order:
            0.0f,  0.0f, 0.0f,//centre
            0.3f, 0.04f, 0.0f,
            0.0f, 0.1f, 0.0f,
            -0.3f, 0.04f, 0.0f,
            -0.3f, -0.04f, 0.0f,
            0.3f, -0.04f, 0.0f,
    	}; 
    public Ast(int size) {
    	x=0;y=0;vx=0;vy=0;angle=0;active=0;vangle=0;//set everything up not active
    	
    	resetAsteroids=0;
    	
    	//first set up the vertex coordinates
    	//consider 5 points spaced equally around a circle with radius random between 0.4 and 0.7
    	float RADMIN=0.05f;
    	float RADMAX=0.15f;
    	int iii=0;
    	
    	if(size==LARGE)
    	{
    		RADMIN=0.10f;
    		RADMAX=0.15f;
    	}
    	
    	else if(size==MED)
    	{
    		RADMIN=0.08f;
    		RADMAX=0.12f;
    	}
    	
    	else if(size==SMALL)
    	{
    		RADMIN=0.04f;
    		RADMAX=0.08f;
    	}
    	
    	float anglestep=(float)(Math.PI*0.4);
    	for(iii=1;iii<6;iii++){
    		Random r = new Random(System.nanoTime());
    		float rad=RADMIN+r.nextFloat()*(RADMAX-RADMIN);
    		float rand=r.nextFloat()*0.2f;
    		astCoords[3*iii]=rad*(float)(Math.sin((iii-1)*(anglestep-0.1f+rand)));// x-coordinates
    		astCoords[(3*iii)+1]=rad*(float)(Math.cos((iii-1)*(anglestep-0.1f+rand)));//y-coordinates
    	}
    	
    	
    	
    	
        // Initialise vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                astCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(astCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        // Initialise byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

       
    }
    
    public void resetAst(int size){
    	
    	//first set up the vertex coordinates
    	//consider 5 points spaced equally around a circle with radius random between 0.4 and 0.7
    	float RADMIN=0.05f;
    	float RADMAX=0.15f;
    	int iii=0;
    	
    	if(size==LARGE)
    	{
    		RADMIN=0.10f;
    		RADMAX=0.15f;
    	}
    	
    	else if(size==MED)
    	{
    		RADMIN=0.08f;
    		RADMAX=0.12f;
    	}
    	
    	else if(size==SMALL)
    	{
    		RADMIN=0.04f;
    		RADMAX=0.08f;
    	}
    	
    	
    	float anglestep=(float)(Math.PI*0.4);
    	for(iii=1;iii<6;iii++){
    		Random r = new Random(System.nanoTime());
    		float rad=RADMIN+r.nextFloat()*(RADMAX-RADMIN);
    		float rand=r.nextFloat()*0.2f;
    		astCoords[3*iii]=rad*(float)(Math.sin((iii-1)*(anglestep-0.1f+rand)));// x-coordinates
    		astCoords[(3*iii)+1]=rad*(float)(Math.cos((iii-1)*(anglestep-0.1f+rand)));//y-coordinates
    	}
        ByteBuffer bb = ByteBuffer.allocateDirect(astCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(astCoords);
        vertexBuffer.position(0);
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(riGraphicTools.sp_SolidColor);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_SolidColor, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "vColor");

        // Set color for drawing the triangle
        
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glLineWidth(4);
        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_LINE_LOOP/*
                GLES20.GL_TRIANGLES*/, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
