package com.github.lookupgroup27.lookup.util.opengl

import android.opengl.GLES20
import android.util.Log

/**
 * Manages an OpenGL shader program, including compiling and linking vertex and fragment shaders.
 */
class ShaderProgram(vertexShaderCode: String, fragmentShaderCode: String) {

    val programId: Int

    init {
        // Compile the vertex and fragment shaders
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // Link the shaders into a program
        programId = linkProgram(vertexShader, fragmentShader)
    }

    /**
     * Compiles a shader of the given type (vertex or fragment).
     *
     * @param type The type of shader (GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER).
     * @param shaderCode The source code of the shader.
     * @return The ID of the compiled shader.
     */
    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderId = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shaderId, shaderCode)
        GLES20.glCompileShader(shaderId)

        // Check for compilation errors
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: ${GLES20.glGetShaderInfoLog(shaderId)}")
            GLES20.glDeleteShader(shaderId)
            throw RuntimeException("Shader compilation failed")
        }
        return shaderId
    }

    /**
     * Links the vertex and fragment shaders into a program.
     *
     * @param vertexShader The ID of the compiled vertex shader.
     * @param fragmentShader The ID of the compiled fragment shader.
     * @return The ID of the linked program.
     */
    private fun linkProgram(vertexShader: Int, fragmentShader: Int): Int {
        val programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)
        GLES20.glLinkProgram(programId)

        // Check for linking errors
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            Log.e(TAG, "Error linking program: ${GLES20.glGetProgramInfoLog(programId)}")
            GLES20.glDeleteProgram(programId)
            throw RuntimeException("Program linking failed")
        }
        return programId
    }

    /**
     * Activates this shader program for rendering.
     */
    fun use() {
        GLES20.glUseProgram(programId)
    }

    companion object {
        private const val TAG = "ShaderProgram"
    }
}
