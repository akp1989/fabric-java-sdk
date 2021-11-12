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
		properties.put("pemFile","ripe/crypto/root/msp/tlscacerts/tlsca-cert.pem");
 		properties.put("allowAllHostNames", "true");
		CAClient caClient = new CAClient("https://localhost:17720", properties);
		
		// Create a wallet for managing identities
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("ripe","gatewayWalletTest"));
		//Wallet wallet = Wallet.createFileSystemWallet(Paths.get("ceadar","test","wallet"));
		// Check to see if we've already enrolled the admin user.
		boolean adminExists = wallet.exists("root_caadmin"); 
		if (!adminExists) {
			//If not enroll the admin user
			Enrollment enrollment = caClient.enrollUser("icadmin.boot", "icadminpw",new EnrollmentRequest(), null); 
			Identity caadmin = Identity.createIdentity("RootMSP", enrollment.getCert(),enrollment.getKey());
		    wallet.put("root_caadmin", caadmin); 
		 } 
		 Identity adminIdentity = wallet.get("root_caadmin");
		 //Set the admin user context with which we will register new ID's
		 UserContext adminUser = new UserContext(); adminUser.setName("admin");
		 adminUser.setIdentity(adminIdentity);
		 adminUser.setMspId(adminIdentity.getMspId()); 
		 
//		 boolean userExists = wallet.exists("test_client01");
//		 if(userExists)
//			 wallet.remove("test_client01");
//
//		 String responsesecret = caClient.registerUser("testclient01","test","client",adminUser,null);
//		 System.out.println(responsesecret);
//    
//	     // Enroll the User and add it to wallet
//		  EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
//		  enrollmentRequest.addHost("127.0.0.1");
//		  enrollmentRequest.addHost("localhost"); 
//		  enrollmentRequest.addHost("0.0.0.0");
//		  enrollmentRequest.addHost("*.test.ripe.io");
//		  //enrollmentRequest.setProfile("tls");
//		  Enrollment enrollment = caClient.enrollUser("testclient01", responsesecret, enrollmentRequest, null);
//		  Identity user = Identity.createIdentity("TestMSP", enrollment.getCert(),enrollment.getKey()); 
//		  wallet.put("test_client01", user); 
//		  System.out.println(enrollment.getCert().toString());
		  
		  
		 boolean userExists = wallet.exists("root_client01");
		 if(userExists)
			 wallet.remove("root_client01");

		 String responsesecret = caClient.registerUser("rootclient01","root","client",adminUser,null);
		 System.out.println(responsesecret);
    
	     // Enroll the User and add it to wallet
		  EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
		  enrollmentRequest.addHost("127.0.0.1");
		  enrollmentRequest.addHost("localhost"); 
		  enrollmentRequest.addHost("0.0.0.0");
		  enrollmentRequest.addHost("*.root.ripe.io");
		  //enrollmentRequest.setProfile("tls");
		  Enrollment enrollment = caClient.enrollUser("rootclient01", responsesecret, enrollmentRequest, null);
		  Identity user = Identity.createIdentity("RootMSP", enrollment.getCert(),enrollment.getKey()); 
		  wallet.put("root_client01", user); 
		  System.out.println(enrollment.getCert().toString());
	}
}
