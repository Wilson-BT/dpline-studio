package com.dpline.console.handler;

import com.dpline.dao.domain.AbstractDeployConfig;
import com.dpline.dao.entity.JarFile;
import com.dpline.dao.entity.Job;

import java.util.List;

public abstract class DeployConfigConverter {

    protected abstract void convertToMotorDeployConfig(Job job);

    public abstract void mainJar(JarFile mainJarFile);

    public abstract void connectorJar(List<JarFile> connectorJarFiles);
    public abstract void udfJar(List<JarFile> udfJarFiles);

    public abstract void extendJar(List<JarFile> extendedJarFiles);

    public abstract AbstractDeployConfig getDeployConfig();


}
