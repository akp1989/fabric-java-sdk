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
			UserContext orgaAdmin = new UserContext();
			orgaAdmin.setMspId(Config.ORGA_MSP);
			orgaAdmin.setName(Config.ORGA+"_admin");
			orgaAdmin.setIdentity(wallet.get(orgaAdmin.getName()));
			
			//Load the admin user from the Wallet
			UserContext orgbAdmin = new UserContext();
			orgbAdmin.setMspId(Config.ORGB_MSP);
			orgbAdmin.setName(Config.ORGB+"_admin");
			orgbAdmin.setIdentity(wallet.get(orgbAdmin.getName()));
			
			Properties properties = new Properties();
			/* Properties to pass the TLS CA certificates for SSL connection */
			properties.put("pemFile","ceadar/crypto/orga/msp/tlscacerts/tlsca-cert.pem");
			
			
			// Create a fabric client instance for the first organization
			FabricClient fabClient = new FabricClient(orgaAdmin);

			Channel clientChannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME);
			
			// Define the orderer
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			
			// Define the peers
			Peer peer0_orga = fabClient.getInstance().newPeer(Config.ORGA_PEER_0, Config.ORGA_PEER_0_URL, properties);
			Peer peer0_orgb = fabClient.getInstance().newPeer(Config.ORGB_PEER_0, Config.ORGB_PEER_0_URL, properties);
			
			// Creating the event hubs and intializing the channel
			clientChannel.addOrderer(orderer);
			clientChannel.addPeer(peer0_orga);
			clientChannel.addPeer(peer0_orgb);
			clientChannel.initialize();
			
			
			List<Peer> orgaPeers = new ArrayList<Peer>();
			orgaPeers.add(peer0_orga);
			
			List<Peer> orgbPeers = new ArrayList<Peer>();
			orgbPeers.add(peer0_orgb);
			
			ChaincodeID chaincodeID;
			Collection<ProposalResponse> response;
			
			if(Config.CHAINCODE_LANGUAGE == Type.NODE) {
				chaincodeID = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).setVersion(Config.CHAINCODE_1_VERSION).build();
			}else
				chaincodeID = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).setVersion(Config.CHAINCODE_1_VERSION).setPath(Config.CHAINCODE_1_PATH).build();
			response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
					chaincodeID, Config.CHAINCODE_ROOT_DIR, Type.NODE,
					Config.CHAINCODE_1_VERSION, orgaPeers);
		 
			for (ProposalResponse res : response) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
			}

			fabClient.getInstance().setUserContext(orgbAdmin);
			
			response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
					chaincodeID, Config.CHAINCODE_ROOT_DIR, Type.NODE,
					Config.CHAINCODE_1_VERSION, orgbPeers);
			
			
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
