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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.widget.Toast;

public class Bullet {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    public static float uvs[];
    public FloatBuffer uvBuffer;//stuff for the texture;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private float mAngle;
    private float mX,mY,mVx,mVy,mActive;
    private float mLife;//give the bullet a max time on screen
    
    Context mContext;
    
    public static int MAX_BULLETS=1;
    
    public static void incBullets(Context mContext){
    	if(Bullet.MAX_BULLETS<15){
    		if(MyGLRenderer.MONEY>(100*MAX_BULLETS-1)){
    	Bullet.MAX_BULLETS=Bullet.MAX_BULLETS+1;
    	MyGLRenderer.MONEY = MyGLRenderer.MONEY-100*MAX_BULLETS;
    		}
    		else
    		{
    			CharSequence text = "Cost: " + Integer.toString(100*MAX_BULLETS);
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(mContext, text, duration);
				toast.show();
    		}
    	}
    	else
    	{
    		CharSequence text = "Too many bullets";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(mContext, text, duration);
			toast.show();
    	}
    }
    public static final float RADIUS=0.0112f;
    public static final float ANGLE=26;
    public static final float SPEED=0.04f;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float bulletCoords[] = {
            // in counterclockwise order:
            -0.01f,  0.02f, 0.0f,   // top right
           -0.01f, -0.02f, 0.0f,   // bottom right
           0.01f, -0.02f, 0.0f,    // top left
           0.01f, 0.02f, 0.0f    // bottom left
    };
    private final short drawOrder[] = { 0, 1, 2, 0, 2,3 }; // order to draw vertices
    //private final int vertexCount = shipCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 1.0f, 1.0f, 1.0f, 0.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    
    public static void drawBullets(float[] mMVPMatrix, Bullet[] bullet){
    	int iii=0;
    	float[] mRotationMatrix = new float[16];
    	for(iii=0;iii<Bullet.MAX_BULLETS;iii++){
    		if(bullet[iii].getActive()==1){
    	    	float[] scratch = new float[16];
    	    	float[] mMVPMatrixTrans = new float[16];
    	    	Matrix.translateM(mMVPMatrixTrans,0,mMVPMatrix, 0, bullet[iii].getX(), bullet[iii].getY(), 0);
    	    	Matrix.setRotateM(mRotationMatrix, 0, bullet[iii].getAngle(), 0f, 0f, 1f);
    	    	Matrix.multiplyMM(scratch, 0, mMVPMatrixTrans, 0, mRotationMatrix, 0);
    	    	
    	    	bullet[iii].draw(scratch);
    		}
    	}
    }
    
    public void setLife(float life){
    	mLife=life;
    }
    public float getLife(){
    	return mLife;
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
    public void setActive(float active){
    	mActive=active;
    }
    public float getActive(){
    	return mActive;
    }
    
    public void updateTexture(){
    	int id = mContext.getResources().getIdentifier("drawable/bullet", null, mContext.getPackageName());
    	Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
    	
    	int[] texturenames = new int[1];
		GLES20.glGenTextures(1, texturenames, 0);//we need 1 texture for the bullets
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);//the rest are used for text;
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
	    bmp.recycle();
    }
    
   
    public Bullet(Context context) {
    	mContext = context;
    	mAngle=0;mX=0;mY=0;mVx=0;mVy=0;mActive=0;//set it inactive to start with
        // Initialise vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(bulletCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(bulletCoords);
        vertexBuffer.position(0);
        
        // Initialise byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        
     // Create our UV coordinates.
     	uvs = new float[] {
     				0.0f, 0.0f,
     				0.0f, -1.0f,
     				1.0f, -1.0f,			
     				1.0f, 0.0f			
     	};
     		
     	ByteBuffer ubb = ByteBuffer.allocateDirect(uvs.length * 4);
    	ubb.order(ByteOrder.nativeOrder());
    	uvBuffer = ubb.asFloatBuffer();
    	uvBuffer.put(uvs);
    	uvBuffer.position(0);
    	
    	
		int id = mContext.getResources().getIdentifier("drawable/bullet", null, mContext.getPackageName());
    	Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
    	
    	int[] texturenames = new int[1];
		GLES20.glGenTextures(1, texturenames, 0);//we need 1 texture for the bullets
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);//the rest are used for text;
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
	    bmp.recycle();
    	
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
    	int currentProgram = riGraphicTools.sp_Image;
        GLES20.glUseProgram(currentProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(currentProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        
     // Get handle to texture coordinates location
	    int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord" );
	    
	    // Enable generic vertex attribute array
	    GLES20.glEnableVertexAttribArray ( mTexCoordLoc );
	    
	    // Prepare the texturecoordinates
	    GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false, 
                0, uvBuffer);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(currentProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
        
     // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation (riGraphicTools.sp_Image, "s_texture" );
        
        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i ( mSamplerLoc, 3);

        // Draw the triangle
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

}
