
package com.java.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;



public class FabricClient {

	private HFClient instance;

	/**
	 * Return an instance of HFClient.
	 * 
	 * @return
	 */
	public HFClient getInstance() {
		return instance;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @throws CryptoException
	 * @throws InvalidArgumentException
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public FabricClient(User context) throws CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		// setup the client
		instance = HFClient.createNewInstance();
		instance.setCryptoSuite(cryptoSuite);
		instance.setUserContext(context);
	}

	/**
	 * Create a channel client.
	 * 
	 * @param name
	 * @return
	 * @throws InvalidArgumentException
	 */
	public ChannelClient createChannelClient(String name) throws InvalidArgumentException {
		Channel channel = instance.newChannel(name);
		ChannelClient client = new ChannelClient(name, channel, this);
		return client;
	}

	/**
	 * Deploy chain code.
	 * 
	 * @param chainCodeName
	 * @param chaincodePath
	 * @param codepath
	 * @param language
	 * @param version
	 * @param peers
	 * @return
	 * @throws InvalidArgumentException
	 * @throws IOException
	 * @throws ProposalException
	 */
	public Collection<ProposalResponse> deployChainCode(String chainCodeName, ChaincodeID chaincodeID, String codepath,
			Type language, String version, Collection<Peer> peers)
			throws InvalidArgumentException, IOException, ProposalException {
		InstallProposalRequest request = instance.newInstallProposalRequest();

		Logger.getLogger(FabricClient.class.getName()).log(Level.INFO,
				"Deploying chaincode " + chainCodeName + " using Fabric client " + instance.getUserContext().getMspId()
						+ " " + instance.getUserContext().getName());
		request.setChaincodeID(chaincodeID);
		request.setUserContext(instance.getUserContext());
		request.setChaincodeSourceLocation(new File(codepath));
		request.setChaincodeVersion(version);
		request.setChaincodeLanguage(language);
		Collection<ProposalResponse> responses = instance.sendInstallProposal(request, peers);
		return responses;
	}

}
