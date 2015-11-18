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

import com.drs.gem.injector.module.Module;

/**
 *
 * @author Diarsid
 */
public interface Container {
    
    void declareModule(String moduleInterface, String moduleImplem, ModuleType type);
    
    void init();
    
    <M extends Module> M getModule(Class<M> moduleClass);
    
    static Container buildContainer(){
        GemInjectorFactory factory = new GemInjectorFactory();
        return new ModulesContainer(factory);
    }
    
    static Container buildContainer(Declaration... declarations){
        GemInjectorFactory factory = new GemInjectorFactory();
        return new ModulesContainer(factory, declarations);
    }
}
