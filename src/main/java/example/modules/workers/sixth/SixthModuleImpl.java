/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.sixth;

import com.drs.gem.injector.module.InjectedConstructor;

import example.modules.FourthModule;
import example.modules.SixthModule;

/**
 *
 * @author Diarsid
 */
class SixthModuleImpl implements SixthModule {
    
    private final FourthModule fourth;
    
    SixthModuleImpl() {
        this.fourth = null;
    }
    
    @InjectedConstructor
    SixthModuleImpl(FourthModule fourth) {
        this.fourth = fourth;
        System.out.println("[ 6 MODULE] - constructed.");
    }
    
    @Override
    public String getInfo(){
        System.out.println("[ 6 MODULE] - invoked.");
        return " 6666 " + fourth.getInfo();
    }
    
    @Override
    public FourthModule getFourthModule() {
        return fourth;
    }
}
