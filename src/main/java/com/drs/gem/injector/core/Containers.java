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
 * This interface has been planned as connection point of all other 
 * {@link Container containers} that have been declared in this application.
 * It can be useful to transmit initialized modules, info about dependencies
 * or declaration info between different containers.
 * 
 * It doesn't have any methods or implementation because I don't have clear
 * vision of its real functionality and behavior yet.
 * 
 * @author Diarsid
 * @see Container
 */
public interface Containers {
    
}
