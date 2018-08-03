package com.varian.oiscn.hisintegration.dto;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gbt1220 on 6/12/2017.
 */
@Data
public class RegistrationVO {

	/** The avatar data String in Base64 */
	protected String photo;
	/** The avatar data in byte */
	protected byte[] photoByte;
	private String patientID;
	private String diagnosis;
	private String id;
	private String ariaId;
	private String hisId;
	private String nationalId;
	private String chineseName;
	private String englishName;
	private String pinyin;
	private String gender;
	private Date birthday;
	private String contactPerson;
	private String contactPhone;
	private String patientSer;
	private String physicianGroupId;
	private String physicianGroupName;
	private String physicianId;
	private String physicianBId;
	private String physicianCId;
	private String physicianName;
	private String physicianBName;
	private String physicianCName;
	private String physicianPhone;
	private String telephone;
	private String address;
	private boolean urgent;
	private String warningText;
	private String healthSummary;
	private String diagnosisCode;
	private String diagnosisDesc;
	private String recurrent;
	private String bodypart;
	private String bodypartDesc;
	private String staging;
	private String stagingScheme;
	private String tcode;
	private String ncode;
	private String mcode;
	private Date diagnosisDate;
	private String diagnosisNote;
	private String cpTemplateId;
	private String cpTemplateName;
	private String patientHistory;
	/** ECOG评分: 0,1,2,3,4 or free text,32 English chars */
	private String ecogScore;
	/** ECOG评分描述 */
	private String ecogDesc;
	/** 阳性标识: free text, 32 English chars */
	private String positiveSign;
	/** 医疗保险code */
	private String insuranceTypeCode;
	/** 医疗保险类型 */
	private String insuranceType;
	/** 患者来源: 门诊/住院 */
	private String patientSource;
	// 当前是否还在治疗中的标志
	private boolean activeFlag = true;

	/**
	 * 医生备注
	 */
	private String physicianComment;

	/**
	 * 过敏信息
	 */
	private String allergyInfo;

	/** 年龄 */
	private String age;

	private String carePathTemplateId;
	private String carePathInstanceId;
	private Map<String, String> dynamicFormItems = new HashMap<>();
	
	public void addDynamicFormItem(String name, String value) {
		dynamicFormItems.put(name, value);
	}
}
