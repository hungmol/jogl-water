/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shaders;

import static com.jogamp.opengl.GL2ES2.GL_COMPILE_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_LINK_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
//import com.jogamp.opengl.GLContext;

/**
 *
 * @author duonghung
 */
public class ShaderCreator {   
    
    public int vfProgram;  
    public int model_mat_location; 
    public int view_mat_location; 
    public int proj_mat_location;
    public int color_location;
    
    public void createShaderProgram(String vertLocation, String fragLocation)
    {
        GL3 gl3 = (GL3) GLContext.getCurrentGL();
        
        String vshaderSource[] = ShaderDataReader.readShaderFromSource(vertLocation);       
        String fshaderSource[] = ShaderDataReader.readShaderFromSource(fragLocation);
        
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        int[] linked = new int[1];
        
        /* Create vertex shader*/
        int vShader = gl3.glCreateShader(GL_VERTEX_SHADER);
        gl3.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl3.glCompileShader(vShader);
        ShaderCompileChecker.checkOpenGLError();
        gl3.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
        System.out.println("");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        if (vertCompiled[0] == 1)
        {
            System.out.println("[INFO]Vertex Fragment load successed: " + vertLocation );
        } else {
            System.out.println("[ERROR]Vertex Fragment load failed: " + vertLocation);
            ShaderCompileChecker.printShaderLog(vShader);
        }
        
        /*Create fragment shader*/
        int fShader = gl3.glCreateShader(GL_FRAGMENT_SHADER);
        gl3.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
        gl3.glCompileShader(fShader);
        ShaderCompileChecker.checkOpenGLError();
        gl3.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
        if (fragCompiled[0] == 1)
        {
            System.out.println("[INFO]Fragment file load successed: " + fragLocation);
        } else {
            System.out.println("[ERROR]Fragment file liad failed: " + fragLocation);
            ShaderCompileChecker.printShaderLog(fShader);
        }
        
        if ((vertCompiled[0] != 1) || (fragCompiled[0] != 1))
        {
            System.out.println("\nCompilation error; return flags: ");
            System.out.println(" vertCompiled = " + vertCompiled[0] + "; fragCompiled = " + fragCompiled[0]);
        } 
        else
        {
            System.out.println("Successful compilation");
        }
        
        /*Create shader program  */
        vfProgram = gl3.glCreateProgram();
        gl3.glAttachShader(vfProgram, vShader);
        gl3.glAttachShader(vfProgram, fShader);
        gl3.glLinkProgram(vfProgram);
        
        // Catch error while linking shaders
        ShaderCompileChecker.checkOpenGLError();
        gl3.glGetProgramiv(vfProgram, GL_LINK_STATUS, linked, 0);
        if (linked[0] == 1)
        {
            System.out.println("[INFO]Program Linking successed");
        }
        else
        {
            System.out.println("[ERROR]Program Linking failed");
            ShaderCompileChecker.printProgramLog(vfProgram);
        }
        System.out.println("****************************************");
        
        gl3.glDeleteShader(vShader);
        gl3.glDeleteShader(fShader);

        // Set up the uniform locations
        this.model_mat_location = gl3.glGetUniformLocation(this.vfProgram, "model_mat");
        this.view_mat_location = gl3.glGetUniformLocation(this.vfProgram, "view_mat");
        this.proj_mat_location = gl3.glGetUniformLocation(this.vfProgram, "proj_mat");
        this.color_location = gl3.glGetUniformLocation(this.vfProgram, "color");
    }
}
