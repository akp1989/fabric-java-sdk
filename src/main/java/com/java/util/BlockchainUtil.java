package com.java.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.hyperledger.fabric.protos.common.Common.Block;
import org.hyperledger.fabric.protos.common.Common.BlockHeader;
import org.hyperledger.fabric.protos.common.Common.ChannelHeader;
import org.hyperledger.fabric.protos.common.Common.Envelope;
import org.hyperledger.fabric.protos.common.Common.Payload;
import org.hyperledger.fabric.protos.common.Common.SignatureHeader;
import org.hyperledger.fabric.protos.ledger.rwset.Rwset;
import org.hyperledger.fabric.protos.ledger.rwset.Rwset.TxReadWriteSet;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset.KVRWSet;
import org.hyperledger.fabric.protos.msp.Identities.SerializedIdentity;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeID;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeInput;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeInvocationSpec;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeSpec;
import org.hyperledger.fabric.protos.peer.FabricProposal.ChaincodeAction;
import org.hyperledger.fabric.protos.peer.FabricProposal.ChaincodeHeaderExtension;
import org.hyperledger.fabric.protos.peer.FabricProposal.ChaincodeProposalPayload;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse.ProposalResponsePayload;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse.Response;
import org.hyperledger.fabric.protos.peer.FabricTransaction.ChaincodeActionPayload;
import org.hyperledger.fabric.protos.peer.FabricTransaction.ProcessedTransaction;
import org.hyperledger.fabric.protos.peer.FabricTransaction.Transaction;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.TransactionInfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.protobuf.InvalidProtocolBufferException;

public class BlockchainUtil{
	
	private String[] channelHeaderType = {
			"MESSAGE", 
			"CONFIG", 
			"CONFIG_UPDATE", 
			"ENDORSER_TRANSACTION",
			"ORDERER_TRANSACTION", 
			"DELIVER_SEEK_INFO", 
			"CHAINCODE_PACKAGE"
		};
	private String[] chaincodeType = {"UNDEFINED", "GOLANG", "NODE", "CAR", "JAVA"};
 	
	
	public JsonObject processBlock(BlockInfo blockInfo) throws NoSuchAlgorithmException, InvalidProtocolBufferException {
		Block block = blockInfo.getBlock();
		JsonObject blockJSON = new JsonObject();
		
		BlockHeader blockHeader = block.getHeader();
 		JsonObject blockHeaderJSON = new JsonObject();
		blockHeaderJSON.addProperty("number",(blockHeader.getNumber()));
		blockHeaderJSON.addProperty("previousHash",(digestSHA256(blockHeader.getPreviousHash().toByteArray(), true)));
		blockHeaderJSON.addProperty("dataHash",(digestSHA256(blockHeader.getDataHash().toByteArray(), true)));

		blockJSON.add("blockHeader",blockHeaderJSON);
		Envelope transactionEnvelope = Envelope.parseFrom(block.getData().getData(0));
		blockJSON.add("transactionEnvelope", processTransaction(transactionEnvelope));
		return blockJSON;
	}
	
	public JsonObject processTransaction(Envelope transactionEnvelope) throws InvalidProtocolBufferException, NoSuchAlgorithmException {

		JsonObject transactionEnvelopeJSON = new JsonObject();
		transactionEnvelopeJSON.add("header",processTransactionHeader(transactionEnvelope));
		
		JsonObject dataJSON = new JsonObject();
			
		Payload payLoad = Payload.parseFrom(transactionEnvelope.getPayload());
		dataJSON.add("transaction", processTransaction(payLoad));	
		
		return dataJSON;
		
	}
	
	private JsonObject processTransactionHeader(Envelope transactionEnvelope) throws InvalidProtocolBufferException
	{
		JsonObject transactionHeaderJSON = new JsonObject();
		Payload payLoad = Payload.parseFrom(transactionEnvelope.getPayload());
		JsonObject payLoadJSON = new JsonObject();
		
		//Process Channel Header
		ChannelHeader channelHeader = ChannelHeader.parseFrom(payLoad.getHeader().getChannelHeader());
		JsonObject channelHeaderJSON = new JsonObject();
		payLoadJSON.add("channelHeader",processChannelHeader(channelHeader));
		
		//Process Signature Header
		SignatureHeader signatureHeader = SignatureHeader.parseFrom(payLoad.getHeader().getSignatureHeader());
		JsonObject signatureHeaderJSON = new JsonObject();
		payLoadJSON.add("signatureHeader", processSignatureHeader(signatureHeader));
		transactionHeaderJSON.add("payLoad", payLoadJSON);	
		return transactionHeaderJSON;

	}
	private JsonObject processChannelHeader(ChannelHeader channelHeader) throws InvalidProtocolBufferException {
		//Processing Channel header
		JsonObject channelHeaderJSON = new JsonObject();
		channelHeaderJSON.addProperty("version", channelHeader.getVersion());
		channelHeaderJSON.addProperty("channelId",channelHeader.getChannelId());
		channelHeaderJSON.addProperty("transactionId",channelHeader.getTxId());
		channelHeaderJSON.addProperty("epoch",channelHeader.getEpoch());
		channelHeaderJSON.addProperty("type", channelHeaderType[channelHeader.getType()]);
		
		ChaincodeHeaderExtension chaincodeHeaderExtension = ChaincodeHeaderExtension.parseFrom(channelHeader.getExtension());
		JsonObject chaincodeHeaderExtensionJSON = new JsonObject();
		
		ChaincodeID chaincodeHeaderExtensionID = (chaincodeHeaderExtension.getChaincodeId());
		JsonObject chaincode_ID = new JsonObject();
		chaincode_ID.addProperty("name",chaincodeHeaderExtensionID.getName());
		chaincode_ID.addProperty("path",chaincodeHeaderExtensionID.getPath());
		chaincode_ID.addProperty("version", chaincodeHeaderExtensionID.getVersion());
			
		chaincodeHeaderExtensionJSON.add("chaincode_ID", chaincode_ID);
		channelHeaderJSON.add("extension", chaincodeHeaderExtensionJSON);
		
		return channelHeaderJSON;
	}
	private JsonObject processSignatureHeader(SignatureHeader signatureHeader) throws InvalidProtocolBufferException {
		//Processing Signature Header
		SerializedIdentity sIdentity = SerializedIdentity.parseFrom(signatureHeader.getCreator());
		JsonObject creatorJSON = new JsonObject();
		creatorJSON.addProperty("mspID",sIdentity.getMspid());
		
		byte[] idBytes = Base64.encodeBase64(sIdentity.getIdBytes().toByteArray());	
		idBytes = Base64.decodeBase64(idBytes);
		creatorJSON.addProperty("certHash", new String(idBytes));
		
		return creatorJSON;
		
	}
	
	private JsonArray processTransaction(Payload payload) throws InvalidProtocolBufferException, NoSuchAlgorithmException {
		Transaction transaction = Transaction.parseFrom(payload.getData());
		JsonArray transactionJSONArray = new JsonArray();
		int chaincodeActionCount = transaction.getActionsCount();
		System.out.println("Chaincode Action count is : "+ chaincodeActionCount);
		for (int actionCount=0; actionCount<chaincodeActionCount; actionCount++)
		{	
			JsonObject transactionJSON = new JsonObject();
			ChaincodeActionPayload chaincodeActionPayLoad = ChaincodeActionPayload.parseFrom(transaction.getActions(actionCount).getPayload());
			transactionJSON.add("chaincode_proposal_payload", transactionChaincodeProposalPayload(chaincodeActionPayLoad));
			transactionJSON.add("chaincodeProposalResponse", transactionProposalResponsePayload(chaincodeActionPayLoad));
			transactionJSONArray.add(transactionJSON);
		}
		return transactionJSONArray;
	}
	
	private JsonObject transactionChaincodeProposalPayload(ChaincodeActionPayload chaincodeActionPayLoad) throws InvalidProtocolBufferException {
		
		ChaincodeProposalPayload chaincodeProposalPayload = ChaincodeProposalPayload.parseFrom(chaincodeActionPayLoad.getChaincodeProposalPayload());
		JsonObject chainCodeProposalPayloadJSON = new JsonObject();
		
		ChaincodeInvocationSpec chaincodeInvocationSpec = ChaincodeInvocationSpec.parseFrom(chaincodeProposalPayload.getInput());
		JsonObject chaincodeInvocationSpecJSON = new JsonObject();
		
		ChaincodeSpec chaincodeSpec = chaincodeInvocationSpec.getChaincodeSpec();
		JsonObject chaincodeSpecJSON = new JsonObject();
		chaincodeSpecJSON.addProperty("chaincode_type", chaincodeType[chaincodeSpec.getTypeValue()]);
		ChaincodeID chaincodeSpecID = chaincodeSpec.getChaincodeId();
		chaincodeSpecJSON.addProperty("chaincode_id", chaincodeSpecID.getName());
		ChaincodeInput chaincodeSpecInput = chaincodeSpec.getInput();
		JsonArray chaincodeInputArgsJSON = new JsonArray();
		int chaincodeInputArgsCount = chaincodeSpecInput.getArgsCount();
		for(int iCount =0; iCount<chaincodeInputArgsCount; iCount++) {
			chaincodeInputArgsJSON.add(chaincodeSpecInput.getArgs(iCount).toStringUtf8());
		}								
		chaincodeSpecJSON.add("chaincode_args", chaincodeInputArgsJSON);

		chaincodeInvocationSpecJSON.add("chaincode_spec", chaincodeSpecJSON);
	
		chainCodeProposalPayloadJSON.add("chaincode_invocation_spec", chaincodeInvocationSpecJSON);
		
		return chainCodeProposalPayloadJSON;
	}
	private JsonObject transactionProposalResponsePayload(ChaincodeActionPayload chaincodeActionPayLoad) throws InvalidProtocolBufferException, NoSuchAlgorithmException {
	
		JsonObject chaincodeEndorsedActionJSON = new JsonObject();
		
		ProposalResponsePayload proposalResponsePayload = ProposalResponsePayload.parseFrom(chaincodeActionPayLoad.getAction().getProposalResponsePayload());
		ChaincodeAction chaincodeAction = ChaincodeAction.parseFrom(proposalResponsePayload.getExtension());
		JsonObject proposalResponsePayloadJSON = new JsonObject();	
		proposalResponsePayloadJSON.addProperty("proposal_hash",digestSHA256(proposalResponsePayload.getProposalHash().toByteArray(), true));
		proposalResponsePayloadJSON.add("proposal_response", transactionnProposalResponsePayload_pResponse(chaincodeAction));
		proposalResponsePayloadJSON.add("txn_kvrwset", transacitonProposalResponsePayload_kvrwset(chaincodeAction));
		return proposalResponsePayloadJSON;
	}
	
	private JsonObject transactionnProposalResponsePayload_pResponse(ChaincodeAction chaincodeAction){
		
		JsonObject chaincodeActionJSON = new JsonObject();
		
		ChaincodeID chaincodeIDAction = chaincodeAction.getChaincodeId();
		chaincodeActionJSON.addProperty("chaincode_id", chaincodeIDAction.getName());
		chaincodeActionJSON.addProperty("chaincode_version", chaincodeIDAction.getVersion());
		Response chaincodeResponseAction = chaincodeAction.getResponse();									
		chaincodeActionJSON.addProperty("txn_status", chaincodeResponseAction.getStatus()); 
		chaincodeActionJSON.addProperty("txn_message_payload", chaincodeResponseAction.getPayload().toStringUtf8());
		
		return chaincodeActionJSON;
	}
	
	private JsonArray transacitonProposalResponsePayload_kvrwset(ChaincodeAction chaincodeAction) throws InvalidProtocolBufferException {
		TxReadWriteSet transactionRWSet = Rwset.TxReadWriteSet.parseFrom(chaincodeAction.getResults());
		JsonArray  transactionRWSetJSONArray = new JsonArray();
		

		JsonArray transactionReadSetJSONArray = new JsonArray();
		JsonArray transactionWriteSetJSONArray = new JsonArray();

		int nsrwsetCount = transactionRWSet.getNsRwsetCount();
		for(int iCounter=0;iCounter<nsrwsetCount;iCounter++ ) {
			KVRWSet rwSet = KVRWSet.parseFrom(transactionRWSet.getNsRwset(iCounter).getRwset());
			JsonObject transactionRWSetJSON = new JsonObject();
			transactionRWSetJSON.addProperty("nameSpace", transactionRWSet.getNsRwset(iCounter).getNamespace());
			int readCount = rwSet.getReadsCount();
			int writeCount = rwSet.getWritesCount();
			for(int readCounter=0 ; readCounter<readCount; readCounter++ ) {
				JsonObject transactionReadSetJSON = new JsonObject();

				transactionReadSetJSON.addProperty("readSetKey",rwSet.getReads(readCounter).getKey());
				transactionReadSetJSON.addProperty("readSetVersionBlockNumber",rwSet.getReads(readCounter).getVersion().getBlockNum());
				transactionReadSetJSON.addProperty("readSetVersionTxnNumber",rwSet.getReads(readCounter).getVersion().getTxNum());
				
				transactionReadSetJSONArray.add(transactionReadSetJSON);
			}
			transactionRWSetJSON.add("readSets",transactionReadSetJSONArray);
			
			for(int writeCounter=0; writeCounter<writeCount;writeCounter++) {
				JsonObject transactionWriteSetJSON = new JsonObject();
				transactionWriteSetJSON.addProperty("writSetKey", rwSet.getWrites(writeCounter).getKey());
				transactionWriteSetJSON.addProperty("writeSetIsDelete", rwSet.getWrites(writeCounter).getIsDelete());
					
				transactionWriteSetJSONArray.add(transactionWriteSetJSON);
			}
			transactionRWSetJSON.add("writeSets",transactionWriteSetJSONArray);
			transactionRWSetJSONArray.add(transactionRWSetJSON);
		}
		
		
		return transactionRWSetJSONArray;
		
	}
	public String digestSHA256(byte[] hashValue, boolean hexFlag) throws NoSuchAlgorithmException {
		MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
		byte[] nonHexDigest = sha256Digest.digest(hashValue);
		
		
		StringBuffer stringBuffer = new StringBuffer();
		for(int i=0; i<nonHexDigest.length; i++) {
			stringBuffer.append(Integer.toString((nonHexDigest[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		if(hexFlag)
			return stringBuffer.toString();
		else
			return new String(nonHexDigest);
		
		
	}
		
}
