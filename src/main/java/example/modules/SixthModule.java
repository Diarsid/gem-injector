/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.modules;

import com.drs.gem.injector.module.Module;

/**
 *
 * @author Diarsid
 */
public interface SixthModule extends Module {
    
    String getInfo();
    
    FourthModule getFourthModule();
}
