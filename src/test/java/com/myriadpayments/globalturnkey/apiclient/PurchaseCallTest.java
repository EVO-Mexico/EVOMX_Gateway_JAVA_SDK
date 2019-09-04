package com.myriadpayments.globalturnkey.apiclient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myriadpayments.globalturnkey.apiclient.code.Channel;
import com.myriadpayments.globalturnkey.apiclient.code.CountryCode;
import com.myriadpayments.globalturnkey.apiclient.code.CurrencyCode;
import com.myriadpayments.globalturnkey.apiclient.exception.RequiredParamException;
import com.myriadpayments.globalturnkey.config.ApplicationConfig;
import com.myriadpayments.globalturnkey.config.TestConfig;

public class PurchaseCallTest {

	private static ApplicationConfig config;

	@BeforeClass
	public static void setUp() {
		config = TestConfig.getInstance();
	}

	/**
	 * successful case
	 */
	@Test
	public void noExTestCall() {

		// TOKENIZE
		final Map<String, String> tokenizeParams = new HashMap<>();
		tokenizeParams.put("number", "5454545454545454");
		tokenizeParams.put("nameOnCard", "John Doe");
		tokenizeParams.put("expiryMonth", "12");
		tokenizeParams.put("expiryYear", "2018");

		final TokenizeCall tokenize = new TokenizeCall(config, tokenizeParams, null);
		final JSONObject tokenizeCall = tokenize.execute();

		// PURCHASE
		final Map<String, String> purchaseParams = new HashMap<>();
		purchaseParams.put("amount", "20.0");
		purchaseParams.put("channel", Channel.ECOM.getCode());
		purchaseParams.put("country", CountryCode.GB.getCode());
		purchaseParams.put("currency", CurrencyCode.EUR.getCode());
		purchaseParams.put("paymentSolutionId", "500");
		purchaseParams.put("customerId", tokenizeCall.getString("customerId"));
		purchaseParams.put("specinCreditCardToken", tokenizeCall.getString("cardToken"));
		purchaseParams.put("specinCreditCardCVV", "111");

		final PurchaseCall call = new PurchaseCall(config, purchaseParams, null);
		JSONObject result = call.execute();

		// note that any error will cause the throwing of some kind of SDKException (which extends RuntimeException)
		// still we make an assertNotNull

		Assert.assertNotNull(result);

	}

	/**
	 * RequiredParamException test (intentionally left out param)
	 */
	@Test(expected = RequiredParamException.class)
	public void reqParExExpTestCall() {

		try {

			final Map<String, String> inputParams = new HashMap<>();
			inputParams.put("amount", "20.0");
			inputParams.put("channel", Channel.ECOM.getCode());
			// inputParams.put("country", CountryCode.GB.getCode());
			// inputParams.put("currency", CurrencyCode.EUR.getCode());
			inputParams.put("paymentSolutionId", "500");
			inputParams.put("customerId", "8Gii57iYNVSd27xnFZzR");

			final PurchaseCall call = new PurchaseCall(config, inputParams, null);
			call.execute();

		} catch (RequiredParamException e) {

			Assert.assertEquals(new HashSet<>(Arrays.asList("currency", "country")), e.getMissingFields());
			throw e;

		}
	}

}
