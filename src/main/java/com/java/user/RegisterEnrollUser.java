
package com.java.user;

import java.util.Properties;
import com.java.client.CAClient;
import com.java.config.Config;
import com.java.util.Util;

public class RegisterEnrollUser {

	public static void main(String args[]) {
		try {
			
			Properties properties = new Properties();
			properties.put("pemFile","ceadar/crypto/org1/msp/cacerts/ca-cert.pem");
			
			CAClient caClient = new CAClient(Config.CA_ORG_URL, properties);
			
			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG1);
			adminUserContext.setMspId(Config.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
			
			// Register and Enroll user to Org1MSP
			UserContext userContext = new UserContext();
//			String name = "client1_org1";
//			userContext.setName(name);
//			userContext.setAffiliation(Config.ORG1);
//			userContext.setMspId(Config.ORG1_MSP);
//
//			String eSecret = caClient.registerUser(name,null);
//			if(eSecret != null)
//			userContext = caClient.enrollUser(userContext, eSecret);
			
			
			// Enroll Admin to Org2MSP
			properties.clear();
			properties.put("pemFile","ceadar/crypto/org2/msp/cacerts/ca-cert.pem");
			caClient = new CAClient(Config.CA_ORG_URL, properties);
			adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG2);
			adminUserContext.setMspId(Config.ORG2_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
			
			// Register and Enroll user to Org2MSP
			userContext = new UserContext();
			String name = "client1_org2";
			userContext.setName(name);
			userContext.setAffiliation(Config.ORG2);
			userContext.setMspId(Config.ORG2_MSP);
			
			String eSecret = caClient.registerUser(name,null);
			if(eSecret != null)
				userContext = caClient.enrollUser(userContext, eSecret);
			
			// Enroll Admin to Org3MSP
//			properties.clear();
//			properties.put("pemFile","ceadar/crypto/org3/msp/cacerts/ca-cert.pem");
//			caClient = new CAClient(Config.CA_ORG_URL, properties);
//			adminUserContext = new UserContext();
//			adminUserContext.setName(Config.ADMIN);
//			adminUserContext.setAffiliation(Config.ORG3);
//			adminUserContext.setMspId(Config.ORG3_MSP);
//			caClient.setAdminUserContext(adminUserContext);
//			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
//			
//			// Register and Enroll user to Org3MSP
//			userContext = new UserContext();
//			name = "client1_org3";
//			userContext.setName(name);
//			userContext.setAffiliation(Config.ORG3);
//			userContext.setMspId(Config.ORG3_MSP);
//
			eSecret = caClient.registerUser(name,null);
			if(eSecret != null)
				userContext = caClient.enrollUser(userContext, eSecret);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
