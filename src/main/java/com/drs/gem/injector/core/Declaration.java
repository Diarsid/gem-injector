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
 * Interface that could be used for modules declaration as alternative way to
 * {@link com.drs.gem.injector.core.Container#declareModule(java.lang.String, 
 * java.lang.String, com.drs.gem.injector.core.ModuleType) Container.declareModule(). }
 * 
 * @author Diarsid
 */
public interface Declaration {
    
    /**
     * Returns set of {@link com.drs.gem.injector.core.ModuleDeclaration 
     * ModuleDeclaration} objects. Each object describes one declared module 
     * that should be processed by appropriate 
     * {@link com.drs.gem.injector.core.Container Container} instance.
     * 
     * @return  set of ModuleDeclaration objects.
     * @see     com.drs.gem.injector.core.Container
     * @see     com.drs.gem.injector.core.ModuleDeclaration
     */
    Set<ModuleDeclaration> getDeclaredModules();
}
