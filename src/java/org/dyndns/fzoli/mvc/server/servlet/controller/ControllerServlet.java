package org.dyndns.fzoli.mvc.server.servlet.controller;

import org.dyndns.fzoli.mvc.common.message.ModelMessage;
import org.dyndns.fzoli.mvc.common.message.ReturnMessage;
import org.dyndns.fzoli.mvc.server.servlet.ModelServlet;

/**
 *
 * @author zoli
 */
public interface ControllerServlet<EventType, PropsType> extends ModelServlet<EventType, PropsType> {
    
    String PARAM_MEM_SIZE = "mem_size";
    String PARAM_MAX_SIZE = "max_size";
    String PARAM_TMP_DIR = "tmp_dir";
    
    String modelMessageToString(ModelMessage<PropsType> msg);
    
    String returnMessageToString(ReturnMessage msg);
    
}