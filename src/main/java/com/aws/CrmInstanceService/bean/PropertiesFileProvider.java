package com.aws.CrmInstanceService.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileProvider {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    void saveProperties(Properties p,String path) throws IOException
    {
        FileOutputStream fout = new FileOutputStream(path);
        p.store(fout, "Properties");
        fout.close();
        logger.info("After saving properties: " + p);
    }

    //Creating properties for UJ-1 DB source properties
    public void createPropUJ1(String ip) throws IOException {
        logger.info("Creating properties file for :" + ip);
        String url = "jdbc:mysql://"+ip+":3306/bitnami_suitecrm";
        Properties prop = new Properties();
        prop.setProperty("spring.datasource.url",url);
        prop.setProperty("spring.datasource.username","root");
        prop.setProperty("spring.datasource.password","");
        prop.setProperty("spring.jpa.show-sql","true");
        prop.setProperty("server.port","3001");
        String path = "/home/bitnami/application-uj1.properties";
        saveProperties(prop,path);


    }

    protected void createPropUJ2(){

    }

    protected void createPropUJ3(){

    }

    public static void main(String args[]) throws IOException {
        PropertiesFileProvider p = new PropertiesFileProvider();
        p.createPropUJ1("10.1.1.1");
    }
}
