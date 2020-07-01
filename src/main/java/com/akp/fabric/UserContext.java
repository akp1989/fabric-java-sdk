package com.akp.fabric;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Set;

import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

/**
 * Implementation class for User
 * 
 * @author Prabakaran AK
 *
 */

public class UserContext implements User, Serializable {
	
	private static final long serialVersionUID = 1L;
	protected String name;
	protected Set<String> roles;
	protected String account;
	protected String affiliation;
	protected Enrollment enrollment;
	protected String mspId;
	protected Identity identity;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public void setEnrollment(Enrollment enrollment) {
		this.enrollment = enrollment;
	}

	public void setMspId(String mspId) {
		this.mspId = mspId;
	}
	
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public String getAffiliation() {
		return affiliation;
	}
	
	public Identity getIdentity() {
		return identity;
	}

	@Override
	public Enrollment getEnrollment() {
		if(identity!=null) {
		return new Enrollment() {
			@Override
			public PrivateKey getKey() {
				return identity.getPrivateKey();
			}

			@Override
			public String getCert() {
				return identity.getCertificate();
			}
		};
		}else
			return null;
	}

	@Override
	public String getMspId() {
		return mspId;
	}

}
