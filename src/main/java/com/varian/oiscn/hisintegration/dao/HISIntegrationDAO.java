package com.varian.oiscn.hisintegration.dao;

import com.varian.oiscn.hisintegration.Configuration;
import com.varian.oiscn.hisintegration.dto.RegistrationVO;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HISIntegrationDAO {
    private Configuration configuration;
    public HISIntegrationDAO(Configuration configuration){
        this.configuration = configuration;
    }

    /**
     *
     * @param conn
     * @param id
     * @return
     */
    public RegistrationVO queryPatientByZyId(Connection conn,String id) {
        String viewName = configuration.getHisPatientViewName();
        String viewKey = configuration.getHisPatientViewKey();
        if(configuration.isDummy()){
            if (id != null && id.length() > 0 && '0' == id.charAt(0)) {
                return dummy();
            } else {
                return null;
            }
        }else{
            Map<String, Object> ret = query(conn,id, viewName, viewKey);
            if (ret != null && ret.keySet().size() > 0) {
                String orderViewName = configuration.getHisPatientOrderViewName();
                Map<String, Object> orderInfo = query(conn, id, orderViewName, viewKey);
                log.info("Order info: [{}] for id[{}].", orderInfo.toString(), id);
                ret.putAll(orderInfo);
                log.info("Query and returned data with [{}]", id);
                return transformer(ret);
            } else {
                log.info("Query and returned nothing with [{}]", id);
                return null;
            }
        }
    }

    /**
     *
     * @param conn
     * @param id
     * @return
     */
    public Map<String, Object> queryPaymentById(Connection conn,String id) {
        String viewName = configuration.getHisPaymentViewName();
        String viewKey = configuration.getHisPaymentViewKey();
        return query(conn,id, viewName, viewKey);
    }

    protected Map<String, Object> query(Connection conn,String id, String viewName, String viewKey) {
        Map<String, Object> mapColumn = new HashMap<>();
        try {
            if (conn != null) {
                String sql = "SELECT * FROM " + viewName;
                if (viewKey != null && viewKey.length() > 0) {
                    sql += " WHERE " + viewKey + " = ? ";
                }

                @Cleanup
                PreparedStatement ps = conn.prepareStatement(sql);
                if (viewKey != null && viewKey.length() > 0) {
                    if (id.length() == 6) {
                        ps.setString(1, "0000" + id);
                    } else {
                        ps.setString(1, id);
                    }
                }
                log.info("Query sql: [{}].", sql);
                @Cleanup
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                if (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rsmd.getColumnName(i);
                        Object columnObj = rs.getObject(i);
                        if (columnObj == null) {
                            columnObj = "";
                        }
                        log.debug("row {} \t - [{}] = [{}]", i, columnName, columnObj);
                        mapColumn.put(columnName, columnObj);
                    }
                }
            }
        } catch (Exception e) {
            log.error("query exception: {}", e.getMessage());
        }
        return mapColumn;
    }

    protected RegistrationVO transformer(Map<String, Object> ret) {
        RegistrationVO vo = new RegistrationVO();

        log.debug("Input: {}", ret);
        // HIS_ID
        vo.setAriaId((String) ret.get("HIS_ID"));

        // 1[HOSPITALIZED_NO]=[ZY040000183960]
        String hospitalizedNo = (String) ret.get("HOSPITALIZED_NO");
        String patientSource = "InPatient";
        if (hospitalizedNo != null && hospitalizedNo.length() >= 10) {
            patientSource = hospitalizedNo.substring(0, 2);
            hospitalizedNo = hospitalizedNo.substring(hospitalizedNo.length() - 10);
            if ("ZY".equals(patientSource)) {
                vo.setPatientSource("住院");
            }
        }
        vo.setHisId(hospitalizedNo);

        // 2[NAME]=[胡清水]
        vo.setChineseName((String) ret.get("NAME"));
        // 3[SEX]=[M] / [F]
        String gender = (String) ret.get("SEX");
        if ("M".equalsIgnoreCase(gender)) {
            vo.setGender("Male");
        } else if ("F".equalsIgnoreCase(gender)) {
            vo.setGender("Female");
        } else {
            vo.setGender("Unknown");
        }

        // 4[CARDNO]=[372927193905200014]
        vo.setNationalId((String) ret.get("CARDNO"));
        // 5[BIRTHDAY]=[1939-05-20 00:00:00.0]
        // vo.setBirthday(new Date((String)ret.get("BIRTHDAY")));
        Object birthObj = ret.get("BIRTHDAY");
        if (birthObj != null) {
            String birStr = birthObj.toString().replace("-", "").replace(":", "");
            if (birStr.length() >= 8) {
                try {
                    vo.setBirthday(new SimpleDateFormat("yyyyMMdd").parse(birStr.substring(0, 8)));
                } catch (ParseException e) {
                    log.error("Parse BIRTHDAY[{}] Exception: {}", birStr, e.getMessage());
                }
            }
        }
        // 6[AGE]=[79岁]
        vo.setAge((String) ret.get("AGE"));
        // 7[DEPENDENCY_ID]=[山东省梁山县水泊东路57号4号楼5单元201室]

        // 8[DOMATE_NO]=[]
        // 9[LESION]=[内科五病区]
        // 10[PACT_CODE]=[1]
        vo.setInsuranceTypeCode((String) ret.get("PACT_CODE"));
        // 11[PACT_NAME]=[现金]
        vo.setInsuranceType((String) ret.get("PACT_NAME"));

        // 12[MEDICAL_NUMBER]=[]
        // 13[BED_NUMBER]=[210542]
        vo.addDynamicFormItem("chuangHao", (String) ret.get("BED_NUMBER"));
        // 14[DEPARTMENT]=[内科五病区]
        vo.addDynamicFormItem("bingQu", (String) ret.get("DEPARTMENT"));
        vo.addDynamicFormItem("yiZhuXinXi", (String) ret.get("ITEM_NAME"));
        // 15[COMPANY]=[山东省梁山县水泊东路57号4号楼5单元201室]
        vo.setAddress((String) ret.get("COMPANY"));
        // 16[SOURCE_ID]=[]
        // 17[HEIGHT]=[]
        // 18[WEIGHT]=[]
        // 19[PREGNANCY_HISTORY]=[]
        // 20[TRANSFUSION_HISTORY]=[]
        vo.addDynamicFormItem("TRANSFUSION_HISTORY", (String) ret.get("TRANSFUSION_HISTORY"));
        // 21[APPLICATION_BLOOD]=[]
        vo.addDynamicFormItem("APPLICATION_BLOOD", (String) ret.get("APPLICATION_BLOOD"));
        // 22[RH_TYPE]=[]
        vo.addDynamicFormItem("RH_TYPE", (String) ret.get("RH_TYPE"));
        // 23[CLINICAL_DIGNOSIS]=[结肠癌]
        vo.setDiagnosisDesc((String) ret.get("CLINICAL_DIGNOSIS"));
        // 24[PREGNANCY]=[]
        // 25[PRODUCTION]=[]
        // 26[REACTION_HISTORY]=[]
        final String allergyInfo = (String) ret.get("REACTION_HISTORY");
        if (allergyInfo != null && allergyInfo.trim().length() > 0) {
            vo.setAllergyInfo(allergyInfo.trim());
        } else {
            vo.setAllergyInfo("无");
        }
        // 27[IRREGULAR_ANTIBODY]=[]
        // 28[IN_DATE]=[2018-05-23 09:10:10.0]
        Object inDate = ret.get("IN_DATE");
        if (inDate != null) {
            vo.addDynamicFormItem("ruYuanShiJian", inDate.toString());
        }
        // 29[DIAG_NAME]=[]
        // 主要症状和体征 & 超声CT检查所见
        String mainSymptom = (String) ret.get("MAIN_SYMPTOM");
        if (mainSymptom != null) {
            vo.addDynamicFormItem("zhuYaoZhengZhuangHeTiZheng", mainSymptom);
        }

        String examinationFinding = (String) ret.get("EXAMINATION_FINDING");
        if (examinationFinding == null) {
            vo.addDynamicFormItem("jianChaSuoJian", examinationFinding);
        }

        // 1.患者联系电话 TEL
        // 2.紧急联系人地址 lINKMAN_ADD
        // 3.紧急联系人电话 LINKMAN_TEL
        vo.setTelephone((String) ret.get("TEL"));
        vo.setContactPerson((String) ret.get("lINKMAN_ADD"));
        vo.setContactPhone((String) ret.get("LINKMAN_TEL"));
        log.debug("Output: {}", vo);
        return vo;
    }

    protected RegistrationVO dummy() {
        RegistrationVO vo = new RegistrationVO();

        // 1[HOSPITALIZED_NO]=[ZY040000183960]
        // String hospitalizedNo = (String)ret.get("HOSPITALIZED_NO");
        // if (hospitalizedNo != null && hospitalizedNo.length() >= 10) {
        // hospitalizedNo = hospitalizedNo.substring(hospitalizedNo.length() -
        // 10);
        // }
        String dymmyAriaId = new SimpleDateFormat("yyMddHmmss").format(new Date());
        vo.setAriaId("v10" + dymmyAriaId);
        vo.setHisId("01" + dymmyAriaId);
        vo.setPatientSource("住院");

        // 2[NAME]=[胡清水]
        // vo.setChineseName((String)ret.get("NAME"));
        vo.setChineseName("胡清水" + dymmyAriaId.substring(6, 8)) ;
        // 3[SEX]=[M] / [F]
        if (System.currentTimeMillis() / 2 == 0) {
            vo.setGender("Male");
        } else {
            vo.setGender("Female");
        }
        // 4[CARDNO]=[372927193905200014]
        vo.setNationalId("110108" + dymmyAriaId);
        // 5[BIRTHDAY]=[1939-05-20 00:00:00.0]
        // vo.setBirthday(new Date((String)ret.get("BIRTHDAY")));
        Object birthObj = "1939-05-20 00:00:00.0";
        if (birthObj != null) {
            String birStr = birthObj.toString().replace("-", "").replace(":", "");
            if (birStr.length() >= 8) {
                try {
                    vo.setBirthday(new SimpleDateFormat("yyyyMMdd").parse(birStr.substring(0, 8)));
                } catch (ParseException e) {
                    log.error("Parse BIRTHDAY[{}] Exception: {}", birStr, e.getMessage());
                }
            }
        }
        // 6[AGE]=[79岁]
        vo.setAge("22");//应该不重新计算
        // 7[DEPENDENCY_ID]=[山东省梁山县水泊东路57号4号楼5单元201室]

        // 8[DOMATE_NO]=[]
        // 9[LESION]=[内科五病区]
        // 10[PACT_CODE]=[1]
        vo.setInsuranceTypeCode("1");
        // 11[PACT_NAME]=[现金]
        vo.setInsuranceType("现金");

        // 12[MEDICAL_NUMBER]=[]
        // 13[BED_NUMBER]=[210542]
        vo.addDynamicFormItem("chuangHao", "210542");
        // 14[DEPARTMENT]=[内科五病区]
        vo.addDynamicFormItem("bingQu", "内科五病区");
        // 15[COMPANY]=[山东省梁山县水泊东路57号4号楼5单元201室]
        vo.setAddress("山东省梁山县水泊东路57号4号楼5单元201室");
        // 16[SOURCE_ID]=[]
        // 17[HEIGHT]=[]
        // 18[WEIGHT]=[]
        // 19[PREGNANCY_HISTORY]=[]
        // 20[TRANSFUSION_HISTORY]=[]
        // 21[APPLICATION_BLOOD]=[]
        // 22[RH_TYPE]=[]
        // 23[CLINICAL_DIGNOSIS]=[结肠癌]
        vo.setDiagnosisDesc("结肠癌");
        // 24[PREGNANCY]=[]
        // 25[PRODUCTION]=[]
        // 26[REACTION_HISTORY]=[]
        vo.setAllergyInfo("有过敏的信息");
        // 27[IRREGULAR_ANTIBODY]=[]
        // 28[IN_DATE]=[2018-05-23 09:10:10.0]
        // vo.setDiagnosisDate((String)ret.get("IN_DATE"));
        vo.addDynamicFormItem("ruYuanShiJian", "2018年05月23日");
        // 29[DIAG_NAME]=[]
//		1.患者联系电话 TEL
//		2.紧急联系人地址 lINKMAN_ADD
//		3.紧急联系人电话 LINKMAN_TEL
        vo.setTelephone("13688888888");
        vo.setContactPerson("紧急联系人");
        vo.setContactPhone("010-110");
        return vo;
    }
}
