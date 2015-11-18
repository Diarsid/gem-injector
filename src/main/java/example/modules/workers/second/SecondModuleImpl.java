/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.second;

import example.modules.SecondModule;

/**
 *
 * @author Diarsid
 */
class SecondModuleImpl implements SecondModule {

    SecondModuleImpl() {
    }
    
    @Override
    public String getInfo(){
        return "begin-in-2!";
    }
}
