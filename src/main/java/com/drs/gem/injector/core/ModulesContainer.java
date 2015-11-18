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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.drs.gem.injector.exceptions.ContainerInitializationException;
import com.drs.gem.injector.exceptions.ForbiddenModuleDeclarationException;
import com.drs.gem.injector.exceptions.InvalidBuilderDeclarationException;
import com.drs.gem.injector.exceptions.InvalidModuleImplementationException;
import com.drs.gem.injector.exceptions.InvalidModuleInterfaceException;
import com.drs.gem.injector.exceptions.ModuleDeclarationException;
import com.drs.gem.injector.module.Module;
import com.drs.gem.injector.module.ModuleBuilder;

/**
 *
 * @author Diarsid
 */
public class ModulesContainer implements Container, ModulesInfo {
    
    private final Map<Class, Class> declaredModules;
    private final Map<Class, Constructor> constructors;
    private final Map<Class, ModuleType> moduleTypes;
    private final Map<Class, Module> singletonModules;
    private final PriorityQueue<InjectionPriority> injectionPriorities;    
    private final boolean constructorDeclaration;
    private boolean wasContainerInitialized;
    private final GemInjectorFactory factory;
    

    ModulesContainer(GemInjectorFactory factory) {
        this.declaredModules = new HashMap<>();
        this.constructors = new HashMap<>();
        this.moduleTypes = new HashMap<>();
        this.singletonModules = new HashMap<>();
        this.injectionPriorities = new PriorityQueue<>();
        this.constructorDeclaration = false;
        this.wasContainerInitialized = false;
        this.factory = factory;
    }
    
    ModulesContainer(GemInjectorFactory factory, Declaration... declarations) {
        this.declaredModules = new HashMap<>();
        this.constructors = new HashMap<>();
        this.moduleTypes = new HashMap<>();
        this.singletonModules = new HashMap<>();
        this.injectionPriorities = new PriorityQueue<>();
        this.constructorDeclaration = true;  
        this.wasContainerInitialized = false;
        this.factory = factory;
        processDeclarations(declarations);
    }
    
    private void processDeclarations(Declaration[] declarations){
        for (Declaration dec : declarations){
            for (ModuleDeclaration moduleDec : dec.getDeclaredModules()){
                String moduleInterface = moduleDec.getModuleInterfaceName();
                String moduleBuildClass = moduleDec.getModuleBuildClassName();
                ModuleType type = moduleDec.getModuleType();
                parseModuleDeclaration(moduleInterface, moduleBuildClass, type);
            }            
        }
    }
    
    @Override
    public void declareModule(
            String moduleInterfaceName, String moduleImplemName, ModuleType type){
        if (constructorDeclaration){
            throw new ForbiddenModuleDeclarationException(
                    "This container use constructor module declaration via " +
                    Declaration.class.getCanonicalName() + 
                    " implementation. Explicit module declaration via" +
                    " Modules::declareModule(String, String) is not allowed.");
        }
        if ( ! singletonModules.isEmpty()){
            throw new ForbiddenModuleDeclarationException(
                    "Modules has been initialized already. Additional module " +
                    "declaration is not allowed after initialization.");
        }
        parseModuleDeclaration(moduleInterfaceName, moduleImplemName, type);
    }
           
    private void parseModuleDeclaration(
            String moduleInterfaceName, String moduleImplemName, ModuleType type){
        
        Class moduleInterface = getClassByName(moduleInterfaceName);
        Class moduleBuildClass = getClassByName(moduleImplemName);        
        
        verifyModuleInterface(moduleInterface);
        verifyModuleImplementation(moduleInterface, moduleBuildClass);
        Class moduleBuilder = ifModuleHasBuilder(moduleImplemName);
        if ( moduleBuilder != null ) {
            verifyModuleBuilder(moduleBuilder);
            moduleBuildClass = moduleBuilder;
        }
        
        if (moduleBuildClass != null & moduleInterface != null){
            declaredModules.put(moduleInterface, moduleBuildClass);
            moduleTypes.put(moduleInterface, type);
        } else {
            throw new ModuleDeclarationException("Module declaration failure.");
        }
    }
    
    private Class ifModuleHasBuilder(String moduleImplemName){
        String moduleBuilderName = moduleImplemName + "Builder";
        try {
            Class builderClass = Class.forName(moduleBuilderName);
            return builderClass;
        } catch (ClassNotFoundException e){
            return null;
        }
    }
    
    private void verifyModuleBuilder(Class builderClass){
        if ( ! ModuleBuilder.class.isAssignableFrom(builderClass) ){
            throw new InvalidBuilderDeclarationException(
                    "Invalid module builder implementation: " + 
                    builderClass.getCanonicalName() + 
                    "does not implement " + 
                    ModuleBuilder.class.getCanonicalName());
        }
    }
    
    private void verifyModuleInterface(Class moduleInterface){
        if ( ! moduleInterface.isInterface()){
            throw new ModuleDeclarationException(
                    "Incorrect module declaration: class " + 
                    moduleInterface.getCanonicalName() +
                    " is not interface.");
        } else if ( ! Module.class.isAssignableFrom(moduleInterface)){
            throw new InvalidModuleInterfaceException(
                    "Invalid Module interface: " + 
                    moduleInterface.getCanonicalName() + 
                    " does not implement " + Module.class.getCanonicalName() + ".");
        }
    }
    
    private void verifyModuleImplementation(Class moduleInterface, Class moduleImplem){
        if ( ! moduleInterface.isAssignableFrom(moduleImplem)){
            throw new InvalidModuleImplementationException(
                    "Invalid module implementation: " + 
                    moduleImplem.getCanonicalName() + 
                    "does not implement " + 
                    moduleInterface.getCanonicalName() + ".");
        }
    }
    
    private Class getClassByName(String className){
        Class classImpl;
        try {
            classImpl = Class.forName(className);
            return classImpl;
        } catch (ClassNotFoundException e){
            throw new ModuleDeclarationException(
                    "Incorrect module declaration: class " +
                    className + " does not exist.");
        }
    }
    
    @Override
    public void init(){     
        if (declaredModules.isEmpty()){
            throw new ModuleDeclarationException(
                    "No modules have been declared.");
        }
        collectConstructors();
        rateConstructorsByPriority();
        injectSingletons();
        wasContainerInitialized = true;
    }    
    
    private void collectConstructors(){
        for(Map.Entry<Class, Class> pair : declaredModules.entrySet()){
            Class moduleInterface = pair.getKey();
            Class moduleBuildClass = pair.getValue();
            
            Constructor[] moduleConss = moduleBuildClass.getDeclaredConstructors();
            
            if (moduleConss.length != 1){
                if (moduleBuildClass.getSimpleName().contains("ModuleBuilder")){
                    throw new InvalidBuilderDeclarationException(
                            "Invalid module builder implementation: " + 
                            moduleBuildClass.getCanonicalName() + 
                            " has more than one declared constructor.");
                } else {
                    throw new InvalidModuleImplementationException(
                            "Invalid module implementation: " + 
                            moduleBuildClass.getCanonicalName() + 
                            " has more than one declared constructor.");
                }
            }
            
            Constructor moduleCon = moduleConss[0];
            moduleCon.setAccessible(true);
            constructors.put(moduleInterface, moduleCon);
        }
    }
    
    private void rateConstructorsByPriority(){
        InjectionPriorityCalculator priorityCalculator = 
                factory.buildCalculator((ModulesInfo) this);
        for(Map.Entry<Class, Class> pair : declaredModules.entrySet()){
            Class moduleInterface = pair.getKey();
            Constructor buildCons = constructors.get(moduleInterface);
            ModuleType type = moduleTypes.get(moduleInterface);
            
            InjectionPriority ip = factory.newInjectionPriority(
                    moduleInterface, buildCons, type);
            int priority = priorityCalculator.calculatePriority(ip);
            ip.setPriority(priority);          
            injectionPriorities.offer(ip);
        }
    }
    
    private void injectSingletons(){
        Injector injector = factory.buildInjector((ModulesInfo) this);
        for (InjectionPriority ip : getPriorities()){
            if (ip.getType().equals(ModuleType.SINGLETON)){
                Constructor buildCons = ip.getConstructor();        
                Class moduleInterface = ip.getModuleInterface();
                
                Module module = injector.newModule(buildCons, moduleInterface);
                singletonModules.put(moduleInterface, module);
            }
        }
    }
    
    @Override
    public <M extends Module> M getModule(Class<M> moduleInterface){      
        if ( ! wasContainerInitialized){
            throw new ContainerInitializationException(
                    "Modules::init() was not invoked.");
        }
        if (isModuleSingleton(moduleInterface)){
            Module uncastedModule = singletonModules.get(moduleInterface);
            M module = moduleInterface.cast(uncastedModule);
            return module;
        } else {
            Injector injector = factory.buildInjector((ModulesInfo) this);
            Constructor buildCons = constructors.get(moduleInterface);
            Module uncastedModule = injector.newModule(buildCons, moduleInterface);
            M module = moduleInterface.cast(uncastedModule);
            return module;
        }
    }
    
    @Override
    public boolean isModuleSingleton(Class moduleInterface){
        return (moduleTypes.get(moduleInterface).equals(ModuleType.SINGLETON));
    }
    
    @Override
    public Constructor getConstructor(Class moduleInterface){
        return constructors.get(moduleInterface);
    }
    
    @Override
    public boolean ifConstructorExists(Class moduleInterface){
        return constructors.containsKey(moduleInterface);
    }
    
    @Override
    public Map<Class, Module> getSingletons(){
        return singletonModules;
    }
    
    @Override
    public InjectionPriority[] getPriorities(){
        InjectionPriority[] priorities = injectionPriorities
                .toArray(new InjectionPriority[injectionPriorities.size()]);
        Arrays.sort(priorities);
        return priorities;
    }
}
