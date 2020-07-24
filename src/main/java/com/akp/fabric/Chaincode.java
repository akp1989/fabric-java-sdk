/*
SPDX-License-Identifier: Apache-2.0
*/

package com.akp.fabric;

import java.nio.charset.StandardCharsets;
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
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;

public class Chaincode {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "false");
	}

	public static void main(String[] args) throws Exception {
		// Load a file system based wallet for managing identities.
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("ceadar","wallet","orga"));

		// Load the network config path
		Path networkConfigPath = Paths.get("ceadar", "fabric-network", "connection.yaml");
 
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "orgaclient1").networkConfig(networkConfigPath).discovery(false);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("testchannel");

			//Contract contract = network.getContract("blockchain-smart-contracts","ProductKeyBatchRequestContract");
			Contract contract = network.getContract("blockchain-smart-contracts","ProductKeyContract");
			Channel channelTest = network.getChannel();
			//QueryByChaincodeRequest queryRequest = new QueryByChaincodeRequest();
			//Transaction invokeTransaction = contract.createTransaction("createProductKeyBatchRequest");
			Transaction invokeTransaction = contract.createTransaction("createProductKey");
			//Transaction queryTransaction = contract.createTransaction("readProductKeyBatchRequest");
			Transaction queryTransaction = contract.createTransaction("readProductKey");
			byte[] result;
			
			Map<String,byte[]> transientMap = new HashMap<>();
			Encoder encoder = Base64.getEncoder();
			String transientString = new String();
			
			
			transientString = "key2";
			transientMap.put("keyNumber",transientString.getBytes()); 
			transientString = "sku2"; 
			transientMap.put("sKU",transientString.getBytes());
			transientString = "batch1"; 
			transientMap.put("batchID",transientString.getBytes());
			
			
//			transientString = "10";	
//			transientMap.put("quantity",transientString.getBytes());
//			transientString = "sku2";
//			transientMap.put("sku",transientString.getBytes());
//			transientString = "batch2";
//			transientMap.put("batchID",transientString.getBytes());

			invokeTransaction.setTransient(transientMap);
			//result = invokeTransaction.submit("productKeyBatchRequestId:2","OrgAMSP","OrgBMSP");
			//result = invokeTransaction.submit("productKeyId:2","OrgAMSP","OrgBMSP");
 
			
			result = queryTransaction.submit("productKeyId:2");
			
			System.out.println(new String(result));
			
			BlockchainInfo blockchainInfo = channelTest.queryBlockchainInfo();
			Long blockHeight = blockchainInfo.getHeight();
			byte[] currentBlockHash = blockchainInfo.getCurrentBlockHash();
			byte[] previousBlockHash = blockchainInfo.getPreviousBlockHash();
	
			System.out.println("Current block height is : " + blockHeight + " and hash is :"+currentBlockHash);
			
			BlockInfo blockInfo = channelTest.queryBlockByHash(currentBlockHash);
			System.out.println("The transaction for current hash is :" + channelTest.queryBlockByHash(currentBlockHash));
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