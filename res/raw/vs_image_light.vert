uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_texCoord;

varying vec2 v_texCoord;
varying vec3 v_Position;
varying vec3 v_Normal;
void main() {
	v_Position = vec3(uMVMatrix * a_Position);
	v_Normal = vec3(uMVMatrix * vec4(a_Normal, 1.0));
	gl_Position = uMVPMatrix * a_Position;

	v_texCoord = a_texCoord;
}
