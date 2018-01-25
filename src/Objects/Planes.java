/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import static com.jogamp.opengl.GL2GL3.*;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.GL3;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

/**
 *
 * @author duonghung
 */
public class Planes {
    private final float[] point_buffer =  
    {
        -1f, 1f, 0f,
         1f, 1f, 0f,
        -1f, -1f, 0f,
        1f, 1f, 0f,
        1f, -1f, 0f,
        -1f, -1f, 0f
    };
    
    private final float[] normals_buffer = 
    {
        0f, 0f, 1f, 
        0f, 0f, 1f, 
        0f, 0f, 1f, 
        0f, 0f, 1f, 
        0f, 0f, 1f, 
        0f, 0f, 1f
    };
    
    public IntBuffer vaoPlane = GLBuffers.newDirectIntBuffer(1);
    public Planes()
    {
        GL3 gl3 = (GL3) GLContext.getCurrentGL();
        IntBuffer points_vbo = GLBuffers.newDirectIntBuffer(1);
        IntBuffer normals_vbo = GLBuffers.newDirectIntBuffer(1);
        IntBuffer texture_coords_vbo = GLBuffers.newDirectIntBuffer(1);
         
        gl3.glGenVertexArrays(1, vaoPlane);
        gl3.glGenBuffers(1, points_vbo);
        gl3.glGenBuffers(1, normals_vbo);
        gl3.glGenBuffers(1, texture_coords_vbo);
        
        gl3.glBindVertexArray(vaoPlane.get(0));
        
        // Setup the points vbo
        gl3.glBindBuffer(GL_ARRAY_BUFFER, points_vbo.get(0));
        FloatBuffer pointBuffer = GLBuffers.newDirectFloatBuffer(point_buffer);
        gl3.glBufferData(GL_ARRAY_BUFFER, 4*18, pointBuffer, GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0);
        gl3.glEnableVertexAttribArray(0);
        
        gl3.glBindBuffer(GL_ARRAY_BUFFER, normals_vbo.get(0));
        FloatBuffer normalBuffer = GLBuffers.newDirectFloatBuffer(normals_buffer);
        gl3.glBufferData(GL_ARRAY_BUFFER, 4*18, normalBuffer, GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(1, 3, GL_FLOAT, false, 12, 0);
        gl3.glEnableVertexAttribArray(1);
        
        gl3.glBindVertexArray(0);
    }   
}
