package com.java.network;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.java.client.FabricClient;
import com.java.config.Config;
import com.java.user.UserContext;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;


public class CreateChannel {

	public static void main(String[] args) {
		try {
			
			CryptoSuite.Factory.getCryptoSuite();
	
			 
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
			
			Properties properties = new Properties();
			/* Properties to pass the TLS CA certificates for SSL connection */
			properties.put("pemFile","ceadar/crypto/org1/msp/tlscacerts/tlsca-cert.pem");

			
			// Create a fabric client instance for the first organization
			FabricClient fabClient = new FabricClient(org1Admin);

			// Choose the orderer for channel creation 
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			
			// Choose the channel configuration from the channel.tx file
			ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Config.CHANNEL_CONFIG_PATH+Config.CHANNEL_NAME+".tx"));
			
			// Get the channel configuration signed by organization admin that is going to create the channel
			byte[] channelConfigurationSignatures = fabClient.getInstance().getChannelConfigurationSignature(channelConfiguration, org1Admin);

			// Create the channel
			Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME, orderer, channelConfiguration,
					channelConfigurationSignatures);

			// Define the peers
			Peer peer0_org1 = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL, properties);
			Peer peer0_org2 = fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL, properties);
			Peer peer0_org3 = fabClient.getInstance().newPeer(Config.ORG3_PEER_0, Config.ORG3_PEER_0_URL, properties); 
			
			// Join the first organization peers
			mychannel.joinPeer(peer0_org1);
			
			// Add the orderer to the channel and initialize the channel
			mychannel.addOrderer(orderer);
			mychannel.initialize();
			
			// Create a fabric client instance for the next organization
			fabClient.getInstance().setUserContext(org2Admin);
			
			//Get the already created channel 
			mychannel = fabClient.getInstance().getChannel(Config.CHANNEL_NAME);
			
			// Join the next organization peers
			mychannel.joinPeer(peer0_org2);
			mychannel.joinPeer(peer0_org3);
			
			Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO, "Channel created "+mychannel.getName());
            
			Collection<Peer> peers = mychannel.getPeers();
            Iterator<Peer> peerIter = peers.iterator();
            while (peerIter.hasNext())
            {
            	  Peer pr = (Peer) peerIter.next();
            	  Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO,pr.getName()+ " at " + pr.getUrl());
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
