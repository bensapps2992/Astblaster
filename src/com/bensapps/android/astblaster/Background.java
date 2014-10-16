package com.bensapps.android.astblaster;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Background {
	 private final FloatBuffer vertexBuffer;
	    private final ShortBuffer drawListBuffer;
	    private float ratio;

	    // number of coordinates per vertex in this array
	    static final int COORDS_PER_VERTEX = 3;
	   

	    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

	    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	    float color1[] = { 1f,1f, 1f, 1.0f };
	    float color2[]={0,0,0,0};

	    /**
	     * Sets up the drawing object data for use in an OpenGL ES context.
	     */
	    public Background(float ratio1) {
	    	ratio=ratio1;
	    	float BackgroundCoords[] = {
		            -ratio, 1f, 0.0f,   // top left
		            -ratio, -1f, 0.0f,   // bottom left
		            ratio, -1, 0.0f,   // bottom right
		            ratio, 1f, 0.0f }; // top right
	        // initialize vertex byte buffer for shape coordinates
	        ByteBuffer bb = ByteBuffer.allocateDirect(BackgroundCoords.length * 4);
	        bb.order(ByteOrder.nativeOrder());
	        vertexBuffer = bb.asFloatBuffer();
	        vertexBuffer.put(BackgroundCoords);
	        vertexBuffer.position(0);

	        // initialize byte buffer for the draw list
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
	    public void draw(float[] mvpMatrix, float width, float height, float time, Ship mShip/*float shipx, float shipy, float angle*/) {
	        // Add program to OpenGL environment
	        GLES20.glUseProgram(riGraphicTools.sp_bg);

	        // get handle to vertex shader's vPosition member
	        int mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_bg, "vPosition");

	        // Enable a handle to the triangle vertices
	        GLES20.glEnableVertexAttribArray(mPositionHandle);

	        // Prepare the triangle coordinate data
	        GLES20.glVertexAttribPointer(
	                mPositionHandle, COORDS_PER_VERTEX,
	                GLES20.GL_FLOAT, false,
	                vertexStride, vertexBuffer);
	        
	        int mResHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "resolution");
	        MyGLRenderer.checkGlError("glGetUniformLocation");
	        float[] res = {width,height};
	        GLES20.glUniform2fv(mResHandle, 1, res,0);
	        
	        int mShipHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "ship");
	        MyGLRenderer.checkGlError("glGetUniformLocation");
	        float[] ship = {mShip.getX(),mShip.getY()};
	        GLES20.glUniform2fv(mShipHandle, 1, ship,0);
	        
	        int mTimeHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "time");
	        MyGLRenderer.checkGlError("glGetUniformLocation");
	        GLES20.glUniform1f(mTimeHandle, time);
	        
	        int mRatioHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "ratio");
	        MyGLRenderer.checkGlError("glGetUniformLocation");
	        GLES20.glUniform1f(mRatioHandle, ratio);
	        
	        int mAngleHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "angle");
	        GLES20.glUniform1f(mAngleHandle,(float)(Math.toRadians(mShip.getAngle())));
	        
	        int mmaxDistHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "maxDist");
	        GLES20.glUniform1f(mmaxDistHandle,Ship.Light_Max_Dist);
	        
	        int mhalfAngleHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "halfAngle");
	        GLES20.glUniform1f(mhalfAngleHandle,(float)(Math.toRadians((Ship.Light_Half_Angle))));

	        // get handle to shape's transformation matrix
	        int mMVPMatrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_bg, "uMVPMatrix");
	        MyGLRenderer.checkGlError("glGetUniformLocation");

	        // Apply the projection and view transformation
	        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
	        MyGLRenderer.checkGlError("glUniformMatrix4fv");

	        // Draw the fire
	        GLES20.glDrawElements(
	                GLES20.GL_TRIANGLES, drawOrder.length,
	                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

	        // Disable vertex array
	        GLES20.glDisableVertexAttribArray(mPositionHandle);
	    }
}
