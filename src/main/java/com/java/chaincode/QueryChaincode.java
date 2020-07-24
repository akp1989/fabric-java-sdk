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
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;


public class QueryChaincode {


	public static void main(String args[]) {
		try {
			Wallet wallet = Wallet.createFileSystemWallet(Config.WALLET_PATH);
			
			//Load the admin user from the Wallet
			UserContext orgaClient = new UserContext();
			orgaClient.setMspId(Config.ORGA_MSP);
			orgaClient.setName(Config.ORGA+"_client1");
			orgaClient.setIdentity(wallet.get(orgaClient.getName()));
			
			FabricClient fabClient = new FabricClient(orgaClient);
			
						
			ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			
			Properties properties = new Properties();
			/* Properties to pass the TLS CA certificates for SSL connection */
			properties.put("pemFile","ceadar/crypto/orga/msp/tlscacerts/tlsca-cert.pem");
			
			Peer peer = fabClient.getInstance().newPeer(Config.ORGA_PEER_0, Config.ORGA_PEER_0_URL,properties);
			EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			
			channel.addPeer(peer);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();
			
			//Argument to be passed for query request
			String[] arguments = {"b"};
			
			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying "+ Config.CHAINCODE_1_NAME );
			Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode(Config.CHAINCODE_1_NAME, "query", arguments);
			for (ProposalResponse pres : responsesQuery) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
