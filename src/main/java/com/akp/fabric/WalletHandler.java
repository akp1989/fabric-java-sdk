/*
SPDX-License-Identifier: Apache-2.0
*/

package com.akp.fabric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.GatewayException;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class WalletHandler {

	public static void main(String[] args) throws IOException {
			// A wallet stores a collection of identities
		    Path walletPath = Paths.get("..", "ceadar", "org1", "wallet");
	        // Location of credentials to be stored in the wallet
			Path credentialPath = Paths.get("ceadar",
					"peerOrganizations", "org1.example.com", "users", "Admin@org1.example.com", "msp");
			Path certificatePem = credentialPath.resolve(Paths.get("signcerts",
					"Admin@org1.example.com-cert.pem"));
			Path privateKey = credentialPath.resolve(Paths.get("keystore",
					"48ce78c94ed5ecb9ea9dfcbfc8baf44972846997ec94ead99183e52a5e1ad604_sk"));

		    addToWallet(walletPath,credentialPath, certificatePem, privateKey);
			
	
	}
	public static void addToWallet(Path walletPath, Path credentialPath, Path certificate, Path key) throws IOException {
		
		Wallet wallet = Wallet.createFileSystemWallet(walletPath);
		
		String identityLabel = "admin";
		Identity identity = Identity.createIdentity("Org1MSP", Files.newBufferedReader(certificate), Files.newBufferedReader(key));

		wallet.put(identityLabel, identity);

	}
	
	

}
