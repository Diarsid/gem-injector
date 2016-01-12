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
 * <p>This exception can be thrown if class specified as module 
 * does not implement {@link com.drs.gem.injector.module.GemModule
 * GemModule} interface or does not satisfy constructor requirements.</p>
 * 
 * <p>See {@link com.drs.gem.injector.module.GemModule
 * GemModule} for more details about constructor requirements for 
 * module object.</p>
 * 
 * @author Diarsid
 */
public class InvalidModuleImplementationException extends RuntimeException{
    
    public InvalidModuleImplementationException(String message){
        super(message);
    }
}
