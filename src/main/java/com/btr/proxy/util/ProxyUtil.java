package com.btr.proxy.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.util.Logger.LogLevel;

/*****************************************************************************
 * Small helper class for some common utility methods.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class ProxyUtil {
	
	public static final int DEFAULT_PROXY_PORT = 80;
	
	private static List<Proxy> noProxyList;
	private static Pattern pattern = Pattern.compile("\\w*?:?/*([^:/]+):?(\\d*)/?");
	
	/*************************************************************************
	 * Parse host and port out of a proxy variable.
	 * 
	 * @param proxyVar the proxy string. example: http://192.168.10.9:8080/
	 * @return a FixedProxySelector using this settings, null on parse error.
	 ************************************************************************/
	
	public static FixedProxySelector parseProxySettings(String proxyVar) {
		Logger.log(ProxyUtil.class, LogLevel.TRACE, "parseProxySettings({0})", proxyVar);
		if(proxyVar == null || proxyVar.trim().length() == 0) {
			return null;
		}
		
		Matcher matcher = pattern.matcher(proxyVar);
		if(matcher.matches()) {
			String host = matcher.group(1);
			int port;
			if(!"".equals(matcher.group(2))) {
				port = Integer.parseInt(matcher.group(2));
			} else {
				port = DEFAULT_PROXY_PORT;
			}

			Logger.log(ProxyUtil.class, LogLevel.TRACE, "host:{0}, port:{1}", host, port);
			return new FixedProxySelector(host.trim(), port);
		} else {
			return null;
		}
	}
	
	/*************************************************************************
	 * Gets an unmodifiable proxy list that will have as it's only entry an
	 * DIRECT proxy.
	 * 
	 * @return a list with a DIRECT proxy in it.
	 ************************************************************************/
	
	public static synchronized List<Proxy> noProxyList() {
		if(noProxyList == null) {
			ArrayList<Proxy> list = new ArrayList<Proxy>(1);
			list.add(Proxy.NO_PROXY);
			noProxyList = Collections.unmodifiableList(list);
		}
		
		Logger.log(ProxyUtil.class, LogLevel.TRACE, "noProxyList:{0}", noProxyList);
		return noProxyList;
	}
	
	/*************************************************************************
	 * Build a PAC proxy selector for the given URL.
	 * 
	 * @param url to fetch the PAC script from.
	 * @return a PacProxySelector or null if it is not possible to build a
	 *         working
	 *         selector.
	 ************************************************************************/
	public static PacProxySelector buildPacSelectorForUrl(String url) {
		PacProxySelector pacProxySelector = null;
		PacScriptSource pacSource = new UrlPacScriptSource(url);
		if(pacSource.isScriptValid()) {
			pacProxySelector = new PacProxySelector(pacSource);
		}
		
		return pacProxySelector;
	}
	
	/*************************************************************************
	 * Logs proxy selection information at INFO level.
	 * This utility method provides consistent logging format across all proxy selectors.
	 * 
	 * @param clazz the class that is logging (used for logger context)
	 * @param uri the URI for which the proxy was selected
	 * @param proxies the list of proxies selected (may be empty or null)
	 * @param context optional context string (e.g., "protocol: https", "PAC script", etc.)
	 *                Can be null if no additional context is needed.
	 ************************************************************************/
	public static void logProxySelection(Class<?> clazz, URI uri, List<Proxy> proxies, String context) {
		if (proxies == null || proxies.isEmpty()) {
			String message = "Request to {0}";
			if (context != null && !context.isEmpty()) {
				message += " (" + context + ")";
			}
			message += " will be sent DIRECT (no proxy)";
			Logger.log(clazz, LogLevel.INFO, message, uri);
			return;
		}
		
		Proxy selectedProxy = proxies.get(0);
		if (selectedProxy == null || selectedProxy == Proxy.NO_PROXY) {
			String message = "Request to {0}";
			if (context != null && !context.isEmpty()) {
				message += " (" + context + ")";
			}
			message += " will be sent DIRECT (no proxy)";
			Logger.log(clazz, LogLevel.INFO, message, uri);
			return;
		}
		
		SocketAddress address = selectedProxy.address();
		String message = "Request to {0}";
		if (context != null && !context.isEmpty()) {
			message += " (" + context + ")";
		}
		
		if (address instanceof InetSocketAddress) {
			InetSocketAddress inetAddr = (InetSocketAddress) address;
			message += " will be sent via proxy: {1}://{2}:{3}";
			Logger.log(clazz, LogLevel.INFO, message, 
				uri, selectedProxy.type(), inetAddr.getHostString(), inetAddr.getPort());
		} else {
			message += " will be sent via proxy: {1}";
			Logger.log(clazz, LogLevel.INFO, message, uri, selectedProxy);
		}
	}
	
}
