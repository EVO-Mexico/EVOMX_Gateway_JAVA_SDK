package com.myriadpayments.globalturnkey.web.servlet.sample.s2s;

import java.io.IOException;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myriadpayments.globalturnkey.apiclient.RefundCall;
import com.myriadpayments.globalturnkey.apiclient.exception.ActionCallException;
import com.myriadpayments.globalturnkey.apiclient.exception.GeneralException;
import com.myriadpayments.globalturnkey.apiclient.exception.RequiredParamException;
import com.myriadpayments.globalturnkey.apiclient.exception.TokenAcquirationException;

/**
 * Sample
 * 
 * @author erbalazs
 *
 */
@WebServlet(name = "Refund", description = "Refund operation (can be called for a previous capture operation)", urlPatterns = "/refund")
public class RefundServlet extends AbstractServlet {

	@Override
	protected void process(final HttpServletRequest req, final HttpServletResponse resp) throws RequiredParamException, ActionCallException, TokenAcquirationException, GeneralException, IOException {

		final Map<String, String> inputParams = AbstractServlet.extractParams(req);
		new RefundCall(config, inputParams, resp.getWriter()).execute();

	}
}
