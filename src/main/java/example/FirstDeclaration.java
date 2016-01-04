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
class FirstDeclaration implements Declaration {

    FirstDeclaration() {
    }
    
    @Override
    public Set<ModuleDeclaration> getDeclaredModules(){
        Set<ModuleDeclaration> modules = new HashSet<>();
        
        modules.add(new ModuleDeclaration(
                "example.modules.FirstModule", 
                "example.modules.workers.first.FirstModuleImpl",
                ModuleType.SINGLETON));
        
        modules.add(new ModuleDeclaration(
                "example.modules.SeventhModule", 
                "example.modules.workers.seventh.SeventhModuleImpl",
                ModuleType.PROTOTYPE));
        
        modules.add(new ModuleDeclaration(
                "example.modules.EightModule", 
                "example.modules.workers.eight.EightModuleWorker",
                ModuleType.PROTOTYPE));
        
        modules.add(new ModuleDeclaration(
                "example.modules.SecondModule", 
                "example.modules.workers.second.SecondModuleImpl",
                ModuleType.PROTOTYPE));
        
        modules.add(new ModuleDeclaration(
                "example.modules.ThirdModule", 
                "example.modules.workers.third.ThirdModuleImpl",
                ModuleType.SINGLETON));
        
        modules.add(new ModuleDeclaration(
                "example.modules.FourthModule", 
                "example.modules.workers.fourth.FourthModuleImpl",
                ModuleType.PROTOTYPE));
        
        modules.add(new ModuleDeclaration(
                "example.modules.FifthModule", 
                "example.modules.workers.fifth.FifthModuleImpl",
                ModuleType.SINGLETON));
        
        modules.add(new ModuleDeclaration(
                "example.modules.SixthModule", 
                "example.modules.workers.sixth.SixthModuleImpl",
                ModuleType.PROTOTYPE));
        
        return modules;
    }
}
