package com.java.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.sdk.TransactionRequest.Type;

public class Config {
	
	//public static final Path WALLET_PATH = Paths.get("ceadar","wallet");
	public static final Path WALLET_PATH = Paths.get("ceadar","gatewayWalletTest");
	
	public static final String ORG1_MSP = "Org1MSP";

	public static final String ORG1 = "org1";

	public static final String ORG2_MSP = "Org2MSP";

	public static final String ORG2 = "org2";
	
	public static final String ORG3_MSP = "Org3MSP";

	public static final String ORG3 = "org3";


	public static final String ADMIN = "admin";

	public static final String ADMIN_PASSWORD = "adminroot";
	
	public static final String CHANNEL_CONFIG_PATH = "ceadar/channelartifacts/";
	
	public static final String DEFAULT_CLIENT_ID = "client";
	
	// File location of the organization admin users
	public static final String ORG1_USR_ADMIN_CERT = "ceadar" +
									File.separator + "crypto" +
									File.separator + "org1" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "signcerts"+ 
									File.separator + "cert.pem";
	
	public static final String ORG2_USR_ADMIN_CERT = "ceadar" +
									File.separator + "crypto" +
									File.separator + "org2" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "signcerts"+ 
									File.separator + "cert.pem";
	
	public static final String ORG3_USR_ADMIN_CERT = "ceadar" +
									File.separator + "crypto" +
									File.separator + "org3" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "signcerts"+ 
									File.separator + "cert.pem";
	
	public static final String ORG1_USR_ADMIN_KEY = "ceadar" +
									File.separator + "crypto" +
									File.separator + "org1" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "keystore"+ 
									File.separator + "c89c636698daf9d6d759338fee9d74732f77fb1ab82509bb0c3beafcce0a2bbc_sk";
	
	public static final String ORG2_USR_ADMIN_KEY = "ceadar" +
									File.separator + "crypto" +
									File.separator + "org2" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "keystore"+ 
									File.separator + "81a8ed8c394a61ccb0da228cc93f1efd1a77d1d823c8e527c105ddd6aac13ea1_sk";
	
	public static final String ORG3_USR_ADMIN_KEY = "ceadar" +
									File.separator + "crypto" +
									File.separator + "org3" + 
									File.separator + "adminuser" +
									File.separator + "msp"+
									File.separator + "keystore"+ 
									File.separator + "af0751b2df9b60397c7c9ed9d6ff0bc599b461e53d2d082098fdb790826e1674_sk";

	
	public static final String CA_ORG_URL = "https://localhost:7710";
	
	//public static final String CA_ORG1_URL = "https://192.168.0.175:7010";
	
	//public static final String CA_ORGB_URL = "https://192.168.0.175:9010";
	
	public static final String ORDERER_URL = "grpcs://localhost:7050";
	
	public static final String ORDERER_NAME = "orderer1.ceadar.org";
	
	public static final String CHANNEL_NAME = "testchannel";
	
	public static final String ORG1_PEER_0 = "peer0.org1.ceadar.org";
	
	public static final String ORG1_PEER_0_URL = "grpcs://localhost:7051";

	
    public static final String ORG2_PEER_0 = "peer0.org2.ceadar.org";
	
	public static final String ORG2_PEER_0_URL = "grpcs://localhost:9051";
	
    public static final String ORG3_PEER_0 = "peer0.org3.ceadar.org";
	
	public static final String ORG3_PEER_0_URL = "grpcs://localhost:11051";
	
 
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
	
	public static final String CHAINCODE_1_NAME = "privateasset";
	
	public static final String CHAINCODE_1_PATH = "null";
	
	public static final String CHAINCODE_1_VERSION = "0.0.1";
	
	// Can be Type.NODE or Type.GO_LANG or Type.JAVA
	public static final Type CHAINCODE_LANGUAGE = Type.NODE;
	
	// Can be INSTANTIATION or UPGRADE
	public static final String CHAINCODE_ACTION = "UPGRADE";
	

}
