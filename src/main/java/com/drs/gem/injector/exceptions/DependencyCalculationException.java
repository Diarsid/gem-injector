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
 * <p>This exception can be thrown by inner container class that contains 
 * module dependencies and used by container. 
 * It is thrown if module dependencies have not been 
 * calculated before container begins the injection of dependencies.</p>
 * 
 * <p>In normal case, this exception would not be thrown.<br> If this exception
 * occurs it means that inner container work algorithm has been broken.</p>
 * 
 * @author Diarsid
 */
public class DependencyCalculationException extends RuntimeException {

    public DependencyCalculationException(String message) {
        super(message);
    }
}
