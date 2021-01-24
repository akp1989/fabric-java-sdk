package com.java.chaincode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.java.client.ChannelClient;
import com.java.client.FabricClient;
import com.java.config.Config;
import com.java.user.UserContext;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;


public class InvokeChaincode {
	

	private static byte[] EXPECTED_EVENT_DATA;
	private static String EXPECTED_EVENT_NAME;

	public static void main(String args[]) {
		try {
      
            Wallet wallet = Wallet.createFileSystemWallet(Config.WALLET_PATH);
			
			//Load the admin user from the Wallet
			UserContext org1Client = new UserContext();
			org1Client.setMspId(Config.ORG1_MSP);
			org1Client.setName(Config.ORG1+"_client1_org1");
			org1Client.setIdentity(wallet.get(org1Client.getName()));
			
			//Load the admin user from the Wallet
			UserContext org2Client = new UserContext();
			org2Client.setMspId(Config.ORG2_MSP);
			org2Client.setName(Config.ORG2+"_client1_org2");
			org2Client.setIdentity(wallet.get(org2Client.getName()));
			
			//Load the admin user from the Wallet
			UserContext org3Client = new UserContext();
			org3Client.setMspId(Config.ORG3_MSP);
			org3Client.setName(Config.ORG3+"_client1_org3");
			org3Client.setIdentity(wallet.get(org3Client.getName()));
			
			FabricClient fabClient = new FabricClient(org2Client);
			
			ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			
			Properties properties = new Properties();
			/* Properties to pass the TLS CA certificates for SSL connection */
			properties.put("pemFile","ceadar/crypto/org1/msp/tlscacerts/tlsca-cert.pem");
			
			// Define the peers
			Peer peer0_org1 = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL, properties);
			Peer peer0_org2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL, properties);
			Peer peer0_org3 = fabClient.getInstance().newPeer(Config.ORG3_PEER_0, Config.ORG3_PEER_0_URL, properties);

			EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			channel.addPeer(peer0_org1);
			channel.addPeer(peer0_org2);
			channel.addPeer(peer0_org3);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();

			TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
			ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
			request.setChaincodeID(ccid);
			
			request.setFcn("AssetContract:assetCreate");
			
			//Argument to be passed for invoke request
			String[] arguments = {"assetNo:2","org2","Called from org2"};
			request.setArgs(arguments);
			
			request.setProposalWaitTime(120000);

			Map<String, byte[]> tm2 = new HashMap<>();
			tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes("UTF-8")); 																								
			tm2.put("method", "TransactionProposalRequest".getBytes("UTF-8")); 
			tm2.put("result", ":)".getBytes("UTF-8"));
			EXPECTED_EVENT_DATA = "!".getBytes("UTF-8");
			EXPECTED_EVENT_NAME = "event";
			tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); 
			tm2.put("assetPrice", "price-from-java".getBytes());
			request.setTransientMap(tm2);
			
			Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
			
			for (ProposalResponse res: responses) {
				Status status = res.getStatus();
				Logger.getLogger(InvokeChaincode.class.getName()).log(Level.INFO,"Invoked createCar on "+Config.CHAINCODE_1_NAME + ". Status - " + status);
			}
									
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
