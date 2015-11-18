/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.third;

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

    ThirdModuleImpl(FirstModule first, SecondModule second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public String getInfo(){
        return "great-from-3 " + first.getInfo() + second.getInfo();
    }
}
