/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.third;

import com.drs.gem.injector.module.InjectedConstructor;

import example.modules.FirstModule;
import example.modules.SecondModule;
import example.modules.ThirdModule;

/**
 *
 * @author Diarsid
 */
class ThirdModuleImpl implements ThirdModule {
    
    private final FirstModule first;
    private final SecondModule second;

    public ThirdModuleImpl() {
        this.first = null;
        this.second = null;
    }
    
    public ThirdModuleImpl(FirstModule first) {
        this.first = first;
        this.second = null;
    }
    
    @InjectedConstructor
    ThirdModuleImpl(FirstModule first, SecondModule second) {
        this.first = first;
        this.second = second;
        System.out.println("[ 3 MODULE] - constructed.");
    }
    
    @Override
    public String getInfo(){
        System.out.println("[ 3 MODULE] - invoked.");
        return " 33333 " + first.getInfo() + second.getInfo();
    }
}
