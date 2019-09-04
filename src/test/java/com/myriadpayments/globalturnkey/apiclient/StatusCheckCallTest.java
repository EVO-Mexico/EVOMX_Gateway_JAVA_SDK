package com.myriadpayments.globalturnkey.apiclient;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myriadpayments.globalturnkey.apiclient.code.Channel;
import com.myriadpayments.globalturnkey.apiclient.code.CountryCode;
import com.myriadpayments.globalturnkey.apiclient.code.CurrencyCode;
import com.myriadpayments.globalturnkey.apiclient.exception.ActionCallException;
import com.myriadpayments.globalturnkey.config.ApplicationConfig;
import com.myriadpayments.globalturnkey.config.TestConfig;

public class StatusCheckCallTest {

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

		final Map<String, String> inputParams = new HashMap<>();
		inputParams.put("txId", authCall.getString("txId"));
		inputParams.put("merchantTxId", authCall.getString("merchantTxId"));

		final StatusCheckCall call = new StatusCheckCall(config, inputParams, null);
		JSONObject result = call.execute();

		// note that any error will cause the throwing of some kind of SDKException (which extends RuntimeException)
		// still we make an assertNotNull

		Assert.assertNotNull(result);

	}

	/**
	 * ActionCallException test
	 */
	@Test(expected = ActionCallException.class)
	public void reqParExExpTestCall() {

		final Map<String, String> inputParams = new HashMap<>();
		// inputParams.put("txId", "11387591"); // intentionally left out
		// inputParams.put("merchantTxId", "11282092"); // intentionally left out

		final StatusCheckCall call = new StatusCheckCall(config, inputParams, null);
		call.execute();

	}
}
