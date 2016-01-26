
## Gem injector Tutorial

* [Intro and Module concept](#intro-and-module-concept-arrow_up_small)
* [Common usage](#common-usage)
  * [Simple modular app](#simple-modular-app-arrow_up_small)
  * [Dependency graph and constructor injection](#dependency-graph-and-constructor-injection-arrow_up_small)
  * [Container usage and module declarations](#container-usage-and-module-declarations-arrow_up_small)
  * [Declaration interface](#declaration-interface-arrow_up_small)
* [Module Builders](#module-builders-arrow_up_small)
  * [ModuleBuilder interface](#modulebuilder-interface-arrow_up_small)
  * [Module Builder restrictions](#module-builder-restrictions-arrow_up_small)
* [Constructor injection](#constructor-injection-arrow_up_small)
  * [@InjectedConstructor annotation](#injectedconstructor-annotation-arrow_up_small)
* [Container details](#container-details)
  * [Common code requirements](#common-code-requirements-arrow_up_small)
  * [Module Types](#module-types-arrow_up_small)
  * [Cyclic dependencies](#cyclic-dependencies-arrow_up_small)

[:arrow_backward: Back to main](https://github.com/Diarsid/gem-injector)

#### Intro and Module concept [:arrow_up_small:](#gem-injector-tutorial)

Gem injector is based on “modules” idea and assumes that your whole program can be divided into logic blocks of classes or packages that are hidden by encapsulation and can interact between packages through appropriate interfaces. 

Let’s assume that some important functional part in your program is hidden in some package by package private access modifier and is available for use to outer packages with only one public interface that provides all public functions because of which this entire module has been designed. We can call this only interface “module”, and  package that contains all classes providing and supporting this interface implementation (or even several packages of such classes) as “module implementation”.

It is clear the other program’s parts requiring some or all functions that this module can grant depend on this single module. So, we can say the other classes can have this module as their dependency.

#### Common usage
##### Simple modular app [:arrow_up_small:](#gem-injector-tutorial)
So, how can you use this DI container? Here is a simple example how one can use Gem injector. Let’s start!

In order to illustrate container work let’s create a skeleton of a simple program that doesn’t rely on DI container yet. Assume that our program has only three modules and they are called **FirstModule**, **SecondModule** and **ThirdModule** to avoid complex names.

![first_module_interf](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/1.1_FirstModule_interf.png) 

![second_module_interf](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/1.2_SecondModule_interf.PNG) 

![third_module_interf](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/1.3_ThirdModule_interf.png)

Let’s implement those interfaces with concrete classes and call them “workers”. **FirstModuleWorker**, **SecondModuleWorker** and so on. Note that every module implementation is located in its own separate package and has package-private class access.

![first_module](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/2.1_FirstModule_worker.PNG)

![second_module](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/2.2_SecondModule_worker.png)

![third_module](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/2.3_ThirdModule_worker.png)

##### Dependency graph and constructor injection [:arrow_up_small:](#gem-injector-tutorial)
It’s done. Of course, these modules cannot be completely independent because if they actually are, they can be useless. Whole program is designed to deal with some tasks and all its inner parts have to work in conjunction to achieve some common goals. Modules will have inner dependencies on themselves to use one another as a services. Let’s depict the scheme of their dependencies as a graph:

![dependencies_graph](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/Modules_3_graph_scheme.PNG)

It’s clear from scheme above that **FirstModule** is independent and doesn’t require any other module to do its work. But more complex **SecondModule** and **ThirdModule** need other modules in order to execute their tasks. Let’s add constructors into worker classes that will accept other modules as dependencies accordingly to that scheme:

![first_class](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/3.1_FirstModule_worker_constructor.png)

![second_class](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/3.2_SecondModule_worker_constructor.png)

![third_class](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/3.3_ThirdModule_worker_constructor.png)

From that point it is possible to begin the assembling of the program and wiring modules together. Without DI container it is necessary to write a lot of code with the “new” operator, explicitly passing module instances into other constructors of appropriate worker classes and casting their instances to appropriate module interfaces. This approach leads to inconvenient cumbersome code which often requires a lot of static methods and breaking of package-private classes encapsulation.

##### Container usage and module declarations [:arrow_up_small:](#gem-injector-tutorial)
To avoid that we will use DI container. In order to begin with it, it is required to edit our previous code to extend module interfaces from void **com.drs.gem.injector.module.GemModule** interface:

![first_module_interf_extend_module](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/4.1_FirstModule_interf_GemModule.png)
![first_module_interf_extend_module](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/4.2_SecondModule_interf_GemModule.png)
![first_module_interf_extend_module](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/4.3_ThirdModule_interf_GemModule.png)

Then just obtain instance of **com.drs.gem.injector.core.Container**, declare appropriate modules, invoke .*init*() method and that’s all! After it instance of any declared module can be obtained by invocation of **Container**.*getModule*(**Class** moduleClass) method.

![module_declarations](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/5.1_Container_module_declarations.png)

Returned module instance is fully initialized and has all required dependecies set. 

When modules have been declared and method .*init*() invoked on **Container** instance, container collects all information about dependencies graph, initializes them all, injects all required dependencies where they are required and saves all singleton module instances. 

##### Declaration interface [:arrow_up_small:](#gem-injector-tutorial)

Alternatively, it is possible not to declare modules with **Container**.*declareModule*() method. There are interface **Declaration** and class **GemModuleDeclaretion** which allow to separate module declarations code from immediate usage of container instance. Interface **Declaration** contains one abstract method *getDeclaredModules*() that returns **java.util.Set<com.drs.gem.injector.core.GemModuleDeclaration>** where each **GemModuleDeclaration** instance represents the declaration of one module. Data needed to declare module in this way are just the same that is needed when **Container**.*declareModule*() method is used. Let's create appropriate class called **MyAppModules** and override interface's method:

![declaration_class](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/5.2_Declaration_interf_usage.png)

Now it is possible to refactor previous code to remove direct module declarations from main method and use **MyAppModules** class instead.

![declaration_class_usage](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/5.3_Declaration_interf_usage_in_main().png)

Method **GemInjector**.*buildContainer*() accepts Declaration... array argument so it is possible to separate different declarations in different **Declaration** classes and pass several **Declaration** as method arguments. It could be useful if there are large number of different modules in the application.

#### Module Builders [:arrow_up_small:](#gem-injector-tutorial)

Assume that there is some complex module depending on several other modules. Let it be, for example, **FifthModule** depends on **SecondModule**, **ThirdModule** and **FourthModule** (assume there are more then three previous modules have been designed). But while developing its functionality it turns out that there isn’t real necessity for **FifthModule** instance to use **ThirdModule** all the time. What is actually required is only some initial data that can be obtained from **ThirdModule** only once. After it **ThirdModule** instance actually becomes useless and redundant for **FifthModule**. But we still need its information i.e. depend on it. Moreover, let’s assume that it is necessary to perform some additional preliminary actions to initialize **FifthModule**. Those operations can be incapsulated in package private classes and methods in the same package in which **FifthModule** is located. 

Let’s extend our previous dependencies graph with **FourthModule** and **FifthModule**. This will look something like this:

![second_graph](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/Modules_5_graph_scheme.png)

Let’s create new module interface as has been described above:

![5th_interface](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/6.1_FifthModule_interf.png)

And implement it as we need:

![5th_class](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/6.2_FifthModule_worker.png)

##### ModuleBuilder interface [:arrow_up_small:](#gem-injector-tutorial)
This is the point where **ModuleBuilder** comes into play! To provide **FifthModule** instance with all necessary additional data wee need some method and object that can perform all required actions, collect all results and initialize **FifthModuleWorker** instance. And .*buildModule*() method of **com.drs.gem.injector.module.ModuleBuilder** interface can serve as such entry point. Just create appropriate **ModuleBuilder** implementation class, provide it with an appropriate constructor, that accepts all required module dependencies, **@Override** .*buildModule*() method and place this class in the same package with **FifthModuleWorker**. It is described in code snippet below:

![module_builder](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/6.3_FifthModule_worker_Builder.png)

As you can see, any other helper classes like **FifthModuleAssistant** and **SomeDataVerifier** or any other actions can be incorporated in initialization process as described above. Note, that all dependencies which are required to process preliminary actions are declared as **ModuleBuilder** implementation class constructor's arguments. They will be injected during **ModuleBuilder** object creation by container itself so it is necessary to save them somewhere to use them later in .*buildModule*() method.

All we need now in order to use **FifthModule** (in conjunction with container) is to declare new modules as we have done it for previous modules earlier:

And that’s all! **Container** will find all **ModuleBuilder** imlemenetations of appropriate modules, init them and execute .*buildModule*() methods to get appropriate module instances.

##### Module Builder restrictions [:arrow_up_small:](#gem-injector-tutorial)
There are only several restrictions about **ModuleBuilder**:
*	**ModuleBuilder** implementation classes must be located in the same packages as their corresponding Module implementation classes;
*	**ModuleBuilder** implementation class name must be equal to **Module** implementation class name but must end with “Builder”. E.g. module implementation class called “my.app.some.package.SomeImportantModule” then ModuleBuilder implementation class must be called “my.app.some.package.SomeImportantModuleBuilder”.
*	Container ignores any other **ModuleBuilder** implementation class methods, that's why all preliminary actions must be performed directly in .*buildModule*() method body.

#### Constructor injection [:arrow_up_small:](#gem-injector-tutorial)

Pivotal implementation idea of injection mechanism in this container is constructor injection. This dependency injection type has been chosen because setter injection is usually regarded by many developers as anti-pattern as it allows incomplete objects initialization. Setter initialization does not provide the way developer could be confident that object has all its fields initialized properly.

Therefore container cares about constructors of module classes and does not analyze their fields or .setXXX() methods. Container collects all constructors from declared objects set and gets their arguments. The object's constructor arguments list is further regarded as set of object's dependencies. Subsequent injection of all necessary dependencies is also performed based on list of constructor arguments.

Container neither collects or process object's fields or methods information nor performs direct field injection or setter injection.

##### @InjectedConstructor annotation [:arrow_up_small:](#gem-injector-tutorial)

It is permitted for module class to have more than one constructor. But in this case container could not be able to collect module's dependencies properly. 

That's why in order to provide consistent and precise container's and module's behavior and avoid uncertainty or complex configurations annotation **@InjectedConstructor** should be used if module (or module builder) class has more than one constructor. There must be only one **@InjectedConstructor** annotation in class otherwise exception will be thrown. 
If class has only one explicitly declared constructor there is no need to use this annotation.

Let's depict some class that has two constructors and uses **@InjectedConstructor** annotation:

![inj-constr-ann](https://github.com/Diarsid/gem-injector/blob/master/docs/tutorial-pictures-v2/7.1_InjectedConstructor_usage.png)

#### Container details
##### Common code requirements [:arrow_up_small:](#gem-injector-tutorial)

There are also several common requirements to use this container:
*	**Module** implementation classes must have only one constructor with all explicitly declared dependencies. Although it can, of course, contain setter methods if you want to inject some dependencies later manually.
*	.*init*() method must be called only after all modules have been declared through **Container**.*declareModule*() method otherwise exception will be thrown.
*	There must be at least one declared module before **Container**.*init*() invocation otherwise exception will be thrown.
*	**Container**.*init*() method must be called only once for one **Container** instance. Second and all subsequent invocations will throw an exception.
*	It is not allowed to use **Container**.*declareModule*() method along with usage of **Declaration** instances during container creation with **GemInjector**.*buildContainer*() method for thw same container simultaneously. If container was created with **Declaration** instances passed to .*buildContainer*() method and if one tries to use .*declareModule*() method on this container instance, then exception will be thrown.
*	All modules that are used in constructor of any declared module must also be declared as modules otherwise exception will be thrown.

##### Module Types [:arrow_up_small:](#gem-injector-tutorial)

**ModuleType.SINGLETON** as argument in **Container**.*declareModule*() method means that whenever **Container**.*getModule*() is invoked it will return the same module object regardless of how many times this method has been invoked previously. Singletons will be initialized during .*init*() method execution and saved inside of **Container** object.

**ModuleType.PROTOTYPE** means that whenever **Container**.*getModule*() is invoked it will return new module object every time. If it has dependencies on other modules that have been also declared as prototypes, those modules will also be new objects every time. If it has dependecies on modules which are singletons, they will always be the same object, as definition of singleton pattern implies.

##### Cyclic dependencies [:arrow_up_small:](#gem-injector-tutorial)

While developing application situation can arise when some module will depend on other modules and so on, but one of those underlying modules will depend on first module, i.e. chain of dependencies will become cyclic. It is impossible to resolve such endless initializaton loop. Therefore in this case **CyclicDependencyException** will be thrown from **Container**.*init*() method.

[:arrow_backward: Back to main](https://github.com/Diarsid/gem-injector)

