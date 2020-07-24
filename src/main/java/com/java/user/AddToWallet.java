
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
			credentialProperties.put("mspID",Config.ORGA_MSP);
			credentialProperties.put("certPath", Config.ORGA_USR_ADMIN_CERT);
			credentialProperties.put("keyPath", Config.ORGA_USR_ADMIN_KEY);
			credentialProperties.put("identityName", Config.ORGA+"_admin");
			
			Util.addToWallet(credentialProperties);
				
			credentialProperties = new Properties();
			credentialProperties.put("walletPath",Config.WALLET_PATH);
			credentialProperties.put("mspID",Config.ORGB_MSP);
			credentialProperties.put("certPath", Config.ORGB_USR_ADMIN_CERT);
			credentialProperties.put("keyPath", Config.ORGB_USR_ADMIN_KEY);
			credentialProperties.put("identityName", Config.ORGB+"_admin");
			
			Util.addToWallet(credentialProperties);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
