package jaci.gradle.deploy.cache

import groovy.transform.CompileStatic
import jaci.gradle.Resolver
import org.gradle.api.Project
import org.gradle.api.internal.DefaultNamedDomainObjectSet
import org.gradle.internal.reflect.DirectInstantiator

@CompileStatic
class CacheExtension extends DefaultNamedDomainObjectSet<CacheMethod> implements Resolver<CacheMethod> {

    Project project

    CacheExtension(Project project) {
        super(CacheMethod.class, DirectInstantiator.INSTANCE)
        this.project = project

        method("md5file", Md5FileCacheMethod, {})
        method("md5sum", Md5SumCacheMethod, {})
    }

    def method(String name, Class<? extends AbstractCacheMethod> type, final Closure config) {
        def cm = type.newInstance(name)
        project.configure(cm, config)
        this << (cm)
    }

    def method(String name, final Closure config) {
        method(name, DefaultCacheMethod, config)
    }

    CacheMethod resolve(Object cache) {
        if (cache == null || cache == false) {
            return null
        } else if (cache instanceof CacheMethod) {
            return (CacheMethod)cache
        } else if (cache instanceof String || cache instanceof GString) {
            return getByName(cache.toString())
        } else if (cache instanceof Closure<Boolean>) {
            def dcm = new DefaultCacheMethod("customCacheMethod")
            dcm.cache = (cache as Closure<Boolean>)
            return dcm
        }

        throw new IllegalArgumentException("Unknown Cache Method Type: ${cache.class}.\nMust be one of:\n" +
                "- instance of CacheMethod\n" +
                "- The name (String) of a CacheMethod stored in deploy.cache\n" +
                "- A closure returning whether the file needs update (true) or not (false)\n" +
                "- Null or False for no caching")
    }

}
