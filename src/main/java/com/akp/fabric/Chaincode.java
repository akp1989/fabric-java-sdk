/*
SPDX-License-Identifier: Apache-2.0
*/

package com.akp.fabric;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

public class Chaincode {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "false");
	}

	public static void main(String[] args) throws Exception {
		// Load a file system based wallet for managing identities.
		Wallet wallet = Wallet.createFileSystemWallet(Paths.get("..","ceadar","org1","wallet"));

		// load a CCP
		Path networkConfigPath = Paths.get("..", "ceadar", "fabric-network", "connection.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "testuser").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("testchannel");
			Contract contract = network.getContract("marbles");

			byte[] result;
 

			result = contract.submitTransaction("readMarble", "marbletest");
			System.out.println(new String(result));

		}
	}

}
