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
import com.drs.gem.injector.module.GemModule;

/**
 * Pivotal class representing Dependency Injection container. <br> 
 * Receives information about
 * modules, produces module instances and relay information about modules
 * into {@link Injector}.
 * 
 * @author Diarsid
 * @see Container.
 * @see ModulesInfo.
 */
final class ModulesContainer implements Container, ModulesInfo {
    
    /**
     * Map<Class, Class> that contains entries where key is module interface 
     * class object and value is class being responsible for
     * module implementation class object creation. Entry value can be module
     * implementation class itself or its appropriate {@link 
     * com.drs.gem.injector.module.GemModuleBuilder GemModuleBuilder} class.
     * 
     * @see com.drs.gem.injector.module.GemModuleBuilder.
     */
    private final Map<Class, Class> declaredModules;
    
    /**
     * Map<Class, Constructor> that contains entries where key is module interface 
     * class object and value is constructor of appropriate module 
     * implementation class. It can be GemModuleBuilder constructor as well.
     * 
     * @see com.drs.gem.injector.module.GemModuleBuilder.
     */
    private final Map<Class, Constructor> constructors;
    
    /**
     * Map<Class, GemModuleType> that contains entries where key is module interface 
     * class object and value is {@link GemModuleType} object.
     * 
     * @see com.drs.gem.injector.core.GemModuleType.
     */
    private final Map<Class, GemModuleType> moduleTypes;
    
    /**
     * Map<Class, GemModule> that contains entries where key is module interface 
     * class object with GemModuleType = SINGLETON and value is corresponding
     * fully initialized and ready to work module instance.
     * 
     * @see com.drs.gem.injector.module.GemModule.
     */
    private final Map<Class, GemModule> singletonModules;
    
    /**
     * List<ModuleMetaData> that contains ModuleMetaData 
     * objects sorted in their natural ascending order. 
     * See {@link ModuleMetaData} for more details.
     * 
     * @see ModuleMetaData.
     */
    private List<ModuleMetaData> injectionPriorities;
    
    /**
     * Map<Class, ModuleMetaData> that contains module interfaces and 
     * appropriate ModuleMetaData objects.
     * 
     * @see ModuleMetaData.
     */
    private final Map<Class, ModuleMetaData> moduleDatas;
    
    /**
     * TRUE if modules were declared via {@link Declaration} objects. 
     * FALSE otherwise.
     * 
     * @see Declaration.
     */
    private final boolean constructorDeclaration;  
    
    /**
     * Simple factory object used to avoid "new" operator inside 
     * the container instance methods.
     * 
     * @see Factory.
     */
    private final Factory factory;
    
    /**
     * Object containing helper methods helping to verify module interfaces, 
     * implementations, builders, constructors and so on.
     * 
     * @see ContainerHelper.
     */
    private final ContainerHelper helper;
    
    /**
     * <p>Indicates if container should use {@link RecursiveInjector} 
     * for module instantiation.</p>
     * <p>Default value is false, thus default injector is 
     * {@link PriorityLoopInjector}. It is not recommended to 
     * use RecursiveInjector.</p>
     * 
     * @see RecursiveInjector.
     * @see PriorityLoopInjector.
     */
    private boolean useRecursiveInjector;

    /**
     * Accepts only {@link Factory} instance. <br>
     * Boolean field <b>constructorDeclaration</b> is FALSE.
     * 
     * @param factory   {@link Factory} instance.
     * @see             Container.
     */
    ModulesContainer(Factory factory) {
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
    
    /**
     * Accepts {@link Factory} and {@link Declaration} instances.<br>
     * Boolean field <b>constructorDeclaration</b> is TRUE.
     * Read more about module declarations in {@link Container}.
     * 
     * @param factory       {@link Factory} instance.
     * @param declarations  {@link Declaration} instances.
     * @see                 Container.
     */
    ModulesContainer(Factory factory, Declaration... declarations) {
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
    
    private void processDeclarations(Declaration[] declarations) {
        for (Declaration dec : declarations) {
            for (GemModuleDeclaration moduleDec : dec.getDeclaredModules()) {
                String moduleInterface = moduleDec.getModuleInterfaceName();
                String moduleBuildClass = moduleDec.getModuleBuildClassName();
                GemModuleType type = moduleDec.getModuleType();
                parseModuleDeclaration(moduleInterface, moduleBuildClass, type);
            }            
        }
    }
    
    /**
     * Overrides abstract method in {@link Container} interface. 
     * See full method description in {@link Container}.
     * 
     * @see Container.
     */    
    @Override
    public void declareModule(
            String moduleInterfaceName, 
            String moduleImplemName, 
            GemModuleType type) {
        
        if (constructorDeclaration){
            throw new ForbiddenModuleDeclarationException(
                    "This container uses constructor module declaration via " +
                    Declaration.class.getCanonicalName() + 
                    " implementation. Explicit module declaration via" +
                    " Container::declareModule(String, String, GemModuleTyoe)" +
                    "is not allowed.");
        }
        if ( injectionPriorities != null ){
            throw new ForbiddenModuleDeclarationException(
                    "Modules has been initialized already. Additional module " +
                    "declarations are not premitted after container initialization.");
        }
        parseModuleDeclaration(moduleInterfaceName, moduleImplemName, type);
    }
    
    /**
     * Processes all necessary information about module from String parameters,
     * verifies it and saves in container.
     * 
     * @param moduleInterfaceName   module interface canonical name.
     * @param moduleImplemName      module implementation class canonical name.
     * @param type                  module type, singleton or prototype.
     */
    private void parseModuleDeclaration(
            String moduleInterfaceName, String moduleImplemName, GemModuleType type){
        
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
    
    /**
     * Overrides abstract method in {@link Container} interface. 
     * See full method description in {@link Container}.
     * 
     * @see Container.
     */ 
    @Override
    public void useRecursiveInjector(){
        useRecursiveInjector = true;
    }
    
    /**
     * Overrides abstract method in {@link Container} interface. 
     * See full method description in {@link Container}.
     * 
     * @see Container.
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
     * Collects all constructors of all declared modules and saves them
     * in container for further object instantiations.
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
    
    /**
     * Creates ModuleMetaData for all declared modules and stores them.
     */
    private void initializeModuleMetaDatas() {
        for(Map.Entry<Class, Class> pair : declaredModules.entrySet()) {
            Class moduleInterface = pair.getKey();
            Constructor buildCons = constructors.get(moduleInterface);
            GemModuleType type = moduleTypes.get(moduleInterface);
            
            ModuleMetaData metaData = factory.buildMetaData(
                    moduleInterface, buildCons, type);
            
            moduleDatas.put(moduleInterface, metaData);
        }
    }
    
    /**
     * Using {@link InjectionPriorityCalculator}, calculates 
     * actual priority of all modules, sorts them by their natural ordering
     * and stores them in injectionPriority field.
     * 
     * @see ModuleMetaData
     * @see InjectionPriorityCalculator
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
     * Initializes all singleton modules in corresponding with
     * ModuleMetaData natural ordering, provided by injectionPriority list.
     * 
     * @see ModuleMetaData
     */
    private void injectSingletons() {
        Injector injector = getInjector();
        for (ModuleMetaData metaData : injectionPriorities){
            if (metaData.getType().equals(GemModuleType.SINGLETON)){
                Constructor buildCons = metaData.getConstructor();        
                Class moduleInterface = metaData.getModuleInterface();
                
                GemModule module = injector.newModule(buildCons, moduleInterface);
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
     * Overrides abstract method in {@link Container} interface. 
     * See full method description in {@link Container}.
     * 
     * @see Container.
     */ 
    @Override
    public <M extends GemModule> M getModule(Class<M> moduleInterface) {      
        if ( injectionPriorities == null ) {
            throw new ContainerInitializationException(
                    "Modules::init() was not invoked.");
        }
        if (isModuleSingleton(moduleInterface)){
            GemModule uncastedModule = singletonModules.get(moduleInterface);
            M module = moduleInterface.cast(uncastedModule);
            return module;
        } else {
            Injector injector = getInjector();
            Constructor buildCons = constructors.get(moduleInterface);
            GemModule uncastedModule = injector.newModule(buildCons, moduleInterface);
            M module = moduleInterface.cast(uncastedModule);
            return module;
        }
    }
 
    /**
     * Overrides abstract method in {@link ModulesInfo} interface. 
     * See full method description in {@link ModulesInfo}.
     * 
     * @see ModulesInfo.
     */
    @Override
    public boolean isModuleSingleton(Class moduleInterface) {
        return (moduleTypes.get(moduleInterface).equals(GemModuleType.SINGLETON));
    }
    
    /**
     * Overrides abstract method in {@link ModulesInfo} interface. 
     * See full method description in {@link ModulesInfo}.
     * 
     * @see ModulesInfo.
     */   
    @Override
    public Constructor getConstructorOfModule(Class moduleInterface) {
        return constructors.get(moduleInterface);
    }
    
    /**
     * Overrides abstract method in {@link ModulesInfo} interface. 
     * See full method description in {@link ModulesInfo}.
     * 
     * @see ModulesInfo.
     */
    @Override
    public boolean ifConstructorExists(Class moduleInterface) {
        return constructors.containsKey(moduleInterface);
    }
    
    /**
     * Overrides abstract method in {@link ModulesInfo} interface. 
     * See full method description in {@link ModulesInfo}.
     * 
     * @see ModulesInfo.
     */
    @Override
    public Map<Class, GemModule> getSingletons() {
        return singletonModules;
    }
    
    /**
     * Overrides abstract method in {@link ModulesInfo} interface. 
     * See full method description in {@link ModulesInfo}.
     * 
     * @see ModulesInfo.
     */
    @Override
    public List<ModuleMetaData> getModuleDependenciesData(Class moduleInterface) {
        for (int i = 0; i < injectionPriorities.size(); i++) {
            if(injectionPriorities.get(i).getModuleInterface().equals(moduleInterface)){
                return new ArrayList<>(injectionPriorities.subList(0, i + 1));
            }
        }
        throw new UndeclaredDependencyException(
                "Undeclared dependency: " + moduleInterface.getCanonicalName() + 
                " is not contained in this Container");
    }
    
    /**
     * Overrides abstract method in {@link ModulesInfo} interface. 
     * See full method description in {@link ModulesInfo}.
     * 
     * @see ModulesInfo.
     */
    @Override
    public ModuleMetaData getMetaDataOfModule(Class moduleInterface) {
        return moduleDatas.get(moduleInterface);
    }
}
