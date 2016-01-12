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
package com.drs.gem.injector.module;

/**
 * <p>
 * This is the marker interface for defining other interfaces as modules in
 * order to use them within this container. Modules themselves, their
 * interactions and dependencies on another modules are 
 * key work concept of this container.</p>
 * 
 * <p>Only instances implementing interface marked as module can be processed, 
 * initialized and injected by this container.</p>
 * 
 * <p> There are several key requirements to consider a bunch of classes as 
 * module in the context of this container.</p>
 * <ul>
 * <li>Module considering as separate working entity must have its own 
 * interface that describes all module's functionality that is available for 
 * outer world;</li>
 * <li>Obviously, there must be a class that implements this interface;</li>
 * <li>Interface of this module must extend this {@link GemModule} 
 * interface;</li>
 * <li>Module must be declared in {@link com.drs.gem.injector.core.Container 
 * Container} instance.</li>
 * </ul>
 * <p>For more details about module declaration see {@link 
 * com.drs.gem.injector.core.Container Container}, {@link 
 * com.drs.gem.injector.core.Declaration Declaration} and 
 * {@link com.drs.gem.injector.core.GemModuleDeclaration GemModuleDeclaration}.
 * </p>
 * 
 * 
 * <p><b>Important note.</b><br>
 * Module implementation class can have any number of constructors with 
 * different parameters. But this container accepts only one constructor 
 * as source for detection of this module dependencies. That is way if 
 * module implementation class have more than one constructor, one of them
 * must be marked with {@link InjectedConstructor @InjectedConstructor}.
 * This defines which constructor the container should use to collect 
 * dependencies and instantiate module object. But if there is only one
 * constructor in class, it is not need to annotate it with mentioned 
 * annotation.<br>
 * The same rules are applicable for {@link GemModuleBuilder module builder}
 * classes. 
 * </p>
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.core.Container
 * @see com.drs.gem.injector.core.Declaration
 * @see com.drs.gem.injector.core.GemModuleDeclaration
 */
public interface GemModule {
    
}
