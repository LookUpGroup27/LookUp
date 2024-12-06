attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec2 vTexCoord;
uniform mat4 uMVPMatrix;
varying vec4 vInterpolatedColor;
varying vec2 vInterpolatedTexCoord;
void main() {
    gl_Position = uMVPMatrix * vPosition;
    vInterpolatedColor = vColor;
    vInterpolatedTexCoord = vTexCoord;
}