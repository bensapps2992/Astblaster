package com.bensapps.android.astblaster;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class Text {

	public static float vertices[];
	public static short indices[];
	public static float uvs[];
	public FloatBuffer vertexBuffer;
	public ShortBuffer drawListBuffer;
	public FloatBuffer uvBuffer;
	private String text = new String("Hello World");
	private int texno;
	
	//statics
	private static int score;
	private static boolean updateScore;
	
	Text(String message, int textureno){
		text=message;
		texno=textureno;
		SetupText();
		SetupImage();
	}
	
	//score related stuff
	public static void setScore(int mScore){
    	score=mScore;
    }
    
    public static int getScore(){
    	return score;
    }
	
    public static void drawScore(float[] mMVPMatrix,Text mScore, float ratio){
    	Matrix.translateM(mMVPMatrix, 0, ratio/2, +0.8f, 0);
        if(updateScore){
        	updateScore=false;
        	mScore.updateText("Score: " + Text.getScore());
        }
        mScore.draw(mMVPMatrix);
    }
	
    public static void updateScore(){
    	updateScore=true;
    }
    //end of score stuff
    //start of labels related stuff
    public static void drawLabels(float[] mMVPMatrix, float ratio, Text fireText, Text thrustText){
    	float[] mMVPMatrixTrans = new float[16];
    	Matrix.translateM(mMVPMatrixTrans, 0 , mMVPMatrix, 0, -9*ratio/16, -0.57f, 0);
    	fireText.draw(mMVPMatrixTrans);
    	Matrix.translateM(mMVPMatrixTrans, 0, mMVPMatrix, 0, 14*ratio/16, -0.57f, 0);
    	thrustText.draw(mMVPMatrixTrans);
    }
	//end of labels
	
	public void SetupText()
	{
		// We have to create the vertices of our triangle.
		vertices = new float[]
		           {-0.5f,0.2f, 0f,
					-0.5f, -0.2f, 0f,
					0.5f, -0.2f, 0f,
					0.5f, 0.2f, 0f,
		           };
		
		indices = new short[] {0, 1, 2, 0, 2, 3}; // The order of vertex rendering.

		// The vertex buffer.
		ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(indices);
		drawListBuffer.position(0);
		
		
	}
	
	//updates the text in the string then renders to the correct texture;
	public void updateText(String message){
		text=message;
		SetupImage();
	}
	
	public void SetupImage()
	{
		// Create our UV coordinates.
		uvs = new float[] {
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,			
				1.0f, 0.0f			
	    };
		
		// The texture buffer
		ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		uvBuffer = bb.asFloatBuffer();
		uvBuffer.put(uvs);
		uvBuffer.position(0);
		
		// Generate Textures, if more needed, alter these numbers.
		int[] texturenames = new int[3];
		GLES20.glGenTextures(3, texturenames, 0);
		

		Bitmap bmp=Bitmap.createBitmap(256,100,Bitmap.Config.ARGB_4444);
		Canvas canvas=new Canvas(bmp);
		bmp.eraseColor(Color.TRANSPARENT);
		
		Paint textPaint = new Paint();
		textPaint.setTextSize(32);
		textPaint.setAntiAlias(true);
		
		textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
		canvas.drawText(text,16,50,textPaint);
		
		// Bind texture to texturename
		//make sure we are writing to the correct texno
		if(texno==0)GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		if(texno==1)GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		if(texno==2)GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[texno]);
		
		// Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        
        // We are done using the bitmap so we should recycle it.
		bmp.recycle();
	}
	
public void draw(float[] m) {
		GLES20.glUseProgram(riGraphicTools.sp_Image);
        // get handle to vertex shader's vPosition member
	    int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");
	    
	    // Enable generic vertex attribute array
	    GLES20.glEnableVertexAttribArray(mPositionHandle);

	    // Prepare the triangle coordinate data
	    GLES20.glVertexAttribPointer(mPositionHandle, 3,
	                                 GLES20.GL_FLOAT, false,
	                                 0, vertexBuffer);
	    
	    // Get handle to texture coordinates location
	    int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord" );
	    
	    // Enable generic vertex attribute array
	    GLES20.glEnableVertexAttribArray ( mTexCoordLoc );
	    
	    // Prepare the texturecoordinates
	    GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false, 
                0, uvBuffer);
	    
	    // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        
        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation (riGraphicTools.sp_Image, "s_texture" );
        
        // Set the sampler texture unit, where we have saved the texture.
        GLES20.glUniform1i ( mSamplerLoc, texno);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);  	
	}
}
