package com.java.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.java.client.ChannelClient;
import com.java.client.FabricClient;
import com.java.config.Config;
import com.java.user.UserContext;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;


public class DeployInstantiateChaincode {

	public static void main(String[] args) {
		try {
						
			Wallet wallet = Wallet.createFileSystemWallet(Config.WALLET_PATH);
			
			//Load the admin user from the Wallet
			UserContext org1Admin = new UserContext();
			org1Admin.setMspId(Config.ORG1_MSP);
			org1Admin.setName(Config.ORG1+"_admin");
			org1Admin.setIdentity(wallet.get(org1Admin.getName()));
			
			//Load the admin user from the Wallet
			UserContext org2Admin = new UserContext();
			org2Admin.setMspId(Config.ORG2_MSP);
			org2Admin.setName(Config.ORG2+"_admin");
			org2Admin.setIdentity(wallet.get(org2Admin.getName()));
			
			//Load the admin user from the Wallet
			UserContext org3Admin = new UserContext();
			org3Admin.setMspId(Config.ORG3_MSP);
			org3Admin.setName(Config.ORG3+"_admin");
			org3Admin.setIdentity(wallet.get(org3Admin.getName()));
			
			Properties properties = new Properties();
			/* Properties to pass the TLS CA certificates for SSL connection */
			properties.put("pemFile","ceadar/crypto/org1/msp/tlscacerts/tlsca-cert.pem");
			
			
			// Create a fabric client instance for the first organization
			FabricClient fabClient = new FabricClient(org1Admin);

			Channel clientChannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME);
			
			// Define the orderer
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			
			// Define the peers
			Peer peer0_org1 = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL, properties);
			Peer peer0_org2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL, properties);
			Peer peer0_org3 = fabClient.getInstance().newPeer(Config.ORG3_PEER_0, Config.ORG3_PEER_0_URL, properties);
			
			// Creating the event hubs and intializing the channel
			clientChannel.addOrderer(orderer);
			clientChannel.addPeer(peer0_org1);
			clientChannel.addPeer(peer0_org2);
			clientChannel.addPeer(peer0_org3); 
			clientChannel.initialize();
			
			
			List<Peer> org1Peers = new ArrayList<Peer>();
			org1Peers.add(peer0_org1);
			
			List<Peer> org2Peers = new ArrayList<Peer>();
			org2Peers.add(peer0_org2);
			
			List<Peer> org3Peers = new ArrayList<Peer>();
			org3Peers.add(peer0_org3);
			
			ChaincodeID chaincodeID;
			Collection<ProposalResponse> response;
			
			if(Config.CHAINCODE_LANGUAGE == Type.NODE) {
				chaincodeID = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).setVersion(Config.CHAINCODE_1_VERSION).build();
			}else
				chaincodeID = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).setVersion(Config.CHAINCODE_1_VERSION).setPath(Config.CHAINCODE_1_PATH).build();
			
			response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
					chaincodeID, Config.CHAINCODE_ROOT_DIR, Type.NODE,
					Config.CHAINCODE_1_VERSION, org1Peers);
		 
			for (ProposalResponse res : response) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
			}

			fabClient.getInstance().setUserContext(org2Admin);
			
			response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
					chaincodeID, Config.CHAINCODE_ROOT_DIR, Type.NODE,
					Config.CHAINCODE_1_VERSION, org2Peers);
			
			
			for (ProposalResponse res : response) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
			}
			
			response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
					chaincodeID, Config.CHAINCODE_ROOT_DIR, Type.NODE,
					Config.CHAINCODE_1_VERSION, org3Peers);
			
			
			for (ProposalResponse res : response) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
			}

			ChannelClient channelClient = new ChannelClient(clientChannel.getName(), clientChannel, fabClient);

			String[] arguments = { "a","100","b","200" };
			
			
			
			if(Config.CHAINCODE_ACTION.equalsIgnoreCase("INSTANTIATION"))
			{
				response = channelClient.instantiateChainCode(Config.CHAINCODE_1_NAME, Config.CHAINCODE_1_VERSION,
						chaincodeID, Type.NODE, "init", arguments, Config.CHAINCODE_1_ENDORSEMENT );
			}else {
				response = channelClient.upgradeChainCode(Config.CHAINCODE_1_NAME, Config.CHAINCODE_1_VERSION,
						chaincodeID, Type.NODE, "init", arguments, Config.CHAINCODE_1_ENDORSEMENT );
			}
				
					

			for (ProposalResponse res : response) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
