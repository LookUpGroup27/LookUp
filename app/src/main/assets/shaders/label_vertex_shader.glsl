uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjMatrix;

attribute vec4 aPosition;
attribute vec2 aTexCoordinate;

varying vec2 vTexCoordinate;

void main() {
    gl_Position = uProjMatrix * uViewMatrix * uModelMatrix * aPosition;
    vTexCoordinate = aTexCoordinate;
}