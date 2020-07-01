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
		//properties.put("pemFile","ceadar/peerOrganizations/org1.example.com/tlsca/tlsca.org1.example.com-cert.pem");
		properties.put("pemFile","ceadar/ca-cert.pem");
		properties.put("allowAllHostNames", "true");
		CAClient caClient = new CAClient("https://localhost:7001", properties);
		
		// Create a wallet for managing identities
		//Wallet wallet = Wallet.createFileSystemWallet(Paths.get("..","ceadar","org1","wallet"));
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("..","ceadar","test","wallet"));
		// Check to see if we've already enrolled the admin user.
		boolean adminExists = wallet.exists("caadmin"); 
		if (!adminExists) {
			//If not enroll the admin user
			Enrollment enrollment = caClient.enrollUser("admin", "adminroot",new EnrollmentRequest(), null); 
			Identity admintest = Identity.createIdentity("Org1MSP", enrollment.getCert(),enrollment.getKey());
		    wallet.put("caadmin", admintest); 
		 } 
		 Identity adminIdentity = wallet.get("caadmin");
		 //Set the admin user context with which we will register new ID's
		 UserContext adminUser = new UserContext(); adminUser.setName("admin");
		 adminUser.setIdentity(adminIdentity);
		 adminUser.setMspId(adminIdentity.getMspId()); 
		 
		 boolean userExists = wallet.exists("testuser1");
		 if(userExists)
			 wallet.remove("testuser1");

		 String responsesecret = caClient.registerUser("testuser1","org1","user",adminUser,null);
		 System.out.println(responsesecret);
    
	     // Enroll the User and add it to wallet
		  EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
		  enrollmentRequest.addHost("192.168.0.66");
		  enrollmentRequest.addHost("10.0.2.2");
		  //enrollmentRequest.setProfile("tls");
		  Enrollment enrollment = caClient.enrollUser("testuser1", responsesecret, enrollmentRequest, null);
		  Identity user = Identity.createIdentity("Org1MSP", enrollment.getCert(),
		  enrollment.getKey()); 
		  wallet.put("testuser1", user); 
		  System.out.println(enrollment.getCert().toString());
	}
}
