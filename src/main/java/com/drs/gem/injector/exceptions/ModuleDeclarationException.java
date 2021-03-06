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
 * <p>This exception is thrown when there are errors during 
 * {@link com.drs.gem.injector.core.Container Container} creation
 * due to incorrect module declaration.</p>
 * 
 * <p>For more details about module declaration see {@link 
 * com.drs.gem.injector.core.Container Container}, {@link 
 * com.drs.gem.injector.core.Declaration Declaration} and 
 * {@link com.drs.gem.injector.core.GemModuleDeclaration GemModuleDeclaration}.
 * </p>
 * 
 * @author Diarsid
 */
public class ModuleDeclarationException extends RuntimeException{
    
    public ModuleDeclarationException(String message){
        super(message);
    }
}
