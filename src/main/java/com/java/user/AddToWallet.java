
package com.java.user;

import java.util.Properties; 
import com.java.config.Config;
import com.java.util.Util; 
public class AddToWallet {
	
	private static Properties credentialProperties;
	
	public static void main(String args[]) {
		try {
			 
			credentialProperties = new Properties();
			credentialProperties.put("walletPath",Config.WALLET_PATH);
			credentialProperties.put("mspID",Config.ORG1_MSP);
			credentialProperties.put("certPath", Config.ORG1_USR_ADMIN_CERT);
			credentialProperties.put("keyPath", Config.ORG1_USR_ADMIN_KEY);
			credentialProperties.put("identityName", Config.ORG1+"_admin");
			
			Util.addToWallet(credentialProperties);
				
			credentialProperties = new Properties();
			credentialProperties.put("walletPath",Config.WALLET_PATH);
			credentialProperties.put("mspID",Config.ORG2_MSP);
			credentialProperties.put("certPath", Config.ORG2_USR_ADMIN_CERT);
			credentialProperties.put("keyPath", Config.ORG2_USR_ADMIN_KEY);
			credentialProperties.put("identityName", Config.ORG2+"_admin");
			
			Util.addToWallet(credentialProperties);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
