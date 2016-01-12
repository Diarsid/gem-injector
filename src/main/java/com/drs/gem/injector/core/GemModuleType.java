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

/**
 * <p>Enum describes possible module types.</p>
 * 
 * <p>Singleton module type means that there can be only one 
 * instance of this module in {@link Container}. <br>
 * In case of singleton {@link Container#getModule(java.lang.Class)
 * Container.getModule()} will always return the same object of 
 * specified module interface.</p>
 * 
 * <p>Prototype module type means that every invocation of {@link 
 * Container#getModule(java.lang.Class)
 * Container.getModule()} will always create new object of specified 
 * module interface.</p>
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.module.GemModule
 */
public enum GemModuleType {
    SINGLETON,
    PROTOTYPE
}
