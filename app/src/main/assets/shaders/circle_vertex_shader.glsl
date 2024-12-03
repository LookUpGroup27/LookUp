attribute vec4 vPosition;
attribute vec4 vColor;
uniform mat4 uMVPMatrix;
varying vec4 vInterpolatedColor;
void main() {
    gl_Position = uMVPMatrix * vPosition;
    vInterpolatedColor = vColor;
}