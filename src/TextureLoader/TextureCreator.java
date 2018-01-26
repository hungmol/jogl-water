/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package TextureLoader;

import com.jogamp.common.nio.Buffers;
import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_WRAP_R;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author duonghung
 */
public class TextureCreator
{
    private static BufferedImage getBufferedImage(String fileName)
    {
        BufferedImage img;
        try
        {
            img	= ImageIO.read(new File(fileName));
        }
        catch (IOException e)
        {
            System.err.println("Error reading '" + fileName + '"');
            throw new RuntimeException(e);
        }
        return img;
    }
    
    private static byte[] getRGBAPixelData(BufferedImage img)
    {
        byte[] imgRGBA;
        int height = img.getHeight(null);
        int width = img.getWidth(null);
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
        ComponentColorModel colorModel	= new ComponentColorModel( ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[]{8, 8, 8, 8 }, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        
        BufferedImage newImage = new BufferedImage(colorModel, raster, false, null);
        AffineTransform gt = new AffineTransform();
        gt.translate(0, height);
        gt.scale(1, -1d);
        Graphics2D g = newImage.createGraphics();
        g.transform(gt);
        g.drawImage(img, null, null);
        g.dispose();
        DataBufferByte dataBuf = (DataBufferByte) raster.getDataBuffer();
        imgRGBA = dataBuf.getData();
        return imgRGBA;
    }
    
    public static IntBuffer createTexture(String textureFileName)
    {
        GL3 gl3 = (GL3) GLContext.getCurrentGL();
        
        // Load image data to buffer image
        
        BufferedImage bufferImage = getBufferedImage(textureFileName);
        
        // Get Pixel data
        byte[] imgRGBA = getRGBAPixelData(bufferImage);
        ByteBuffer rgbaBuffer =	ByteBuffer.wrap(imgRGBA);

        IntBuffer location = GLBuffers.newDirectIntBuffer(1);
        gl3.glGenTextures(1, location);
        gl3.glBindTexture(GL_TEXTURE_2D, location.get(0));
        gl3.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bufferImage.getWidth(), bufferImage.getHeight(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, rgbaBuffer);
        gl3.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl3.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl3.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl3.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl3.glBindTexture(GL_TEXTURE_2D, 0);
        return location;
    }
    
    public static IntBuffer createSkyBox()
    {
        GL3 gl3 = (GL3) GLContext.getCurrentGL();
        IntBuffer textureLoc = GLBuffers.newDirectIntBuffer(1);
        gl3.glGenTextures(1, textureLoc);
        gl3.glBindTexture(GL_TEXTURE_CUBE_MAP, textureLoc.get(0));
        
        
        ArrayList<String> skyboxTexture =  new ArrayList<>();
        skyboxTexture.add("./textures/skybox/right.jpg");
        skyboxTexture.add("./textures/skybox/left.jpg");
        skyboxTexture.add("./textures/skybox/bottom.jpg");
        skyboxTexture.add("./textures/skybox/top.jpg");
        skyboxTexture.add("./textures/skybox/front.jpg");
        skyboxTexture.add("./textures/skybox/back.jpg");
        
//        skyboxTexture.add("./textures/skybox2/right.jpg");
//        skyboxTexture.add("./textures/skybox2/left.jpg");
//        skyboxTexture.add("./textures/skybox2/bottom.jpg");
//        skyboxTexture.add("./textures/skybox2/top.jpg");
//        skyboxTexture.add("./textures/skybox2/front.jpg");
//        skyboxTexture.add("./textures/skybox2/back.jpg");
        
        for (int i = 0; i < skyboxTexture.size(); ++i)
        {
            BufferedImage bufferImage = getBufferedImage(skyboxTexture.get(i));
            byte[] imgRGBA = getRGBAPixelData(bufferImage);
            ByteBuffer rgbaBuffer = Buffers.newDirectByteBuffer(imgRGBA);
            gl3.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, bufferImage.getWidth(), bufferImage.getHeight(), 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, rgbaBuffer);
            
        }
        gl3.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl3.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl3.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl3.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl3.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        
        return textureLoc;
    }
}
