/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import com.drs.gem.injector.core.Declaration;
import com.drs.gem.injector.core.Container;

import example.modules.FifthModule;
import example.modules.FirstModule;
import example.modules.SecondModule;
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
        Declaration secondDec = new SecondDeclaration();
        Container container = Container.buildContainer(firstDec, secondDec);
        
        container.init();
        
        FifthModule fifthModule = container.getModule(FifthModule.class);
        fifthModule.printInfo();
        
        FifthModule fifthModuleSame = container.getModule(FifthModule.class);
                
        System.out.println("Modules (dif) equality: " + (fifthModule==fifthModuleSame));
        
        FirstModule first1 = container.getModule(FirstModule.class);
        FirstModule first2 = container.getModule(FirstModule.class);
        
        System.out.println("Modules (equ) equality: " + (first1==first2));
        
        System.out.println("second Mod in FIFTH (dif) equality: " + 
                (fifthModule.getSecondModule()==fifthModuleSame.getSecondModule()));
        System.out.println("fourth Mod in FIFTH (equ) equality: " + 
                (fifthModule.getFourthModule()==fifthModuleSame.getFourthModule()));
        System.out.println("sixth Mod in FIFTH (dif) equality: " + 
                (fifthModule.getSixthModule()==fifthModuleSame.getSixthModule()));
        
        SecondModule second = container.getModule(SecondModule.class);
        System.out.println(second.getInfo());
        ThirdModule third = container.getModule(ThirdModule.class);
        System.out.println(third.getInfo());
        SixthModule sixth = container.getModule(SixthModule.class);
        System.out.println(sixth.getInfo());
        long stop = System.currentTimeMillis();
        
        System.out.println("");
        System.out.println("time spent: " + (stop - start));
        
    }

}
