/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Shaders;

import static com.jogamp.opengl.GL.GL_NO_ERROR;
import static com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;
/**
 *
 * @author duonghung
 */
public class ShaderCompileChecker
{
    private static final GL3 gl3 = (GL3) GLContext.getCurrentGL();
    public static void printShaderLog(int shader)
    {
        int[] len = new int[1];
        int[] chWritten = new int[1];
        byte[] log = null;
        
        // Determine the length of the shader compilation log
        gl3.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
        
        if (len[0] > 0)
        {
            log = new byte[len[0]];
            gl3.glGetShaderInfoLog(shader, len[0], chWritten, 0, log, 0);
            System.out.println("Shader info log: ");
            for (int i = 0; i < log.length; ++i)
            {
                System.out.print((char) log[i]);
            }
        }
    }
    
    public static void printProgramLog(int prog)
    {
        int[] len = new	int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;
        
        // Determine the length	of the program linking log
        gl3.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0)
        {				
            log	= new byte[len[0]];
            gl3.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
            System.out.println("Program	Info Log: ");
            for	(int i = 0; i <	log.length; i++)
            {
                System.out.print((char) log[i]);
            }
        }
    }
    
    public static boolean checkOpenGLError()
    {		
        boolean	foundError = false;
        GLU glu	= new GLU();
        int glErr = gl3.glGetError();
        while (glErr != GL_NO_ERROR)	
        {		
            System.err.println("glError:	" + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl3.glGetError();
        }
        return foundError;
    }
}
