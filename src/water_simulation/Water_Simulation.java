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
        canvas.addMouseListener(new MouseEventHandler());
        canvas.setSize(1000, 1000);

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
        cam_targ = new vec3(-1.0f, 0.0f, 0.0f);
        cam_up = new vec3(0.0f, 1.0f, 0.0f);   
        view_mat = mat4.look_at(cam_pos, cam_targ, cam_up);
        proj_mat = mat4.perspective_projection(60, canvas_width/canvas_heigth, 0.1f, 1000f);
        
        // For water surface
        
        
        surfaceShader.createShaderProgram("./shaders/surface.vert", "./shaders/surface.frag");
        floorShader.createShaderProgram("./shaders/floor.vert", "./shaders/floor.frag");
        skyboxShader.createShaderProgram("./shaders/skybox.vert", "./shaders/skybox.frag");
        
        plane = new Planes();
        waterSurface = new WaterSurface();
        skyBox = new SkyBox();
        
        skyTexture = TextureCreator.createTexture("./textures/sky2.jpg");
        waterTexture = TextureCreator.createTexture("./textures/water.jpg");
        skyBoxTexture = TextureCreator.createSkyBox();
      
    }
    
    @Override
    public void display(GLAutoDrawable drawable)
    {
        GL3 gl = (GL3)GLContext.getCurrentGL();
        gl.glClear (GL_COLOR_BUFFER_BIT |  GL_DEPTH_BUFFER_BIT );
        
        // Calculated elasped time
 
        float currentTime = (float)TimeUnit.NANOSECONDS.toMillis(System.nanoTime())/1000.0f;
        deltaTime = currentTime - lastFrame;
        lastFrame = currentTime;
        
        // Update the viewport (Really should not happen every frame, only if the width/height changes)
        this.canvas_heigth = frame.getHeight();
        this.canvas_width = frame.getWidth(); 
        gl.glViewport(0, 0, canvas_width, canvas_heigth);
        proj_mat = mat4.perspective_projection(60, canvas_width / canvas_heigth, (float)0.1, 100f);
        
        // **************************************
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
//        mat4 model_view = mat4.identity();
        gl.glUniformMatrix4fv(surfaceShader.model_mat_location, 1, true, model_view.m,0);
        gl.glUniformMatrix4fv(surfaceShader.view_mat_location, 1, true, view_mat.m,0);
        gl.glUniformMatrix4fv(surfaceShader.proj_mat_location, 1, true, proj_mat.m,0);
        gl.glUniform3f(surfaceShader.color_location, (float)0.527, (float)0.843, (float)0.898);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, waterTexture.get(0));

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
    
    class MouseEventHandler implements MouseListener, MouseMotionListener, KeyListener
    {
        
        public MouseEventHandler() {}
        
        @Override
        public void mouseClicked(MouseEvent event)
        {
//            int xpos = event.getX();
//            int ypos = event.getY();
//            
//            vec3 new_mouse_pos = new vec3((2.0f * xpos) / canvas_width - 1.0f, 1.0f - (2.0f * ypos) / canvas_heigth, 1.0f);
//            vec3 mouse_world = mat4.mult(mat4.mult(inv_view_mat, inv_proj_mat), new_mouse_pos);
//            mouse_world.make_unit_length();
//            vec3 mouse_intersection = cam_pos.plus(mouse_world.mult(-cam_pos.z / mouse_world.z));
//            
//            if (mouse_intersection.x > -3.0 && mouse_intersection.x < 3.0 &&
//                    mouse_intersection.y > -3.0 && mouse_intersection.y < 3.0)
//            {
//                int i = (int)((mouse_intersection.x + 3.0f) / 6.0f * waterSurface.width);
//                int j = (int)((mouse_intersection.y + 3.0f) / 6.0f * waterSurface.height);
//                
//                if (i > 0 && j > 0 && i < waterSurface.width - 1 && j < waterSurface.height - 1)
//                {
//                    waterSurface.u[i][j] = 1.2f;
//                    waterSurface.u[i-1][j-1] = 0.7f;
//                    waterSurface.u[i-1][j] = 0.7f;
//                    waterSurface.u[i-1][j+1] = 0.7f;
//                    waterSurface.u[i+1][j-1] = 0.7f;
//                    waterSurface.u[i+1][j] = 0.7f;
//                    waterSurface.u[i+1][j+1] = 0.7f;
//                    waterSurface.u[i][j+1] = 0.7f;
//                    waterSurface.u[i][j-1] = 0.5f;
//                }
//            }
        }
        
        @Override
        public void mousePressed(MouseEvent me) {}
        
        @Override
        public void mouseReleased(MouseEvent me) {}
        
        @Override
        public void mouseEntered(MouseEvent me) {}
        
        @Override
        public void mouseExited(MouseEvent me) {}

        @Override
        public void mouseDragged(MouseEvent me) {}

        @Override
        public void mouseMoved(MouseEvent me) 
        {
        }

        @Override
        public void keyPressed(KeyEvent ke) 
        {
//            System.out.println(ke.getKeyChar());
//            if(ke.getKeyChar() == 'W' | ke.getKeyChar() == 'w'){
//                System.out.println("Dang an");
//                camera.ProcessKeyboard(Camera.Camera_Movement.FORWARD, deltaTime);
//            } else if (ke.getKeyChar() =='D' | ke.getKeyChar() == 'd') {
//                camera.ProcessKeyboard(Camera.Camera_Movement.RIGHT, deltaTime);
//            } else if (ke.getKeyChar() == 'A' | ke.getKeyChar() == 'a') {
//                camera.ProcessKeyboard(Camera.Camera_Movement.LEFT, deltaTime);
//            } else if (ke.getKeyChar() == 'S' | ke.getKeyChar() == 's') {
//                camera.ProcessKeyboard(Camera.Camera_Movement.BACKWARD, deltaTime);
//            }
        }

        @Override
        public void keyReleased(KeyEvent ke) {
            System.out.println(ke.getKeyChar());
        }
    }
}
