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
 * Marker interface to define concrete interfaces as modules to use them 
 * within a container. To become module in the sense of this container 
 * and to become available for processing in container any interface that 
 * has been designed as module has to extend this interface.
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.core.Container
 */
public interface Module {    
}
