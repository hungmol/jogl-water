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
public class mat4 {
    public static final float DEGREES_PER_RADIAN = (float)0.01745329251;
    public static final float EPSILON = (float)0.0001;
    public float[] m;
    
    public mat4()
    {
        this.m = new float[16];
        this.m[0] = (float)1;
        this.m[5] = (float)1;
        this.m[10] = (float)1;
        this.m[15] = (float)1;
    }
    
    public mat4(float a, float b, float c, float d,
            float e, float f, float g, float h,
            float i, float j, float k, float l,
            float m, float n, float o, float p)
    {
        this.m = new float[16];
        this.m[0] = a;
        this.m[1] = b;
        this.m[2] = c;
        this.m[3] = d;
        this.m[4] = e;
        this.m[5] = f;
        this.m[6] = g;
        this.m[7] = h;
        this.m[8] = i;
        this.m[9] = j;
        this.m[10] = k;
        this.m[11] = l;
        this.m[12] = m;
        this.m[13] = n;
        this.m[14] = o;
        this.m[15] = p;
    }
    
    public mat4 inverse()
    {
        float det =
                m[0]*m[5]*m[10]*m[15] +
                m[0]*m[6]*m[11]*m[13] +
                m[0]*m[7]*m[9]*m[14] +
                
                m[1]*m[4]*m[11]*m[14] +
                m[1]*m[6]*m[8]*m[15] +
                m[1]*m[7]*m[10]*m[12] +
                
                m[2]*m[4]*m[9]*m[15] +
                m[2]*m[5]*m[11]*m[12] +
                m[2]*m[7]*m[8]*m[13] +
                
                m[3]*m[4]*m[10]*m[13] +
                m[3]*m[5]*m[8]*m[14] +
                m[3]*m[6]*m[9]*m[12] -
                
                m[0]*m[5]*m[11]*m[14] -
                m[0]*m[6]*m[9]*m[15] -
                m[0]*m[7]*m[10]*m[13] -
                
                m[1]*m[4]*m[10]*m[15] -
                m[1]*m[6]*m[11]*m[12] -
                m[1]*m[7]*m[8]*m[14] -
                
                m[2]*m[4]*m[11]*m[13] -
                m[2]*m[5]*m[8]*m[15] -
                m[2]*m[7]*m[9]*m[12] -
                
                m[3]*m[4]*m[9]*m[14] -
                m[3]*m[5]*m[10]*m[12] -
                m[3]*m[6]*m[8]*m[13];
        
        float a0 = m[5]*m[10]*m[15] + m[6]*m[11]*m[13] + m[7]*m[9]*m[14]
                - m[5]*m[11]*m[14] - m[6]*m[9]*m[15] - m[7]*m[10]*m[13];
        
        float a1 = m[1]*m[11]*m[14] + m[2]*m[9]*m[15] + m[3]*m[10]*m[13]
                - m[1]*m[10]*m[15] - m[2]*m[11]*m[13] - m[3]*m[9]*m[14];
        
        float a2 = m[1]*m[6]*m[15] + m[2]*m[7]*m[13] + m[3]*m[5]*m[14]
                - m[1]*m[7]*m[14] - m[2]*m[5]*m[15] - m[3]*m[6]*m[13];
        
        float a3 = m[1]*m[7]*m[10] + m[2]*m[5]*m[11] + m[3]*m[6]*m[9]
                - m[1]*m[6]*m[11] - m[2]*m[7]*m[9] - m[3]*m[5]*m[10];
        
        float a4 = m[4]*m[11]*m[14] + m[6]*m[8]*m[15] + m[7]*m[10]*m[12]
                - m[4]*m[10]*m[15] - m[6]*m[11]*m[12] - m[7]*m[8]*m[14];
        
        float a5 = m[0]*m[10]*m[15] + m[2]*m[11]*m[12] + m[3]*m[8]*m[14]
                - m[0]*m[11]*m[14] - m[2]*m[8]*m[15] - m[3]*m[10]*m[12];
        
        float a6 = m[0]*m[7]*m[14] + m[2]*m[4]*m[15] + m[3]*m[6]*m[12]
                - m[0]*m[6]*m[15] - m[2]*m[7]*m[12] - m[3]*m[4]*m[14];
        
        float a7 = m[0]*m[6]*m[11] + m[2]*m[7]*m[8] + m[3]*m[4]*m[10]
                - m[0]*m[7]*m[10] - m[2]*m[4]*m[11] - m[3]*m[6]*m[8];
        
        float a8 = m[4]*m[9]*m[15] + m[5]*m[11]*m[12] + m[7]*m[8]*m[13]
                - m[4]*m[11]*m[13] - m[5]*m[8]*m[15] - m[7]*m[9]*m[12];
        
        float a9 = m[0]*m[11]*m[13] + m[1]*m[8]*m[15] + m[3]*m[9]*m[12]
                - m[0]*m[9]*m[15] - m[1]*m[11]*m[12] - m[3]*m[8]*m[13];
        
        float a10 = m[0]*m[5]*m[15] + m[1]*m[7]*m[12] + m[3]*m[4]*m[13]
                - m[0]*m[7]*m[13] - m[1]*m[4]*m[15] - m[3]*m[5]*m[12];
        
        float a11 = m[0]*m[7]*m[9] + m[1]*m[4]*m[11] + m[3]*m[5]*m[8]
                - m[0]*m[5]*m[11] - m[1]*m[7]*m[8] - m[3]*m[4]*m[9];
        
        float a12 = m[4]*m[10]*m[13] + m[5]*m[8]*m[14] + m[6]*m[9]*m[12]
                - m[4]*m[9]*m[14] - m[5]*m[10]*m[12] - m[6]*m[8]*m[13];
        
        float a13 = m[0]*m[9]*m[14] + m[1]*m[10]*m[12] + m[2]*m[8]*m[13]
                - m[0]*m[10]*m[13] - m[1]*m[8]*m[14] - m[2]*m[9]*m[12];
        
        float a14 = m[0]*m[6]*m[13] + m[1]*m[4]*m[14] + m[2]*m[5]*m[12]
                - m[0]*m[5]*m[14] - m[1]*m[6]*m[12] - m[2]*m[4]*m[13];
        
        float a15 = m[0]*m[5]*m[10] + m[1]*m[6]*m[8] + m[2]*m[4]*m[9]
                - m[0]*m[6]*m[9] - m[1]*m[4]*m[10] - m[2]*m[5]*m[8];
        
        return new mat4(
                a0 / det, a1 / det, a2 / det, a3 / det,
                a4 / det, a5 / det, a6 / det, a7 / det,
                a8 / det, a9 / det, a10 / det, a11 / det,
                a12 / det, a13 / det, a14 / det, a15 / det
        );
        
    }
    
    public mat4 transpose()
    {
        return new mat4(
                m[0], m[4], m[8], m[12],
                m[1], m[5], m[9], m[13],
                m[2], m[6], m[10], m[14],
                m[3], m[7], m[11], m[15]
        );
    }
    
    public mat4 normal_transform()
    {
        return inverse().transpose();
    }
    
    public void make_zero()
    {
        for (int i = 0; i < 16; ++i)
        {
            this.m[i] = (float)0;
        }
    }
    
    public static mat4 identity()
    {
        return new mat4(1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }
    
    public static mat4 translation(vec3 v)
    {
        return new mat4 (1, 0, 0, v.x,
                0, 1, 0, v.y,
                0, 0, 1, v.z,
                0, 0, 0, 1);
    }
    
    public static mat4 scale(vec3 v) {
        return new mat4(v.x, 0, 0, 0,
                0, v.y, 0, 0,
                0, 0, v.z, 0,
                0, 0, 0, 1);
    }
    
    public static mat4 rotation_x(float theta) {
        float c = (float)Math.cos(theta);
        float s = (float)Math.sin(theta);
        return new mat4(
                1, 0, 0, 0,
                0, c, -s, 0,
                0, s, c, 0,
                0, 0, 0, 1
        );
    }
    
    public static mat4 rotation_y(float theta) {
        float c = (float)Math.cos(theta);
        float s = (float)Math.sin(theta);
        return new mat4(
                c, 0, s, 0,
                0, 1, 0, 0,
                -s, 0, c, 0,
                0, 0, 0, 1
        );
    }
    
    public static mat4 rotation_z(float theta) {
        float c = (float)Math.cos(theta);
        float s = (float)Math.sin(theta);
        return new mat4(
                c, -s, 0, 0,
                s, c, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }
    
    public static mat4 perspective_projection(float fov, float aspect, float near, float far) {
        float f = (float)1.0 / (float)Math.tan(fov * DEGREES_PER_RADIAN / 2.0);
        float denominator = near - far;
        float a = f / aspect;
        float b = f;
        float c = (far + near) / denominator;
        float d = (float)(2.0 * far * near) / denominator;
        
        return new mat4(
                a, 0, 0, 0,
                0, b, 0, 0,
                0, 0, c, d,
                0, 0, -1, 0
        );
    }
    
    public static mat4 look_at(vec3 position, vec3 target, vec3 up) {
        vec3 up_p = up;
        up_p.make_unit_length();
        
        vec3 f = target.minus(position);
        f.make_unit_length();
        
        vec3 s = f.crossProduct(up_p);
        s.make_unit_length();
        
        vec3 u = s.crossProduct(f);
        u.make_unit_length();
        
        mat4 M = new mat4(
                s.x, s.y, s.z, 0,
                u.x, u.y, u.z, 0,
                -f.x, -f.y, -f.z, 0,
                0, 0, 0, 1
        );
        mat4 T = translation(position.negated());
        return mult(M, T);
    }
    
    public static mat4 mult(mat4 A, mat4 B) {
        float[] a = A.m;
        float[] b = B.m;
        
        float c0 = a[0]*b[0] + a[1]*b[4] + a[2]*b[8] + a[3]*b[12];
        float c1 = a[0]*b[1] + a[1]*b[5] + a[2]*b[9] + a[3]*b[13];
        float c2 = a[0]*b[2] + a[1]*b[6] + a[2]*b[10] + a[3]*b[14];
        float c3 = a[0]*b[3] + a[1]*b[7] + a[2]*b[11] + a[3]*b[15];
        
        float c4 = a[4]*b[0] + a[5]*b[4] + a[6]*b[8] + a[7]*b[12];
        float c5 = a[4]*b[1] + a[5]*b[5] + a[6]*b[9] + a[7]*b[13];
        float c6 = a[4]*b[2] + a[5]*b[6] + a[6]*b[10] + a[7]*b[14];
        float c7 = a[4]*b[3] + a[5]*b[7] + a[6]*b[11] + a[7]*b[15];
        
        float c8 = a[8]*b[0] + a[9]*b[4] + a[10]*b[8] + a[11]*b[12];
        float c9 = a[8]*b[1] + a[9]*b[5] + a[10]*b[9] + a[11]*b[13];
        float c10 = a[8]*b[2] + a[9]*b[6] + a[10]*b[10] + a[11]*b[14];
        float c11 = a[8]*b[3] + a[9]*b[7] + a[10]*b[11] + a[11]*b[15];
        
        float c12 = a[12]*b[0] + a[13]*b[4] + a[14]*b[8] + a[15]*b[12];
        float c13 = a[12]*b[1] + a[13]*b[5] + a[14]*b[9] + a[15]*b[13];
        float c14 = a[12]*b[2] + a[13]*b[6] + a[14]*b[10] + a[15]*b[14];
        float c15 = a[12]*b[3] + a[13]*b[7] + a[14]*b[11] + a[15]*b[15];
        
        return new mat4(
                c0, c1, c2, c3,
                c4, c5, c6, c7,
                c8, c9, c10, c11,
                c12, c13, c14, c15
        );
    }
    
    public static vec3 mult(mat4 m, vec3 u)
    {
        float xt = u.x*m.m[0] + u.y*m.m[1] + u.z*m.m[2] + m.m[3];
        float yt = u.x*m.m[4] + u.y*m.m[5] + u.z*m.m[6] + m.m[7];
        float zt = u.x*m.m[8] + u.y*m.m[9] + u.z*m.m[10] + m.m[11];
        return new vec3(xt, yt, zt);
    }
    
    public void print() {
        System.out.println("[" + m[0] + ", " + m[1] + ", " + m[2] + ", " + m[3]);
        System.out.println(" " + m[4] + ", " + m[5] + ", " + m[6] + ", " + m[7]);
        System.out.println(" " + m[8] + ", " + m[9] + ", " + m[10] + ", " + m[11]);
        System.out.println(" " + m[12] + ", " + m[13] + ", " + m[14] + ", " + m[15] + "]");
    }
}
