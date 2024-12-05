// TODO: Add classes to use this multi-task shader as given

// MVP matrix
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projMatrix;

attribute vec4 vPosition;

// Color
attribute bool useColor;
varying bool vUseColor;
attribute vec4 vColor;
varying vec4 vInterpolatedColor;

// Texture
attribute bool useTexture;
varying bool vUseTexture;
attribute vec2 vTexCoord;
varying vec2 vInterpolatedTexCoord;

void main() {
    gl_Position = projMatrix * viewMatrix * modelMatrix * vPosition;
    vUseColor = useColor;
    vUseTexture = useTexture;
    if (useColor) { vInterpolatedColor = vColor; }
    if (useTexture){ vInterpolatedTexCoord = vTexCoord; }
}