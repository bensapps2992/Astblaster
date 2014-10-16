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

public class Ship {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private float mAngle;
    private float mX;
    private float mY;
    private float mVx;
    private float mVy;
    private float mAx;
    private float mAy;
    public int histLength=100;
    public static float MAX_SPEED=0.015f;
    public static int LIGHT_STRENGTH=1;
    public static float Light_Max_Dist = 100f;
    public static float Light_Half_Angle = 180f;
    
    public static void increaseLightStrength(){
    	if(Light_Half_Angle<=160){
    		if(MyGLRenderer.MONEY>100*LIGHT_STRENGTH){
    	LIGHT_STRENGTH ++;
    	Light_Max_Dist += 0.06f;
    	Light_Half_Angle +=20;
    	MyGLRenderer.MONEY = MyGLRenderer.MONEY-100*LIGHT_STRENGTH;
    	}
    	}
    }
   
    public static float ACC = 0.0015f;
    private float[] coordHist=new float[3*histLength];

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float shipCoords[] = {
            // in counterclockwise order:
            0.0f,  0.1f, 0.0f,   // top
           0.05f, -0.05f, 0.0f,   // bottom left
           0.0f, 0.0f, 0.0f,    // bottom right
           -0.05f, -0.05f, 0.0f    // bottom right
    };
    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    //private final int vertexCount = shipCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 1.0f, 1.0f, 1.0f, 0.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    
    public static void drawShip(float[] mMVPMatrix, Ship mShip, Line mTrail, float width, float height){
    	float[] scratch = new float[16];
    	float[] mRotationMatrix = new float[16];
    	float[] mMVPMatrixTrans = new float[16];
    	Matrix.translateM(mMVPMatrixTrans, 0, mMVPMatrix, 0, mShip.getX(), mShip.getY(), 0);
        Matrix.setRotateM(mRotationMatrix, 0, mShip.getAngle()-90, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrixTrans, 0, mRotationMatrix, 0);
        mShip.draw(scratch, width, height);
        
        short drawOrder[]=new short[mShip.histLength];
        for(int iii=0;iii<mShip.histLength;iii++){drawOrder[iii]=(short) iii;}
        Random r = new Random();
        float color[] = { 0.5f, 0,  0.5f, 1.0f };
        mTrail.draw(mMVPMatrix,mShip.getHist() ,drawOrder ,color );
    }
    
    public void saveCoords(float x, float y){
    	//we have all the coords in coordHist
    	for(int iii=3*histLength-4;iii>-1;iii--){
    		coordHist[iii+3]=coordHist[iii];
    	}
    	coordHist[0]=x;
    	coordHist[1]=y;
    	coordHist[2]=0;
    }
    
    public float[] getHist(){
    	return coordHist;
    }
    
    public void clearHist(){
    	for(int iii=0;iii<histLength;iii++){
    		coordHist[3*iii]=mX;
    		coordHist[3*iii+1]=mY;
    		coordHist[3*iii+2]=0;
    	}
    }
    
    
    public void setAngle(float angle) {
        mAngle = angle;
    }
    
    public float getAngle() {
        return mAngle;
    }
    public float getVx()
    {
    	return mVx;
    }
    public float getVy()
    {
    	return mVy;
    }
    public float getAx()
    {
    	return mAx;
    }
    public float getAy()
    {
    	return mAy;
    }
    
    public float getX()
    {
    	return mX;
    }
   
    public float getY()
    {
    	return mY;
    }
    public void setXY(float x, float y){
    	mX=x;
    	mY=y;
    }
    public void setVxy(float Vx, float Vy){
    	mVx=Vx;
    	mVy=Vy;
    }
    public void setAxy(float Ax, float Ay){
    	mAx=Ax;
    	mAy=Ay;
    }
    public Ship() {
    	for(int iii=0;iii<30;iii++){
    		coordHist[iii]=0;
    	}
    	mAngle=0;
    	mX=0;
    	mY=0;
    	mVx=0;
    	mVy=0;
    	mAx=0;
    	mAy=0;
        // Initialise vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                shipCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(shipCoords);
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

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix, float width, float height) {
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
        
        int mShipHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "ship");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        float[] ship = {mX,mY};
        GLES20.glUniform2fv(mShipHandle, 1, ship,0);
        
        int mAngleHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "angle");
        GLES20.glUniform1f(mAngleHandle,(float)(Math.toRadians(mAngle)));
        
        int mResHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "resolution");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        float[] res = {width,height};
        GLES20.glUniform2fv(mResHandle, 1, res,0);
        
        int mmaxDistHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "maxDist");
        GLES20.glUniform1f(mmaxDistHandle,Ship.Light_Max_Dist);
        
        int mhalfAngleHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "halfAngle");
        GLES20.glUniform1f(mhalfAngleHandle,(float)(Math.toRadians((Ship.Light_Half_Angle))));


        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
