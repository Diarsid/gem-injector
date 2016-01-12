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

package com.drs.gem.injector.exceptions;

/**
 * This exception is thrown if some {@link
 * com.drs.gem.injector.module.GemModule module} or {@link
 * com.drs.gem.injector.module.GemModuleBuilder module builder} has dependency
 * in its constructor that is not actually a module and does not implement 
 * {@link com.drs.gem.injector.module.GemModule GemModule} interface.
 * 
 * @author Diarsid
 */
public class UndeclaredDependencyException extends RuntimeException {

    public UndeclaredDependencyException(String message) {
        super(message);
    }
}
