/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.fourth;

import example.modules.FirstModule;
import example.modules.FourthModule;
import example.modules.ThirdModule;

/**
 *
 * @author Diarsid
 */
class FourthModuleImpl implements FourthModule {
    
    private final FirstModule first;
    private final ThirdModule third;

    FourthModuleImpl(ThirdModule third, FirstModule first) {
        this.first = first;
        this.third = third;
        System.out.println("[ 4 MODULE] - constructed.");
    }    
    
    @Override
    public String getInfo(){
        System.out.println("[ 4 MODULE] - invoked.");
        return " 44444 " + third.getInfo() + first.getInfo();
    }
}
