package com.btr.proxy.util;

import static junit.framework.Assert.*;

import java.net.Proxy;
import java.util.List;

import org.junit.Test;

import com.btr.proxy.TestUtil;
import com.btr.proxy.selector.fixed.FixedProxySelector;


/*****************************************************************************
 * Unit tests for proxy util methods  
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class ProxyUtilTest {
	
	/*************************************************************************
	 * Test parsing method.
	 ************************************************************************/
	
	@Test
	public void testParseProxySettings() {
		FixedProxySelector rs = ProxyUtil.parseProxySettings("http://http_proxy.unit-test.invalid/");
		List<Proxy> psList = rs.select(TestUtil.HTTP_TEST_URI);
		// JDK 21+ includes [/<unresolved>] for unresolved addresses
		String proxyString = psList.get(0).toString();
		assertTrue("Expected proxy string to contain host and port", 
			proxyString.contains("http_proxy.unit-test.invalid") && proxyString.contains(":80"));
	}
	
	/*************************************************************************
	 * Test parsing method.
	 ************************************************************************/
	
	@Test
	public void testParseProxySettings2() {
		FixedProxySelector rs = ProxyUtil.parseProxySettings("http://http_proxy.unit-test.invalid:8080/");
		List<Proxy> psList = rs.select(TestUtil.HTTP_TEST_URI);
		// JDK 21+ includes [/<unresolved>] for unresolved addresses
		String proxyString = psList.get(0).toString();
		assertTrue("Expected proxy string to contain host and port", 
			proxyString.contains("http_proxy.unit-test.invalid") && proxyString.contains(":8080"));
	}
	
	/*************************************************************************
	 * Test parsing method.
	 ************************************************************************/
	
	@Test
	public void testParseProxySettings3() {
		FixedProxySelector rs = ProxyUtil.parseProxySettings("http_proxy.unit-test.invalid");
		List<Proxy> psList = rs.select(TestUtil.HTTP_TEST_URI);
		// JDK 21+ includes [/<unresolved>] for unresolved addresses
		String proxyString = psList.get(0).toString();
		assertTrue("Expected proxy string to contain host and port", 
			proxyString.contains("http_proxy.unit-test.invalid") && proxyString.contains(":80"));
	}

	/*************************************************************************
	 * Test parsing method.
	 ************************************************************************/
	
	@Test
	public void testParseProxySettings4() {
		FixedProxySelector rs = ProxyUtil.parseProxySettings("http_proxy.unit-test.invalid:8080");
		List<Proxy> psList = rs.select(TestUtil.HTTP_TEST_URI);
		// JDK 21+ includes [/<unresolved>] for unresolved addresses
		String proxyString = psList.get(0).toString();
		assertTrue("Expected proxy string to contain host and port", 
			proxyString.contains("http_proxy.unit-test.invalid") && proxyString.contains(":8080"));
	}

	/*************************************************************************
	 * Test parsing method.
	 ************************************************************************/
	
	@Test
	public void testParseProxySettings5() {
		FixedProxySelector rs = ProxyUtil.parseProxySettings("192.123.123.1:8080");
		List<Proxy> psList = rs.select(TestUtil.HTTP_TEST_URI);
		// JDK 21+ includes [/<unresolved>] for unresolved addresses
		String proxyString = psList.get(0).toString();
		assertTrue("Expected proxy string to contain host and port", 
			proxyString.contains("192.123.123.1") && proxyString.contains(":8080"));
	}


}

