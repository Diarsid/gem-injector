/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import com.drs.gem.injector.core.Declaration;
import com.drs.gem.injector.core.Container;
import com.drs.gem.injector.core.Containers;

import example.modules.FifthModule;
import example.modules.SixthModule;

/**
 *
 * @author Diarsid
 */
public class Main {
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Declaration firstDec = new FirstDeclaration();        
        Container container = Containers.buildContainer("main", firstDec);
        System.out.println("[MAIN] - Container created.");
        
        //useRecursiveInjector();
        System.out.println("[MAIN] - Container initialization...");
        Containers.getContainer("main").init();
        System.out.println("[MAIN] - Container initialized.");
        System.out.println();
        
        System.out.println("[MAIN] - 5 module obtaining...");
        FifthModule fifth = Containers.getContainer("main").getModule(FifthModule.class);
        System.out.println();
        
        System.out.println("[MAIN] - 6 module obtaining...");
        SixthModule sixth = Containers.getContainer("main").getModule(SixthModule.class);
        
        boolean equ = (sixth.getFourthModule() == fifth.getFourthModule());
        System.out.println("fourth module equality: "+equ);
        long stop = System.currentTimeMillis();
        System.out.println("");
        System.out.println("time spent: " + (stop - start));
        
    }
    
    static void useRecursiveInjector(){
        Containers.getContainer("main").useRecursiveInjector();
        System.out.println("[MAIN] - Recursive injector is used.");
    }

}
