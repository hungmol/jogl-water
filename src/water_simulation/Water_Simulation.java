/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package water_simulation;
import static com.jogamp.opengl.GL3.*;
import com.jogamp.opengl.GL3;
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
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
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
    private mat4 inv_view_mat = new mat4();
    private mat4 proj_mat = new mat4();
    private mat4 inv_proj_mat = new mat4();
    
    // Setup all the objects
    private WaterSurface water_surface;
    private Planes plane;
    
    // Setup the texturese
    private IntBuffer floor_texture ;
    private IntBuffer sky_texture;
    private IntBuffer water_texture;
    
    // Shaders
    // Setup shaders
    public Shaders.ShaderCreator surface_shader = new ShaderCreator();
    public Shaders.ShaderCreator floor_shader = new ShaderCreator();
    public Shaders.ShaderCreator rectangle_shader = new ShaderCreator();
    
    // Time elapsed 
    public float deltaTime = 0.0f;	// time between current frame and last frame
    public float lastFrame = 0.0f;
    
    // Window
    private final JFrame frame;
    private int canvas_width;
    private int canvas_heigth;
    
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
        canvas.addMouseListener(new MouseEventHandler());
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
        GL3 gl = (GL3) GLContext.getCurrentGL();
        cam_pos = new vec3(15, 0, 3);
        cam_targ = new vec3(0, 0, -2);
        cam_up = new vec3(0, 0, 1);
        
        view_mat = mat4.look_at(cam_pos, cam_targ, cam_up);
        inv_view_mat = view_mat.inverse();
        proj_mat = mat4.perspective_projection(60, 1, (float)0.1, 100);
        inv_proj_mat = proj_mat.inverse();

        surface_shader.createShaderProgram("./shaders/surface.vert", "./shaders/surface.frag");
        floor_shader.createShaderProgram("./shaders/floor.vert", "./shaders/floor.frag");
        
        plane = new Planes();
        water_surface = new WaterSurface();
        
        floor_texture = TextureCreator.createTexture("./textures/floor.jpg");
        sky_texture = TextureCreator.createTexture("./textures/sky2.jpg");
        water_texture =TextureCreator.createTexture("./textures/water.jpg");
        /*
        * Initialize the shaders
        */
        gl.glUseProgram(surface_shader.vfProgram);
        int floor_texture_location = gl.glGetUniformLocation(surface_shader.vfProgram, "floor_texture");
        gl.glUniform1i(floor_texture_location, 0);
        
        int sky_texture_location = gl.glGetUniformLocation(surface_shader.vfProgram, "sky_texture");
        gl.glUniform1i(sky_texture_location, 1);
        gl.glUseProgram(0);
    }
    
    @Override
    public void display(GLAutoDrawable drawable)
    {
        GL3 gl = (GL3)GLContext.getCurrentGL();
        gl.glClear (GL_COLOR_BUFFER_BIT |  GL_DEPTH_BUFFER_BIT );
        this.canvas_heigth = frame.getHeight();
        this.canvas_width = frame.getWidth();
        
        // Update the viewport (Really should not happen every frame, only if the width/height changes)
        gl.glViewport(0, 0, canvas_width, canvas_heigth);
        proj_mat = mat4.perspective_projection(60, canvas_width / canvas_heigth, (float)0.1, 100);
        inv_proj_mat = proj_mat.inverse();
        
        
        // Calculated elasped time
        float currentTime = (float)TimeUnit.NANOSECONDS.toMillis(System.nanoTime())/1000.0f;
        deltaTime = deltaTime + (currentTime - lastFrame);
        lastFrame = currentTime;
        
        if (deltaTime > 4.0f)
        {
            deltaTime = 0.0f;
            int i = (int)((2.8f + 3.0f) / 6.0f * water_surface.width);
            int j = (int)((2.8f + 3.0f) / 6.0f * water_surface.height);
            if (i > 0 && j > 0 && i < water_surface.width - 1 && j < water_surface.height - 1)
            {
                water_surface.u[i][j] = 1.2f;
                water_surface.u[i-1][j-1] = 0.7f;
                water_surface.u[i-1][j] = 0.7f;
                water_surface.u[i-1][j+1] = 0.7f;
                water_surface.u[i+1][j-1] = 0.7f;
                water_surface.u[i+1][j] = 0.7f;
                water_surface.u[i+1][j+1] = 0.7f;
                water_surface.u[i][j+1] = 0.7f;
                water_surface.u[i][j-1] = 0.5f;
            }
        }
        
        /*
        * Draw the box which contains the water
        */
        {
            gl.glUseProgram(floor_shader.vfProgram);
            
            // Set up uniforms for floor of water
//            mat4 model_mat = mat4.mult(mat4.mult(mat4.translation(new vec3(0, 0, -3)),mat4.scale(new vec3(3, 3, 1))), mat4.rotation_z((float)Math.PI/2));
            gl.glUniformMatrix4fv(floor_shader.view_mat_location, 1, true, view_mat.m, 0);
            gl.glUniformMatrix4fv(floor_shader.proj_mat_location, 1, true, proj_mat.m, 0);
//            gl.glUniformMatrix4fv(floor_shader.model_mat_location, 1, true, model_mat.m, 0);
       
            mat4 scale_mat = mat4.mult(mat4.translation(new vec3(-3, 0, 4)),mat4.scale(new vec3(8, 10, 1)));
            mat4 model_mat = mat4.mult(scale_mat, mat4.rotation_y((float)(Math.PI/2)));
            gl.glUniformMatrix4fv(floor_shader.model_mat_location, 1, true, model_mat.m, 0);
            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, sky_texture.get(0));
            
            // Draw side 2 of water
            gl.glBindVertexArray(plane.vaoPlane.get(0));
            gl.glDrawArrays(GL_TRIANGLES, 0, 18);
            gl.glBindVertexArray(0);
            
            gl.glUseProgram(0);
        }
        
        water_surface.update((float)0.016);
        gl.glUseProgram(surface_shader.vfProgram);
        
        // Set up uniforms for water surface
//        mat4 model_mat = mat4.identity();
        mat4 model_mat = mat4.mult(mat4.mult(mat4.translation(new vec3(2,0,0)),mat4.identity()),mat4.scale(new vec3(8, 10, 1)));
        gl.glUniformMatrix4fv(surface_shader.model_mat_location, 1, true, model_mat.m,0);
        gl.glUniformMatrix4fv(surface_shader.view_mat_location, 1, true, view_mat.m,0);
        gl.glUniformMatrix4fv(surface_shader.proj_mat_location, 1, true, proj_mat.m,0);
        gl.glUniform3f(surface_shader.color_location, 0.527f, 0.843f, 0.898f);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, water_texture.get(0));
         gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, sky_texture.get(0));
        
        
        // Draw the water surface
        gl.glBindVertexArray(water_surface.vaoWaterSurf.get(0));
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, water_surface.elements_vbo.get(0));
        gl.glDrawElements(GL_TRIANGLES, (water_surface.N - 1) * (water_surface.N - 1) * 2 * 3, GL_UNSIGNED_INT, 0);
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
    
    class MouseEventHandler implements MouseListener
    {
        
        public MouseEventHandler() {}
        
        @Override
        public void mouseClicked(MouseEvent event)
        {
            int xpos = event.getX();
            int ypos = event.getY();
            
            vec3 new_mouse_pos = new vec3((2.0f * xpos) / canvas_width - 1.0f, 1.0f - (2.0f * ypos) / canvas_heigth, 1.0f);
            vec3 mouse_world = mat4.mult(mat4.mult(inv_view_mat, inv_proj_mat), new_mouse_pos);
            mouse_world.make_unit_length();
            vec3 mouse_intersection = cam_pos.plus(mouse_world.mult(-cam_pos.z / mouse_world.z));
            
            if (mouse_intersection.x > -3.0 && mouse_intersection.x < 3.0 &&
                    mouse_intersection.y > -3.0 && mouse_intersection.y < 3.0)
            {
                int i = (int)((mouse_intersection.x + 3.0f) / 6.0f * water_surface.width);
                int j = (int)((mouse_intersection.y + 3.0f) / 6.0f * water_surface.height);
                
                if (i > 0 && j > 0 && i < water_surface.width - 1 && j < water_surface.height - 1)
                {
                    water_surface.u[i][j] = 1.2f;
                    water_surface.u[i-1][j-1] = 0.7f;
                    water_surface.u[i-1][j] = 0.7f;
                    water_surface.u[i-1][j+1] = 0.7f;
                    water_surface.u[i+1][j-1] = 0.7f;
                    water_surface.u[i+1][j] = 0.7f;
                    water_surface.u[i+1][j+1] = 0.7f;
                    water_surface.u[i][j+1] = 0.7f;
                    water_surface.u[i][j-1] = 0.5f;
                }
            }
        }
        
        @Override
        public void mousePressed(MouseEvent me) {}
        
        @Override
        public void mouseReleased(MouseEvent me) {}
        
        @Override
        public void mouseEntered(MouseEvent me) {}
        
        @Override
        public void mouseExited(MouseEvent me) {}
    }
}
