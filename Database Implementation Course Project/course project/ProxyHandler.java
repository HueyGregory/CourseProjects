package edu.yu.oats.oatsdb.dbms.v1a;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProxyHandler implements InvocationHandler {

	private Logger logger = LogManager.getLogger(getClass());
	private Object target;
	
	public ProxyHandler(Object object) {
		this.target = object;
		logger.debug("Created a new ProxyHandler(object)");
	}
	
	public ProxyHandler() {
		logger.debug("Created a new ProxyHandler()");
	}

	@Override
	public Object invoke(Object arg0, Method method, Object[] arg2) throws Throwable {
		logger.debug("Entered invoke() with Method: " + method.getName());
		Object returningObject;
		try {
			returningObject = method.invoke(this.target, arg2);
		} catch (InvocationTargetException e) {
            throw e.getCause();		// From https://amitstechblog.wordpress.com/2011/07/24/java-proxies-and-undeclaredthrowableexception/
		} 
		logger.debug("Exiting invoke() of Method: " + method.getName() + "() and returning object: " + returningObject);
		return returningObject;
	}
	
}
