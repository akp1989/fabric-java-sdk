package com.java.chaincode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.protos.common.Common.Envelope;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import com.google.gson.JsonObject;
import com.java.client.ChannelClient;
import com.java.client.FabricClient;
import com.java.config.Config;
import com.java.user.UserContext;
import com.java.util.BlockchainUtil;
 

public class QueryBlockchain{
	
	public static void main(String args[]) throws IOException, CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, TransactionException, ProposalException, NoSuchAlgorithmException {
		
		Wallet wallet = Wallet.createFileSystemWallet(Config.WALLET_PATH);
		BlockchainUtil blockchainUtil = new BlockchainUtil();
		//Load the admin user from the Wallet
		UserContext orgClient = new UserContext();
		orgClient.setMspId(Config.ORG1_MSP);
		orgClient.setName(Config.ORG1+"_client1_org1");
//		orgClient.setIdentity(wallet.get(orgClient.getName()));
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
		
		String[] arguments = {"assetNo:1"};
//		String transactionID = "a14edf0e691792dfb4fb090aaec4d40d1c68c27c00db1d0d316e8db006086689";
//		String transactionID = "aa310f1497170366f575b48adbb2c257743706d56699ff3c3ef8541aaccec02a";
//		String transactionID = "0cba28fee6fb7176216cb1a4170a952f03fded176d705b2fb1111ed14c7b55d0";
//		String transactionID = "f44dda8e4b9bf88ceb82f88a5f291d2921a25a17d66527e1eda8ee36075b07b1";
//		String transactionID = "7b1d9dec76c11490ac6691a485be6fd514573074ddacc82481fc637d38699cc2";
		String transactionID = "f33b114c7941cf90847e8c6cae9374ada8a409dd509a61031da525171def4163";

		BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();
		
	
		BlockInfo blockInfo = channel.queryBlockByTransactionID(transactionID);
		JsonObject blockHeaderJSON = blockchainUtil.processBlock(blockInfo);
		System.out.println(blockHeaderJSON);
//		int blockSize = block.getData().getDataCount();
//		
//		for(int intCount=0; intCount<blockSize; intCount++) {
//			ByteString blockData = block.getData().getData(intCount);
//			System.out.println(new String (blockData.toByteArray()));
//			System.out.println("Minion");
//		}
		TransactionInfo transactionInfo = channel.queryTransactionByID(transactionID);
		Envelope transactionEnvelope = transactionInfo.getProcessedTransaction().getTransactionEnvelope();
		JsonObject dataJSON = blockchainUtil.processTransaction(transactionEnvelope);
		System.out.println(dataJSON);
		
	}
}