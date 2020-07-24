package com.java.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.sdk.TransactionRequest.Type;

public class Config {
	
	public static final Path WALLET_PATH = Paths.get("ceadar","wallet");
	
	public static final String ORGA_MSP = "OrgAMSP";

	public static final String ORGA = "orga";

	public static final String ORGB_MSP = "OrgBMSP";

	public static final String ORGB = "orgb";

	public static final String ADMIN = "admin";

	public static final String ADMIN_PASSWORD = "adminroot";
	
	public static final String CHANNEL_CONFIG_PATH = "ceadar/channelartifacts/";
	
	public static final String DEFAULT_CLIENT_ID = "client";
	
	// File location of the organization admin users
	public static final String ORGA_USR_ADMIN_CERT = "ceadar" +
									File.separator + "crypto" +
									File.separator + "orga" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "signcerts"+ 
									File.separator + "cert.pem";
	
	public static final String ORGB_USR_ADMIN_CERT = "ceadar" +
									File.separator + "crypto" +
									File.separator + "orgb" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "signcerts"+ 
									File.separator + "cert.pem";
	
	public static final String ORGA_USR_ADMIN_KEY = "ceadar" +
									File.separator + "crypto" +
									File.separator + "orga" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "keystore"+ 
									File.separator + "27b106ea35f05f8df8e3371bf02a176965d476064cbc2dcddaa8f0bd7c21023c_sk";
	
	public static final String ORGB_USR_ADMIN_KEY = "ceadar" +
									File.separator + "crypto" +
									File.separator + "orgb" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "keystore"+ 
									File.separator + "7c2cffeece4fab8a0f1719ba72cf5afce213b3f770ccf9b3b0ed04ac868a8010_sk";
	
	public static final String CA_ORGA_URL = "https://192.168.0.175:7010";
	
	public static final String CA_ORGB_URL = "https://192.168.0.175:9010";
	
	public static final String ORDERER_URL = "grpcs://192.168.0.175:7050";
	
	public static final String ORDERER_NAME = "orderer1.akp.com";
	
	public static final String CHANNEL_NAME = "clientchannel";
	
	public static final String ORGA_PEER_0 = "peer0.orga.akp.com";
	
	public static final String ORGA_PEER_0_URL = "grpcs://192.168.0.175:7051";

	
    public static final String ORGB_PEER_0 = "peer0.orgb.akp.com";
	
	public static final String ORGB_PEER_0_URL = "grpcs://192.168.0.175:9051";
	
 
	/*****************************************************************************************************************
	 * For Go lang chaincode the path structure will be <chaincode root dir>/src/<chaincode_path>
	 * Example : 
	 * 		 CHAINCODE_ROOT_DIR = "ceadar/chaincode";
	 * 		 CHAINCODE_1_NAME = "fabcar";
	 *       CHAINCODE_1_PATH = "github.com/fabcar";
	 *       
	 * For node chaincode only the <chaincode_root_dir> will be considered and the <chaincode_path> should be null
	 * Example : 
	 * 		 CHAINCODE_ROOT_DIR = "ceadar/chaincode/src/example02";
	 * 		 CHAINCODE_1_NAME = "chaincode_example02";
	 *       CHAINCODE_1_PATH = "null";
	 *  
	 *****************************************************************************************************************/
	public static final String CHAINCODE_1_ENDORSEMENT = "ceadar/chaincode/src/example02/endorsement/endorsementcondition.yaml";
	
	public static final String CHAINCODE_ROOT_DIR = "ceadar/chaincode/src/example02";
	
	public static final String CHAINCODE_1_NAME = "chaincode_example02";
	
	public static final String CHAINCODE_1_PATH = "null";
	
	public static final String CHAINCODE_1_VERSION = "4";
	
	// Can be Type.NODE or Type.GO_LANG or Type.JAVA
	public static final Type CHAINCODE_LANGUAGE = Type.NODE;
	
	// Can be INSTANTIATION or UPGRADE
	public static final String CHAINCODE_ACTION = "UPGRADE";
	

}
