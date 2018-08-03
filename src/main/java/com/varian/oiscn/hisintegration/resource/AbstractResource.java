package com.varian.oiscn.hisintegration.resource;

import com.varian.oiscn.hisintegration.Configuration;
import io.dropwizard.setup.Environment;

/**
 *
 */
public class AbstractResource {
    protected Configuration configuration;
    protected Environment environment;
    public AbstractResource(Configuration configuration,Environment environment){
        this.configuration = configuration;
        this.environment = environment;
    }
}
