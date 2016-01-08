/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.fifth;

import com.drs.gem.injector.module.InjectedConstructor;
import com.drs.gem.injector.module.GemModuleBuilder;

import example.modules.FifthModule;
import example.modules.FourthModule;
import example.modules.SecondModule;
import example.modules.SixthModule;

/**
 *
 * @author Diarsid
 */
class FifthModuleImplBuilder implements GemModuleBuilder<FifthModule>{
    
    private SecondModule second;
    private SixthModule sixth;
    private FourthModule fourth;

    @InjectedConstructor
    FifthModuleImplBuilder(SecondModule second, SixthModule sixth, FourthModule fourth) {
        this.second = second;
        this.sixth = sixth;
        this.fourth = fourth;
        System.out.println("[ 5 MODULE-BUILDER] - constructed.");
    }    
    
    @Override
    public FifthModule buildModule(){
        System.out.println("[ 5 MODULE-BUILDER] - 5 module building...");
        return new FifthModuleImpl(second, fourth, sixth);
    }
}
