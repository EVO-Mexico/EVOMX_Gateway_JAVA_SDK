package com.myriadpayments.globalturnkey;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.myriadpayments.globalturnkey.apiclient.AuthCall;
import com.myriadpayments.globalturnkey.apiclient.CaptureCall;
import com.myriadpayments.globalturnkey.apiclient.GetAvailablePaymentSolutionsCall;
import com.myriadpayments.globalturnkey.apiclient.PurchaseCall;
import com.myriadpayments.globalturnkey.apiclient.RefundCall;
import com.myriadpayments.globalturnkey.apiclient.StatusCheckCall;
import com.myriadpayments.globalturnkey.apiclient.TokenizeCall;
import com.myriadpayments.globalturnkey.apiclient.VoidCall;
import com.myriadpayments.globalturnkey.apiclient.code.ActionType;
import com.myriadpayments.globalturnkey.apiclient.exception.ActionCallException;
import com.myriadpayments.globalturnkey.apiclient.exception.GeneralException;
import com.myriadpayments.globalturnkey.apiclient.exception.PostToApiException;
import com.myriadpayments.globalturnkey.apiclient.exception.RequiredParamException;
import com.myriadpayments.globalturnkey.apiclient.exception.TokenAcquirationException;
import com.myriadpayments.globalturnkey.config.ApplicationConfig;

/**
 * Command line version
 *
 * @author erbalazs
 */
public class Start {

	private final static Logger logger = Logger.getLogger(Start.class.getName());

	public static void main(final String[] args) {

		// sample cmd params:
		// -action TOKENIZE -merchantId 5000 -password 5678 -number 5454545454545454 -nameOnCard "John Doe" -expiryMonth 12 -expiryYear 2018

		final Map<String, String> params = new HashMap<>();

		String key = null;

		for (final String a : args) {

			if (a == null || a.trim().isEmpty()) {
				System.err.println("Error at an argument");
				System.exit(1);
			}

			if (a.charAt(0) == '-') {
				if (a.length() < 2) {
					System.err.println("Error at argument: " + a);
					System.exit(1);
				}

				key = a.substring(1);

			} else {

				if (key == null) {
					System.err.println("Error at argument: " + a);
					System.exit(1);
				}

				params.put(key, a);
				key = null;

			}

		}

		ActionType action = ActionType.GET_AVAILABLE_PAYSOLS;
		try {
			action = ActionType.valueOfCode(params.get("action"));
		} catch (final IllegalArgumentException ex) {
			System.err.println("Illegal action parameter usage");
			System.exit(1);
		}

		try {

			final ApplicationConfig config = ApplicationConfig.getInstanceBasedOnSysProp();

			JSONObject result; // note that the optional PrintWriter prints the result too (and if errors/exceptions happen, they will be printed too)

			switch (action) {
			case AUTH:
				result = new AuthCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			case CAPTURE:
				result = new CaptureCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			case GET_AVAILABLE_PAYSOLS:
				result = new GetAvailablePaymentSolutionsCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			case PURCHASE:
				result = new PurchaseCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			case REFUND:
				result = new RefundCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			case STATUS_CHECK:
				result = new StatusCheckCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			case TOKENIZE:
				result = new TokenizeCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			case VOID:
				result = new VoidCall(config, params, new PrintWriter(System.out, true)).execute();
				break;
			default:
				System.err.println("Illegal action parameter usage");
				System.exit(1);
				break;
			}

		} catch (final RequiredParamException e) {
			logger.log(Level.WARNING, "missing parameters", e);
			System.exit(2);
		} catch (final TokenAcquirationException e) {
			logger.log(Level.WARNING, "could not acquire token", e);
			System.exit(3);
		} catch (final ActionCallException e) {
			logger.log(Level.WARNING, "error during the action call", e);
			System.exit(4);
		} catch (final PostToApiException e) {
			logger.log(Level.SEVERE, "outgoing POST failed", e);
			System.exit(5);
		} catch (final GeneralException e) {
			logger.log(Level.SEVERE, "general SDK error", e);
			System.exit(6);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "other error", e);
			System.exit(7);
		}

	}

}
