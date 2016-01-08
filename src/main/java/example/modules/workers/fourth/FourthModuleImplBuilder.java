/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.fourth;

import com.drs.gem.injector.module.InjectedConstructor;
import com.drs.gem.injector.module.GemModuleBuilder;

import example.modules.FirstModule;
import example.modules.FourthModule;
import example.modules.ThirdModule;

/**
 *
 * @author Diarsid
 */
class FourthModuleImplBuilder implements GemModuleBuilder<FourthModule>{
    
    private ThirdModule third;
    private FirstModule first;
    
    FourthModuleImplBuilder(FirstModule firstModule) {
        this.first = firstModule;
    }

    @InjectedConstructor
    public FourthModuleImplBuilder(FirstModule first, ThirdModule third) {
        this.third = third;
        this.first = first;        
        System.out.println("[ 4 MODULE-BUILDER] - constructed.");
    }
    
    @Override 
    public FourthModule buildModule(){
        System.out.println("[ 4 MODULE-BUILDER] - 4 module building...");
        return new FourthModuleImpl(third, first);
    }
}
