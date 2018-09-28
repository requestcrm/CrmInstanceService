package com.aws.CrmInstanceService.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "aws")
@Component
public class ConfigProperties {

    private String s3url;
    private String keypair;

    private String host;
    private String smtpusername;
    private String smtppassword;
    private String smptpport;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getSendemail() {
        return sendemail;
    }

    public void setSendemail(String sendemail) {
        this.sendemail = sendemail;
    }

    private String sendemail;

    public String getFrommailid() {
        return frommailid;
    }

    public void setFrommailid(String frommailid) {
        this.frommailid = frommailid;
    }

    private String frommailid;

    public String getBccemail() {
        return bccemail;
    }

    public void setBccemail(String bccemail) {
        this.bccemail = bccemail;
    }

    private String bccemail;

    public String getEmaildomain() {
        return emaildomain;
    }

    public void setEmaildomain(String emaildomain) {
        this.emaildomain = emaildomain;
    }

    private String emaildomain;

    public String getS3url() {
        return s3url;
    }

    public void setS3url(String s3url) {
        this.s3url = s3url;
    }

    public String getKeypair() {
        return keypair;
    }

    public void setKeypair(String keypair) {
        this.keypair = keypair;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSmtpusername() {
        return smtpusername;
    }

    public void setSmtpusername(String smtpusername) {
        this.smtpusername = smtpusername;
    }

    public String getSmtppassword() {
        return smtppassword;
    }

    public void setSmtppassword(String smtppassword) {
        this.smtppassword = smtppassword;
    }

    public String getSmptpport() {
        return smptpport;
    }

    public void setSmptpport(String smptpport) {
        this.smptpport = smptpport;
    }
}
