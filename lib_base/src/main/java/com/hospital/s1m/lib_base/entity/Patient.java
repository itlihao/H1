package com.hospital.s1m.lib_base.entity;

import com.hospital.s1m.lib_base.utils.PinyinUtils;

/**
 * @author 杨亚坤
 */
public class Patient {
    /**
     * 本地id
     */
    private String id;
    /**
     * 后台id
     */
    private String idS;
    /**
     * 患者表欠款totalArrears字段
     */
    private int totalArrears;
    /**
     * 家庭住址
     */
    private String homeAddress;
    /**
     * 名字
     */
    private String userName;
    /**
     * 简拼
     */
    private String userShortName;
    /**
     * 过敏史
     */
    private String allergicHistory;
    /**
     * 疾病史
     */
    private String illHistory;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 性别 1、男 2、女 3、其它
     */
    private int sex;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 家庭地址
     */
    private String address;
    /**
     * 阅读设备类型
     */
    private String diagnose;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 签发机关
     */
    private String agency;
    /**
     * 民族
     */
    private String folk;
    /**
     * 有效期
     */
    private String validityTime;
    /**
     * 有无指纹有无指纹（0： 没有  1：有）（刷身份证）
     */
    private int fingerPrint;
    /**
     * 阅读设备类型 阅读设备(0：手动输入 1 :ZKT  2: 精伦)（刷身份证）
     */
    private int deviceType;
    /**
     * 数据在服务器端的版本号
     */
    private String baseVersion;
    /**
     * 是否删除 0未删 1已删
     */
    private String isDelete;
    /**
     * 患者来源 0:其他，1:致医健康
     */
    private String patientSource;

    public String simpleNumber;

    // 联系人拼音全拼
    public String conPinyin;
    // 联系人拼音字头
    public String conPinyinHdr;

    public Patient(String id, String idS, int totalArrears, String homeAddress, String userName,
                   String userShortName, String allergicHistory, String illHistory, String birthday,
                   int sex, String phone, String address, String diagnose, String idCardNo,
                   String agency, String folk, String validityTime, int fingerPrint, int deviceType,
                   String baseVersion, String isDelete, String patientSource) {
        this.id = id;
        this.idS = idS;
        this.totalArrears = totalArrears;
        this.homeAddress = homeAddress;
        this.userName = userName;
        this.userShortName = userShortName;
        this.allergicHistory = allergicHistory;
        this.illHistory = illHistory;
        this.birthday = birthday;
        this.sex = sex;
        this.phone = phone;
        this.address = address;
        this.diagnose = diagnose;
        this.idCardNo = idCardNo;
        this.agency = agency;
        this.folk = folk;
        this.validityTime = validityTime;
        this.fingerPrint = fingerPrint;
        this.deviceType = deviceType;
        this.baseVersion = baseVersion;
        this.isDelete = isDelete;
        this.patientSource = patientSource;
    }

    public Patient() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdS() {
        return this.idS;
    }

    public void setIdS(String idS) {
        this.idS = idS;
    }

    public int getTotalArrears() {
        return this.totalArrears;
    }

    public void setTotalArrears(int totalArrears) {
        this.totalArrears = totalArrears;
    }

    public String getHomeAddress() {
        return this.homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.conPinyin = PinyinUtils.getPinYin(this.userName);
        this.conPinyinHdr = PinyinUtils.getPinyinjp(this.userName);
    }

    public String getUserShortName() {
        return this.userShortName;
    }

    public void setUserShortName(String userShortName) {
        this.userShortName = userShortName;
    }

    public String getAllergicHistory() {
        return this.allergicHistory;
    }

    public void setAllergicHistory(String allergicHistory) {
        this.allergicHistory = allergicHistory;
    }

    public String getIllHistory() {
        return this.illHistory;
    }

    public void setIllHistory(String illHistory) {
        this.illHistory = illHistory;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        if (phone != null) {
            this.simpleNumber = phone.replaceAll("\\-|\\s", "");
        }
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiagnose() {
        return this.diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getIdCardNo() {
        return this.idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getAgency() {
        return this.agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getFolk() {
        return this.folk;
    }

    public void setFolk(String folk) {
        this.folk = folk;
    }


    public int getFingerPrint() {
        return this.fingerPrint;
    }

    public void setFingerPrint(int fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public int getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }


    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getPatientSource() {
        return this.patientSource;
    }

    public void setPatientSource(String patientSource) {
        this.patientSource = patientSource;
    }

    public String getValidityTime() {
        return this.validityTime;
    }

    public void setValidityTime(String validityTime) {
        this.validityTime = validityTime;
    }

    public String getBaseVersion() {
        return this.baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", idS='" + idS + '\'' +
                ", totalArrears=" + totalArrears +
                ", homeAddress='" + homeAddress + '\'' +
                ", userName='" + userName + '\'' +
                ", userShortName='" + userShortName + '\'' +
                ", allergicHistory='" + allergicHistory + '\'' +
                ", illHistory='" + illHistory + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", diagnose='" + diagnose + '\'' +
                ", idCardNo='" + idCardNo + '\'' +
                ", agency='" + agency + '\'' +
                ", folk='" + folk + '\'' +
                ", validityTime='" + validityTime + '\'' +
                ", fingerPrint=" + fingerPrint +
                ", deviceType=" + deviceType +
                ", baseVersion='" + baseVersion + '\'' +
                ", isDelete='" + isDelete + '\'' +
                ", patientSource='" + patientSource + '\'' +
                '}';
    }
}
