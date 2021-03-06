/*
 * Copyright (C) 2016 Diarsid
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
 * If there is no {@link com.drs.gem.injector.core.Container Container} with 
 * specified 
 * name when {@link com.drs.gem.injector.core.GemInjector#getContainer(String) 
 * GemInjector.getContainer()} method is invoked  
 * this exception is thrown.
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.core.Container
 * @see com.drs.gem.injector.core.GemInjector
 */
public class NoSuchContainerException extends RuntimeException {
    
    public NoSuchContainerException(String message) {
        super(message);
    }
}
