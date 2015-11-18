/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.sixth;

import example.modules.FourthModule;
import example.modules.SixthModule;

/**
 *
 * @author Diarsid
 */
class SixthModuleImpl implements SixthModule {
    
    private final FourthModule fourth;
    
    SixthModuleImpl(FourthModule fourth) {
        this.fourth = fourth;
    }
    
    @Override
    public String getInfo(){
        return "Hello-from-6! " + fourth.getInfo();
    }
}
