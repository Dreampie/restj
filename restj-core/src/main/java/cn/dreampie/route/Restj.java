/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.dreampie.route;

import cn.dreampie.config.*;
import cn.dreampie.handler.Handler;
import cn.dreampie.handler.HandlerFactory;
import cn.dreampie.plugin.IPlugin;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;

/**
 * JFinal
 */
public final class Restj {
	
	private Constants constants;
	private ActionMapping actionMapping;
	private Handler handler;
	private ServletContext servletContext;
	private String contextPath = "";
	
	Handler getHandler() {
		return handler;
	}
	
	private static final Restj me = new Restj();
	
	private Restj() {
	}
	
	public static Restj me() {
		return me;
	}
	
	boolean init(Config config, ServletContext servletContext) {
		this.servletContext = servletContext;
		this.contextPath = servletContext.getContextPath();
		
		ConfigLoader.config(config);	// start plugin and init logger factory in this method
		constants = ConfigLoader.getConstants();
		
		initActionMapping();
		initHandler();
		
		return true;
	}
	

	
	private void initHandler() {
		Handler actionHandler = new ActionHandler(actionMapping, constants);
		handler = HandlerFactory.getHandler(ConfigLoader.getHandlers().getHandlerList(), actionHandler);
	}
	

	
	private void initActionMapping() {
		actionMapping = new ActionMapping(ConfigLoader.getRoutes(), ConfigLoader.getInterceptors());
		actionMapping.buildActionMapping();
	}
	
	void stopPlugins() {
		List<IPlugin> plugins = ConfigLoader.getPlugins().getPluginList();
		if (plugins != null) {
			for (int i=plugins.size()-1; i >= 0; i--) {		// stop plugins
				boolean success = false;
				try {
					success = plugins.get(i).stop();
				} 
				catch (Exception e) {
					success = false;
					e.printStackTrace();
				}
				if (!success) {
					System.err.println("Plugin stop error: " + plugins.get(i).getClass().getName());
				}
			}
		}
	}
	
	public ServletContext getServletContext() {
		return this.servletContext;
	}
	
	public List<String> getAllActionKeys() {
		return actionMapping.getAllActionKeys();
	}
	
	public Constants getConstants() {
		return ConfigLoader.getConstants();
	}
	
	public Action getAction(String url, String[] urlPara) {
		return actionMapping.getAction(url, urlPara);
	}
	
	public String getContextPath() {
		return contextPath;
	}
}










