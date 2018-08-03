package com.varian.oiscn.hisintegration.service;

import com.varian.oiscn.hisintegration.dto.RegistrationVO;

import java.util.Map;

public interface QueryPatientInterface {

    public RegistrationVO queryPatientByZyId(String id);
    
    public Map<String, Object> queryPaymentById(String id);
}
