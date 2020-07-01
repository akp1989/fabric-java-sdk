package com.akp.fabric;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

/**
 * Class to register and enroll users.
 * 
 * @author Prabhakaran AK
 *
 */

public class CAClient {

	String caUrl;
	Properties caProperties;
	HFCAClient instance;


	/**
	 * Constructor
	 * 
	 * @param caUrl 
	 * @param caProperties
	 * @throws MalformedURLException
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws InvalidArgumentException 
	 * @throws CryptoException 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public CAClient(String caUrl, Properties caProperties) throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
		this.caUrl = caUrl;
		this.caProperties = caProperties;
		init();
	}

	public void init() throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		instance = HFCAClient.createNewInstance(caUrl, caProperties);
		instance.setCryptoSuite(cryptoSuite);
	}

	public HFCAClient getInstance() {
		return instance;
	}

	/**
	 * Enroll user.
	 * 
	 * @param user
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public Enrollment enrollAdmin(String username, String secret, Properties properties) throws Exception {
		
		final EnrollmentRequest enrollementRequest = new EnrollmentRequest();
		if(properties != null && !properties.isEmpty())
		{
			enrollementRequest.addHost("localhost");
			enrollementRequest.addHost("192.168.0.66");
			
		}
		Enrollment enrollment = instance.enroll(username, secret);
		return enrollment;
	}
	
	/**
	 * Enroll user.
	 * 
	 * @param user
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public Enrollment enrollUser(String username, String secret, EnrollmentRequest enrollmentRequest, Properties properties) throws Exception {
		
		if(properties != null && !properties.isEmpty())
		{
			
			
		}
		Enrollment enrollment = instance.enroll(username, secret, enrollmentRequest);
		return enrollment;
	}
	/**
	 * Register user.
	 * 
	 * @param username
	 * @param organization
	 * @return
	 * @throws Exception
	 */
	public String registerUser(String username, String organization, String idType,UserContext registrarUser,Properties properties) throws Exception {
 
		RegistrationRequest registrationRequest = new RegistrationRequest(username);
		registrationRequest.setEnrollmentID(username);
		
		// Set the ID type as client by default - Needed to connect via client
		if(idType == null)  
			registrationRequest.setType("client");
		else 
			registrationRequest.setType(idType);

		String enrollmentSecret = instance.register(registrationRequest, registrarUser);
		
		return enrollmentSecret;
	}



}
