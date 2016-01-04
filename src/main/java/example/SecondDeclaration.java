/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import java.util.HashSet;
import java.util.Set;

import com.drs.gem.injector.core.Declaration;
import com.drs.gem.injector.core.ModuleDeclaration;
import com.drs.gem.injector.core.ModuleType;

/**
 *
 * @author Diarsid
 */
public class SecondDeclaration implements Declaration {

    SecondDeclaration() {
    }
    
    @Override
    public Set<ModuleDeclaration> getDeclaredModules(){
        Set<ModuleDeclaration> modules = new HashSet<>();
        
        modules.add(new ModuleDeclaration(
                "example.modules.FourthModule", 
                "example.modules.workers.fourth.FourthModuleImpl",
                ModuleType.PROTOTYPE));
        
        modules.add(new ModuleDeclaration(
                "example.modules.FifthModule", 
                "example.modules.workers.fifth.FifthModuleImpl",
                ModuleType.PROTOTYPE));
        
        modules.add(new ModuleDeclaration(
                "example.modules.SixthModule", 
                "example.modules.workers.sixth.SixthModuleImpl",
                ModuleType.SINGLETON));
        
        return modules;
    }
}
