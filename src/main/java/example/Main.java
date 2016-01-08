/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import com.drs.gem.injector.core.Declaration;
import com.drs.gem.injector.core.Container;
import com.drs.gem.injector.core.GemInjector;

import example.modules.FifthModule;
import example.modules.SixthModule;
import example.modules.ThirdModule;

/**
 *
 * @author Diarsid
 */
public class Main {
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Declaration firstDec = new FirstDeclaration();        
        Container container = GemInjector.buildContainer("main", firstDec);
        System.out.println("[MAIN] - Container created.");
        
        //useRecursiveInjector();
        System.out.println("[MAIN] - Container initialization...");
        long startInit = System.currentTimeMillis();
        GemInjector.getContainer("main").init();
        long stopInit = System.currentTimeMillis();
        System.out.println("[MAIN] - Container initialized.");
        System.out.println();
        
        System.out.println("[MAIN] - 5 module obtaining...");
        FifthModule fifth = GemInjector.getContainer("main").getModule(FifthModule.class);
        System.out.println();
        
        System.out.println("[MAIN] - 6 module obtaining...");
        SixthModule sixth = GemInjector.getContainer("main").getModule(SixthModule.class);
        
        boolean equ = (sixth.getFourthModule() == fifth.getFourthModule());
        System.out.println("fourth module equality: "+equ);
        
        ThirdModule third1 = GemInjector.getContainer("main").getModule(ThirdModule.class);
        ThirdModule third2 = GemInjector.getContainer("main").getModule(ThirdModule.class);
        ThirdModule third3 = GemInjector.getContainer("main").getModule(ThirdModule.class);
        ThirdModule third4 = GemInjector.getContainer("main").getModule(ThirdModule.class);
        FifthModule fifth2 = GemInjector.getContainer("main").getModule(FifthModule.class);
        
        long stop = System.currentTimeMillis();
        System.out.println("");
        System.out.println("[MAIN] container init time : " + (stopInit - startInit));
        System.out.println("[MAIN] total time : " + (stop - start));
        
    }
    
    static void useRecursiveInjector(){
        GemInjector.getContainer("main").useRecursiveInjector();
        System.out.println("[MAIN] - Recursive injector is used.");
    }

}
