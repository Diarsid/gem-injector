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

import java.util.Objects;

/**
 *
 * @author Diarsid
 */
public class ModuleDeclaration {
    
    private final String moduleInterfaceName;
    private final String moduleBuildClassName;
    private final ModuleType moduleType;

    public ModuleDeclaration(String moduleName, String moduleBuildClass, ModuleType type) {
        this.moduleInterfaceName = moduleName;
        this.moduleBuildClassName = moduleBuildClass;
        this.moduleType = type;
    }

    public String getModuleInterfaceName() {
        return moduleInterfaceName;
    }

    public String getModuleBuildClassName() {
        return moduleBuildClassName;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.moduleInterfaceName);
        hash = 89 * hash + Objects.hashCode(this.moduleBuildClassName);
        hash = 89 * hash + Objects.hashCode(this.moduleType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModuleDeclaration other = (ModuleDeclaration) obj;
        if (!Objects.equals(this.moduleInterfaceName, other.moduleInterfaceName)) {
            return false;
        }
        if (!Objects.equals(this.moduleBuildClassName, other.moduleBuildClassName)) {
            return false;
        }
        if (this.moduleType != other.moduleType) {
            return false;
        }
        return true;
    }
}
