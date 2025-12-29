package com.btr.proxy.selector;

import static org.junit.Assert.*;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.btr.proxy.TestUtil;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.misc.ProxyListFallbackSelector;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.util.Logger;
import com.btr.proxy.util.Logger.LogLevel;

/*****************************************************************************
 * Unit tests to verify that proxy selection logging works correctly.
 * This test ensures that INFO level logs are generated when proxies are selected.
 * 
 * @author Test
 ****************************************************************************/

public class ProxySelectorLoggingTest {
	
	private List<String> logMessages;
	private Logger.LogBackEnd originalBackend;
	
	/*************************************************************************
	 * Setup a logging backend that captures log messages.
	 ************************************************************************/
	@Before
	public void setup() {
		logMessages = new ArrayList<String>();
		originalBackend = Logger.getBackend();
		
		// Install a test logging backend that captures INFO level messages
		Logger.setBackend(new Logger.LogBackEnd() {
			@Override
			public void log(Class<?> clazz, LogLevel loglevel, String msg, Object... params) {
				// Only capture INFO level messages for proxy selection
				if (loglevel == LogLevel.INFO) {
					String formattedMessage = MessageFormat.format(msg, params);
					logMessages.add(formattedMessage);
					// Also print to console for verification
					System.out.println("[LOG] " + formattedMessage);
				}
			}

			@Override
			public boolean isLogginEnabled(LogLevel logLevel) {
				return logLevel == LogLevel.INFO || logLevel == LogLevel.ERROR;
			}
		});
	}
	
	/*************************************************************************
	 * Restore original logging backend.
	 ************************************************************************/
	@After
	public void teardown() {
		Logger.setBackend(originalBackend);
		logMessages.clear();
	}
	
	/*************************************************************************
	 * Test that FixedProxySelector logs proxy information.
	 ************************************************************************/
	@Test
	public void testFixedProxySelectorLogging() {
		ProxySelector ps = new FixedProxySelector("test-proxy.example.com", 8080);
		ps.select(TestUtil.HTTP_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Verify the log message contains expected information
		String logMessage = logMessages.get(0);
		assertTrue("Log should contain URI", logMessage.contains(TestUtil.HTTP_TEST_URI.toString()));
		assertTrue("Log should contain proxy host", logMessage.contains("test-proxy.example.com"));
		// MessageFormat may format numbers with commas, so check for port in various formats
		assertTrue("Log should contain proxy port", 
			logMessage.contains("8080") || logMessage.contains("8,080"));
		assertTrue("Log should indicate proxy usage", logMessage.contains("via proxy"));
		
		System.out.println("✓ FixedProxySelector logging verified: " + logMessage);
	}
	
	/*************************************************************************
	 * Test that FixedProxySelector logs DIRECT when NO_PROXY is used.
	 ************************************************************************/
	@Test
	public void testFixedProxySelectorDirectLogging() {
		ProxySelector ps = new FixedProxySelector(Proxy.NO_PROXY);
		ps.select(TestUtil.HTTP_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Verify the log message indicates DIRECT connection
		String logMessage = logMessages.get(0);
		assertTrue("Log should contain URI", logMessage.contains(TestUtil.HTTP_TEST_URI.toString()));
		assertTrue("Log should indicate DIRECT connection", 
			logMessage.contains("DIRECT") || logMessage.contains("no proxy"));
		
		System.out.println("✓ FixedProxySelector DIRECT logging verified: " + logMessage);
	}
	
	/*************************************************************************
	 * Test that ProtocolDispatchSelector logs proxy information with protocol context.
	 ************************************************************************/
	@Test
	public void testProtocolDispatchSelectorLogging() {
		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		ps.setSelector("http", new FixedProxySelector(TestUtil.HTTP_TEST_PROXY));
		
		ps.select(TestUtil.HTTP_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Find the log message that contains protocol context (may be multiple messages)
		String logMessage = null;
		for (String msg : logMessages) {
			if (msg.contains("protocol: http")) {
				logMessage = msg;
				break;
			}
		}
		assertNotNull("Should have found log message with protocol context", logMessage);
		assertTrue("Log should contain URI", logMessage.contains(TestUtil.HTTP_TEST_URI.toString()));
		assertTrue("Log should contain protocol context", logMessage.contains("protocol: http"));
		assertTrue("Log should contain proxy host", logMessage.contains("http_proxy.unit-test.invalid"));
		
		System.out.println("✓ ProtocolDispatchSelector logging verified: " + logMessage);
	}
	
	/*************************************************************************
	 * Test that ProxyListFallbackSelector logs proxy information.
	 ************************************************************************/
	@Test
	public void testProxyListFallbackSelectorLogging() {
		FixedProxySelector delegate = new FixedProxySelector(TestUtil.HTTP_TEST_PROXY);
		ProxySelector ps = new ProxyListFallbackSelector(delegate);
		
		ps.select(TestUtil.HTTP_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Verify the log message contains expected information
		String logMessage = logMessages.get(0);
		assertTrue("Log should contain URI", logMessage.contains(TestUtil.HTTP_TEST_URI.toString()));
		assertTrue("Log should contain proxy host", logMessage.contains("http_proxy.unit-test.invalid"));
		
		System.out.println("✓ ProxyListFallbackSelector logging verified: " + logMessage);
	}
	
	/*************************************************************************
	 * Test that NoProxySelector logs DIRECT connection information.
	 ************************************************************************/
	@Test
	public void testNoProxySelectorLogging() {
		ProxySelector ps = NoProxySelector.getInstance();
		ps.select(TestUtil.HTTP_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Verify the log message indicates DIRECT connection
		String logMessage = logMessages.get(0);
		assertTrue("Log should contain URI", logMessage.contains(TestUtil.HTTP_TEST_URI.toString()));
		assertTrue("Log should indicate DIRECT connection", 
			logMessage.contains("DIRECT") || logMessage.contains("no proxy"));
		assertTrue("Log should contain context", logMessage.contains("no proxy configured"));
		
		System.out.println("✓ NoProxySelector logging verified: " + logMessage);
	}
	
	/*************************************************************************
	 * Test that PacProxySelector logs proxy information with PAC script context.
	 ************************************************************************/
	@Test
	public void testPacProxySelectorLogging() throws Exception {
		// Use a simple PAC script that returns a proxy
		String pacScriptUrl = new java.io.File(
			TestUtil.TEST_DATA_FOLDER + "pac", "test1.pac").toURI().toURL().toString();
		
		PacProxySelector ps = new PacProxySelector(new UrlPacScriptSource(pacScriptUrl));
		ps.select(TestUtil.HTTP_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Verify the log message contains PAC script context
		String logMessage = logMessages.get(0);
		assertTrue("Log should contain URI", logMessage.contains(TestUtil.HTTP_TEST_URI.toString()));
		assertTrue("Log should contain PAC script context", logMessage.contains("PAC script"));
		
		System.out.println("✓ PacProxySelector logging verified: " + logMessage);
	}
	
	/*************************************************************************
	 * Test that logging works for HTTPS protocol.
	 ************************************************************************/
	@Test
	public void testHttpsProtocolLogging() {
		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		ps.setSelector("https", new FixedProxySelector(TestUtil.HTTPS_TEST_PROXY));
		
		ps.select(TestUtil.HTTPS_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Find the log message that contains protocol context (may be multiple messages)
		String logMessage = null;
		for (String msg : logMessages) {
			if (msg.contains("protocol: https")) {
				logMessage = msg;
				break;
			}
		}
		assertNotNull("Should have found log message with protocol context", logMessage);
		assertTrue("Log should contain protocol context", logMessage.contains("protocol: https"));
		assertTrue("Log should contain HTTPS proxy host", logMessage.contains("https_proxy.unit-test.invalid"));
		
		System.out.println("✓ HTTPS protocol logging verified: " + logMessage);
	}
	
	/*************************************************************************
	 * Test that logging includes proxy type (HTTP/SOCKS).
	 ************************************************************************/
	@Test
	public void testProxyTypeLogging() {
		ProxySelector ps = new FixedProxySelector(TestUtil.SOCKS_TEST_PROXY);
		ps.select(TestUtil.HTTP_TEST_URI);
		
		// Verify that a log message was generated
		assertTrue("Should have logged proxy selection", logMessages.size() > 0);
		
		// Verify the log message contains proxy type
		String logMessage = logMessages.get(0);
		assertTrue("Log should contain proxy type", 
			logMessage.contains("SOCKS://") || logMessage.contains("SOCKS"));
		
		System.out.println("✓ Proxy type logging verified: " + logMessage);
	}
}

