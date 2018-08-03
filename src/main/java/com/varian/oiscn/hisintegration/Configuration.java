package com.varian.oiscn.hisintegration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class Configuration extends io.dropwizard.Configuration{
    public io.dropwizard.client.JerseyClientConfiguration httpClientConfiguration
            = new io.dropwizard.client.JerseyClientConfiguration();
    @NotNull
    private DbConfiguration database = new DbConfiguration();

    private String hisPatientViewName;
    private String hisPatientViewKey;
    private String hisPaymentViewName;
    private String hisPaymentViewKey;
    private String hisPatientOrderViewName;
    private boolean dummy;
}
