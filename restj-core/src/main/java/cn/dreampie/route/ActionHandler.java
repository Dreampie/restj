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


import cn.dreampie.config.Constants;
import cn.dreampie.exception.WebException;
import cn.dreampie.handler.Handler;
import cn.dreampie.log.Logger;
import cn.dreampie.log.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ActionHandler
 */
final class ActionHandler extends Handler {
	
	private final boolean devMode;
	private final ActionMapping actionMapping;
	private static final Logger log = LoggerFactory.getLogger(ActionHandler.class);
	
	public ActionHandler(ActionMapping actionMapping, Constants constants) {
		this.actionMapping = actionMapping;
		this.devMode = constants.isDevMode();
	}
	
	/**
	 * handle
	 * 1: Action action = actionMapping.getAction(target)
	 * 2: new ActionInvocation(...).invoke()
	 * 3: render(...)
	 */
	public final void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (target.indexOf('.') != -1) {
			return ;
		}
		
		isHandled[0] = true;
		String[] urlPara = {null};
		Action action = actionMapping.getAction(target, urlPara);
		
		if (action == null) {
				String qs = request.getQueryString();
			throw new WebException("404 Action Not Found: " + (qs == null ? target : target + "?" + qs));
		}


		Controller controller = null;
		try {
			controller = action.getControllerClass().newInstance();
		} catch (Exception e) {
			throw new WebException("");
		}
		controller.init(request, response, urlPara[0]);

			if (devMode) {
				boolean isMultipartRequest = ActionReporter.reportCommonRequest(controller, action);
				new ActionInvocation(action, controller).invoke();
				if (isMultipartRequest) ActionReporter.reportMultipartRequest(controller, action);
			} else {
				new ActionInvocation(action, controller).invoke();
			}

	}
}





