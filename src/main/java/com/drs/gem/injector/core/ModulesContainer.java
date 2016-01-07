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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.drs.gem.injector.exceptions.ContainerInitializationException;
import com.drs.gem.injector.exceptions.ForbiddenModuleDeclarationException;
import com.drs.gem.injector.exceptions.ModuleDeclarationException;
import com.drs.gem.injector.exceptions.UndeclaredDependencyException;
import com.drs.gem.injector.module.Module;

/**
 * Pivotal class represents container itself. Receives information about
 * modules, produces module instances and relay information about modules 
 * into {@link com.drs.gem.injector.core.RecursiveInjector RecursiveInjector}.
 * 
 * @author Diarsid
 * @see com.drs.gem.injector.core.Container
 * @see com.drs.gem.injector.core.ModulesInfo
 */
class ModulesContainer implements Container, ModulesInfo {
    
    /**
     * Map<Class, Class> contains entries where key is module interface 
     * class object and value is class whose is responsible for
     * module implementation class object creation. It can be module
     * implementation class itself or its appropriate {@link 
     * com.drs.gem.injector.module.ModuleBuilder ModuleBuilder} class.
     * 
     * @see com.drs.gem.injector.module.ModuleBuilder
     */
    private final Map<Class, Class> declaredModules;
    
    /**
     * Map<Class, Constructor> contains entries where key is module interface 
     * class object and value is constructor of appropriate module 
     * implementation class. It can be ModuleBuilder constructor as well.
     * 
     * @see com.drs.gem.injector.module.ModuleBuilder
     */
    private final Map<Class, Constructor> constructors;
    
    /**
     * Map<Class, ModuleType> contains entries where key is module interface 
     * class object and value is {@link 
     * com.drs.gem.injector.core.ModuleType ModuleType} object.
     * 
     * @see com.drs.gem.injector.core.ModuleType
     */
    private final Map<Class, ModuleType> moduleTypes;
    
    /**
     * Map<Class, Module> contains entries where key is module interface 
     * class object with Module Type SINGLETON and value is corresponding
     * module instance which is fully initialized and ready to work.
     */
    private final Map<Class, Module> singletonModules;
    
    /**
     * List<ModuleMetaData> that contains ModuleMetaData 
     * objects sorted with natural ordering. See {@link 
     * com.drs.gem.injector.core.ModuleMetaData ModuleMetaData}
     * for more details.
     * 
     * @see com.drs.gem.injector.core.ModuleMetaData
     */
    private List<ModuleMetaData> injectionPriorities;
    
    /**
     * 
     */
    private final Map<Class, ModuleMetaData> moduleDatas;
    
    /**
     * TRUE if modules were declared via {@link com.drs.gem.injector.core.Declaration
     * Declaration} objects. FALSE otherwise.
     * 
     * @see com.drs.gem.injector.core.Declaration
     */
    private final boolean constructorDeclaration;  
    
    /**
     * Simple factory object is used to avoid "new" operator inside 
     * the container instance methods.
     * 
     * @see com.drs.gem.injector.core.GemInjectorFactory
     */
    private final GemInjectorFactory factory;
    
    /**
     * Object contains helper methods to verify module interfaces, 
     * implementations, builders, constructors.
     * 
     * @see com.drs.gem.injector.core.ContainerHelper
     */
    private final ContainerHelper helper;
    
    /**
     * Indicates if container should use {@link com.drs.gem.injector.core.RecursiveInjector
     * RecursiveInjector} for module instantiation.
     * Default value is false, thus default injector is {@link 
     * com.drs.gem.injector.core.LoopInjector LoopInjector}.
     * 
     * @see com.drs.gem.injector.core.RecursiveInjector
     * @see com.drs.gem.injector.core.LoopInjector
     */
    private boolean useRecursiveInjector;
    
    

    ModulesContainer(GemInjectorFactory factory) {
        this.declaredModules = new HashMap<>();
        this.constructors = new HashMap<>();
        this.moduleTypes = new HashMap<>();
        this.singletonModules = new HashMap<>();
        this.injectionPriorities = null;
        this.moduleDatas = new HashMap<>();
        this.constructorDeclaration = false;
        this.useRecursiveInjector = false;
        this.factory = factory;
        this.helper = factory.buildHelper();
    }
    
    ModulesContainer(GemInjectorFactory factory, Declaration... declarations) {
        this.declaredModules = new HashMap<>();
        this.constructors = new HashMap<>();
        this.moduleTypes = new HashMap<>();
        this.singletonModules = new HashMap<>();
        this.injectionPriorities = null;
        this.moduleDatas = new HashMap<>();
        this.constructorDeclaration = true;
        this.useRecursiveInjector = false;
        this.factory = factory;
        this.helper = factory.buildHelper();
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
    
    /**
     * Overrides abstract method in {@link com.drs.gem.injector.core.Container
     * Container} interface. See full method description in Container interface.
     * 
     * @see com.drs.gem.injector.core.Container
     */    
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
        if ( injectionPriorities != null ){
            throw new ForbiddenModuleDeclarationException(
                    "Modules has been initialized already. Additional module " +
                    "declaration is not allowed after initialization.");
        }
        parseModuleDeclaration(moduleInterfaceName, moduleImplemName, type);
    }
    
    /**
     * Processes all necessary information about module from String parameters,
     * verifies it and saves in container.
     * 
     * @param moduleInterfaceName   module interface canonical name
     * @param moduleImplemName      module implementation class canonical name
     * @param type                  module type singleton or prototype
     */
    private void parseModuleDeclaration(
            String moduleInterfaceName, String moduleImplemName, ModuleType type){
        
        Class moduleInterface = helper.getClassByName(moduleInterfaceName);
        Class moduleBuildClass = helper.getClassByName(moduleImplemName);        
        
        helper.verifyModuleInterface(moduleInterface);
        helper.verifyModuleImplementation(moduleInterface, moduleBuildClass);
        Class moduleBuilder = helper.ifModuleHasBuilder(moduleImplemName);
        if ( moduleBuilder != null ) {
            helper.verifyModuleBuilder(moduleBuilder);
            moduleBuildClass = moduleBuilder;
        }
        
        if (moduleBuildClass != null & moduleInterface != null){
            declaredModules.put(moduleInterface, moduleBuildClass);
            moduleTypes.put(moduleInterface, type);
        } else {
            throw new ModuleDeclarationException("Module declaration failure.");
        }
    }   
    
    @Override
    public void useRecursiveInjector(){
        useRecursiveInjector = true;
    }
    
    /**
     * Overrides abstract method in {@link com.drs.gem.injector.core.Container
     * Container} interface. See full method description in Container interface.
     * 
     * @see com.drs.gem.injector.core.Container
     */ 
    @Override
    public void init(){     
        if (declaredModules.isEmpty()){
            throw new ModuleDeclarationException(
                    "No modules have been declared.");
        }
        collectConstructors();
        initializeModuleMetaDatas();
        rateModulesByInjectionPriority();
        injectSingletons();
    }    
    
    /**
     * Collects all constructors of all declared module and saves them
     * in container for further object instantiation.
     */
    private void collectConstructors(){
        for(Map.Entry<Class, Class> pair : declaredModules.entrySet()){
            Class moduleInterface = pair.getKey();
            Class moduleBuildClass = pair.getValue();
            
            Constructor[] moduleConss = moduleBuildClass.getDeclaredConstructors();            
            Constructor moduleCon = helper.resolveModuleConstructors(moduleConss);
            moduleCon.setAccessible(true);
            constructors.put(moduleInterface, moduleCon);
        }
    }
    
    private void initializeModuleMetaDatas() {
        for(Map.Entry<Class, Class> pair : declaredModules.entrySet()) {
            Class moduleInterface = pair.getKey();
            Constructor buildCons = constructors.get(moduleInterface);
            ModuleType type = moduleTypes.get(moduleInterface);
            
            ModuleMetaData metaData = factory.buildMetaData(
                    moduleInterface, buildCons, type);
            
            moduleDatas.put(moduleInterface, metaData);
        }
    }
    
    /**
     * Creates new {@link com.drs.gem.injector.core.ModuleMetaData 
     * ModuleMetaData} objects, calculates their actual priority using
     * {@link com.drs.gem.injector.core.InjectionPriorityCalculator 
     * InjectionPriorityCalculator} sort them by their natural ordering
     * and stores them in ModuleMetaData[] container field.
     * 
     * @see com.drs.gem.injector.core.ModuleMetaData
     * @see com.drs.gem.injector.core.InjectionPriorityCalculator
     */
    private void rateModulesByInjectionPriority() {
        InjectionPriorityCalculator priorityCalculator = 
                factory.buildCalculator((ModulesInfo) this);
        List<ModuleMetaData> metaDatas = new ArrayList<>();
        
        for(Map.Entry<Class, ModuleMetaData> pair : moduleDatas.entrySet()) {                       
            ModuleMetaData metaData = pair.getValue();
            priorityCalculator.calculateAndSetPriority(metaData);
            
            metaDatas.add(metaData);
        }
        
        Collections.sort(metaDatas);
        injectionPriorities = Collections.unmodifiableList(metaDatas);
    }
    
    /**
     * Initializes all singleton modules in order of corresponding 
     * ModuleMetaData natural ordering, provided by PriorityQueue.
     * 
     * @see com.drs.gem.injector.core.ModuleMetaData
     */
    private void injectSingletons() {
        Injector injector = getInjector();
        for (ModuleMetaData metaData : injectionPriorities){
            if (metaData.getType().equals(ModuleType.SINGLETON)){
                Constructor buildCons = metaData.getConstructor();        
                Class moduleInterface = metaData.getModuleInterface();
                
                Module module = injector.newModule(buildCons, moduleInterface);
                singletonModules.put(moduleInterface, module);
            }
        }
    }
    
    private Injector getInjector() {
        if ( useRecursiveInjector ) {
            return factory.buildRecursiveInjector((ModulesInfo) this);
        } else {
            return factory.buildLoopInjector((ModulesInfo) this);
        }
    }
    
    /**
     * Overrides abstract method in {@link com.drs.gem.injector.core.Container
     * Container} interface. See full method description in Container interface.
     * 
     * @see com.drs.gem.injector.core.Container
     */ 
    @Override
    public <M extends Module> M getModule(Class<M> moduleInterface) {      
        if ( injectionPriorities == null ) {
            throw new ContainerInitializationException(
                    "Modules::init() was not invoked.");
        }
        if (isModuleSingleton(moduleInterface)){
            Module uncastedModule = singletonModules.get(moduleInterface);
            M module = moduleInterface.cast(uncastedModule);
            return module;
        } else {
            Injector injector = getInjector();
            Constructor buildCons = constructors.get(moduleInterface);
            Module uncastedModule = injector.newModule(buildCons, moduleInterface);
            M module = moduleInterface.cast(uncastedModule);
            return module;
        }
    }
 
    /**
     * Overrides abstract method in {@link com.drs.gem.injector.core.ModulesInfo
     * ModulesInfo} interface. See full method description in ModulesInfo interface.
     * 
     * @see com.drs.gem.injector.core.ModulesInfo
     */
    @Override
    public boolean isModuleSingleton(Class moduleInterface) {
        return (moduleTypes.get(moduleInterface).equals(ModuleType.SINGLETON));
    }
    
    /**
     * Overrides abstract method in {@link com.drs.gem.injector.core.ModulesInfo
     * ModulesInfo} interface. See full method description in ModulesInfo interface.
     * 
     * @see com.drs.gem.injector.core.ModulesInfo
     */    
    @Override
    public Constructor getConstructorOfModule(Class moduleInterface) {
        return constructors.get(moduleInterface);
    }
    
    /**
     * Overrides abstract method in {@link com.drs.gem.injector.core.ModulesInfo
     * ModulesInfo} interface. See full method description in ModulesInfo interface.
     * 
     * @see com.drs.gem.injector.core.ModulesInfo
     */
    @Override
    public boolean ifConstructorExists(Class moduleInterface) {
        return constructors.containsKey(moduleInterface);
    }
    
    /**
     * Overrides abstract method in {@link com.drs.gem.injector.core.ModulesInfo
     * ModulesInfo} interface. See full method description in ModulesInfo interface.
     * 
     * @see com.drs.gem.injector.core.ModulesInfo
     */
    @Override
    public Map<Class, Module> getSingletons() {
        return singletonModules;
    }
    
    @Override
    public List<ModuleMetaData> getModuleDependenciesData(Class moduleInterface) {
        for (int i = 0; i < injectionPriorities.size(); i++) {
            if(injectionPriorities.get(i).getModuleInterface().equals(moduleInterface)){
                return new ArrayList<>(injectionPriorities.subList(0, i + 1));
            }
        }
        throw new UndeclaredDependencyException(
                "Undeclared dependency: " + moduleInterface.getCanonicalName() + 
                "not contained in Container.");
    }
    
    @Override
    public ModuleMetaData getMetaDataOfModule(Class moduleInterface) {
        return moduleDatas.get(moduleInterface);
    }
}
