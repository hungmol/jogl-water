/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package water_simulation;
import com.jogamp.opengl.GL3;
import static com.jogamp.opengl.GL3.*;
import com.jogamp.opengl.GLContext;

// For display window
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLAutoDrawable;
// ------------------- //
import Shaders.ShaderCreator;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import com.jogamp.opengl.util.FPSAnimator;
import Objects.*;
import java.nio.IntBuffer;
import TextureLoader.TextureCreator;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import MathUtils.vec3;
import MathUtils.mat4;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author duonghung
 */
public class Water_Simulation extends JFrame implements GLEventListener
{
    // Setup vector for camera view
    private vec3 cam_pos = new vec3();
    private vec3 cam_targ = new vec3();
    private vec3 cam_up = new vec3();
    
    // For view and projection matrices
    private mat4 view_mat = new mat4();
    private mat4 proj_mat = new mat4();
    private mat4 inv_view_mat = new mat4();
    private mat4 inv_proj_mat = new mat4();
    
    // Setup all the objects
//    private Rectangles rectangle;
    private WaterSurface waterSurface;
    private Planes plane;
    private SkyBox skyBox;
    
    // Setup the texturese
//    private IntBuffer floor_texture ;
    private IntBuffer skyTexture;
    private IntBuffer waterTexture;
    private IntBuffer skyBoxTexture;
    
    // Shaders
    // Setup shaders
    private final ShaderCreator surfaceShader = new ShaderCreator();
    private final ShaderCreator floorShader = new ShaderCreator();
    private final ShaderCreator skyboxShader = new ShaderCreator();
    
    // Window
    private final JFrame frame;
    private int canvas_width;
    private int canvas_heigth;
    
    // Time elapsed 
    public float deltaTime = 0.0f;	// time between current frame and last frame
    public float lastFrame = 0.0f;
    
    
    public Water_Simulation()
    {
        // For window display
        GLCanvas canvas ;
        GLProfile profile;
        GLCapabilities capabilities;
        
        // Init window
        profile = GLProfile.get(GLProfile.GL3);
        capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.setSize(1000, 600);

        // Adding canvas to it
        frame = new JFrame("Water Simulation");
        frame.getContentPane().add(canvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);
        this.canvas_heigth = frame.getHeight();
        this.canvas_width = frame.getWidth();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        //Instantiating and Initiating Animator
        final FPSAnimator animator = new FPSAnimator(canvas, 100, true);
        animator.start();
    }
    
    @Override
    public void init(GLAutoDrawable drawable)
    {
        // For skybox 
        cam_pos = new vec3(5.0f, 2.0f, 0.0f);
        cam_targ = new vec3(0.0f, 0.0f, 0.0f);
        cam_up = new vec3(0.0f, 1.0f, 0.0f);   
        
//        cam_pos = new vec3(15.0f, 12.0f,15.0f);
//        cam_targ = new vec3(0.0f, 0.0f, -2.0f);
//        cam_up = new vec3(0.0f, 0.0f, 1.0f);  

        inv_view_mat = view_mat.inverse();
        inv_proj_mat = proj_mat.inverse();
        

        
        view_mat = mat4.look_at(cam_pos, cam_targ, cam_up);
        proj_mat = mat4.perspective_projection(60, canvas_width/canvas_heigth, 0.1f, 100f);
        
        // For water surface
        
        
        surfaceShader.createShaderProgram("./shaders/surface.vert", "./shaders/surface.frag");
        floorShader.createShaderProgram("./shaders/floor.vert", "./shaders/floor.frag");
        skyboxShader.createShaderProgram("./shaders/skybox.vert", "./shaders/skybox.frag");
        
        plane = new Planes();
        waterSurface = new WaterSurface();
        skyBox = new SkyBox();
        
        skyTexture = TextureCreator.createTexture("./textures/skybox/top.jpg");
        waterTexture = TextureCreator.createTexture("./textures/skybox/top.jpg");
        skyBoxTexture = TextureCreator.createSkyBox();
    }
    
    @Override
    public void display(GLAutoDrawable drawable)
    {
        GL3 gl = (GL3)GLContext.getCurrentGL();
        gl.glClear (GL_COLOR_BUFFER_BIT |  GL_DEPTH_BUFFER_BIT );
        
        // Calculated elasped time
        float currentTime = (float)TimeUnit.NANOSECONDS.toMillis(System.nanoTime())/1000.0f;
        deltaTime = deltaTime + (currentTime - lastFrame);
        lastFrame = currentTime;
        
        if (deltaTime >= 5.0f)
        {  
//            System.out.println("delta time = " + deltaTime);
            deltaTime = 0.0f;
            int i = (int)((2.85f + 3.0f) / 6.0f * waterSurface.width);
            int j = (int)((2.85f + 3.0f) / 6.0f * waterSurface.height);
            
            if (i > 0 && j > 0 && i < waterSurface.width - 1 && j < waterSurface.height - 1)
            {
                float ratio = 1.0f;
                float min_value = 0.7f;
                float max_value = 1.2f;
    
                waterSurface.u[i][j] = max_value/ratio;
                waterSurface.u[i-1][j-1] = min_value/ratio;
                waterSurface.u[i-1][j] = min_value/ratio;
                waterSurface.u[i-1][j+1] = min_value/ratio;
                waterSurface.u[i+1][j-1] = min_value/ratio;
                waterSurface.u[i+1][j] = min_value/ratio;
                waterSurface.u[i+1][j+1] = min_value/ratio;
                waterSurface.u[i][j+1] = min_value/ratio;
                waterSurface.u[i][j-1] = min_value/ratio;
            }           
        }
        
        waterSurface.update((float)0.016);
        
        /* Draw sky box view*/
        gl.glDepthMask(false);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glUseProgram(skyboxShader.vfProgram);
        
        // Set uniform sky box
        mat4 model_view = mat4.identity();
        gl.glUniformMatrix4fv(skyboxShader.model_mat_location, 1, true, model_view.m,0);
        gl.glUniformMatrix4fv(skyboxShader.view_mat_location, 1, true, view_mat.m,0);
        gl.glUniformMatrix4fv(skyboxShader.proj_mat_location, 1, true, proj_mat.m,0);
        
        // Bind texture and draw sky box
        gl.glBindVertexArray(skyBox.skyboxVAO.get(0));
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxTexture.get(0));
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        gl.glDepthMask(true);
        gl.glBindVertexArray(0);
        gl.glDepthFunc(GL_LESS);
        gl.glDepthMask(true);
        gl.glUseProgram(0);
        
        /* Draw water surface */
        gl.glUseProgram(surfaceShader.vfProgram);
       
        // Set up uniforms for water surface
        mat4 model_mat = mat4.mult(mat4.mult(mat4.scale(new vec3(6.0f, 1.0f, 5.5f)),mat4.identity()), mat4.rotation_x(-(float)Math.PI/2));
//        mat4 model_mat = mat4.mult(mat4.identity(), mat4.rotation_x(-(float)Math.PI/2));
//        mat4 model_mat = mat4.identity();
        
        gl.glUniformMatrix4fv(surfaceShader.model_mat_location, 1, true, model_mat.m,0);
        gl.glUniformMatrix4fv(surfaceShader.view_mat_location, 1, true, view_mat.m,0);
        gl.glUniformMatrix4fv(surfaceShader.proj_mat_location, 1, true, proj_mat.m,0);
        gl.glUniform3f(surfaceShader.color_location, (float)0.527, (float)0.843, (float)0.898);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, waterTexture.get(0));
        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, skyTexture.get(0));

        // Draw the water surface
        gl.glBindVertexArray(waterSurface.vaoWaterSurf.get(0));
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, waterSurface.elements_vbo.get(0));
        gl.glDrawElements(GL_TRIANGLES, (waterSurface.N - 1) * (waterSurface.N - 1) * 2 * 3, GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);

    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int heigth)
    {}
    
    
    @Override
    public void dispose(GLAutoDrawable drawable){
    }
    
    public static void main(String[] args)
    {
        new Water_Simulation();
    }   
}
