README
======

Jars
----

BeanUtils now comes packaged (into jars) in two different ways: 

    an all-in-one jar (commons-beanutils.jar);
    and as modular component jars:
	commons-beanutils-core.jar (core classes)
	commons-beanutils-bean-collections.jar (additional dependency on commons-collections 3.0)

Those who want it all should grab the all-in-one jar (and the optional dependencies) 
whereas those who need minimal dependencies should use commons-beanutils-core.jar
plus any optional jars they plan to use.

All classes that were in the last release are in commons-beanutils-core except for BeanComparator. 

Commons-Collections
-------------------
BeanUtils now ships with a small number of commons collections classes. 
This is a temporary measure intended to allow BeanUtils core to be used with 
either commons-collections 2 or commons-collections-3 without a dependency on either.
It is intended that soon BeanUtils core will have no dependency on any commons collection
packaged classes.