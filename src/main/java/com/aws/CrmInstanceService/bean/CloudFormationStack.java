package com.aws.CrmInstanceService.bean;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.model.*;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/*
* Make requests to AWS CloudFormation using the AWS SDK for Java.
*/

@Component
public class CloudFormationStack {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ConfigProperties configProperties;


    public Map createStack(Map map) throws Exception{

        logger.info("===========================================");
        logger.info("Getting Started with AWS CloudFormation");
        logger.info("===========================================\n");

        AmazonCloudFormation stackbuilder = AmazonCloudFormationClientBuilder.standard()
                .withCredentials(CredentialsProvider.getProvider())
                .withRegion(Regions.US_EAST_1)
                .build();

        String stackName = null;
        try{
            //set EC2 KeyName
            Collection<Parameter> parameters = new LinkedList<>();
            Parameter individualParam = new Parameter();
            individualParam.withParameterKey("KeyName").withParameterValue(configProperties.getKeypair());
            parameters.add(individualParam);

            //Unique stack name for each request,can be amended the stack with requestor company name
            String company = (String)map.get("company");
            stackName = company.replaceAll("\\s","")+"-"+ UUID.randomUUID().toString();

            CreateStackRequest createRequest = new CreateStackRequest();
            createRequest.setStackName(stackName);
            createRequest.setTemplateURL(configProperties.getS3url());
            createRequest.setParameters(parameters);
            stackbuilder.createStack(createRequest);

            Map outputFromAws = waitForCompletion(stackbuilder, stackName);
            return outputFromAws;
        }
        catch(AmazonServiceException amazonServiceException){
            logger.error("Caught an AmazonServiceException, which means your request made it to AWS CloudFormation, " +
                    "but was rejected with an error response for some reason.",amazonServiceException);
            logger.error("Error Message:    " + amazonServiceException.getMessage());
            logger.error("AWS Error Code:    " + amazonServiceException.getErrorCode());
            throw amazonServiceException;
        }
        catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS CloudFormation, "
                    + "such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
            throw ace;
        }


    }


    //TODO Return true only if CREATE_COMPLETE so as to send email..for POC it is sufficient to assume it is always COMPLETE
    public Map<String, String> waitForCompletion(AmazonCloudFormation stackbuilder, String stackName) throws Exception {

        DescribeStacksRequest wait = new DescribeStacksRequest();
        wait.setStackName(stackName);
        Boolean completed = false;
        String  stackStatus = "Unknown";

        String  stackReason = "";
        Map<String, String> map = new HashMap<>();
        System.out.print("Waiting");

        while (!completed) {
            List<Stack> stacks = stackbuilder.describeStacks(wait).getStacks();
            if (stacks.isEmpty())
            {
                completed   = true;
                stackStatus = "NO_SUCH_STACK";
                stackReason = "Stack has been deleted";
            } else {
                for (Stack stack : stacks) {
                    if (stack.getStackStatus().equals(StackStatus.CREATE_COMPLETE.toString()) ||
                            stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.ROLLBACK_FAILED.toString()) ||
                            stack.getStackStatus().equals(StackStatus.DELETE_FAILED.toString())) {
                        completed = true;
                        stackStatus = stack.getStackStatus();
                        stackReason = stack.getStackStatusReason();
                        List<Output> outputs = stack.getOutputs();
                        for(Output output:outputs){
                            System.out.println(output);
                            map.put(output.getOutputKey(), output.getOutputValue());
                        }

                    }
                }
            }

            // Show we are waiting
            System.out.print(".");

            // Not done yet so sleep for 10 seconds.
            if (!completed) Thread.sleep(10000);
        }

        // Show we are done
        logger.info("===========================================");
        logger.info("Results of AWS CloudFormation");
        logger.info("===========================================\n");

        System.out.print("done\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            logger.info("Key: " + key + " , value :" + value);
        }
        System.out.println("Stack creation completed, the stack " + stackName + " completed with stackStatus " + stackReason);
        return map;
    }
}
