/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import java.util.HashSet;
import java.util.Set;

import com.drs.gem.injector.core.Declaration;
import com.drs.gem.injector.core.GemModuleDeclaration;
import com.drs.gem.injector.core.GemModuleType;

/**
 *
 * @author Diarsid
 */
public class SecondDeclaration implements Declaration {

    SecondDeclaration() {
    }
    
    @Override
    public Set<GemModuleDeclaration> getDeclaredModules(){
        Set<GemModuleDeclaration> modules = new HashSet<>();
        
        modules.add(new GemModuleDeclaration(
                "example.modules.FourthModule", 
                "example.modules.workers.fourth.FourthModuleImpl",
                GemModuleType.PROTOTYPE));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.FifthModule", 
                "example.modules.workers.fifth.FifthModuleImpl",
                GemModuleType.PROTOTYPE));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.SixthModule", 
                "example.modules.workers.sixth.SixthModuleImpl",
                GemModuleType.SINGLETON));
        
        return modules;
    }
}
