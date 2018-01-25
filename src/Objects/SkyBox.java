/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Objects;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.GLBuffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author lucifer
 */
public class SkyBox {
    public float skyboxVertices[] = new float[]{
        // positions
        // Front face
        -1.0f,  2.0f, -1.0f,
        -1.0f, 0.0f, -1.0f,
        1.0f, 0.0f, -1.0f,
        1.0f, 0.0f, -1.0f,
        1.0f,  2.0f, -1.0f,
        -1.0f,  2.0f, -1.0f,
        
        // Left face
        -1.0f, 0.0f,  1.0f,
        -1.0f, 0.0f, -1.0f,
        -1.0f,  2.0f, -1.0f,
        -1.0f,  2.0f, -1.0f,
        -1.0f,  2.0f,  1.0f,
        -1.0f, 0.0f,  1.0f,
        
        // Right face
        1.0f, 0.0f, -1.0f,
        1.0f, 0.0f,  1.0f,
        1.0f,  2.0f,  1.0f,
        1.0f,  2.0f,  1.0f,
        1.0f,  2.0f, -1.0f,
        1.0f, 0.0f, -1.0f,
        
        // Back face
        -1.0f, 0.0f,  1.0f,
        -1.0f,  2.0f,  1.0f,
        1.0f,  2.0f,  1.0f,
        1.0f,  2.0f,  1.0f,
        1.0f, 0.0f,  1.0f,
        -1.0f, 0.0f,  1.0f,
        
        // Top face
        -1.0f,  2.0f, -1.0f,
        1.0f,  2.0f, -1.0f,
        1.0f,  2.0f,  1.0f,
        1.0f,  2.0f,  1.0f,
        -1.0f,  2.0f,  1.0f,
        -1.0f,  2.0f, -1.0f,
        
        // Bottom face
        -1.0f, 0.0f, -1.0f,
        -1.0f, 0.0f,  1.0f,
        1.0f, 0.0f, -1.0f,
        1.0f, 0.0f, -1.0f,
        -1.0f, 0.0f,  1.0f,
        1.0f, 0.0f,  1.0f
    };
    
    public IntBuffer skyboxVAO = GLBuffers.newDirectIntBuffer(1);
    
    public SkyBox()
    {
        GL3 gl3 = (GL3)GLContext.getCurrentGL();
        IntBuffer skybox_vbo = GLBuffers.newDirectIntBuffer(1);
        
        for (int i = 0; i < skyboxVertices.length; ++i)
        {
            skyboxVertices[i] = skyboxVertices[i]*6;
        }
        
        gl3.glGenVertexArrays(1, skyboxVAO);
        gl3.glGenBuffers(1, skybox_vbo);
        gl3.glBindVertexArray(skyboxVAO.get(0));
        gl3.glBindBuffer(GL_ARRAY_BUFFER, skybox_vbo.get(0));
        
        FloatBuffer skyBoxVerticesBuff = GLBuffers.newDirectFloatBuffer(skyboxVertices);
        gl3.glBufferData(GL_ARRAY_BUFFER, 4*skyboxVertices.length, skyBoxVerticesBuff, GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        gl3.glEnableVertexAttribArray(0);
        gl3.glBindVertexArray(0);
    }
}
