package com.bensapps.android.astblaster;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Rect {
	
	public static float vertices[];
	public static short indices[];
	public static float uvs[];
	public FloatBuffer vertexBuffer;
	public ShortBuffer drawListBuffer;
	public FloatBuffer uvBuffer;
	
	float color[] = { 1.0f, 1.0f, 1.0f, 0.0f };
	
	Rect(){
		SetupRect();
	}
	
	public void SetupRect()
	{
		// We have to create the vertices of our triangle.
		vertices = new float[]
		           {0, 0f, 0.0f,
					1, 0f, 0.0f,
					0, 1f, 0.0f,
					1, 1f, 0.0f,
		           };
		
		indices = new short[] {0, 1, 2, 1,3,2}; // The order of vertexrendering.

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
//float color[] = { 1.0f, 1.0f, 1.0f, 0.0f };
public void draw(float[] m, float[] color,float width, float height) {
	// Add program to OpenGL environment
	int cp = riGraphicTools.sp_SolidColorButtons;
    GLES20.glUseProgram(cp);

    // get handle to vertex shader's vPosition member
    int mPositionHandle = GLES20.glGetAttribLocation(cp, "vPosition");

    // Enable a handle to the triangle vertices
    GLES20.glEnableVertexAttribArray(mPositionHandle);

    // Prepare the triangle coordinate data
    GLES20.glVertexAttribPointer(
            mPositionHandle, 3,
            GLES20.GL_FLOAT, false,
            12, vertexBuffer);

    // get handle to fragment shader's vColor member
    int mColorHandle = GLES20.glGetUniformLocation(cp, "vColor");
    GLES20.glUniform4fv(mColorHandle, 1, color, 0);
    
    
   
    // get handle to shape's transformation matrix
    int mMVPMatrixHandle = GLES20.glGetUniformLocation(cp, "uMVPMatrix");
    MyGLRenderer.checkGlError("glGetUniformLocation");

    // Apply the projection and view transformation
    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, m, 0);
    MyGLRenderer.checkGlError("glUniformMatrix4fv");

    // Draw the triangle
    GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, indices.length,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(mPositionHandle);
        	
	}
}
