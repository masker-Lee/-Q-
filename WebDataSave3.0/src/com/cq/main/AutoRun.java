package com.cq.main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AutoRun implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		WebDataSave.main(null);
	}

}
