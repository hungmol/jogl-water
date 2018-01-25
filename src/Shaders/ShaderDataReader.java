/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Shaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author duonghung
 */
public class ShaderDataReader {
    public static String[] readShaderFromSource(String fileName)
    {
        ArrayList<String> lines = new ArrayList<>();
        Scanner	sc;
        try
        {	
            sc = new Scanner(new File(fileName));	
        }
        catch (IOException e)
        {		
            System.err.println("IOException reading file: " + e);
            return null;
        }
        while (sc.hasNext())
        {		
            lines.add(sc.nextLine());
//            lines.addElement(sc.nextLine());
        }
            String[]program = new String[lines.size()];
        for (int i = 0;	i < lines.size(); i++)
        {			
//            program[i] = (String) lines.elementAt(i) + "\n";
            program[i] = (String) lines.get(i) + "\n";
        }
        return	program;
    }
}
