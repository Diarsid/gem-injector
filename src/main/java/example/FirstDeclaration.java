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
class FirstDeclaration implements Declaration {

    FirstDeclaration() {
    }
    
    @Override
    public Set<GemModuleDeclaration> getDeclaredModules(){
        Set<GemModuleDeclaration> modules = new HashSet<>();
        
        modules.add(new GemModuleDeclaration(
                "example.modules.FirstModule", 
                "example.modules.workers.first.FirstModuleImpl",
                GemModuleType.SINGLETON));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.SeventhModule", 
                "example.modules.workers.seventh.SeventhModuleImpl",
                GemModuleType.PROTOTYPE));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.EightModule", 
                "example.modules.workers.eight.EightModuleWorker",
                GemModuleType.SINGLETON));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.SecondModule", 
                "example.modules.workers.second.SecondModuleImpl",
                GemModuleType.PROTOTYPE));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.ThirdModule", 
                "example.modules.workers.third.ThirdModuleImpl",
                GemModuleType.PROTOTYPE));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.FourthModule", 
                "example.modules.workers.fourth.FourthModuleImpl",
                GemModuleType.SINGLETON));
        
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
