package com.aws.CrmInstanceService.controller;

import com.aws.CrmInstanceService.SendMailSSL;
import com.aws.CrmInstanceService.bean.CloudFormationStack;
import com.aws.CrmInstanceService.bean.ConfigProperties;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import freemarker.template.Configuration;
import freemarker.template.Template;

import freemarker.template.TemplateException;
@CrossOrigin

@RestController
public class CreateCRMInstanceController {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private CloudFormationStack cloudFormationStack;

    @Autowired
    private SendMailSSL sendEmail;

    @Autowired
    private ConfigProperties configProperties;

    @CrossOrigin
    @RequestMapping(value="/invokeTheCFStack",method = RequestMethod.POST, produces = { "application/json" })
    public ResponseEntity<Object> invoke(@RequestBody String json){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Map userInputMap = gson.fromJson(json, Map.class);

        String emailId = (String) userInputMap.get("email");
        logger.info("Invoke Rest Controller with user Info : emailId : " + emailId);
        String body = null;
        String dummyEmailResponse = "<p>Dear ,</p>\n" +
                "<p>We are pleased to inform you that CRM is now available for your use.</p>\n" +
                "\n" +
                "<p>Please find the access link, user name and password to access your account.</p>\n" +
                "\n" +
                "<p>Link: <a href =\"http://ec2-54-235-3-213.compute-1.amazonaws.com\"> http://ec2-54-235-3-213.compute-1.amazonaws.com </a></p>\n" +
                "\n" +
                "<p>User Name: user </p>" +
                "<p>Password: bitnami </p>\n" +
                "\n" +
                "<p> Please feel free to reach out to us for any clarification or support you need. </p>\n" +
                "\n" +
                "<p>Warm Regards,</p>\n" +
                "<p>Customer Support</p>";
        try {

            //Call the cloud-formation stack
            //Map<String,String> cfoutput = cloudFormationStack.createStack(userInputMap);

            //Generate email body using cloud-formation output
            //body = generateEmailBody(userInputMap,cfoutput);
            body = dummyEmailResponse;



        } catch (Exception e) {
            e.printStackTrace();
           return new ResponseEntity<Object>("CRM Instance creation failed ", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<Object>(body, HttpStatus.OK);
    }

    private String generateEmailBody(Map userInputMap, Map<String, String> cfoutput) {

        String fname = (String) userInputMap.get("fname");
        String PublicDNS = cfoutput.get("PublicDNS");
        Configuration cfg = new Configuration();
        String body = null;
        try {
            Template template = cfg.getTemplate("src\\email_template.tfl\\");
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("fname", fname);
            data.put("PublicDNS", PublicDNS);

            StringWriter outTitle = new StringWriter();
            template.process(data, outTitle);
            StringBuffer sb = outTitle.getBuffer();
            body = sb.toString();
            logger.info("Email Body : " + body);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return body;
    }



    public static void main(String args[]){
        Map userInputMap = new HashMap();
        userInputMap.put("fname","Alan");

        Map cfoutput = new HashMap();
        cfoutput.put("PublicDNS","13.13.13.13");
        String body = new CreateCRMInstanceController().generateEmailBody(userInputMap,cfoutput);
        SendMailSSL sendMailSSL = new SendMailSSL();
        sendMailSSL.sendEmail("prasad.durga1@wipro.com",body);
    }
}
