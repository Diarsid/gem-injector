
## Gem injector Tutorial

* [Intro and Module concept](#intro-and-module-concept-arrow_up_small)
* [Common usage](#common-usage)
  * [Simple modular app](#simple-modular-app-arrow_up_small)
  * [Dependency graph and constructor injection](#dependency-graph-and-constructor-injection-arrow_up_small)
  * [Module and Container usage](#module-and-container-usage-arrow_up_small)
* [Module Builders](#module-builders-arrow_up_small)
  * [ModuleBuilder interface](#modulebuilder-interface-arrow_up_small)
  * [Module Builder restrictions](#module-builder-restrictions-arrow_up_small)
* [Container details](#container-details)
  * [Common code requirements](#common-code-requirements-arrow_up_small)
  * [Constructor injection](#constructor-injection-arrow_up_small)
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

![interfaces](http://i.imgur.com/AfueEmy.png?1)

Let’s implement those interfaces with concrete classes and call them “workers”. **FirstModuleWorker**, **SecondModuleWorker** and so on. Note that every module implementation is located in its own separate package and has package-private class access.

![first_module](http://i.imgur.com/6vgD1qR.png)

![second_module](http://i.imgur.com/JxeTbqf.png)

![third_module](http://i.imgur.com/Kee5PYI.png)

##### Dependency graph and constructor injection [:arrow_up_small:](#gem-injector-tutorial)
It’s done. Of course, these modules cannot be completely independent because if they actually are, they can be useless. Whole program is designed to deal with some tasks and all its inner parts have to work in conjunction to achieve some common goals. Modules will have inner dependencies on themselves to use one another as a services. Let’s depict the scheme of their dependencies as a graph:

![dependencies_graph](http://i.imgur.com/6yrV9A2.png)

It’s clear from scheme above that **FirstModule** is independent and doesn’t require any other module to do its work. But more complex **SecondModule** and **ThirdModule** need other modules in order to execute their tasks. Let’s add constructors into worker classes that will accept other modules as dependencies accordingly to that scheme:

![first_class](http://i.imgur.com/H6dRhGy.png)

![second_class](http://i.imgur.com/OekqMSh.png)

![third_class](http://i.imgur.com/I0gcTrz.png)

From that point it is possible to begin the assembling of the program and wiring modules together. Without DI container it is necessary to write a lot of code with the “new” operator, explicitly passing module instances into other constructors of appropriate worker classes and casting their instances to appropriate module interfaces. This approach leads to inconvenient cumbersome code which often requires a lot of static methods and breaking of package-private classes encapsulation.

##### Module and Container usage [:arrow_up_small:](#gem-injector-tutorial)
To avoid that we will use DI container. To begin with it, it is required to edit our previous code to extend module interfaces from void **com.drs.gem.injector.module.Module** interface:

![interfaces_extend_module](http://i.imgur.com/q5ujW8P.png)

Then just obtain instance of **com.drs.gem.injector.core.Container**, declare appropriate modules, invoke .*init*() method and that’s all! After it instance of any declared module can be obtained by invocation of **Container**.*getModule*(**Class** moduleClass) method.

![module_declarations](http://i.imgur.com/yw7zLpG.png)

Returned module instance is fully initialized and has all required dependecies set. 

When modules are declared and method .*init*() is invoked on **Container** instance, container collects all information about dependencies graph, initializes them all, injects all required dependencies where they are required and saves all singleton module instances. 

#### Module Builders [:arrow_up_small:](#gem-injector-tutorial)

Assume that there is some complex module depending on several other modules. Let it be, for example, **FifthModule** depends on **SecondModule**, **ThirdModule** and **FourthModule** (assume there are more then three previous modules have been designed). But while developing its functionality it turns out that there isn’t real necessity for **FifthModule** instance to use **ThirdModule** all the time. What is actually required is only some initial data that can be obtained from **ThirdModule** only once. After it **ThirdModule** instance actually becomes useless and redundant for **FifthModule**. But we still need its information i.e. depend on it. Moreover, let’s assume that it is necessary to perform some additional preliminary actions to initialize **FifthModule**. Those operations can be incapsulated in package private classes and methods in the same package in which **FifthModule** is located. 

Let’s extend our previous dependencies graph with **FourthModule** and **FifthModule**. This will look something like this:

![second_graph](http://i.imgur.com/0Am4yiA.png)

Let’s create new module interface as has been described above:

![5th_interface](http://i.imgur.com/XrNz5FT.png)

And implement it as we need:

![5th_class](http://i.imgur.com/KRj7I3m.png)

##### ModuleBuilder interface [:arrow_up_small:](#gem-injector-tutorial)
This is the point where **ModuleBuilder** comes into play! To provide **FifthModule** instance with all necessary additional data wee need some method and object that can perform all required actions, collect all results and initialize **FifthModuleWorker** instance. And .*buildModule*() method of **com.drs.gem.injector.module.ModuleBuilder** interface can serve as such entry point. Just create appropriate **ModuleBuilder** implementation class, provide it with an appropriate constructor, that accepts all required module dependencies, **@Override** .*buildModule*() method and place this class in the same package with **FifthModuleWorker**. It is described in code snippet below:

![module_builder](http://i.imgur.com/aquMQtS.png)

As you can see, any other helper classes like **FifthModuleAssistant** and **SomeDataVerifier** or any other actions can be incorporated in initialization process as described above. Note, that all dependencies which are required to process preliminary actions are declared as **ModuleBuilder** implementation class constructor's arguments. They will be injected during **ModuleBuilder** object creation by container itself so it is necessary to save them somewhere to use them later in .*buildModule*() method.

All we need now in order to use **FifthModule** (in conjunction with container) is to declare new modules as we have done it for previous modules earlier:

![additional_declaration](http://i.imgur.com/SYVa4pq.png)

And that’s all! **Container** will find all **ModuleBuilder** imlemenetations of appropriate modules, init them and execute .*buildModule*() methods to get appropriate module instances.

##### Module Builder restrictions [:arrow_up_small:](#gem-injector-tutorial)
There are only several restrictions about **ModuleBuilder**:
*	**ModuleBuilder** implementation classes must be located in the same packages as their corresponding Module implementation classes;
*	**ModuleBuilder** implementation class name must be equal to **Module** implementation class name but must end with “Builder”. E.g. module implementation class called “my.app.some.package.SomeImportantModule” then ModuleBuilder implementation class must be called “my.app.some.package.SomeImportantModuleBuilder”.
*	**ModuleBuilder** implementation class must have only one constructor with all explicitly declared module dependencies. Container finds **ModuleBuilder** constructor, resolves its dependencies (if they have been declared as modules in this constructor), creates **ModuleBuilder** instance and calls .*buildModule*() method. Container ignores any other **ModuleBuilder** implementation class methods, that's why all preliminary actions must be performed directly in .*buildModule*() method body.

#### Container details
##### Common code requirements [:arrow_up_small:](#gem-injector-tutorial)

There are also several common requirements to use this container:
*	**Module** implementation classes must have only one constructor with all explicitly declared dependencies. Although it can, of course, contain setter methods if you want to inject some dependencies later manually.
*	.*init*() method must be called only after all modules have been declared through **Container**.*declareModule*() method otherwise exception will be thrown.
*	There must be at least one declared module before **Container**.*init*() invocation otherwise exception will be thrown.
*	**Container**.*init*() method must be called only once for one **Container** instance. Second and all subsequent invocations will throw an exception.
*	All modules that are used in constructor of any declared module must also be declared as modules otherwise exception will be thrown.

##### Constructor injection [:arrow_up_small:](#gem-injector-tutorial)

Pivotal implementation idea of injection mechanism in this container is constructor injection. This dependency injection type has been choosen because setter injection is usually regarded by many developers as anti-pattern as it allows incomplete objects initialization. Setter initialization does not provide the way developer could be confident that object has all its fields initialized properly.

Therefore container cares about constructors of module classes and does not analyze their fields. Container collects all constructors from declared objects set and gets their constructor arguments. The object's constructor arguments list is further regarded as set of object's dependencies. Subsequent injection of all necessary dependencies is also performed based on list of constructor arguments.

Container neither collects or process object's fields or methods information nor performs direct field injection or setter injection.

In order to provide consistent and precise module's behavior and avoid uncertainty or complex configurations it is allowed for module class to have only one explicit constructor with all necessary dependencies declared as its arguments.

##### Module Types [:arrow_up_small:](#gem-injector-tutorial)

**ModuleType.SINGLETON** as argument in **Container**.*declareModule*() method means that whenever **Container**.*getModule*() is invoked it will return the same module object regardless of how many times this method has been invoked previously. Singletons will be initialized during .*init*() method execution and saved inside of **Container** object.

**ModuleType.PROTOTYPE** means that whenever **Container**.*getModule*() is invoked it will return new module object every time. If it has dependencies on other modules that have been also declared as prototypes, those modules will also be new objects every time. If it has dependecies on modules which are singletons, they will always be the same object, as definition of singleton pattern implies.

##### Cyclic dependencies [:arrow_up_small:](#gem-injector-tutorial)
While developing application situation can arise when some module will depend on other modules and so on, but one of those underlying modules will depend on first module, i.e. chain of dependencies will become cyclic. It is impossible to resolve such endless initializaton loop. Therefore in this case **CyclicDependencyException** will be thrown from **Container**.*init*() method.

[:arrow_backward: Back to main](https://github.com/Diarsid/gem-injector)

