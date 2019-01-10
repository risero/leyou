package com.leyou.sms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 短信属性对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/25 16:48
 */
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {

    private String apikey;
    private String userId;
    private String pwd;
    private String masterIpAddress;
    private String ipAddress1;
    private String ipAddress2;
    private String ipAddress3;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMasterIpAddress() {
        return masterIpAddress;
    }

    public void setMasterIpAddress(String masterIpAddress) {
        this.masterIpAddress = masterIpAddress;
    }

    public String getIpAddress1() {
        return ipAddress1;
    }

    public void setIpAddress1(String ipAddress1) {
        this.ipAddress1 = ipAddress1;
    }

    public String getIpAddress2() {
        return ipAddress2;
    }

    public void setIpAddress2(String ipAddress2) {
        this.ipAddress2 = ipAddress2;
    }

    public String getIpAddress3() {
        return ipAddress3;
    }

    public void setIpAddress3(String ipAddress3) {
        this.ipAddress3 = ipAddress3;
    }
}
