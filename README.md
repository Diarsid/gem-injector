# Gem Injector

It is very simple, basical, lightweight and fast Dependency Injection container based on the idea of code modularity, interfaces usage and constructor injection.

It is built using only Java Reflection API without any third-party libraries. Except for jUnit and Mockito, of course :) It is Maven-based project so if you are familiar with Maven you can take it and build into .jar by yourself.

It doesn’t have complicated and elaborated architecture, hundreds of features and so on, it is intended to be used in simple home or “pet” projects that do not require excessive complexity.

It is based on “modules” idea and assume that all your program can be devided into logic blocks of classes or packages that are hidden by encapsulation and can interact between packages through appropriate interfaces. 

Let’s assume that some important functional part in your program is hidden in some package by package-private access modifier and is exposed for use to outer packages with only one public interface that provides all public functions because of which this entire module has been designed. We can call this only interface as “module” and  package that contain all classes that provide and support this interface implementation (or even several packages of such classes) as “module implementation”.

It is clear, that other program’s parts that require some or all of functions that this module can grant depends on this single module. So, we can say that other classes can have this module as their dependency.

#### Enough description! Show me some code!
So, how can you use this DI container? Here is a simple example how one can use Gem injector. Let’s start!

To illustrate container work let’s create a skeleton of a simple program that doesn’t rely on DI container yet. Assume that our program has only three modules and they are called FirstModule, SecondModule and ThirdModule to avoid complex names.

![interfaces](http://i.imgur.com/AfueEmy.png?1)

Let’s implement those interfaces with concrete classes and call them as “workers”. FirstModuleWorker, SecondModuleWorker and so on. Note that every module implementation is located in its own separate package and has package-private class access.

![first_module](http://i.imgur.com/6vgD1qR.png)

![second_module](http://i.imgur.com/JxeTbqf.png)

![third_module](http://i.imgur.com/Kee5PYI.png)

It’s done. Of course, these modules cannot be completely independent because of if they actually is, they can be useless. Whole program is designed to solve some tasks and all its inner parts have to work in conjunction to achieve some common goals. Modules will have inner dependecies on themselves to use one another as a services. Let’s depict the scheme of their dependencies as a graph:

![dependencies_graph](http://i.imgur.com/6yrV9A2.png)

It’s clear from scheme above that FirstModule is independent and doesn’t require any other module to do its work. But more complex SecondModule and ThirdModule need other modules in order to execute their tasks. Let’s add constructors into worker classes that will accept other modules as dependencies accordingly to that scheme:

![first_class](http://i.imgur.com/H6dRhGy.png)

![second_class](http://i.imgur.com/OekqMSh.png)

![third_class](http://i.imgur.com/I0gcTrz.png)

From that point it is possible to begin the assembling of program and wiring modules together. Without DI container it is nessecary to write a lot of code with “new” operator, explicitly pass module instances into another constructors of approptiate worker classes and cast their instances to appropriate module intefaces. This approach leads to inconvenient cumbersome code which often requires a lot of static methods and breaking of package-private classes encapsulation.

To avoid that we will use DI container. To begin with it, it is required to edit our previous code to extend module interfaces from void com.drs.gem.injector.core.Module interface:

![interfaces_extend_module](http://i.imgur.com/Zn2gMe6.png)

Then just obtain instance of Container, declare appropriate modules, invoke .init() method and that’s all! After it instance of any declared module can be obtained by invocation of Container.getModule(Class moduleClass) method.

![module_declarations](http://i.imgur.com/yw7zLpG.png)

Returned module instance is fully initialized and has all required dependecies set. 

When modules is declared and method .init() is invoked on Container instance, container collects all information about dependecies graph, initializes them all, injects all required dependencies where they are required and saves all singleton module instances. 

While developing application situation can arise when some module will depend on other modules and so on, but one of that underlying modules will depend on that first module, i.e. chain of dependencies will become cyclic. It is impossible to resolve such endless initializaton loop thus in that case CyclicDependencyException will be thrown from .init() method.

ModuleType.SINGLETON as argument in .declareModule() method means that whenever .getModule() is invoked it will return the same module object regardless of how many times this method has been invoked previously. Singletons will be initialized during .init() and will be saved inside of Container object.

ModuleType.PROTOTYPE means that whenever .getModule() is invoked it will return new module object every time. If it has dependencies on other modules that has been also delcared as prototypes, those modules will also be a new objects every time. If it has dependecies on modules which are singletons, they always be the same object, as definition of singleton pattern implies.

##### ModuleBuilder usage

Assume that there are some complex module depends on several other modules. Let it be, for example, FifthModule depends on SecondModule, ThirdModule and FourthModule (assume there are more then three previous modules have been designed). But while developing its functionality it turns out that there isn’t real nessecity for FifthModule instance to use ThirdModule all the time. What is actually required is only some initial data that can be obtained from ThirdModule only once. After it ThirdModule instance actually becomes useless and redundant for FifthModule. But we still need its information i.e. depend on it. Moreover, let’s assume that it is nessecary to perform some additional preliminary actions to initialize FifthModule. Those operations can be incapsulated in package-private classes and methods in the same package in which FifthModule is located. 

Let’s extend our previous dependensies graph with FourthModule and FifthModule. This will look something like this:

![second_graph](http://i.imgur.com/0Am4yiA.png)

Let create new module interface as has been descibed above:

![5th_interface](http://i.imgur.com/XrNz5FT.png)

And implement it as we need:

![5th_class](http://i.imgur.com/KRj7I3m.png)

This is the point where ModuleBuilder comes into play! To provide FifthModule instance with all nessecary additional data wee need some method and object that can perform all required actions, collect all results and initialize FifthModuleWorker instance. And .buildModule() method of com.drs.gem.injector.core.ModuleBuilder interface can serve as such entry point. Just create appropriate ModuleBuilder implementation class in the same package with FifthModuleWorker and @Override .buildModule() as described below:

![module_builder](http://i.imgur.com/uXw25FT.png)

As you can see, any other helper classes like FifthModuleAssistant and SomeDataVerifier or any other actions can be incorporated in initialization process as described above. And we use all dependencies that we need to process preliminary actions as arguments in .buildModule() method. 

All we need now in order to use FifthModule (in conjunction with container) is to declare new modules as we have done it for previous modules earlier:

![additional_declaration](http://i.imgur.com/SYVa4pq.png)

And that’s all! Container will find all ModuleBuilder imlemenetations of appropriate modules, init them and execute .buildModule(Module... modules) methods to get appropriate module instances.

There are only several restrictions about ModuleBuilder:
*	ModuleBuilder implementation classes must be located in the same package as its corresponding Module implementation classes;
*	ModuleBuilder implementation class must have its name equals to Module implementation class name but ends with “Builder”. E.g. module implementation class is called as “my.app.some.package.SomeImportantModule” then ModuleBuilder implementation class must be called as “my.app.some.package.SomeImportantModuleBuilder”.

There are also several common requirements to use this container:
*	Module implementation classes must have only one constructor with all explicitly declared dependencies. Although it can, of course, contain setter methods if you want to inject some dependencies later manually.
*	.init() method must be called only after all modules have been declared through Container.declareModule() method otherwise exception will be thrown.
*	There must be at least one declared module before .init() invocation otherwise exception will be thrown.
*	.init() method must be called only once for one Container instance. Second and all subsequent invocations will throw an exception.
*	All modules that are used in constructor of any declared module must also be declared otherwise exception will be thrown. 

I will be happy if you will find this container useful for your apps.
Feel free to contribute or to contact me if you have found a typo or mistake or something else. 
