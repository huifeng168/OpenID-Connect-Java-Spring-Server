/*******************************************************************************
 * Copyright 2012 The MITRE Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mitre.openid.connect.client;

import java.util.ArrayList;
import java.util.Collection;

import org.mitre.openid.connect.config.OIDCServerConfiguration;
import org.mitre.openid.connect.model.IdToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Michael Walsh, Justin Richer
 * 
 */
public class OpenIdConnectAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 22100073066377804L;
    
	private final Object principal;
	private final String idTokenValue; // string representation of the id token
	private final String accessTokenValue; // string representation of the access token
	private final String refreshTokenValue; // string representation of the refresh token
	private final String issuer; // issuer URL (parsed from the id token)
	private final String userId; // user id (parsed from the id token)

	private final transient OIDCServerConfiguration serverConfiguration; // server configuration used to fulfill this token, don't serialize it
	private final transient UserInfo userInfo; // user info container, don't serialize it b/c it might be huge and can be re-fetched
	
	/**
	 * Constructs OpenIdConnectAuthenticationToken with a full set of authorities, marking this as authenticated.
	 * 
	 * Set to authenticated.
	 * 
	 * Constructs a Principal out of the user_id and issuer.
	 * @param userId
	 * @param authorities
	 * @param principal
	 * @param idToken
	 */
	public OpenIdConnectAuthenticationToken(String userId, String issuer, 
			UserInfo userInfo, Collection<? extends GrantedAuthority> authorities,
			String idTokenValue, String accessTokenValue, String refreshTokenValue) {

		super(authorities);

		this.principal = ImmutableMap.of("user_id", userId, "issuer", issuer);
		this.userInfo = userInfo;
		this.userId = userId;
		this.issuer = issuer;
		this.idTokenValue = idTokenValue;
		this.accessTokenValue = accessTokenValue;
		this.refreshTokenValue = refreshTokenValue;

		this.serverConfiguration = null; // we don't need a server config anymore
		
		setAuthenticated(true);
	}

	/**
	 * Constructs OpenIdConnectAuthenticationToken for use as a data shuttle from the filter to the auth provider. 
	 * 
	 * Set to not-authenticated.
	 * 
	 * Constructs a Principal out of the user_id and issuer.
	 * @param userId
	 * @param idToken
	 */
	public OpenIdConnectAuthenticationToken(String userId, String issuer, 
			OIDCServerConfiguration serverConfiguration, 
			String idTokenValue, String accessTokenValue, String refreshTokenValue) {

		super(new ArrayList<GrantedAuthority>(0));

		this.principal = ImmutableMap.of("user_id", userId, "issuer", issuer);
		this.userId = userId;
		this.issuer = issuer;
		this.idTokenValue = idTokenValue;
		this.accessTokenValue = accessTokenValue;
		this.refreshTokenValue = refreshTokenValue;

		this.userInfo = null; // we don't have a UserInfo yet
		
		this.serverConfiguration = serverConfiguration;
		
		
		setAuthenticated(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.Authentication#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return accessTokenValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.Authentication#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return principal;
	}

	public String getUserId() {
		return userId;
	}

	/**
     * @return the idTokenValue
     */
    public String getIdTokenValue() {
    	return idTokenValue;
    }

	/**
     * @return the accessTokenValue
     */
    public String getAccessTokenValue() {
    	return accessTokenValue;
    }

	/**
     * @return the refreshTokenValue
     */
    public String getRefreshTokenValue() {
    	return refreshTokenValue;
    }

	/**
     * @return the serverConfiguration
     */
    public OIDCServerConfiguration getServerConfiguration() {
    	return serverConfiguration;
    }

	/**
     * @return the issuer
     */
    public String getIssuer() {
    	return issuer;
    }

	/**
     * @return the userInfo
     */
    public UserInfo getUserInfo() {
    	return userInfo;
    }
	
	
}