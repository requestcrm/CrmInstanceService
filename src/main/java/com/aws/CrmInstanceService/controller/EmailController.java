package com.aws.CrmInstanceService.controller;

import com.aws.CrmInstanceService.SendMailSSL;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class EmailController {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    @Autowired
    private SendMailSSL sendEmail;

    @CrossOrigin
    @RequestMapping(value="/sendEmailFor",method = RequestMethod.POST)
    public ResponseEntity sendEmailFor(@RequestBody String json){

        Gson gson = new Gson();
        Map emailDataInput = gson.fromJson(json, Map.class);
        String emailBody = (String) emailDataInput.get("emailBody");
        String emailId = (String) emailDataInput.get("email");
        logger.info("emailBody:" + emailBody);
        logger.info("emailId:" + emailId);

        boolean mailStatus =  sendEmail.sendEmail(emailId,emailBody);
        //boolean mailStatus =  true;

        if(mailStatus){
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

}
