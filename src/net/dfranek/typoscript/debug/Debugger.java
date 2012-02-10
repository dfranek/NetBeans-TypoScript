package net.dfranek.typoscript.debug;

import java.lang.reflect.Method;

/**
 *
 * @author eric
 */
public class Debugger {
    
    private static boolean debug = true;
    
    
    
    public static void pr(Object o){
        if(debug){            
            try{
                System.out.println("Start of Debug-output");
                System.out.println(o);
                System.out.println("End of Debug-output");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
