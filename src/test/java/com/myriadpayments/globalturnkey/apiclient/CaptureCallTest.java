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

public class CaptureCallTest {

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

		// AUTH
		final Map<String, String> authParams = new HashMap<>();
		authParams.put("amount", "20.0");
		authParams.put("channel", Channel.ECOM.getCode());
		authParams.put("country", CountryCode.GB.getCode());
		authParams.put("currency", CurrencyCode.EUR.getCode());
		authParams.put("paymentSolutionId", "500");
		authParams.put("customerId", tokenizeCall.getString("customerId"));
		authParams.put("specinCreditCardToken", tokenizeCall.getString("cardToken"));
		authParams.put("specinCreditCardCVV", "111");

		final AuthCall auth = new AuthCall(config, authParams, null);
		final JSONObject authCall = auth.execute();

		// CAPTURE
		final Map<String, String> inputParams = new HashMap<>();
		inputParams.put("originalMerchantTxId", authCall.getString("merchantTxId"));
		inputParams.put("amount", "20.0");

		final CaptureCall call = new CaptureCall(config, inputParams, null);
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
			inputParams.put("originalMerchantTxId", "8Gii57iYNVSd27xnFZzR");
			// inputParams.put("amount", "20.0");

			final CaptureCall call = new CaptureCall(config, inputParams, null);
			call.execute();

		} catch (RequiredParamException e) {

			Assert.assertEquals(new HashSet<>(Arrays.asList("amount")), e.getMissingFields());
			throw e;

		}
	}
}
