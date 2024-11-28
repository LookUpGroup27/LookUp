uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projMatrix;
attribute vec4 vPosition;
attribute vec3 vColor;
varying vec3 color;
void main() {
  gl_Position = projMatrix * viewMatrix * modelMatrix * vPosition;
  color = vColor;
}