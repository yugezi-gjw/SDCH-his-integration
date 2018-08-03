package com.varian.oiscn.hisintegration.service;

import com.varian.oiscn.hisintegration.Configuration;
import com.varian.oiscn.hisintegration.dao.HISIntegrationDAO;
import com.varian.oiscn.hisintegration.database.ConnectionPool;
import com.varian.oiscn.hisintegration.dto.RegistrationVO;
import com.varian.oiscn.hisintegration.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.Map;

/**
 *
 */
@Slf4j
public class QueryPatientInterfaceDbImpl implements QueryPatientInterface{
    private HISIntegrationDAO hisIntegrationDAO;
    public QueryPatientInterfaceDbImpl(Configuration configuration){
        this.hisIntegrationDAO = new HISIntegrationDAO(configuration);
    }
    @Override
    public RegistrationVO queryPatientByZyId(String id) {
        Connection conn = null;
        RegistrationVO vo = null;
        try{
            try{
                conn = ConnectionPool.getConnection();
                vo = this.hisIntegrationDAO.queryPatientByZyId(conn,id);
            }catch(Exception e){
                log.error(e.getMessage(), e);
            }
        }finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return vo;
    }

    @Override
    public Map<String, Object> queryPaymentById(String id) {
        Connection conn = null;
        Map<String, Object> map = null;
        try{
            try{
                conn = ConnectionPool.getConnection();
                map = this.hisIntegrationDAO.queryPaymentById(conn,id);
            }catch(Exception e){
                log.error(e.getMessage(), e);
            }
        }finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return map;
    }
}
