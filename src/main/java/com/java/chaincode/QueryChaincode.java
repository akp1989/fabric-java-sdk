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
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionInfo;


public class QueryChaincode {


	public static void main(String args[]) {
		try {
			Wallet wallet = Wallet.createFileSystemWallet(Config.WALLET_PATH);
			
			//Load the admin user from the Wallet
			UserContext orgClient = new UserContext();
			orgClient.setMspId(Config.ORG1_MSP);
			orgClient.setName(Config.ORG1+"_client1_org1");
//			orgClient.setIdentity(wallet.get(orgClient.getName()));
			orgClient.setIdentity(wallet.get("org1_client1_org1"));
			
			FabricClient fabClient = new FabricClient(orgClient);
			
						
			ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			
			Properties properties = new Properties();
			/* Properties to pass the TLS CA certificates for SSL connection */
			properties.put("pemFile","ceadar/crypto/org1/msp/tlscacerts/tlsca-cert.pem");
			
			Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL,properties);
			EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			
			channel.addPeer(peer);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();
			
			//Argument to be passed for query request
			String[] arguments = {"assetNo:1"};
			String transactionID = "a14edf0e691792dfb4fb090aaec4d40d1c68c27c00db1d0d316e8db006086689";
			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying "+ Config.CHAINCODE_1_NAME );
			BlockInfo blockInfo = channel.queryBlockByTransactionID(transactionID);
			TransactionInfo transactionInfo = channel.queryTransactionByID(transactionID);
			Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode(Config.CHAINCODE_1_NAME, "AssetContract:getTransactionHistory", arguments);
			for (ProposalResponse pres : responsesQuery) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
