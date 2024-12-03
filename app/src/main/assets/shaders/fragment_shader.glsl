// TODO: Add classes to use this multi-task shader only as given

precision mediump float;

// Color
varying bool vUseColor;
varying vec4 vInterpolatedColor;

// Texture
varying bool vUseTexture;
varying vec2 vInterpolatedTexCoord;
uniform sampler2D uTexture;

void main() {
    if (vUseColor) {
        gl_FragColor = vInterpolatedColor;
    } else if (vUseTexture) {
        gl_FragColor = texture2D(uTexture, vInterpolatedTexCoord);
    }
}