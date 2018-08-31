package com.varian.oiscn.hisintegration.resource;

import com.varian.oiscn.hisintegration.Configuration;
import com.varian.oiscn.hisintegration.dto.RegistrationVO;
import com.varian.oiscn.hisintegration.service.QueryPatientInterface;
import com.varian.oiscn.hisintegration.service.QueryPatientInterfaceDbImpl;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 *
 */
@Path("/HIS")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HISIntegrationResource extends AbstractResource{
    private QueryPatientInterface queryPatientInterface;
    public HISIntegrationResource(Configuration configuration, Environment environment){
        super(configuration,environment);
        queryPatientInterface  = new QueryPatientInterfaceDbImpl(configuration);
    }

    @GET
    @Path("/patient")
    public Response queryPatientInfoFromHis(@QueryParam("type") String type, @QueryParam("patientid") String zyId){
        log.info("Query HIS patient by ZYID: [{}]", zyId);
        RegistrationVO vo = queryPatientInterface.queryPatientByZyId(zyId);
        return Response.ok(vo,MediaType.APPLICATION_JSON_TYPE.withCharset("utf-8")).build();
    }

    @GET
    @Path("/payment")
    public Response queryPaymentFromHis(@QueryParam("id") String id){
        log.info("Query HIS payment by ZYID: [{}]", id);
        Map<String, Object> payment = queryPatientInterface.queryPaymentById(id);
        return Response.ok(payment,MediaType.APPLICATION_JSON_TYPE.withCharset("utf-8")).build();
    }
}
