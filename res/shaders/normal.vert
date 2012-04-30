#version 120
attribute vec3 position;
attribute vec2 texcoord;

varying vec2 vertTexcoord;
varying vec4 vertColor;

void main()
{
	vertTexcoord = texcoord;
	vertColor = vec4(1.0f,1.0f,1.0f,1.0f);

	//normal
	gl_Position = gl_ModelViewProjectionMatrix * vec4(position.x, position.y, position.z, 1.0f);
	//billboard
	//gl_Position = gl_ProjectionMatrix * (gl_ModelViewMatrix * vec4(0.0, 0.0, 0.0, 1.0) + vec4(position.x, position.y, 0.0, 0.0));
}