package com.bensapps.android.astblaster;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Line {
	
		private FloatBuffer vertexBuffer;
	    private ShortBuffer drawListBuffer;
	    private int mPositionHandle;
	    private int mColorHandle;
	    private int mMVPMatrixHandle;
	    
	    static final int COORDS_PER_VERTEX = 3;
	    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex	    
	    public Line() {
	    	
	    }
	    public void draw(float[] mvpMatrix, float[] lineCoords, short[] drawOrder, float[] color) {
	    	ByteBuffer bb = ByteBuffer.allocateDirect(lineCoords.length * 4).order(ByteOrder.nativeOrder());
	    	vertexBuffer = bb.asFloatBuffer();
	    	vertexBuffer.put(lineCoords);
	    	vertexBuffer.position(0);
	    	ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
	        dlb.order(ByteOrder.nativeOrder());
	        drawListBuffer = dlb.asShortBuffer();
	        drawListBuffer.put(drawOrder);
	        drawListBuffer.position(0);	
	    	GLES20.glUseProgram(riGraphicTools.sp_SolidColor);
	        mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_SolidColor, "vPosition");
	        GLES20.glEnableVertexAttribArray(mPositionHandle);
	        GLES20.glVertexAttribPointer(
	        		mPositionHandle, COORDS_PER_VERTEX,
	                GLES20.GL_FLOAT, false,
	                vertexStride, vertexBuffer);
	        mColorHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "vColor");
	        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
	        mMVPMatrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_SolidColor, "uMVPMatrix");
	        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
	        GLES20.glLineWidth(4);
	        GLES20.glDrawElements(
	                GLES20.GL_LINE_STRIP, drawOrder.length,
	                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
	        GLES20.glDisableVertexAttribArray(mPositionHandle);
	    }


}
