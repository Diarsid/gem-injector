# More about Gem Injector

Gem Injector is very simple, plain, lightweight and fast Java [Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection) container based on the idea of code modularity, interfaces usage and constructor injection.

First, it was only the idea that it would be nice to make my own extremely light, evident and simple DI container which would fit well for little projects. I got it while I was reading Spring DI container documentations. I was excited by this idea because it was quite challenging task for me.

This container doesn’t have complicated and elaborated architecture, hundreds of features and so on, because of it is originally intended to be used in simple home or so called “pet” projects that do not require excessive complexity.

It is built using [Java Reflection API](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/package-summary.html) without any third-party libraries. 

Pivotal implementation idea of injection mechanism in this container is constructor injection. Therefore container cares about constructors of modular classes and does not analyze their fields. Read [tutorial](tutorial.md) or source code for more technical details.

It is [Maven-based project](https://maven.apache.org) so if you are familiar with Maven you can copy it and build into .jar by yourself.

I will be happy if you will find this container useful for your apps.

Feel free to contribute or to contact me if you have found a typo or mistake or something else :) 

[Back to README](https://github.com/Diarsid/gem-injector/blob/master/README.md)
