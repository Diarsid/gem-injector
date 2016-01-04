/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.first;

import example.modules.FirstModule;

/**
 *
 * @author Diarsid
 */
class FirstModuleImpl implements FirstModule {

    public FirstModuleImpl() {
        System.out.println("[ 1 MODULE] - constructed.");
    }
    
    @Override
    public String getInfo(){
        System.out.println("[ 1 MODULE] - invoked.");
        return " 11111 ";
    }
}
