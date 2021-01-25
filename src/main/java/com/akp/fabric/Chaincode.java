/*
SPDX-License-Identifier: Apache-2.0
*/

package com.akp.fabric;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64.Encoder;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Transaction;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;

import com.java.chaincode.QueryBlockchain;
import com.java.util.BlockchainUtil;

public class Chaincode {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "false");
	}

	public static void main(String[] args) throws Exception {
		// Load a file system based wallet for managing identities.
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("ceadar","gatewayWalletTest"));

		// Load the network config path
		Path networkConfigPath = Paths.get("ceadar", "fabric-network", "connection.yaml");
 
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "org1_client1_org1").networkConfig(networkConfigPath).discovery(false);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("testchannel");
 
			Contract contract = network.getContract("privateasset","AssetContract"); 
			Channel channelTest = network.getChannel(); 
			
			//Possible invoke transactions 
			//Transaction invokeTransaction = contract.createTransaction("assetCreate");
			//Transaction invokeTransaction = contract.createTransaction("assetUpdate");
			//Transaction invokeTransaction = contract.createTransaction("assetUpdatePrivate");
			Transaction invokeTransaction = contract.createTransaction("assetDelete");
			
			//Possible query transactions
			//Transaction queryTransaction = contract.createTransaction("assetRead");
			//Transaction queryTransaction = contract.createTransaction("assetReadPrivate");
			Transaction queryTransaction = contract.createTransaction("getTransactionHistory");

			byte[] result;
			
			Map<String,byte[]> transientMap = new HashMap<>();
			Encoder encoder = Base64.getEncoder();
			String transientString = new String();
			
			transientString = "gatewayprice00";	
			transientMap.put("assetPrice",transientString.getBytes());

			
			
			invokeTransaction.setTransient(transientMap);
			//result = invokeTransaction.submit("assetID:gateway002","org1","called from org1");
			//result = invokeTransaction.submit("assetID:gateway001","price001");
			result = invokeTransaction.submit("assetID:gateway002");
			System.out.println(new String(result));
			
			result = queryTransaction.submit("assetID:gateway002");
			System.out.println(new String(result));
			

//			BlockInfo blockInfo = channelTest.queryBlockByTransactionID("9de043f5d5f6c4a3aa52494c4c504c0845bb1a70d5c3a0b1b20a807eeef0040b");
//			BlockchainUtil blockchainUtil = new BlockchainUtil();
//			System.out.println("The transaction for current hash is :" + blockchainUtil.processBlock(blockInfo));
		}
	}

}



/*
 * Container to have all the removed methods for future reference
 * 
 *  To encode the string as UTF_8 for transient map. It was removed since normal byte array would suffice
 *  transientString = "10";
 *  transientString = new String(encoder.encode(transientString.getBytes()),StandardCharsets.UTF_8); 
 */