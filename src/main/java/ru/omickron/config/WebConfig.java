package ru.omickron.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@ComponentScan(basePackages = "ru.omickron")
@EnableWebMvc
@EnableCaching
public class WebConfig extends WebMvcConfigurerAdapter implements CachingConfigurer {
    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ) {
        registry.addResourceHandler( "/WEB-INF/pages/**" ).addResourceLocations( "/pages/" );
        registry.addResourceHandler( "/resources/**" ).addResourceLocations( "/resources/" );
    }

    @Bean
    public InternalResourceViewResolver setupViewResolver() {
        InternalResourceViewResolver result = new InternalResourceViewResolver();
        result.setPrefix( "/WEB-INF/pages/" );
        result.setSuffix( ".jsp" );
        result.setViewClass( JstlView.class );
        return result;
    }

    @Bean
    public EhCacheManagerFactoryBean getEhCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean result = new EhCacheManagerFactoryBean();
        result.setConfigLocation( new ClassPathResource( "ehcache.xml" ) );
        result.setShared( true );
        return result;
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        EhCacheCacheManager result = new EhCacheCacheManager();
        result.setCacheManager( getEhCacheManagerFactoryBean().getObject() );
        return result;
    }

    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver( cacheManager() );
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }
}
