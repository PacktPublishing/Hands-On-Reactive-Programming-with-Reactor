package com.sample.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.reactive.result.view.script.ScriptTemplateConfigurer;

@EnableWebFlux
@Configuration
public class WebfluxConfig implements WebFluxConfigurer {

   @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.scriptTemplate();
        registry.freeMarker();
   }

    @Bean
    public ScriptTemplateConfigurer scrptTemplateConfigurer() {
        ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
        configurer.setEngineName("nashorn");
        configurer.setScripts("mustache.js");
        configurer.setRenderObject("Mustache");
        configurer.setResourceLoaderPath("classpath:/mustache/");
        configurer.setRenderFunction("render");
        return configurer;
    }


    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/freemarker/");
        return configurer;
    }
}
