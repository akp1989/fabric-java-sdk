
package com.java.user;

import java.util.Properties;
import com.java.client.CAClient;
import com.java.config.Config;
import com.java.util.Util;

public class RegisterEnrollUser {

	public static void main(String args[]) {
		try {
			
			Properties properties = new Properties();
			properties.put("pemFile","ceadar/crypto/orga/msp/cacerts/ca-cert.pem");
			
			CAClient caClient = new CAClient(Config.CA_ORGA_URL, properties);
			
			// Enroll Admin to OrgAMSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORGA);
			adminUserContext.setMspId(Config.ORGA_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
			
			// Register and Enroll user to OrgAMSP
			UserContext userContext = new UserContext();
			String name = "client1";
			userContext.setName(name);
			userContext.setAffiliation(Config.ORGA);
			userContext.setMspId(Config.ORGA_MSP);

			String eSecret = caClient.registerUser(name,null);
			if(eSecret != null)
			userContext = caClient.enrollUser(userContext, eSecret);
			
			
			// Enroll Admin to OrgBMSP
			properties.clear();
			properties.put("pemFile","ceadar/crypto/orgb/msp/cacerts/ca-cert.pem");
			caClient = new CAClient(Config.CA_ORGB_URL, properties);
			adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORGB);
			adminUserContext.setMspId(Config.ORGB_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
			
			// Register and Enroll user to OrgBMSP
			userContext = new UserContext();
			name = "client1";
			userContext.setName(name);
			userContext.setAffiliation(Config.ORGB);
			userContext.setMspId(Config.ORGB_MSP);

			eSecret = caClient.registerUser(name,null);
			if(eSecret != null)
				userContext = caClient.enrollUser(userContext, eSecret);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
