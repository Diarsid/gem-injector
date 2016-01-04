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
        System.out.println("[ 2 MODULE] - constructed.");
    }
    
    @Override
    public String getInfo(){
        System.out.println("[ 2 MODULE] - invoked.");
        return " 222222 ";
    }
}
