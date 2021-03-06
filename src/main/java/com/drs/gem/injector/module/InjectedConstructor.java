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
package com.drs.gem.injector.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for marking module constructor whose parameters will be treated as
 * set of module dependencies. This constructor will be used by Container to
 * inject collected dependencies and initialize module.
 * Container will resolve only those dependencies that have been declared in
 * constructor marked with this annotation.</p>
 * 
 * <p>It is not permitted to mark more than one constructor in module class with
 * this annotation.</p>
 * 
 * @author Diarsid
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface InjectedConstructor {    
}
