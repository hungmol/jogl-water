/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Objects;

import static com.jogamp.opengl.GL2GL3.*;
//import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import MathUtils.vec3;
/**
 *
 * @author duonghung
 */
public class WaterSurface {
    private final IntBuffer points_vbo = GLBuffers.newDirectIntBuffer(1);
    private final IntBuffer normals_vbo = GLBuffers.newDirectIntBuffer(1);
    private final GL3 gl3 = (GL3) GLContext.getCurrentGL();
    public final int N = 61;
    public final int width = 60;
    public final int height = 60;
    public final float c = (float)16.0;
    
    public float[][] u = new float[width][height];
    public float[][] u_new = new float[width][height];
    public float[][] v = new float[width][height];
    public float[][] control_point_heights = new float[width][height];
    
    public IntBuffer vaoWaterSurf = GLBuffers.newDirectIntBuffer(1);
    public IntBuffer elements_vbo = GLBuffers.newDirectIntBuffer(1);;
    
    public vec3[] points = new vec3[N * N];
    public vec3[] normals = new vec3[N * N];
    public vec3[] elements = new vec3[(N - 1) * (N - 1) * 2];
    
    public float[] points_buffer = new float[N * N * 3];
    public float[] normals_buffer = new float[N * N * 3];
    public int[] elements_buffer = new int[(N - 1) * (N - 1) * 2 * 3];
    
    public WaterSurface()
    {
        // Set up the VAO and VBOS
        gl3.glGenVertexArrays(1, vaoWaterSurf);
        gl3.glGenBuffers(1, elements_vbo);
        gl3.glGenBuffers(1, normals_vbo);
        gl3.glGenBuffers(1, points_vbo);
        
        // Initialize water heights
        for (int i = 0; i < this.width; i++)
        {
            for (int j = 0; j < this.height; j++)
            {   
                if (i > 28 && i < 32 && j > 28 && j < 32)
                {
                    this.u[i][j] = (float)0.7;
                }
                else
                {
                    this.u[i][j] = (float)0.0;
                }
                this.v[i][j] = (float)0.0;
                this.u_new[i][j] = (float)0.0; 
            }
        }
    }
    
    public void update(float dt)
    {
        for (int i = 0; i < this.width; i++)
        {
            for (int j = 0; j < this.height; j++)
            {
                float v1, v2, v3, v4;
                
                if (i == 0) {
                    v1 =  this.u[i][j];
                }
                else {
                    v1 = this.u[i - 1][j];
                }
                
                if (i == this.width - 1) {
                    v2 = this.u[i][j];
                }
                else {
                    v2 = this.u[i + 1][j];
                }
                
                if (j == 0) {
                    v3 = this.u[i][j];
                }
                else {
                    v3 = this.u[i][j - 1];
                }
                
                if (j == this.height - 1) {
                    v4 = this.u[i][j];
                }
                else {
                    v4 = this.u[i][j + 1];
                }
                
                float f = c * c * ((v1 + v2 + v3 + v4) - 4 * this.u[i][j]);
                this.v[i][j] += f * dt;
                this.v[i][j] *= 0.995;
                this.u_new[i][j] = u[i][j] + v[i][j] * dt;
            }
        }
        
        for (int i = 0; i < this.width; i++)
        {
            for (int j = 0; j < this.height; j++) 
            {
                this.u[i][j] = this.u_new[i][j];
                this.control_point_heights[i][j] = this.u[i][j];
            }
        }
        
        int[] x_delta = new int[]{0, -1, -1, 0, 1, 1, 1, 0, -1};
        int[] y_delta = new int[]{0, 0, -1, -1, -1, 0, 1, 1, 1};
        
        for (int i = 3; i < this.width; i+=3)
        {
            for (int j = 3; j < this.height; j+=3)
            {
                vec3[] tmpPoints = new vec3[9];               
                for (int k = 0; k < 9; k++) {
                    int x_index = i + x_delta[k];
                    int y_index = j + y_delta[k];
                    
                    float x_tmp = -3 + 6 * (1 - (x_index / (float) this.width));
                    float y_tmp = -3 + 6 * (1 - (y_index / (float) this.height));
                    float z_tmp = this.control_point_heights[x_index][y_index];
                    tmpPoints[k] = new vec3(x_tmp, y_tmp, z_tmp);
                }
                
                float sum_xx = (float)0.0;
                float sum_yy = (float)0.0;
                float sum_xy = (float)0.0;
                float sum_yz = (float)0.0;
                float sum_xz = (float)0.0;
                
                for (int k = 0; k < 9; k++)
                {
                    sum_xx += tmpPoints[k].x * tmpPoints[k].x;
                    sum_yy += tmpPoints[k].y * tmpPoints[k].y;
                    sum_xy += tmpPoints[k].x * tmpPoints[k].y;
                    sum_yz += tmpPoints[k].y * tmpPoints[k].z;
                    sum_xz += tmpPoints[k].x * tmpPoints[k].z;
                }
                
                float D = sum_xx * sum_yy - sum_xy * sum_xy;
                float a = (sum_yz * sum_xy - sum_xz * sum_yy) / D;
                float b = (sum_xy * sum_xz - sum_xx * sum_yz) / D;
                
                vec3 n = new vec3(a, b, 1);  
                vec3 p = tmpPoints[0];

                for (int k = 1; k < 9; k++)
                {
                    vec3 p0 = tmpPoints[k];
                    
                    float z = (n.x * (p.x - p0.x) + n.y * (p.y - p0.y)) / n.z + p.z;
                    
                    int x_index = i + x_delta[k];
                    int y_index = j + y_delta[k];
                    this.control_point_heights[x_index][y_index] = z;
                }
            }
        }
        
        // Calculate points
        int point_index = 0;
        for (float x = -3; x < 3; x += 0.1) {
            for (float y = -3; y < 3; y += 0.1) {
                int i = (int)(((x + 3.0) / 6.0) * this.width);
                int j = (int)(((y + 3.0) / 6.0) * this.height);
                
                // Take the average height of the blocks around us, weighted by their distances
                float sum_of_weights = (float)0.0;
                float avg_height = (float)0.0;
                
                for (int k = i-1; k <= i+1; k++)
                {
                    if (k < 0 || k >= this.width) {
                        continue;
                    }
                    
                    for (int l = j-1; l <= j+1; l++) {
                        if (l < 0 || l >= this.height) {
                            continue;
                        }
                        
                        float u_pt = -3 + 6 * (k / (float) this.width);
                        float v_pt = -3 + 6 * (l / (float) this.height);
                        
                        // Make sure the weight is larger for smaller distances
                        float weight = (float)(100 - (u_pt - x) * (u_pt - x) + (y - v_pt) * (y - v_pt));
                        avg_height += this.u[k][l] * weight;
                        sum_of_weights += weight;
                    }
                }
                avg_height /= sum_of_weights;               
                points[point_index] = new vec3(x, y, avg_height);
                point_index++;
                
            }
        }
        
        // Calculate normals
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // Average the normals for each triangle around us
                int p1_i = i + N * j;
                int p2_i = i + 1 + N * j;
                int p3_i = i - 1 + N * j;
                int p4_i = i + N * (j - 1);
                int p5_i = i + N * (j + 1);
                
                vec3 normal;
                
                if (i == 0 && j == 0) {
                    vec3 p1 = points[p1_i];
                    vec3 p2 = points[p2_i];
                    vec3 p5 = points[p5_i];
                    normal = p5.minus(p1).crossProduct(p2.minus(p1));
                } 
                else if (i == 0 && j == N - 1) 
                {
                    vec3 p1 = points[p1_i];
                    vec3 p2 = points[p2_i];
                    vec3 p4 = points[p4_i];
                    normal = p2.minus(p1).crossProduct(p4.minus(p1));
                } 
                else if (i == N - 1 && j == 0) 
                {
                    vec3 p1 = points[p1_i];
                    vec3 p3 = points[p3_i];
                    vec3 p5 = points[p5_i];
                    normal = p3.minus(p1).crossProduct(p5.minus(p1));
                } 
                else if (i == N - 1 && j == N - 1) 
                {
                    vec3 p1 = points[p1_i];
                    vec3 p3 = points[p3_i];
                    vec3 p4 = points[p4_i];
                    normal = p4.minus(p1).crossProduct(p3.minus(p1));
                } 
                else if (i == 0) 
                {
                    vec3 p1 = points[p1_i];
                    vec3 p2 = points[p2_i];
                    vec3 p4 = points[p4_i];
                    vec3 p5 = points[p5_i];
                    
                    vec3 n1 = p2.minus(p1).crossProduct(p4.minus(p1));
                    n1.make_unit_length();
                    vec3 n2 = p5.minus(p1).crossProduct(p2.minus(p1));
                    n2.make_unit_length();
                    
                    normal = n1.plus(n2).mult((float)0.5);
                } 
                else if (j == 0) 
                {
                    vec3 p1 = points[p1_i];
                    vec3 p2 = points[p2_i];
                    vec3 p3 = points[p3_i];
                    vec3 p5 = points[p5_i];                    
                    vec3 n1 = p3.minus(p1).crossProduct(p5.minus(p1));                    
                    n1.make_unit_length();
                    vec3 n2 = (p5.minus(p1)).crossProduct(p2.minus(p1));
                    n2.make_unit_length();                   
                    normal = (n1.plus(n2)).mult((float)0.5);
                } 
                else if (i == N - 1) 
                {
                    vec3 p1 = points[p1_i];
                    vec3 p3 = points[p3_i];
                    vec3 p4 = points[p4_i];
                    vec3 p5 = points[p5_i];
                    
                    vec3 n1 = p4.minus(p1).crossProduct(p3.minus(p1));
                    n1.make_unit_length();
                    vec3 n2 = p3.minus(p1).crossProduct(p5.minus(p1));
                    n2.make_unit_length();
                    
                    normal = (n1.plus(n2)).mult((float)0.5);
                } 
                else if (j == N - 1)
                {
                    vec3 p1 = points[p1_i];
                    vec3 p2 = points[p2_i];
                    vec3 p3 = points[p3_i];
                    vec3 p4 = points[p4_i];
                    
                    vec3 n1 = p2.minus(p1).crossProduct(p4.minus(p1));
                    n1.make_unit_length();
                    vec3 n2 = p4.minus(p1).crossProduct(p3.minus(p1));
                    n2.make_unit_length();
                    
                    normal = (n1.plus(n2)).mult((float)0.5);
                }
                else 
                {
                    vec3 p1 = points[p1_i];
                    vec3 p2 = points[p2_i];
                    vec3 p3 = points[p3_i];
                    vec3 p4 = points[p4_i];
                    vec3 p5 = points[p5_i];
                    
                    vec3 n1 = p4.minus(p1).crossProduct(p3.minus(p1));
                    n1.make_unit_length();
                    vec3 n2 = p2.minus(p1).crossProduct(p4.minus(p1));
                    n2.make_unit_length();
                    vec3 n3 = p5.minus(p1).crossProduct(p2.minus(p1));
                    n3.make_unit_length();
                    vec3 n4 = p3.minus(p1).crossProduct(p5.minus(p1));
                    n4.make_unit_length();
                    
                    normal =  (n1.plus(n2).plus(n3).plus(n4)).mult((float)0.25);
                }
                
                normal.make_unit_length();
                normals[p1_i] = normal;
            }
        }
        
        // Calculate the elements for each triangle
        int e_i = 0;
        for (int i = 0; i < N - 1; i++) 
        {
            for (int j = 0; j < N - 1; j++) 
            {
                // First triangle
                int p1 = i + N * j;
                int p2 = i + 1 + N * j;
                int p3 = i + N * (j + 1);

                elements[e_i] = new vec3(p1, p2, p3);
                e_i++;

                // Second triangle
                int p4 = i + 1 + N * j;
                int p5 = i + 1 + N * (j + 1);
                int p6 = i + N * (j + 1);
                
                elements[e_i] = new vec3(p4, p5, p6);
                e_i++;
            }
        }
        
        gl3.glBindVertexArray(vaoWaterSurf.get(0));
        
        // Set up the points vbo
        for (int i = 0; i < N * N; i++) {
            points_buffer[i*3] = points[i].x;
            points_buffer[i*3 + 1] = points[i].y;
            points_buffer[i*3 + 2] = points[i].z;
        }
   
        gl3.glBindBuffer(GL_ARRAY_BUFFER, points_vbo.get(0));
        FloatBuffer pointBuffer = GLBuffers.newDirectFloatBuffer(points_buffer);
        gl3.glBufferData(GL_ARRAY_BUFFER, 4 * points_buffer.length, pointBuffer, GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl3.glEnableVertexAttribArray(0);
        
        // Set up the normals vbo
        for (int i = 0; i < N * N; i++) {
            normals_buffer[i*3] = normals[i].x;
            normals_buffer[i*3 + 1] = normals[i].y;
            normals_buffer[i*3 + 2] = normals[i].z;
        }
        gl3.glBindBuffer(GL_ARRAY_BUFFER, normals_vbo.get(0));
        FloatBuffer normalBuffer = GLBuffers.newDirectFloatBuffer(normals_buffer);
        gl3.glBufferData(GL_ARRAY_BUFFER, 4 * normals_buffer.length, normalBuffer, GL_STATIC_DRAW);
        gl3.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl3.glEnableVertexAttribArray(1);       
        
        // Set up elements vbo
        for (int i = 0; i < (N - 1) * (N - 1) * 2; i++) 
        {
            elements_buffer[i*3] = (int)elements[i].x;
            elements_buffer[i*3 + 1] = (int)elements[i].y;
            elements_buffer[i*3 + 2] = (int)elements[i].z;
        }
        gl3.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elements_vbo.get(0));
        
        IntBuffer elementBuffer = GLBuffers.newDirectIntBuffer(elements_buffer);
        gl3.glBufferData(GL_ELEMENT_ARRAY_BUFFER, 4 * elements_buffer.length, elementBuffer, GL_STATIC_DRAW);
        gl3.glBindVertexArray(0);
    }
}
