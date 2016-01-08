/*
 * Copyright (C) 2015 Diarsid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.drs.gem.injector.core;

import java.util.Set;

/**
 * Interface that can be used for modules declaration as alternative way to
 * {@link com.drs.gem.injector.core.Container#declareModule(java.lang.String, 
 * java.lang.String, com.drs.gem.injector.core.GemModuleType) Container.declareModule()}.
 * 
 * It may look like this:
 * 
 * <pre><code>
 * class MyDeclaration implements Declaration {
 *
 *    MyDeclaration() {
 *    }
 *   
 *    &#64;Override
 *    public Set&#60;GemModuleDeclaration&#62; getDeclaredModules(){
 *        Set&#60;GemModuleDeclaration&#62; modules = new HashSet&#60;&#62;();
       
        modules.add(new GemModuleDeclaration(
                "example.modules.FirstModule", 
                "example.modules.workers.first.FirstModuleImpl",
                ModuleType.PROTOTYPE));
        
        modules.add(new GemModuleDeclaration(
                "example.modules.SecondModule", 
                "example.modules.workers.second.SecondModuleImpl",
                ModuleType.SINGLETON));
        
        return modules;
    }    
 }
 </code></pre>
 * 
 * @author Diarsid
 */
public interface Declaration {
    
    /**
     * Returns set of {@link com.drs.gem.injector.core.GemModuleDeclaration 
     * GemModuleDeclaration} objects. Each object describes one declared module 
     * that should be processed by appropriate 
     * {@link com.drs.gem.injector.core.Container Container} instance.
     * 
     * @return  set of GemModuleDeclaration objects.
     * @see     com.drs.gem.injector.core.Container
     * @see     com.drs.gem.injector.core.GemModuleDeclaration
     */
    Set<GemModuleDeclaration> getDeclaredModules();
}
