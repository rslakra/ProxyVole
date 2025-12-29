package com.btr.proxy.selector.pac;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Calendar;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.btr.proxy.TestUtil;
import com.btr.proxy.util.ProxyException;

/*****************************************************************************
 * Tests for the javax.script PAC script parser. 
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class JavaxPacScriptParserTest {
	
	/*************************************************************************
	 * Check if JavaScript engine (Nashorn) is available.
	 * Nashorn was removed in JDK 15+, so this will return false on JDK 21+.
	 * @return true if JavaScript engine is available, false otherwise.
	 ************************************************************************/
	private static boolean isJavaScriptEngineAvailable() {
		ScriptEngineManager mng = new ScriptEngineManager();
		ScriptEngine engine = mng.getEngineByMimeType("text/javascript");
		return engine != null;
	}
	
	/*************************************************************************
	 * Set calendar for date and time base tests.
	 * Current date for all tests  is: 15. December 1994 12:00.00 
	 * its a Thursday 
	 ************************************************************************/
	@BeforeClass
	public static void setup() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1994);
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 15);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 00);
		
		// TODO Rossi 26.08.2010 need to fake time 
		// JavaxPacScriptParser.setCurrentTime(cal);
	}

	/*************************************************************************
	 * Cleanup after the tests.
	 ************************************************************************/
	@AfterClass
	public static void teadDown() {
		//JavaxPacScriptParser.setCurrentTime(null);
	}
	
	/*************************************************************************
	 * Test method
	 * @throws ProxyException on proxy detection error.
	 * @throws MalformedURLException on URL erros 
	 ************************************************************************/
	@Test
	public void testScriptExecution() throws ProxyException, MalformedURLException {
		Assume.assumeTrue("JavaScript engine (Nashorn) is not available in JDK 15+", isJavaScriptEngineAvailable());
		PacScriptParser p = new JavaxPacScriptParser(new UrlPacScriptSource(toUrl("test1.pac")));
		p.evaluate(TestUtil.HTTP_TEST_URI.toString(), "host1.unit-test.invalid");
	}

	/*************************************************************************
	 * Test method
	 * @throws ProxyException on proxy detection error.
	 * @throws MalformedURLException on URL erros 
	 ************************************************************************/
	@Test
	public void testCommentsInScript() throws ProxyException, MalformedURLException {
		Assume.assumeTrue("JavaScript engine (Nashorn) is not available in JDK 15+", isJavaScriptEngineAvailable());
		PacScriptParser p = new JavaxPacScriptParser(new UrlPacScriptSource(toUrl("test2.pac")));
		p.evaluate(TestUtil.HTTP_TEST_URI.toString(), "host1.unit-test.invalid");
	}
	
	/*************************************************************************
	 * Test method
	 * @throws ProxyException on proxy detection error.
	 * @throws MalformedURLException on URL erros 
	 ************************************************************************/
	@Test
	@Ignore //Test deactivated because it will not run in Java 1.5 and time based test are unstable 
	public void testScriptWeekDayScript() throws ProxyException, MalformedURLException {
		PacScriptParser p = new JavaxPacScriptParser(new UrlPacScriptSource(toUrl("testWeekDay.pac")));
		p.evaluate(TestUtil.HTTP_TEST_URI.toString(), "host1.unit-test.invalid");
	}

	/*************************************************************************
	 * Test method
	 * @throws ProxyException on proxy detection error.
	 * @throws MalformedURLException on URL erros 
	 ************************************************************************/
	@Test
	@Ignore //Test deactivated because it will not run in Java 1.5 and time based test are unstable 
	public void testDateRangeScript() throws ProxyException, MalformedURLException {
		PacScriptParser p = new JavaxPacScriptParser(new UrlPacScriptSource(toUrl("testDateRange.pac")));
		p.evaluate(TestUtil.HTTP_TEST_URI.toString(), "host1.unit-test.invalid");
	}

	/*************************************************************************
	 * Test method
	 * @throws ProxyException on proxy detection error.
	 * @throws MalformedURLException on URL erros 
	 ************************************************************************/
	@Test
	@Ignore //Test deactivated because it will not run in Java 1.5 and time based test are unstable 
	public void testTimeRangeScript() throws ProxyException, MalformedURLException {
		PacScriptParser p = new JavaxPacScriptParser(new UrlPacScriptSource(toUrl("testTimeRange.pac")));
		p.evaluate(TestUtil.HTTP_TEST_URI.toString(), "host1.unit-test.invalid");
	}
	
	/*************************************************************************
	 * Test method
	 * @throws ProxyException on proxy detection error.
	 * @throws MalformedURLException on URL erros 
	 ************************************************************************/
	@Test
	public void methodsShouldReturnJsStrings() throws ProxyException, MalformedURLException {
		Assume.assumeTrue("JavaScript engine (Nashorn) is not available in JDK 15+", isJavaScriptEngineAvailable());
		PacScriptParser p = new JavaxPacScriptParser(new UrlPacScriptSource(toUrl("testReturnTypes.pac")));
		String actual = p.evaluate(TestUtil.HTTP_TEST_URI.toString(), "host1.unit-test.invalid");
		Assert.assertEquals("number boolean string", actual);
	}
	
	/*************************************************************************
	 * Helper method to build the url to the given test file
	 * @param testFile the name of the test file.
	 * @return the URL. 
	 * @throws MalformedURLException
	 ************************************************************************/
	
	private String toUrl(String testFile) throws MalformedURLException {
		return new File(TestUtil.TEST_DATA_FOLDER+"pac", testFile).toURI().toURL().toString();
	}
		
}

