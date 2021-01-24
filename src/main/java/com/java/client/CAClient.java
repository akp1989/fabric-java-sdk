package com.java.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.java.config.Config;
import com.java.user.UserContext;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class CAClient {

	String caUrl;
	Properties caProperties;

	HFCAClient instance;

	UserContext adminContext;
	Wallet wallet;
	
	// Constructor
	public CAClient(String caUrl, Properties caProperties) throws IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException, IOException {
		this.caUrl = caUrl;
		this.caProperties = caProperties;
		init();
	}
	
	// Creates a new fabric instance with the given CAURL and CA properties
	public void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException, IOException {
		
		instance = HFCAClient.createNewInstance(caUrl, caProperties);
		
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		instance.setCryptoSuite(cryptoSuite);
		
		wallet = Wallet.createFileSystemWallet(Config.WALLET_PATH);
	}

	public UserContext getAdminUserContext() {
		return adminContext;
	}

 
	public void setAdminUserContext(UserContext userContext) {
		this.adminContext = userContext;
	}

	

	public HFCAClient getInstance() {
		return instance;
	}

	/**
	 * Enroll admin user.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public UserContext enrollAdminUser(String username, String password) throws Exception {

/* Changes to remove the file structure user details to wallet level user details */
//		UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), username);
//		if (userContext != null) {
//			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl + " admin is already enrolled.");
//			return userContext;
//		}
		String identityName = adminContext.getAffiliation()+"."+username;
		
		if(!wallet.exists(identityName)) {
			//Create an enrollment request to add the hostnames and enrollment type if needed
			EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
			enrollmentRequest.addHost("localhost");
			enrollmentRequest.addHost("192.168.0.175");
			
			// Enroll the ID against the CA
			Enrollment adminEnrollment = instance.enroll(username, password,enrollmentRequest);
			adminContext.setIdentity(Identity.createIdentity(adminContext.getMspId(), adminEnrollment.getCert(),adminEnrollment.getKey()));
			
			//Add the new admin identity to wallet
			wallet.put(identityName, adminContext.getIdentity());

			Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "CA -" + caUrl + " Enrolled Admin.");
//			Util.writeUserContext(adminContext);
			return adminContext;
		}else {
			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl + "admin ID already enrolled");
			adminContext.setIdentity(wallet.get(identityName));
			return adminContext;
		}
	}

	/**
	 * Register user.
	 * 
	 * @param username
	 * @param organization
	 * @return
	 * @throws Exception
	 */
	public String registerUser(String username, String idType) throws Exception {
		String identityName = adminContext.getAffiliation()+"_"+ username;
		
		if(!wallet.exists(identityName)) {
		RegistrationRequest registrationRequest = new RegistrationRequest(username);
		if(idType == null) idType = Config.DEFAULT_CLIENT_ID;
	    registrationRequest.setType(idType);
		String enrollmentSecret = instance.register(registrationRequest, adminContext);
		Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "CA -" + caUrl + " Registered User - " + username);
		return enrollmentSecret;
		}else {
			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl + username + " already registered");
			return null;
		}
		
	}

	/**
	 * Enroll user.
	 * 
	 * @param user
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public UserContext enrollUser(UserContext user, String secret) throws Exception {
		String identityName = user.getAffiliation()+"_"+ user.getName();
		
		if(!wallet.exists(identityName)) {
			//Create an enrollment request to add the hostnames and enrollment type if needed
			EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
			enrollmentRequest.addHost("localhost");
			enrollmentRequest.addHost("192.168.0.175");
			enrollmentRequest.addHost("192.168.0.66");
			
			// Enroll the ID against the CA
			Enrollment adminEnrollment = instance.enroll(user.getName(), secret,enrollmentRequest);
			user.setIdentity(Identity.createIdentity(adminContext.getMspId(), adminEnrollment.getCert(),adminEnrollment.getKey()));
			
			//Add the new admin identity to wallet
			wallet.put(identityName, adminContext.getIdentity());
			Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "CA -" + caUrl +" Enrolled User - " + user.getName());
			
			return user;
		}else {
			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl + user.getName() + "already enrolled");
			user.setIdentity(wallet.get(identityName));
			return user;
			 
		}
		
	}

}
