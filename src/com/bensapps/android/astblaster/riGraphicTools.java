package com.bensapps.android.astblaster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.bensapps.android.astblaster.R;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class riGraphicTools {

	// Program variables
	public static int sp_SolidColor;
	public static int sp_SolidColorButtons;
	public static int sp_Image;
	public static int sp_Image_Text;
	public static int sp_Image_Light;
	public static int sp_bg;
	private static Context mContext;
	

	public static int loadShader(int type, String shaderCode){


	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);
	    
	    // return the shader
	    return shader;
	}
	
	private static String readFromFile(int id) {

	    String ret = "";

	    try {
	        InputStream inputStream = mContext.getResources().openRawResource(id);

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }

	    return ret;
	}
	
	public static void doShaders(Context context){
		
		mContext=context;
		/* SHADER bg
		 * 
		 * This shader is for rendering a background.
		 * 
		 */
		String vs_Bg=readFromFile(R.raw.vs_bg);
		String fs_Bg=readFromFile(R.raw.fs_bg);
		/* SHADER Solid
		 * 
		 * This shader is for rendering a colored primitive.
		 * 
		 */
		String vs_SolidColor=readFromFile(R.raw.vs_solidcolor);
		String fs_SolidColor=readFromFile(R.raw.fs_solidcolor);
		String fs_SolidColorButtons=readFromFile(R.raw.fs_solidcolorbuttons);
		
		/* SHADER Image
		 * 
		 * This shader is for rendering 2D images straight from a texture
		 * No additional effects.
		 * 
		 */
		String vs_Image=readFromFile(R.raw.vs_image);
		String fs_Image=readFromFile(R.raw.fs_image);
		
		/* SHADER Image Text
		 * 
		 * This shader is for rendering 2D images straight from a texture
		 * No additional effects.
		 * 
		 */
		String vs_Image_Text=readFromFile(R.raw.vs_image);
		String fs_Image_Text=readFromFile(R.raw.fs_image_text);
		
		/* SHADER Image Light
		 * 
		 * This shader is for rendering 2D images straight from a texture
		 * No additional effects.
		 * 
		 */
		String vs_Image_Light=readFromFile(R.raw.vs_image_light);
		String fs_Image_Light=readFromFile(R.raw.fs_image_light);
		
		// Create the shaders, solid color
	    int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER,vs_SolidColor);
	    int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER,fs_SolidColor);

	    riGraphicTools.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(riGraphicTools.sp_SolidColor, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(riGraphicTools.sp_SolidColor, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(riGraphicTools.sp_SolidColor);                  // creates OpenGL ES program executables
	    
	 // Create the shaders, images
	    vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, vs_Image);
	    fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, fs_Image);

	    riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executables

	 // Create the shaders, images
	    vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, vs_Image_Text);
	    fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, fs_Image_Text);

	    riGraphicTools.sp_Image_Text = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(riGraphicTools.sp_Image_Text, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(riGraphicTools.sp_Image_Text, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(riGraphicTools.sp_Image_Text);                  // creates OpenGL ES program executables
	 // Create the shaders, images
	    vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, vs_Image_Light);
	    fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, fs_Image_Light);

	    riGraphicTools.sp_Image_Light = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(riGraphicTools.sp_Image_Light, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(riGraphicTools.sp_Image_Light, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(riGraphicTools.sp_Image_Light);                  // creates OpenGL ES program executables

	    vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, vs_Bg);
	    fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, fs_Bg);

	    riGraphicTools.sp_bg = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(riGraphicTools.sp_bg, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(riGraphicTools.sp_bg, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(riGraphicTools.sp_bg);                  // creates OpenGL ES program executables

	    vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, vs_SolidColor);
	    fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, fs_SolidColorButtons);

	    riGraphicTools.sp_SolidColorButtons = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(riGraphicTools.sp_SolidColorButtons, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(riGraphicTools.sp_SolidColorButtons, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(riGraphicTools.sp_SolidColorButtons);                  // creates OpenGL ES program executables

	    
	    // Set our shader programm
		GLES20.glUseProgram(riGraphicTools.sp_Image);
	}
}
