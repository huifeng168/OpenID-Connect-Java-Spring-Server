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
package org.mitre.oauth2.view;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.view.AbstractView;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TokenIntrospection extends AbstractView {
	
	private static Logger logger = LoggerFactory.getLogger(TokenIntrospection.class);

	@Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {

		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				/*
				if (f.getDeclaringClass().isAssignableFrom(OAuth2AccessTokenEntity.class)) {
					// we don't want to serialize the whole object, just the scope and timeout
					if (f.getName().equals("scope")) {
						return false;
					} else if (f.getName().equals("expiration")) {
						return false;
					} else {
						// skip everything else on this class
						return true;
					}
				} else {
					// serialize other classes without filter (lists and sets and things)
					return false;
				}
				*/
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				// skip the JPA binding wrapper
				if (clazz.equals(BeanPropertyBindingResult.class)) {
					return true;
				} else {
					return false;
				}
			}

		})
		.registerTypeAdapter(OAuth2AccessTokenEntity.class, new JsonSerializer<OAuth2AccessTokenEntity>() {
            public JsonElement serialize(OAuth2AccessTokenEntity src, Type typeOfSrc, JsonSerializationContext context) {
            	JsonObject token = new JsonObject();
            	
            	token.addProperty("valid", true);
            	
            	JsonArray scopes = new JsonArray();
            	for (String scope : src.getScope()) {
	                scopes.add(new JsonPrimitive(scope));
                }
            	token.add("scope", scopes);
            	
            	token.add("expires", context.serialize(src.getExpiration()));
            	
            	return token;
            }
			
		})
		.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
		.create();

		response.setContentType("application/json");

		Writer out;
		
		try {
		
			out = response.getWriter();
			Object obj = model.get("entity");
			if (obj == null) {
				obj = model;
			}

			gson.toJson(obj, out);
		
		} catch (IOException e) {
		
			logger.error("IOException occurred in TokenIntrospection.java: " + e.getStackTrace());
		
		}

    }

}
