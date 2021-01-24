/*
SPDX-License-Identifier: Apache-2.0
*/

package com.akp.fabric;

import java.nio.file.Paths;
import java.util.Properties;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class RegisterEnroll {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(String[] args) throws Exception {

		// Create a CA client for interacting with the CA.
		Properties properties = new Properties();
		/* Properties to pass the TLS CA certificates for SSL connection */
		properties.put("pemFile","ceadar/crypto/orga/msp/cacerts/ca-cert.pem");
		//properties.put("pemFile","ceadar/ca-cert.pem");
		properties.put("allowAllHostNames", "true");
		CAClient caClient = new CAClient("https://192.168.0.175:7010", properties);
		
		// Create a wallet for managing identities
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("ceadar","gatewayWalletTest"));
		//Wallet wallet = Wallet.createFileSystemWallet(Paths.get("ceadar","test","wallet"));
		// Check to see if we've already enrolled the admin user.
		boolean adminExists = wallet.exists("orga_caadmin"); 
		if (!adminExists) {
			//If not enroll the admin user
			Enrollment enrollment = caClient.enrollUser("admin", "adminroot",new EnrollmentRequest(), null); 
			Identity caadmin = Identity.createIdentity("OrgAMSP", enrollment.getCert(),enrollment.getKey());
		    wallet.put("orga_caadmin", caadmin); 
		 } 
		 Identity adminIdentity = wallet.get("orga_caadmin");
		 //Set the admin user context with which we will register new ID's
		 UserContext adminUser = new UserContext(); adminUser.setName("admin");
		 adminUser.setIdentity(adminIdentity);
		 adminUser.setMspId(adminIdentity.getMspId()); 
		 
		 boolean userExists = wallet.exists("orgb_client01");
		 if(userExists)
			 wallet.remove("orgb_client01");

		 //String responsesecret = caClient.registerUser("orgbclient01","orgb","client",adminUser,null);
		 //System.out.println(responsesecret);
    
	     // Enroll the User and add it to wallet
		  EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
		  enrollmentRequest.addHost("127.0.0.1");
		  enrollmentRequest.addHost("localhost");
		  enrollmentRequest.addHost("192.168.0.175");
		  enrollmentRequest.addHost("0.0.0.0");
		  enrollmentRequest.addHost("*.orgb.akp.com");
		  //enrollmentRequest.setProfile("tls");
		  Enrollment enrollment = caClient.enrollUser("orgbclient01", "wZVzWLKHSgFp", enrollmentRequest, null);
		  Identity user = Identity.createIdentity("OrgBMSP", enrollment.getCert(),enrollment.getKey()); 
		  wallet.put("orgb_client01", user); 
		  System.out.println(enrollment.getCert().toString());
	}
}
