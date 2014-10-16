package com.bensapps.android.astblaster;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class TextManager {

	public textTextureMap tex;
	private TextObject[] mTextObject = new TextObject[5];//declare 5 text boxes
	private int noObjects;
	
	
	TextManager(int t){
		tex = new textTextureMap(t);// upload the texture and bind it in position t
		for(int iii=0;iii<5;iii++){
			mTextObject[iii]=new TextObject();
		}
		noObjects=0;
	}
	
	public void addText(String text, float[] position, float[] color){
		mTextObject[noObjects].updateTextObject(text, position[0], position[1], color);
		noObjects++;
	}
	
	public void updateText(String text, int index){
		mTextObject[index].updateTextObject(text, mTextObject[index].x, mTextObject[index].y, mTextObject[index].color);
	}
	
	public void clearText(){
		noObjects=0;
	}
	
	public void draw(){
		float[] mViewMatrix = MyGLRenderer.getViewMat();//the view matrix
		float[] mProjectionMatrix = MyGLRenderer.getProjMat();//the projection matrix
		float[] scratch = new float[16];
		Matrix.multiplyMM(scratch, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		if(noObjects>0){
		for(int iii=0;iii<noObjects;iii++){
			Matrix.translateM(scratch, 0, mTextObject[iii].x, mTextObject[iii].y, 0);
			mTextObject[iii].draw(scratch);
			Matrix.translateM(scratch, 0, -mTextObject[iii].x, -mTextObject[iii].y, 0);
		}
		}
	}
	
	private class TextObject{
		String text;
		public float x,y;
		private FloatBuffer vertexBuffer;
		private FloatBuffer uvBuffer;
		private float[] vertices;
		private float[] uvs;
		public float[] color;
		
		
		TextObject(){
			
		}
		
		
		public void updateTextObject(String mText, float mX, float mY, float[] mColor){
			//generate the vertices for this text object
			text=mText;
			x=mX;
			y=mY;
			color=mColor;
			//each text is 37.25 tall and 19 wide in the texture
			float length = text.length();
			float height=0.1f;//define a fixed height
			float width=height*19f/37.25f;//calculate the correct width so that we dont distort the texture
			//height and width are for 1 character
			//we need to define our vertex array as 2 triangles for each char to allow texture atlas
			vertices = new float[(int)(length)*3*6];
			for(int iii=0;iii<length;iii++){
				vertices[(iii*18)+0]=-width+iii*2*width;vertices[(iii*18)+1]=height;vertices[(iii*18)+2]=0f;
				vertices[(iii*18)+3]=-width+iii*2*width;vertices[(iii*18)+4]=-height;vertices[(iii*18)+5]=0f;
				vertices[(iii*18)+6]=width+iii*2*width;vertices[(iii*18)+7]=-height;vertices[(iii*18)+8]=0f;
				vertices[(iii*18)+9]=-width+iii*2*width;vertices[(iii*18)+10]=height;vertices[(iii*18)+11]=0f;
				vertices[(iii*18)+12]=width+iii*2*width;vertices[(iii*18)+13]=-height;vertices[(iii*18)+14]=0f;
				vertices[(iii*18)+15]=width+iii*2*width;vertices[(iii*18)+16]=height;vertices[(iii*18)+17]=0f;
			}
			// The vertex buffer.
			
			//generate the uvs for this text object
			uvs = new float[(int)(length)*2*6];
			
			for(int iii=0;iii<length;iii++){//loop through each letter in the string
				float[] coords = getUV(text.charAt(iii));
				uvs[(iii*12)+0]=coords[0];uvs[(iii*12)+1]=coords[2];
				uvs[(iii*12)+2]=coords[0];uvs[(iii*12)+3]=coords[3];
				uvs[(iii*12)+4]=coords[1];uvs[(iii*12)+5]=coords[3];
				uvs[(iii*12)+6]=coords[0];uvs[(iii*12)+7]=coords[2];
				uvs[(iii*12)+8]=coords[1];uvs[(iii*12)+9]=coords[3];
				uvs[(iii*12)+10]=coords[1];uvs[(iii*12)+11]=coords[2];
			}
			ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
			bb.order(ByteOrder.nativeOrder());
			vertexBuffer = bb.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);
			ByteBuffer ubb = ByteBuffer.allocateDirect(uvs.length * 4);
			ubb.order(ByteOrder.nativeOrder());
			uvBuffer = ubb.asFloatBuffer();
			uvBuffer.put(uvs);
			uvBuffer.position(0);
		}
		//needs to return xmin xmax ymin ymax for a certain character
		private float[] getUV(char mChar){
			//need to use the char to return the right location
			int charIndex = mChar-'a';//gives a number: a=0, b=1 etc
			float charHeight = 38.25f/256f;
			float charWidth = 19f/256f;
			float xmin,xmax,ymin,ymax;
			xmin=0;
			xmax=0;
			ymin=0;
			ymax=0;
			//handle small letters
			if(charIndex>-1 && charIndex<13){
				//we are on the first row
				xmin=charIndex*charWidth;
				xmax=(charIndex+1)*charWidth;
				ymin=0;
				ymax=charHeight;
			}
			else if(charIndex>12 && charIndex<26){
				xmin=(charIndex-13)*charWidth;
				xmax=(charIndex-12)*charWidth;
				ymin=charHeight;
				ymax=2*charHeight;
			}
			//handle capital letters
			else if(charIndex<0 || charIndex>25){
				charIndex = mChar-'A';//gives a number: a=0, b=1 etc
				if(charIndex>-1 && charIndex<13){
					//we are on the first row
					xmin=charIndex*charWidth;
					xmax=(charIndex+1)*charWidth;
					ymin=2*charHeight;
					ymax=3*charHeight;
				}
				else if(charIndex>12 && charIndex<26){
					xmin=(charIndex-13)*charWidth;
					xmax=(charIndex-12)*charWidth;
					ymin=3*charHeight;
					ymax=4*charHeight;
				}
			}
			
		//handle the last line
			charIndex = mChar-'0';
			if(charIndex>-1 && charIndex<10){
				
				xmin=(charIndex)*charWidth;
				xmax=(charIndex+1)*charWidth;
				ymin=4*charHeight;
				ymax=5*charHeight;
			}
			if(mChar=='.'){
				xmin=10*charWidth;
				xmax=11*charWidth;
				ymin=4*charHeight;
				ymax=5*charHeight;
			}
			if(mChar=='+'){
				xmin=11*charWidth;
				xmax=12*charWidth;
				ymin=4*charHeight;
				ymax=5*charHeight;
			}
			if(mChar==':'){
				xmin=12*charWidth;
				xmax=13*charWidth;
				ymin=4*charHeight;
				ymax=5*charHeight;
			}
			
			
			float[] test = {xmin,xmax,ymin,ymax};//xmin,xmax,ymin,ymax
			//float[] test = {0,charWidth,0.0f,charHeight};
			return test;
		}
		
		public void draw(float[] m){
			
			
			
			int cp = riGraphicTools.sp_Image_Text;
			GLES20.glUseProgram(cp);
	        // get handle to vertex shader's vPosition member
		    int mPositionHandle = GLES20.glGetAttribLocation(cp, "vPosition");
		    
		    // Enable generic vertex attribute array
		    GLES20.glEnableVertexAttribArray(mPositionHandle);

		    // Prepare the triangle coordinate data
		    GLES20.glVertexAttribPointer(mPositionHandle, 3,
		                                 GLES20.GL_FLOAT, false,
		                                 0, vertexBuffer);
		    
		    // Get handle to texture coordinates location
		    int mTexCoordLoc = GLES20.glGetAttribLocation(cp, "a_texCoord" );
		    
		    // Enable generic vertex attribute array
		    GLES20.glEnableVertexAttribArray ( mTexCoordLoc );
		    
		    // Prepare the texturecoordinates
		    GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
	                false, 
	                0, uvBuffer);
		    
		    // Get handle to shape's transformation matrix
	        int mtrxhandle = GLES20.glGetUniformLocation(cp, "uMVPMatrix");

	        // Apply the projection and view transformation
	        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
	        
	        // Get handle to textures locations
	        int mSamplerLoc = GLES20.glGetUniformLocation (cp, "s_texture" );
	        
	        // Set the sampler texture unit, where we have saved the texture.
	        GLES20.glUniform1i ( mSamplerLoc, 0);
	        
	        int mColorHandle = GLES20.glGetUniformLocation(cp, "mColor");

	        // Set color for drawing the triangle

	       GLES20.glUniform4fv(mColorHandle, 1, color, 0);

	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length/3);

	        // Disable vertex array
	        GLES20.glDisableVertexAttribArray(mPositionHandle);
	        GLES20.glDisableVertexAttribArray(mTexCoordLoc);  	
		}
	}
	
	class textTextureMap{
		private String[] textTexture = new String[5];
		
	textTextureMap(int t){
		textTexture[0]="abcdefghijklm";
		textTexture[1]="nopqrstuvwxyz";
		textTexture[2]="ABCDEFGHIJKLM";
		textTexture[3]="NOPQRSTUVWXYZ";
		textTexture[4]="0123456789.+:";
		SetupTextureMap(t);
	}
	
	public void SetupTextureMap(int t){//load the text map into texture position t
		//each text is 37.25 tall and 19 wide in the texture
		
		// Generate Textures, if more needed, alter these numbers.
		int[] texturenames = new int[1];
		GLES20.glGenTextures(1, texturenames, 0);
		
		Bitmap bmp=Bitmap.createBitmap(256,256,Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bmp);
		bmp.eraseColor(Color.TRANSPARENT);
		
		Paint textPaint = new Paint();
		textPaint.setTextSize(32);
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.MONOSPACE);
		textPaint.setColor(Color.WHITE);
		float size1 = Math.abs(textPaint.ascent());
		float size2 = Math.abs(textPaint.descent());
		for(int iii=0;iii<5;iii++){
			canvas.drawText(textTexture[iii],0,size1+iii*(size2+size1+1),textPaint);
		}
		
		// Bind texture to texturename
		//make sure we are writing to the correct texno
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0+t);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
		
		// Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        
        // We are done using the bitmap so we should recycle it.
		bmp.recycle();
	}
	}
}
