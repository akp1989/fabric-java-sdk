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

			// Choose the orderer for channel creation 
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL,properties);
			
			// Choose the channel configuration from the channel.tx file
			ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Config.CHANNEL_CONFIG_PATH+Config.CHANNEL_NAME+".tx"));
			
			// Get the channel configuration signed by organization admin that is going to create the channel
			byte[] channelConfigurationSignatures = fabClient.getInstance().getChannelConfigurationSignature(channelConfiguration, orgaAdmin);

			// Create the channel
			Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME, orderer, channelConfiguration,
					channelConfigurationSignatures);

			// Define the peers
			Peer peer0_orga = fabClient.getInstance().newPeer(Config.ORGA_PEER_0, Config.ORGA_PEER_0_URL, properties);
			Peer peer0_orgb = fabClient.getInstance().newPeer(Config.ORGB_PEER_0, Config.ORGB_PEER_0_URL, properties);
			
			// Join the first organization peers
			mychannel.joinPeer(peer0_orga);
			
			// Add the orderer to the channel and initialize the channel
			mychannel.addOrderer(orderer);
			mychannel.initialize();
			
			// Create a fabric client instance for the next organization
			fabClient.getInstance().setUserContext(orgbAdmin);
			
			//Get the already created channel 
			mychannel = fabClient.getInstance().getChannel(Config.CHANNEL_NAME);
			
			// Join the next organization peers
			mychannel.joinPeer(peer0_orgb);
			
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
