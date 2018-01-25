/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package MathUtils;

/**
 *
 * @author duonghung
 */
public class vec3 {
    public float x, y, z;
    public vec3()
    {
        this.x = (float)0.0;
        this.y = (float)0.0;
        this.z = (float)0.0;
    }
    
    public vec3(float xc, float yc, float zc)
    {
        this.x = xc;
        this.y = yc;
        this.z = zc;
    }

    public void set(float xc, float yc, float zc)
    {
        this.x = xc;
        this.y = yc;
        this.z = zc;
    }
    
    public float length()
    {
        return (float)Math.sqrt((x*x + y*y + z*z));
    }
    
    public void make_unit_length()
    {
        float l = length();
        if (l != 0)
        {
            x = x / l;
            y = y / l;
            z = z / l;
        }
    }
    
    public vec3 negated()
    {
        return new vec3(-this.x, -this.y, -this.z);
    }
    
    public float dot(vec3 vector)
    {
        return this.x*vector.x + this.y*vector.y + this.z*vector.z;
    }
    
    public vec3 normalize() {

        float length = ((float) Math.sqrt(x * x + y * y + z * z));
        
        return length != 0 ? new vec3(x / length, y / length, z / length) : new vec3();
    }
    
    public vec3 crossProduct(vec3 v)
    {
        float x_temp = this.y * v.z - this.z * v.y;
        float y_temp = this.z * v.x - this.x * v.z;
        float z_temp = this.x * v.y - this.y * v.x;
        return new vec3(x_temp, y_temp, z_temp);
    }
    
    public vec3 minus(vec3 v)
    {
        return new vec3(x - v.x, y - v.y, z - v.z);
    }
    
    public vec3 plus(vec3 v)
    {   
        return new vec3(x + v.x, y + v.y, z + v.z);
    }
    
    public vec3 mult(mat4 m) 
    {
        float xt = this.x*m.m[0] + this.y*m.m[1] + this.z*m.m[2] + m.m[3];
        float yt = this.x*m.m[4] + this.y*m.m[5] + this.z*m.m[6] + m.m[7];
        float zt = this.x*m.m[8] + this.y*m.m[9] + this.z*m.m[10] + m.m[11];
        return new vec3(xt, yt, zt);
    }
    
    public vec3 mult(float c)
    {
        return new vec3(this.x*c, this.y*c, this.z*c);
    }
    
    public void print()
    {
        System.out.println(x + ", " + y + ", " + z);
    }
}
