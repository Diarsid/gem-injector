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
 * <p>This exception can be thrown if module declaration mechanism has been
 * violated.</p>
 * 
 * <p>For more details about module declaration see {@link 
 * com.drs.gem.injector.core.Container Container}, {@link 
 * com.drs.gem.injector.core.Declaration Declaration} and 
 * {@link com.drs.gem.injector.core.GemModuleDeclaration GemModuleDeclaration}.
 * </p>
 * 
 * @author Diarsid
 */
public class ForbiddenModuleDeclarationException extends RuntimeException {

    public ForbiddenModuleDeclarationException(String message) {
        super(message);
    }
}
