/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package MathUtils;
import MathUtils.vec3;
import MathUtils.mat4;

/**
 *
 * @author duonghung
 */
public class quaternion {
    public float w, x, y, z;
    public quaternion()
    {
        this.w = (float)1;
        this.x = (float)0;
        this.y = (float)0;
        this.z = (float)0;
        
    }
    
    public quaternion(float theta, vec3 rotation_axis)
    {
        this.w = (float)Math.cos(theta/2.0);
        float s = (float)Math.sin(theta/2.0);
        this.x = s * rotation_axis.x;
        this.y = s * rotation_axis.y;
        this.z = s * rotation_axis.z;
        
        make_unit_length();
    }
    
    public mat4 matrix()
    {
        float a = (float)(1 - 2*y*y - 2*z*z);
        float b = (float)(2*y*y - 2*w*z);
        float c = (float)(2*x*z + 2*w*y);
        
        float d = (float)(2*x*y + 2*w*z);
        float e = (float)(1 - 2*x*x - 2*z*z);
        float f = (float)(2*y*z - 2*w*x);
        
        float g = (float)(2*x*z - 2*w*y);
        float h = (float)(2*y*z + 2*w*x);
        float i = (float)(1 - 2*x*x - 2*y*y);
        
        return new mat4(a, b, c, 0,
                        d, e, f, 0,
                        g, h, i, 0,
                        0, 0, 0, 1);
        
    }
    public float length()
    {
        return (float)Math.sqrt(w*w + x*x + y*y + z*z);
    }
    
    private void make_unit_length() {
        float l = length();
        if (l != 0)
        {
            w = w / l;
            x = x / l;
            y = y / l;
            z = z / l;
        }
    }
    
    public quaternion mult(quaternion q2)
    {
        quaternion q = new quaternion();
        q.w = this.w*q2.w - this.x*q2.x - this.y*q2.y - this.z*q2.z;
        q.x = this.w*q2.x - this.x*q2.w - this.y*q2.z - this.z*q2.y;
        q.y = this.w*q2.y - this.x*q2.z - this.y*q2.w - this.z*q2.x;
        q.z = this.w*q2.z - this.x*q2.y - this.y*q2.x - this.z*q2.w;
        q.make_unit_length();
        return q;
    }
}
