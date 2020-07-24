package com.java.chaincode;

import java.util.Collection;
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

//	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
//	private static final String EXPECTED_EVENT_NAME = "event";

	public static void main(String args[]) {
		try {
      
            Wallet wallet = Wallet.createFileSystemWallet(Config.WALLET_PATH);
			
			//Load the admin user from the Wallet
			UserContext orgaClient = new UserContext();
			orgaClient.setMspId(Config.ORGA_MSP);
			orgaClient.setName(Config.ORGA+"_client1");
			orgaClient.setIdentity(wallet.get(orgaClient.getName()));
			
			//Load the admin user from the Wallet
			UserContext orgbClient = new UserContext();
			orgbClient.setMspId(Config.ORGB_MSP);
			orgbClient.setName(Config.ORGB+"_client1");
			orgbClient.setIdentity(wallet.get(orgbClient.getName()));
			
			FabricClient fabClient = new FabricClient(orgbClient);
			
			ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			
			Properties properties = new Properties();
			/* Properties to pass the TLS CA certificates for SSL connection */
			properties.put("pemFile","ceadar/crypto/orga/msp/tlscacerts/tlsca-cert.pem");
			
			// Define the peers
			Peer peer0_orga = fabClient.getInstance().newPeer(Config.ORGA_PEER_0, Config.ORGA_PEER_0_URL, properties);
			Peer peer0_orgb = fabClient.getInstance().newPeer(Config.ORGB_PEER_0, Config.ORGB_PEER_0_URL, properties);
			EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			channel.addPeer(peer0_orga);
			channel.addPeer(peer0_orgb);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();

			TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
			ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
			request.setChaincodeID(ccid);
			
			request.setFcn("invoke");
			
			//Argument to be passed for invoke request
			String[] arguments = { "a", "b", "15" };
			request.setArgs(arguments);
			
			request.setProposalWaitTime(120000);

//			Map<String, byte[]> tm2 = new HashMap<>();
//			tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); 																								
//			tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); 
//			tm2.put("result", ":)".getBytes(UTF_8));
//			tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); 
//			request.setTransientMap(tm2);
			
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
