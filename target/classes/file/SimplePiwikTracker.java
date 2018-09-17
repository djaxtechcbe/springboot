package org.piwik;
/**
 * Piwik - Open source web analytics
 * 
 * @license released under BSD License http://www.opensource.org/licenses/bsd-license.php
 * @version $Id: SimplePiwikTracker.java,v 1.6 2012/06/01 16:42:18 todd Exp $
 * @link http://piwik.org/docs/tracking-api/
 *
 * @category Piwik
 * @package PiwikTracker
 */


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;



/**
 * Piwik - Open source web analytics
 * 
 * Client to record visits, page views, Goals, in a Piwik server.
 * For more information, see http://piwik.org/docs/tracking-api/
 * 
 * released under BSD License http://www.opensource.org/licenses/bsd-license.php
 *
 * @link http://piwik.org/docs/tracking-api/
 * @author Martin Fochler, Klaus Pfeiffer, Bernhard Friedreich, Todd Hivnor
 * @version 1.1
 * 
 * <p>Usage:
 * <pre>
 * 
      int siteid = 1;
      int goalid = 4;
      try {
        	SimplePiwikTracker tracker = new SimplePiwikTracker(siteid, "http://example.com/piwik", request);
        	tracker.doTrackGoal(goalid);
        	
      } catch (Exception ex) {
        	LOG.error("Error tracking Piwik goal", ex); 
        	
        	// You can also trap particular exceptions if you like
        	// For example you might handle IOException differently.
        	// Those will be raised if the network is down. 
        	// So perhaps you want to retry later.
      }
     </pre> 

 *  
 * A SimplePiwikTracker instances can be used to track multiple events for the same user. 
 * But it should not be used for different users! 
 * 
 * This class has many setter values which have questionable utility and have not been tested.
 * For example it is unclear why you would want to call any of the following:
 * setAcceptLanguage, setCustomData, setForcedDatetime, setIp, setPageUrl, 
 * setResolution, setTokenAuth, setUrlReferer, setUserAgent or setVisitorId
 * Of these take particular care with setTokenAuth, setVisitorId as they may leave 
 * the tracker in an inconsistent state.
 */

/*
 *  * Changes (before v1.1)
 *  - coding style fixes
 *  - converted from spaces to tabs
 *  - cookiesupport removed cause was not used
 *  - more checkstyle
 *  - the url will not have empty parameters
 *  - Java 1.5 needed (no! ... we actually need 1.6)
 *  - logging with commons-logging
 *
 * Changes for v1.1 by Todd Hivnor:
 *  - MOST IMPORTANT: correct logic which extracted the visitorId from the cookie. 
 *       The name of the cookie is now something like _pk_id.6.1fff
 *       It is NOT simply "piwik_visitor"
 *       
 *  - Be proxy-aware. When we are behing an Apache Proxy server we now record the correct
 *     IP address of the visitor. Prior to this fix the IP of the proxy server would be recorded. 
 *    
 *  - Remove PiwikException. We now return the raw exception type which makes it simpler to process the results.
 *      You can just trap "Exception" or you can be more fine-grained.
 *      
 *  - When making an instance of SimplePiwikTracker you must specify the SiteID. 
 *      This is important because the SiteID is used to recognize which cookie contains the correct visitor ID.
 *       
 *  - Use a single file 
 *     - Convert two dependant classes into inner classes:  ResponseData and EBrowserPlugins
 *        This way we have a single file which seems a bit simpler to manage.
 *        The classes seem unlikely be used by callers. But if they are needed they are public
 *     - Remove the interface, it seems unlikely to be useful and adds unneeded complexity 
 *      
 *  - Clairify that Java 1.6 is required not Java 1.5. (We use java.net.HttpCookie and String.isEmpty)
 *  
 *  - Remove depricated misspelled methods: getDownloadTackURL and getLinkTackURL
 *  
 *  - In getGoalTrackURL, change type of "goal" argument from String to int and "revenue" to a float
 *  
 *  - Removed setIdSite ... this must be set when the object is created
 *  
 *  - Renamed setRequestInfos to setHttpServletRequest
 *  
 */
public class SimplePiwikTracker  {

	/**
	 * Random object used for the request URL.
	 */
	private final static Random random = new Random(new Date().getTime());

	/**
	 * Our logger.
	 */
	private static final Log LOG = LogFactory.getLog(SimplePiwikTracker.class);

	/**
	 * Piwik API Version
	 */	
	public static final int VERSION = 1;

	/**
	 * Maximum number of custom variables.
	 */
	public static final int MAX_CUSTOM_VARIABLES = 5;

	/**
	 * Maximum length of a custom variable.
	 */
	public static final int MAX_CUSTOM_VARIABLE_LENGTH = 100;

	/**
	 * API URL, where the Piwik server can be reached.
	 *  
	 * Something like http://demo.piwik.org/piwik.php
	 */
	private URL apiurl;

	/**
	 *  Debug only.
	 */
	private String debugAppendUrl = "";

	/** 
	 * has to be set in the Request to the server 'HTTP_USER_AGENT'.
	 */
	private String userAgent;

	/**
	 * has to be set in the request to the server 'HTTP_ACCEPT_LANGUAGE'.
	 */
	private String language;

	private Date localTime;

	private Map<EBrowserPlugins, Boolean> plugins = new EnumMap<EBrowserPlugins, Boolean>(EBrowserPlugins.class);

	/**
	 * Custom data per pageview.
	 */
	private Map<String, String> pageCustomVar = new HashMap<String, String>(SimplePiwikTracker.MAX_CUSTOM_VARIABLES);

	/**
	 * Custom data per visitor.
	 */
	private Map<String, String> visitorCustomVar = new HashMap<String, String>(SimplePiwikTracker.MAX_CUSTOM_VARIABLES);

	private Date forcedDatetime;

	private String tokenAuth;

	/** The piwik tracking cookie.
	 * It has a name like _pk_id.3.1fff 
	 * It contains the visitorID
	 */
	private Cookie requestCookie;

	/** This is always initalized to a random value. 
	 * 
	 * When SimplePiwikTracker is created WITH a HttpServletRequest,
	 * we look at the cookies in the request and try to set the visitorId
	 * based on the cookie. 
	 * 
	 * Alternately you can set the visitorId by
	 * a call to setHttpServletRequest, setVisitorId,
	 * 
	 */
	private String visitorId;

	
	/**
	 * the screen width as an int value.
	 */
	private int width;

	/**
	 * the screen height as an int value.
	 */
	private int height;

	/**
	 * If the page custom variables map is empty use this data.
	 */
	private String pageCustomData;

	/**
	 * If the visitor custom variables map is empty use this data.
	 */
	private String visitorCustomData;

	
	private final int idSite;

	private String pageUrl;

	private String ip;

	private URL urlReferer;

	
	/**
	 * Builds a PiwikTracker object, used to track visits, pages and Goal conversions 
	 * for a specific website, by using the Piwik Tracking API.
	 * 
	 * You probably want to use the other constructor which accepts HttpServletRequest
	 * 
	 * @param apiUrl .. something like http://demo.piwik.org (the piwik.php portion will be appended to the URL for you)
	 * 
	 * 
	 * @throws MalformedURLException 
	 */
	public SimplePiwikTracker(final int idSite, final String apiUrl) throws MalformedURLException {
		this.idSite = idSite;
		this.setApiurl(apiUrl);
		this.setVisitorId(this.md5(UUID.randomUUID().toString()).substring(0, 16));
	}

	/**
	 * Builds a PiwikTracker object, used to track visits, pages and Goal conversions 
	 * for a specific website, by using the Piwik Tracking API.
	 * 
	 * @param idSite Id of the site to be tracked
	 * @param apiUrl points to URL of the tracker server e.g. http://your-website.org/piwik/
	 * @param request HttpServletRequest from the user.
	 * @throws PiwikException .. something like http://demo.piwik.org (the piwik.php portion will be appended to the URL for you)
	 */
	public SimplePiwikTracker(final int idSite, final String apiUrl, final HttpServletRequest request)	throws MalformedURLException, BadCookieException {
		this(idSite, apiUrl);
		this.setHttpServletRequest(request);
	}

	public ResponseData doTrackLink(final String linkurl) throws IOException, HttpResponseException, BadCookieException {
		URL url = this.getLinkTrackURL(linkurl);
		return this.sendRequest(url);		
	}
	
	public ResponseData doTrackDowlonad(final String downloadurl) throws IOException, HttpResponseException, BadCookieException {
		URL url = this.getDownloadTrackURL(downloadurl);
		return this.sendRequest(url);		
	}
	
	public ResponseData doTrackPage(final String pagename) throws IOException, HttpResponseException, BadCookieException {
		URL url = this.getPageTrackURL(pagename);
		return this.sendRequest(url);		
	}
	
	public ResponseData doTrackGoal(final int goalId) throws IOException, HttpResponseException, BadCookieException {
		URL url = this.getGoalTrackURL(goalId);
		return  this.sendRequest(url);		
	}

	public ResponseData doTrackGoal(final int goalId, final float revenue) throws IOException, HttpResponseException, BadCookieException {
		URL url = this.getGoalTrackURL(goalId, revenue);
		return  this.sendRequest(url);		
	}
	
	/** Return the URL of the original request.
	 *  
	 * However, unlike request.getRequestURL, this method is proxy-aware.
	 * 
	 * (Also, for some reason I'm not able to use request.getRequestURL 
	 * It may have something to do with my Java compiler but it doesn't 
	 * recognize the getRequestURL method.) 
	 * 

	 * @param request
	 * @return The URL of the request, something like http://example.com/BestScriptEver.php?y=12
	 */
	public static StringBuffer getRequestURL(HttpServletRequest request) {
		
		
		StringBuffer result = new StringBuffer();
		
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int port = request.getServerPort();
		
		if (request.getHeader("X-Forwarded-Host") != null) {
			
			// We are behind a proxy server, so we can't rely on the information 
			//  from any of the following functions: getScheme(), getServerName(), getServerPort()
			// These will return information about the proxy server not original requestor.
			
			// We can use getHeader("X-Forwarded-Server") in place of getServerName()
			// But I don't know how to get the scheme and port of the original request.
			// To keep it simple we assume the proxy server is using http and port 80.
			// If you have a different situation, change the code :)
			
			// Alternately you may be able to change your proxy server.
			// With Apache the ProxyPreserveHost Directive looks promising
			
			// See also:
			// http://httpd.apache.org/docs/2.2/mod/mod_proxy.html
		
			scheme = "http";
			serverName = request.getHeader("X-Forwarded-Host");
			port = 80;
		}
		
		result.append(scheme); 
		result.append("://");
		result.append(serverName);
		if (scheme.equalsIgnoreCase("http") && port == 80) {
			// No need to append :80 it is implied by http://
		} else if (scheme.equalsIgnoreCase("https") && port == 443) {
			// No need to append :443 it is implied by https://
		} else {
			result.append(":");
			result.append(port); 
		}

		result.append(request.getRequestURI());
		
		if (request.getQueryString() != null) {
			result.append("?");
			result.append(request.getQueryString());
		}
		return result;
	}
	
	/** Grabs information from the HttpServletRequest and sets things like the visitrs IP address, referrer, user agent etc.
	 * 
	 * Also set the visitor ID based on the first-party cookie!!!
	 * 
	 * @param request
	 * @throws MalformedURLException
	 */
	public void setHttpServletRequest(final HttpServletRequest request) throws BadCookieException, MalformedURLException {
		this.setUrlReferer(request.getHeader("Referer"));
		this.setUserAgent(request.getHeader("User-Agent"));		
		this.setPageUrl(getRequestURL(request).toString());
		
		if (request.getHeader("X-Forwarded-For") != null) {
			// IP address of the client when behind a proxy server like the Apache Httpd Server 
			// See http://httpd.apache.org/docs/2.2/mod/mod_proxy.html			
			this.setIp(request.getHeader("X-Forwarded-For"));			
		} else {
			this.setIp(request.getRemoteAddr());
		}
		
		this.setAcceptLanguage(request.getLocale());
		if (request.getCookies() == null) {
			//LOG.debug("HttpServletRequest has no cookies");
		} else {
			for (final Cookie cookie : request.getCookies()) {
				if (isTrackingCookie(cookie)) { 
					// we look for cookies which contain something like "id.3." when idSite is 3
					// This is for compatibility with PiwikTracker.php
					// We could be more strict however. The actual cookie values are like "_pk_id.3.1fff"
					// A more strict test might be: cookie.getName().startsWith("_pk_id." + this.idSite + ".")
					//SimplePiwikTracker.LOG.debug("found tracking cookie");
					this.setRequestCookie(cookie); 					
				}
			}
			//LOG.debug("No valid cookie found, can't set visitor ID");
		}
	}
	
	/** A pattern which matches a valid visitor ID. 
	 * That is, 16 hexadecimal characters 
	 */
	private final static Pattern VISITOR_ID_PATTERN = Pattern.compile("[0-9A-Fa-f]{16}"); 
	
	/** Return true if the string is a valid visitor ID
	 * A valid visitor ID is 16 hexadecimal characters.
	 */
	private boolean isVisitorIdValid(String s) {
		//return s.length() == 16;
		return VISITOR_ID_PATTERN.matcher(s).matches();
	}

	/** Is the supplied tracking cookie the one which is used for this site?
	 */
	private boolean isTrackingCookie(Cookie cookie) {
		return (cookie.getName().contains("id." + this.idSite + ".")); 
			// we look for cookies which contain something like "id.3." when idSite is 3
			// This is for compatibility with PiwikTracker.php
			// We could be more strict however. The actual cookie values are like "_pk_id.3.1fff"
			// A more strict test might be: cookie.getName().startsWith("_pk_id." + this.idSite + ".")
	}
	
	/**
	 * Sets the language set in the browser request. 
	 * This will be used to determine where the request comes from.
	 * 
	 * @param language as a string object in ISO 639 code
	 */
	public final void setAcceptLanguage(final String language) {
		this.language = language;
	}

	/**
	 * Sets the language set in the browser request. 
	 * This will be used to determine where the request comes from.
	 * 
	 * @param locale as a locale object
	 */
	public final void setAcceptLanguage(final Locale locale) {
		String localeLanguage = null;
		if (locale != null) {
			localeLanguage = locale.getLanguage();
		}
		this.setAcceptLanguage(localeLanguage);
	}

	/**
	 * Sets the url of the piwik installation the tracker will track to.
	 * Something like http://example.com/piwik or http://example.com/piwik/piwik.php
	 * 
	 * The given string should be in the format of RFC2396. The string will be
	 * converted to an url with no other url as its context. If this is not 
	 * wanted, create an own url object and use the equivalent function to this.
	 * 
	 * Note that the URL will be modified, typically by appending /piwik.php
	 * 
	 * @param apiurl as a string object
	 * @throws MalformedURLException 
	 */
	private final void setApiurl(final String apiurl) throws MalformedURLException {
		this.setApiurl(new URL(apiurl));
	}

	/**
	 * Sets the url of the piwik installation the tracker will track to.
	 * Something like http://example.com/piwik or http://example.com/piwik/piwik.php
	 * 
	 * Note that the URL will be modified, typically by appending /piwik.php
	 * 
	 * @param apiurl as a URL object
	 * @throws MalformedURLException 
	 */ 
	private final void setApiurl(final URL apiurl) throws MalformedURLException {

		if (apiurl.getPath().endsWith("piwik.php") || apiurl.getPath().endsWith("piwik-proxy.php")) {
			this.apiurl = apiurl;
		} else {
			this.apiurl = new URL(apiurl, apiurl.getPath() + "/piwik.php");
		}
	}

	/**
	 * Sets the custom data.
	 * @param customData the data as a string object
	 */
	public final void setCustomData(final String customData) {
		this.pageCustomData = customData;
	}

	/**
	 * Sets a string for debugging usage. Please only call this function if
	 * debugging is wanted.
	 * @param debugAppendUrl 
	 */
	public final void setDebugAppendUrl(final String debugAppendUrl) {
		this.debugAppendUrl = debugAppendUrl == null ? "" : debugAppendUrl;
	}

	/**
	 * Sets the time the request was send.
	 * 
	 * @param forcedDatetime the time as a date object
	 */
	public final void setForcedDatetime(final Date forcedDatetime) {
		this.forcedDatetime = forcedDatetime;
	}

	/**
	 * Sets the ip from which the request was send.
	 * 
	 * @param ip the ip as a string object
	 */
	public final void setIp(final String ip) {
		this.ip = ip;
	}


	/**
	 * Sets the Page URL.
	 * @param pageUrl  
	 */
	public final void setPageUrl(final String pageUrl) {
		this.pageUrl = pageUrl;
	}

	/**
	 * Sets the screen resolution of the browser which sends the request.
	 * 
	 * @param width the screen width as an int value
	 * @param height the screen height as an int value
	 */
	public final void setResolution(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Sets the piwik cookie of the requester and update the visitor ID
	 * based on the cookies value.
	 * 
	 * Must be a valid tracking cookie as specified by isTrackingCookie. 
	 * If null, not a tracking cookie, or does not contain a visitor ID we will
	 * throw a BadContentException.
	 * 
	 * (Prior to v1.1 this method would set the request cookie to null and return false
	 * when getting an illegal value. It isn't clear why that was done.
	 * That behavior conflicts with the new behavior which also updates the
	 * visitor ID. We want requestCookie and visitorId to stay in sync!)
	 * 
	 * @param requestCookie the piwik cookie as cookie object
	 * @throws BadContentException
	 */
	public final void setRequestCookie(final Cookie requestCookie) throws BadCookieException {

		if (requestCookie == null) 
			throw new BadCookieException("Invalid call to setRequestCookie, the requestCookie argument was null");
		
		if (isTrackingCookie(requestCookie) == false) 
			throw new BadCookieException("Invalid call to setRequestCookie, the requestCookie argument is not a tracking cookie for this site");
			
		String[] cookieParts = requestCookie.getValue().split("\\.");
		
		if (isVisitorIdValid(cookieParts[0]) == false) 
			throw new BadCookieException("Invalid call to setRequestCookie, the requestCookie contains a value in an unexpected format, it did not contain a valie visitorId");
			
		this.visitorId = cookieParts[0];
		this.requestCookie = requestCookie;
	}

	/**
	 * Sets the authentication string from the piwik installation for access 
	 * of piwik data.
	 * 
	 * @param tokenAuth the token as a string object
	 */
	public final void setTokenAuth(final String tokenAuth) {
		this.tokenAuth = tokenAuth;
	}

	/**
	 * Sets the referer url of the request. This will be used to determine where
	 * the request comes from.
	 * 
	 * The given string should be in the format of RFC2396. The string will be
	 * converted to an url with the apiurl as its context. This will makes relative
	 * urls to the apiurl possible. If this is not wanted, create an own url object
	 * and use the equivalent function to this.
	 * 
	 * @param urlReferer the referer url as a string object
	 * @throws MalformedURLException 
	 */
	public final void setUrlReferer(final String urlReferer) throws MalformedURLException {

		if (urlReferer == null) {
			this.urlReferer = null;
		} else {
			this.urlReferer = new URL(apiurl, urlReferer);
		}
	}

	/**
	 * Sets the referer url of the request. This will be used to determine where
	 * the request comes from.
	 * 
	 * @param urlReferer the referer url as a url object
	 */
	public final void setUrlReferer(final URL urlReferer) {
		this.urlReferer = urlReferer;
	}

	/**
	 * Sets the user agent identification of the requester. This will be used to
	 * determine with which kind of client the request was send.
	 * 
	 * @param userAgent the user agent identification as a string object
	 */
	public final void setUserAgent(final String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * Sets the id of the requester. This will be used to determine if the requester
	 * is a returning visitor.
	 * 
	 * This is not normally needed, as we normally pull the visitorId from the requestor's cookie.
	 * This might not work as expected because the visitorId will we out of sync with the 
	 * reqestCookie
	 * 
	 * @param visitorId the id of the visitor as a string object
	 */
	public final void setVisitorId(final String visitorId) {
		if (isVisitorIdValid(visitorId)) {
			this.visitorId = visitorId;
		} else {
			LOG.error("Invalid call to setVisitorId, supplied argument is invalid. It was: " +  visitorId);
		}		
	}

    /**
     * If you have made a call to setVisitorId, this function will return that value.
     * 
     * If the user initiating the request has the Piwik first party cookie, 
     * this function will try and return the ID parsed from this first party cookie.
     * 
     * If you call this function from a server, where the call is triggered by a cron or script
     * not initiated by the actual visitor being tracked, then it will return 
     * the random Visitor ID that was assigned to this visit object.
     * In code this happens when you make a SimplePiwikTracker with no HttpRequest
     * 
     * This can be used if you wish to record more visits, actions or goals for this visitor ID later on.
     * 
     * @return string 16 hex chars visitor ID string
     */
    public final String getVisitorId()  {
    	return this.visitorId;
    }
    	
	/**
	 * Sets page custom variables; ignoring fixed order (differs from PHP version).
	 * still the order shouldn't change anyway.
	 * 
	 * @param name Custom variable name
	 * @param value Custom variable value
	 * @return the count of the custom parameters
	 * @throws ArrayIndexOutOfBoundsException when the maximum size of variables is reached or the name 
	 * 		or the value is longer as the maximum variable length
	 */
	public final int setPageCustomVariable(final String name, final String value) throws ArrayIndexOutOfBoundsException {
		if (!this.pageCustomVar.containsKey(name) && this.pageCustomVar.size() >= SimplePiwikTracker.MAX_CUSTOM_VARIABLE_LENGTH) {
			throw new ArrayIndexOutOfBoundsException("Max size of custom variables are reached. You can only put up to "
					+ SimplePiwikTracker.MAX_CUSTOM_VARIABLE_LENGTH + " custom variables to a request.");
		}

		if (name.length() > MAX_CUSTOM_VARIABLE_LENGTH) {
			throw new ArrayIndexOutOfBoundsException("Parameter \"name\" exceeds maximum length of " + MAX_CUSTOM_VARIABLE_LENGTH
					+ ". Given length is " + name.length());
		}

		if (value.length() > MAX_CUSTOM_VARIABLE_LENGTH) {
			throw new ArrayIndexOutOfBoundsException("Parameter \"value\" exceeds maximum length of " + MAX_CUSTOM_VARIABLE_LENGTH
					+ ". Given length is " + name.length());
		}

		this.pageCustomVar.put(name, value);
		return this.pageCustomVar.size();
	}

	/**
	 * Sets visitor custom variables; ignoring fixed order (differs from PHP version).
	 * still the order shouldn't change anyway.
	 * 
	 * @param name Custom variable name
	 * @param value Custom variable value
	 * @return the count of the custom parameters
	 * @throws ArrayIndexOutOfBoundsException when the maximum size of variables is reached or the name 
	 * 		or the value is longer as the maximum variable length
	 */
	public final int setVisitorCustomVariable(final String name, final String value) throws ArrayIndexOutOfBoundsException {
		if (!this.visitorCustomVar.containsKey(name) && this.visitorCustomVar.size() >= SimplePiwikTracker.MAX_CUSTOM_VARIABLE_LENGTH) {
			throw new ArrayIndexOutOfBoundsException("Max size of custom variables are reached. You can only put up to "
					+ SimplePiwikTracker.MAX_CUSTOM_VARIABLE_LENGTH + " custom variables to a request.");
		}

		if (name.length() > MAX_CUSTOM_VARIABLE_LENGTH) {
			throw new ArrayIndexOutOfBoundsException("Parameter \"name\" exceeds maximum length of " + MAX_CUSTOM_VARIABLE_LENGTH
					+ ". Given length is " + name.length());
		}

		if (value.length() > MAX_CUSTOM_VARIABLE_LENGTH) {
			throw new ArrayIndexOutOfBoundsException("Parameter \"value\" exceeds maximum length of " + MAX_CUSTOM_VARIABLE_LENGTH
					+ ". Given length is " + name.length());
		}

		this.visitorCustomVar.put(name, value);
		return this.visitorCustomVar.size();
	}

	/**
	 * Resets all given custom variables.
	 */
	public final void clearCustomVariables() {
		this.pageCustomVar.clear();
	}

	/**
	 * Adds a browser plugin to the list to detected plugins. With the boolean 
	 * flag is set whether the plugin is enabled or disabled.
	 * 
	 * @param plugin the plugin which was detected
	 * @param enabled <code>true</code> is the plugin is enabled otherwise <code>false</code>
	 */
	public final void setPlugin(final EBrowserPlugins plugin, final boolean enabled) {
		this.plugins.put(plugin, enabled);
	}

	/**
	 * Resets all given browser plugins.
	 */
	public final void clearPluginList() {
		this.plugins.clear();
	}

	/**
	 * Sets local visitor time.
	 * 
	 * @param time the local time as a string object in the format HH:MM:SS
	 * @throws ParseException 
	 */
	public final void setLocalTime(final String time) throws ParseException {
		this.setLocalTime(new SimpleDateFormat("HH:mm:ss").parse(time));
	}

	/**
	 * Sets local visitor time. With null you can reset the time.
	 * 
	 * @param time the local time as a date object
	 */
	public final void setLocalTime(final Date time) {
		this.localTime = time;
	}

	/**
	 * Returns the query part for the url with all parameters from all given 
	 * informations set to this tracker.
	 * This function is called in the defined url for the tracking purpose.
	 * 
	 * @return the query part for the url as string object
	 */
	public final String getGeneralQuery() {
		final URL rootURL = this.apiurl;
		final String rootQuery = rootURL.getQuery();
		final String withIdsite = this.addParameter(rootQuery, "idsite", this.idSite);
		final String withRec = this.addParameter(withIdsite, "rec", 1); // what ever this is
		final String withApiVersion = this.addParameter(withRec, "apiv", SimplePiwikTracker.VERSION);
		final String withURL = this.addParameter(withApiVersion, "url", this.pageUrl);
		final String withURLReferer = this.addParameter(withURL, "urlref", this.urlReferer);
		final String withVisitorId = this.addParameter(withURLReferer, "_id", this.visitorId);
		final String withReferer = this.addParameter(withVisitorId, "ref", this.urlReferer);
		final String withRefererForcedTimestamp = this.addParameter(withReferer, "_refts", this.forcedDatetime);
		final String withIp = this.addParameter(withRefererForcedTimestamp, "cip", this.ip);
		final String withForcedTimestamp = this.addParameter(withIp, "cdt", forcedDatetime == null ? null
				: new SimpleDateFormat("yyyyMMdd HH:mm:ssZ").format(forcedDatetime));
		final String withAuthtoken = this.addParameter(withForcedTimestamp, "token_auth", this.tokenAuth);
		String withPlugins = withAuthtoken;
		for (final Map.Entry<EBrowserPlugins, Boolean> entry : this.plugins.entrySet()) {
			withPlugins = this.addParameter(withPlugins, entry.getKey().toString(), entry.getValue());
		}
		final String withLocalTime;
		if (this.localTime == null) {
			withLocalTime = withPlugins;
		} else {
			final Calendar c = new GregorianCalendar();
			c.setTime(this.localTime);
			final String withHour = this.addParameter(withPlugins, "h", c.get(Calendar.HOUR_OF_DAY));
			final String withMinute = this.addParameter(withHour, "m", c.get(Calendar.MINUTE));
			withLocalTime = this.addParameter(withMinute, "s", c.get(Calendar.SECOND));
		}
		final String withResolution;
		if (this.width > 0 && this.height > 0) {
			withResolution = this.addParameter(withLocalTime, "res", this.width + "x" + this.height);
		} else {
			withResolution = withLocalTime;
		}
		final String withCookieInfo = this.addParameter(withResolution, "cookie", this.requestCookie != null);
		final String withVisitorCustomData = this.addParameter(withCookieInfo, "data", this.visitorCustomData);

		/* ADD VISITOR CUSTOM VARIABLES */

		final String withVisitorCustomVar;
		if (this.visitorCustomVar.isEmpty()) {
			withVisitorCustomVar = withVisitorCustomData;
		} else {
			final Map<String, List<String>> customVariables = new HashMap<String, List<String>>();
			int i = 0;
			for (final Map.Entry<String, String> entry : this.visitorCustomVar.entrySet()) {
				i++;
				final List<String> list = new ArrayList<String>();
				list.add(entry.getKey());
				list.add(entry.getValue());
				customVariables.put(Integer.toString(i), list);
			}

			final JSONArray json = new JSONArray();
			json.put(customVariables);

			// remove unnecessary parent square brackets from JSON-string 
			String jsonString = json.toString().substring(1, json.toString().length() - 1);

			// visitor custom variables: _cvar
			withVisitorCustomVar = this.addParameter(withVisitorCustomData, "_cvar", jsonString);
		}

		/* ADD PAGE CUSTOM VARIABLES */

		final String withPageCustomData = this.addParameter(withVisitorCustomVar, "data", this.pageCustomData);
		final String withPageCustomVar;
		if (this.pageCustomVar.isEmpty()) {
			withPageCustomVar = withPageCustomData;
		} else {
			final Map<String, List<String>> customVariables = new HashMap<String, List<String>>();
			int i = 0;
			for (final Map.Entry<String, String> entry : this.pageCustomVar.entrySet()) {
				i++;
				final List<String> list = new ArrayList<String>();
				list.add(entry.getKey());
				list.add(entry.getValue());
				customVariables.put(Integer.toString(i), list);
			}

			final JSONArray json = new JSONArray();
			json.put(customVariables);

			// remove unnecessary parent square brackets from JSON-string 
			String jsonString = json.toString().substring(1, json.toString().length() - 1);

			// page custom variables: cvar
			withPageCustomVar = this.addParameter(withPageCustomData, "cvar", jsonString);
		}

		final String withRand = this.addParameter(withPageCustomVar, "r", String.valueOf(random.nextDouble())
				.substring(2, 8));
		return (withRand + this.debugAppendUrl);
	}

	/**
	 * 
	 * @param queryString .. the part after ?
	 * @return URL
	 * @throws MalformedURLException 
	 */
	private URL makeURL(final String queryString) throws MalformedURLException {
		return new URL(this.apiurl, apiurl.getPath() + "?" + queryString);
	}

	/**
	 * 
	 * @param rootQuery 
	 * @param name 
	 * @param value 
	 * @return String
	 */
	private String addParameter(final String rootQuery, final String name, final int value) {
		return this.addParameter(rootQuery, name, String.valueOf(value), true);
	}

	/**
	 * 
	 * @param rootQuery 
	 * @param name 
	 * @param value 
	 * @return String
	 */
	private String addParameter(final String rootQuery, final String name, final float value) {
		return this.addParameter(rootQuery, name, String.valueOf(value), true);
	}
	
	/**
	 * 
	 * @param rootQuery 
	 * @param name 
	 * @param value 
	 * @return String
	 */
	private String addParameter(final String rootQuery, final String name, final URL value) {
		return this.addParameter(rootQuery, name, value == null ? null : value.toExternalForm(), true);
	}

	/**
	 * 
	 * @param rootQuery 
	 * @param name 
	 * @param value 
	 * @return String
	 */
	private String addParameter(final String rootQuery, final String name, final Date value) {
		return this.addParameter(rootQuery, name, value == null ? null : String.valueOf(value.getTime()), true);
	}

	/**
	 * 
	 * @param rootQuery 
	 * @param name 
	 * @param selection 
	 * @return String 
	 */
	private String addParameter(final String rootQuery, final String name, final boolean selection) {
		return this.addParameter(rootQuery, name, String.valueOf(selection), true);
	}

	/**
	 * See the equivalent function. Will call this function with ignoreNull set 
	 * to be <code>true</code>.
	 * 
	 * @param rootQuery the root query the new parameter will be added as string object
	 * @param name the name of the parameter as string object
	 * @param value the value ot the parameter as string object
	 * @return the new query as a result of the root query with the new parameter 
	 * and the value
	 */
	private String addParameter(final String rootQuery, final String name, final String value) {
		return this.addParameter(rootQuery, name, value, true);
	}

	/**
	 * Adds a parameter to a given query and returns the full query.
	 * If the given value is <code>null</code> the added query will be the string
	 * representation of <code>null</code> and NOT the empty string.
	 * If the given name is <code>null</code>, the value will be added as a 
	 * single parameter.
	 * Only if both name and value are <code>null</code> the function will
	 * return the root query unmodified.
	 * 
	 * @param rootQuery the root query the new parameter will be added as string object
	 * @param name the name of the parameter as string object
	 * @param value the value ot the parameter as string object
	 * @param ignoreNull <code>true</code> the hole parameter will be ignored if the value is <code>null</code>
	 * @return the new query as a result of the root query with the new parameter 
	 * and the value
	 */
	private String
			addParameter(final String rootQuery, final String name, final String value, final boolean ignoreNull) {
		final String output;
		if ((name == null && value == null && rootQuery != null && !(rootQuery.trim().isEmpty()))
				|| (value == null && ignoreNull)) {
			output = rootQuery;
		} else if (name != null && rootQuery != null && !rootQuery.trim().isEmpty() && value != null) {
			output = rootQuery + "&" + name + "=" + this.urlencode(value);
		} else if (rootQuery != null && !rootQuery.trim().isEmpty() && value != null) {
			output = rootQuery + "&" + this.urlencode(value);
		} else if (name != null && value != null) {
			output = name + "=" + this.urlencode(value);
		} else if (value != null) {
			output = this.urlencode(value);
		} else {
			output = rootQuery;
			LOG.error("value == null!");
		}
		return output;
	}

	/**
	 * 
	 * @param input 
	 * @return String 
	 */
	private String urlencode(final String input) {
		String output = "";
		try {
			output = URLEncoder.encode(input, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			SimplePiwikTracker.LOG.warn("Error while encoding url", e);
			output = input;
		}
		return output;
	}

	/**
	 * Creates an MD5 hash for the given input.
	 * 
	 * @param input the input string
	 * @return the hashed string 
	 */
	private String md5(final String input) {
		String retVal = "";
		try {
			final byte[] b = MessageDigest.getInstance("MD5").digest(input.getBytes());
			final java.math.BigInteger bi = new java.math.BigInteger(1, b);
			retVal = bi.toString(16);
			while (retVal.length() < 32) {
				retVal = "0" + retVal;
			}
		} catch (final NoSuchAlgorithmException e) {
			SimplePiwikTracker.LOG.error("Error while creating a md5 hash", e);
		}
		return retVal;
	}

	/**
	 * @param goal 
	 * @return URL 
	 */
	public final URL getGoalTrackURL(final int goal) {
		URL output = null;
		try {
			final String globalQuery = this.getGeneralQuery();
			final String resultQuery = this.addParameter(globalQuery, "idgoal", goal);
			output = this.makeURL(resultQuery);
		} catch (final MalformedURLException e) {
			SimplePiwikTracker.LOG.error("Error while building tracking url", e);
		}
		return output;
	}

	/**
	 * @param goal 
	 * @param revenue 
	 * @return URL 
	 */
	public final URL getGoalTrackURL(final int goal, final float revenue) {
		URL output = null;
		try {
			final String globalQuery = this.getGeneralQuery();
			final String qoalQuery = this.addParameter(globalQuery, "idgoal", goal);
			final String resultQuery = this.addParameter(qoalQuery, "revenue", revenue);
			output = this.makeURL(resultQuery);
		} catch (final MalformedURLException e) {
			SimplePiwikTracker.LOG.error("Error while building track url", e);
		}
		return output;
	}


	public final URL getDownloadTrackURL(final String downloadurl) {
		URL output = null;
		try {
			final String globalQuery = this.getGeneralQuery();
			final String resultQuery = this.addParameter(globalQuery, "download", downloadurl);
			output = this.makeURL(resultQuery);
		} catch (final MalformedURLException e) {
			SimplePiwikTracker.LOG.error("Error while building track url", e);
		}
		return output;
	}


	public final URL getLinkTrackURL(final String linkurl) {
		URL output = null;
		try {
			final String globalQuery = this.getGeneralQuery();
			final String resultQuery = this.addParameter(globalQuery, "link", linkurl);
			output = this.makeURL(resultQuery);
		} catch (final MalformedURLException e) {
			SimplePiwikTracker.LOG.error("Error while building track url", e);
		}
		return output;
	}


	public final URL getPageTrackURL(final String pagename) {
		URL output = null;
		try {
			final String globalQuery = this.getGeneralQuery();
			final String resultQuery = this.addParameter(globalQuery, "action_name", pagename);
			output = this.makeURL(resultQuery);
		} catch (final MalformedURLException e) {
			SimplePiwikTracker.LOG.error("Error while building track url", e);
		}
		return output;
	}

	/**
	 * Sends the request to the PIWIK-Server.
	 * Returns ResponseData on success or null on failure. 
	 * 
	 * @param destination the built request string.
	 * @return ResponseData 
	 * @throws HttpResponseException, ... if the server returns an unexpcted reponse
	 * 	       IOException ... if there was an error communicating with the server
	 */
	public final ResponseData sendRequest(final URL destination) throws HttpResponseException, IOException, BadCookieException {
		ResponseData responseData = null;


		if (SimplePiwikTracker.LOG.isDebugEnabled()) {
			SimplePiwikTracker.LOG.debug("try to open piwik request url: " + destination);
		}
		HttpURLConnection connection = (HttpURLConnection) destination.openConnection();
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(600);
		connection.setRequestProperty("User-Agent", userAgent);
		connection.setRequestProperty("Accept-Language", language);
		if (requestCookie != null) {
			connection.setRequestProperty("Cookie", requestCookie.getName() + "=" + requestCookie.getValue());
		}

		responseData = new ResponseData(connection);
		List<Cookie> cookies = responseData.getCookies();
		for (Cookie cookie : cookies) {
			if (isTrackingCookie(cookie)) {
				this.setRequestCookie(cookie);
			}
		}
		/* The prior logic here would take an XDEBUG cookie and save it as the request cookie.
		 * But ... by definition ... the cookie with XDEBUG in its name is not the Piwik tracking cookie!
		 * It doesn't make sense.
		 * 
		if (cookies.size() > 0) {
			
			//// in case several cookies returned, we keep only the latest one (ie. XDEBUG puts its cookie first in the list)
			if (cookies.get(cookies.size() - 1).getName().lastIndexOf("XDEBUG") == -1
					&& cookies.get(cookies.size() - 1).getValue().lastIndexOf("XDEBUG") == -1) {
				this.setRequestCookie(cookies.get(cookies.size() - 1); // ?!?
			}
		}
		*/
		if (connection.getResponseCode() != HttpServletResponse.SC_OK) {			
			String error = "error:" + connection.getResponseCode() + " " + connection.getResponseMessage(); 
			SimplePiwikTracker.LOG.error(error);
			HttpResponseException ex = new HttpResponseException(error, connection.getResponseCode());			
			connection.disconnect();
			throw ex;
		}

		connection.disconnect();

		return responseData;
	}

	/**
	 * @return custom data
	 */
	public String getCustomData() {
		return pageCustomData;
	}

	/**
	 * @return site id
	 */
	public int getIdSite() {
		return idSite;
	}

	/**
	 * @return The URL from which the request originated.
	 */
	public String getPageUrl() {
		return pageUrl;
	}

	/**
	 * @return The IP address from which the request originated.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return URL Referer
	 */
	public URL getUrlReferer() {
		return urlReferer;
	}


	public String getVisitorCustomData() {
		return visitorCustomData;
	}

	public void setVisitorCustomData(final String visitorCustomData) {
		this.visitorCustomData = visitorCustomData;
	}

	/** An exception which represents an invalid status code from the server.
	 * A valid HTTP response code is 200, but if we get 404 or a server-error,
	 * this exception will be raised.
	 */
	
	public class HttpResponseException  extends Exception {

		private final int responseCode;
	    public HttpResponseException(final String message, int responseCode) {
	        super(message);
	        this.responseCode = responseCode;
	    }

	    public int getResponseCode() { 
	    	return this.responseCode;
	    }
	}

	/** Raised when cookies are used incorrectly.
	 * This exception probably represents bad programming.
	 *  
	 * It could be a result of receiving intentionally 
	 * invalid cookies from the user.
	 * 
	 * It could be that the Piwik server API has changed, 
	 * and the cookie format is now different.    
	 */
	
	public class BadCookieException  extends Exception {

	    public BadCookieException(final String message) {
	        super(message);
	    }
	}
	
	/** Represents the response we got from the Piwik server.
	 * Used to saver the HTTP headers and thereby extract the cookies. 
	 * 
	 * @author Martin Fochler
	 */
	public class ResponseData {

		/**
		 * Map to store header information.
		 */
		private Map<String, List<String>> headerData;

		/**
		 * Initialize the local header data with the header fields from the connection.
		 * Those information are needed to parse the cookie information.
		 * @param connection used to retrieve the header fields
		 */
		public ResponseData(final HttpURLConnection connection) {
			headerData = connection.getHeaderFields();
		}

		public List<Cookie> getCookies() {
			List<Cookie> cookies = new ArrayList<Cookie>();

			for (String key : headerData.keySet()) {
				List<String> headerParts = headerData.get(key);

				StringBuilder cookieInfo = new StringBuilder();
				for (String part : headerParts) {
					cookieInfo.append(part);
				}

				if (key == null && cookieInfo.toString().equals("")) {
					//LOGGER.debug("No more headers, not proceeding");
					return null;
				}

				if (key == null) {
					//LOGGER.debug("The header value contains the server's HTTP version, not proceeding");
				} else if (key.equals("Set-Cookie")) {
					List<HttpCookie> httpCookies = HttpCookie.parse(cookieInfo.toString());
					for (HttpCookie h : httpCookies) {
						Cookie c = new Cookie(h.getName(), h.getValue());
						c.setComment(h.getComment());
						if (h.getDomain() != null) {
							c.setDomain(h.getDomain());
						}
						c.setMaxAge(Long.valueOf(h.getMaxAge()).intValue());
						c.setPath(h.getPath());
						c.setSecure(h.getSecure());
						c.setVersion(h.getVersion());
						cookies.add(c);
					}
				} else {
					//LOGGER.debug("The provided key (" + key + ") with value (" + cookieInfo
					//		+ ") were not processed because the key is unknown");
				}
			}
			return cookies;
		}

	}
	
	public enum EBrowserPlugins {

		/**
		 *  Browserplugins.
		 */
		FLASH("fla"), JAVA("java"), DIRECTOR("dir"), QUICKTIME("qt"),
		REALPLAYER("realp"), PDF("pdf"), WINDOWSMEDIA("wma"), GEARS("gears"),
		SILVERLIGHT("ag");

		/**
		 * The short URL.
		 */
		private String urlshort;

		/**
		 * Constructor that sets the short URL.
		 * @param urlshort 
		 */
		EBrowserPlugins(final String urlshort) {
			this.urlshort = urlshort;
		}

		@Override
		public String toString() {
			return this.urlshort + "=true";
		}
	}
}
