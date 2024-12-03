precision mediump float;
varying vec4 vInterpolatedColor;
varying vec2 vInterpolatedTexCoord;
uniform sampler2D uTexture;
void main() {
    gl_FragColor = texture2D(uTexture, vInterpolatedTexCoord);
}