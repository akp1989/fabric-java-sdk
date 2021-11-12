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
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("ripe","gatewayWalletTest"));

		// Load the network config path
		Path networkConfigPath = Paths.get("ripe", "fabric-network", "connection.yaml");
 
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet,"test_client01").networkConfig(networkConfigPath).discovery(false);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("testchannel");
			Channel channelTest = network.getChannel(); 
			Contract contract = network.getContract("privateasset","AssetContract"); 
 
			//Possible invoke transactions 
			Transaction invokeTransaction = contract.createTransaction("assetCreate");
			//Transaction invokeTransaction = contract.createTransaction("assetUpdate");
			//Transaction invokeTransaction = contract.createTransaction("assetUpdatePrivate");
			//Transaction invokeTransaction = contract.createTransaction("assetDelete");
			
			//Possible query transactions
			Transaction queryTransaction = contract.createTransaction("assetRead");
			//Transaction queryTransaction = contract.createTransaction("assetReadPrivate");
			//Transaction queryTransaction = contract.createTransaction("getTransactionHistory");

			byte[] result;
			
			Map<String,byte[]> transientMap = new HashMap<>();
 			String transientString = new String();
			
			transientString = "secretprice";	
			transientMap.put("assetPrice",transientString.getBytes());

			
			
//			invokeTransaction.setTransient(transientMap);
//			result = invokeTransaction.submit("testasset1","test","called from test");
			//result = invokeTransaction.submit("assetID:gateway001","price001");
//			result = invokeTransaction.submit("assetID:gateway002");
//			System.out.println(new String(result));
			
			result = queryTransaction.submit("testasset1");
			System.out.println(new String(result));
			

			BlockInfo blockInfo = channelTest.queryBlockByTransactionID("1c58c29b91f64f0ba7ac3975415fa6183a398ce6c81732e70a75f54fbb78623b");
			BlockchainUtil blockchainUtil = new BlockchainUtil();
			System.out.println("The transaction for current hash is :" + blockchainUtil.processBlock(blockInfo));
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