/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package water_simulation;

import MathUtils.mat4;
import MathUtils.vec3;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author duonghung
 */
public class Camera
{
    
    public enum Camera_Movement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    };
   
// Default camera values
    private final float YAW        = -90.0f;
    private final float PITCH      =  0.0f;
    private final float SPEED      =  2.5f;
    private final float SENSITIVTY =  0.1f;
    private final float ZOOM       =  45.0f;
    
    // Camera Attributes
    public vec3 Position;
    public vec3 Front;
    public vec3 Up;
    public vec3 Right;
    public vec3 WorldUp;
    
    // Eular Angles
    public float Yaw;
    public float Pitch;
    
    // Camera options
    public float MovementSpeed;
    public float MouseSensitivity;
    public float Zoom;
    
    // Constructor with vectors
    public Camera()
    {
        Position = new vec3(0.0f, 0.0f, 0.0f);
        WorldUp = new vec3(0.0f, 1.0f, 0.0f);
        Up = new vec3(0.0f, 1.0f, 0.0f);
        Yaw = YAW;
        Pitch = PITCH;
        this.Front = new vec3 (0.0f, 0.0f, -1.0f);
        this.MovementSpeed = SPEED;
        this.MouseSensitivity = SENSITIVTY;
        this.Zoom = ZOOM;
        updateCameraVectors();
    }
    
    public Camera(vec3 position, vec3 up, float yaw, float pitch)
    {
        Position = position;
        WorldUp = up;
        Yaw = yaw;
        Pitch = pitch;
        this.Front = new vec3 (0.0f, 0.0f, -1.0f);
        this.MovementSpeed = SPEED;
        this.MouseSensitivity = SENSITIVTY;
        this.Zoom = ZOOM;
        updateCameraVectors();
    }
    // Constructor with scalar values
    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch)
    {
        this.Position = new vec3(posX, posY, posZ);
        this.WorldUp = new vec3(upX, upY, upZ);
        this.Up = new vec3(upX, upY, upZ);
        this.Yaw = yaw;
        this.Pitch = pitch;
        this.Front = new vec3 (0.0f, 0.0f, -1.0f);
        this.MovementSpeed = SPEED;
        this.MouseSensitivity = SENSITIVTY;
        this.Zoom = ZOOM;
        updateCameraVectors();
    }
    
    // Returns the view matrix calculated using Eular Angles and the LookAt Matrix
    mat4 GetViewMatrix()
    {
        return mat4.look_at(Position, Position.plus(Front), Up);
    }
    
    // Processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)
    public void ProcessKeyboard(Camera_Movement direction, float deltaTime)
    {
        float velocity = MovementSpeed * deltaTime;
        if (direction == Camera_Movement.FORWARD)
            Position = Position.plus(Front.mult(velocity));
        if (direction == Camera_Movement.BACKWARD)
            Position = Position.minus(Front.mult(velocity));
        if (direction == Camera_Movement.LEFT)
            Position = Position.minus(Right.mult(velocity));
        if (direction == Camera_Movement.RIGHT)
            Position = Position.plus(Right.mult(velocity));
    }
    
    // Processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    public void ProcessMouseMovement(float xoffset, float yoffset, boolean constrainPitch)
    {
        constrainPitch = true;
        xoffset *= MouseSensitivity;
        yoffset *= MouseSensitivity;
        
        Yaw   += xoffset;
        Pitch += yoffset;
        
        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch)
        {
            if (Pitch > 89.0f)
                Pitch = 89.0f;
            if (Pitch < -89.0f)
                Pitch = -89.0f;
        }
        
        // Update Front, Right and Up Vectors using the updated Eular angles
        updateCameraVectors();
    }
    
    // Processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis
    public void ProcessMouseScroll(float yoffset)
    {
        if (Zoom >= 1.0f && Zoom <= 45.0f)
            Zoom -= yoffset;
        if (Zoom <= 1.0f)
            Zoom = 1.0f;
        if (Zoom >= 45.0f)
            Zoom = 45.0f;
    }
    
    // Calculates the front vector from the Camera's (updated) Eular Angles
    private void updateCameraVectors()
    {
        // Calculate the new Front vector
        vec3 front = new vec3();
        front.x = (float)(Math.cos(degree2Radian(Yaw)) * Math.cos(degree2Radian(Pitch)));
        front.y = (float)(Math.sin(degree2Radian(Pitch)));
        front.z = (float)(Math.sin(degree2Radian(Yaw)) * Math.cos(degree2Radian(Pitch)));
        Front = front.normalize();
        
        // Also re-calculate the Right and Up vector
        Right = Front.crossProduct(WorldUp).normalize();
        Up = Right.crossProduct(Front).normalize();
    }
    
    public float degree2Radian(float angle)
    {
        return (float)Math.PI*angle/180;
    }
    
}
