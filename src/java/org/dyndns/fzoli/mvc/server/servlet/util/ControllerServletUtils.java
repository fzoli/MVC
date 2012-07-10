package org.dyndns.fzoli.mvc.server.servlet.util;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dyndns.fzoli.mvc.common.message.ControllerCloseMessage;
import org.dyndns.fzoli.mvc.common.message.ModelMessage;
import org.dyndns.fzoli.mvc.common.message.ReturnMessage;
import org.dyndns.fzoli.mvc.common.request.map.ControllerServletRequestMap;
import org.dyndns.fzoli.mvc.server.model.Model;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBean;
import org.dyndns.fzoli.mvc.server.servlet.controller.ControllerServlet;

class ModelInfo<EventType, PropsType> {
    
    private List<String> notFindedModels;
    private List<Model<EventType, PropsType>> models;

    public ModelInfo(List<String> notFinded, List<Model<EventType, PropsType>> models) {
        this.notFindedModels = notFinded;
        this.models = models;
    }

    public List<Model<EventType, PropsType>> getModels() {
        return models;
    }

    public List<String> getNotFindedModels() {
        return notFindedModels;
    }
    
}

/**
 *
 * @author zoli
 */
class ControllerServletUtils<EventType, PropsType> extends ServletUtils<EventType, PropsType> {

    private final static BufferedImage EMPTY_IMG = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    
    public ControllerServletUtils(ControllerServlet<EventType, PropsType> servlet) {
        super(servlet);
    }

    @Override
    protected ControllerServlet<EventType, PropsType> getServlet() {
        return (ControllerServlet<EventType, PropsType>) super.getServlet();
    }
    
    private ModelInfo<EventType, PropsType> getModelInfo(ControllerServletRequestMap requestMap, HttpServletRequest request) {
        List<String> modelStrings = requestMap.getModels();
        List<String> notFindedModels = new ArrayList<String>();
        List<Model<EventType, PropsType>> models = new ArrayList<Model<EventType, PropsType>>();
        for (String modelString : modelStrings) {
            Model<EventType, PropsType> model = getServlet().getModelBean(request).getModel(modelString);
            if (model == null) {
                notFindedModels.add(modelString);
            }
            else {
                models.add(model);
            }
        }
        return new ModelInfo<EventType, PropsType>(notFindedModels, models);
    }
    
    private void manageModel(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response, boolean ask) {
        ModelInfo<EventType, PropsType> modelInfo = getModelInfo(requestMap, request);
        if (checkModelInfo(response, modelInfo)) {
            Model<EventType, PropsType> model = modelInfo.getModels().get(0);
            int i = ask ? model.safeAskModel(request, requestMap) : model.safeSetProperty(request, requestMap);
            printReturnMessage(response, Integer.toString(i));
        }
    }
    
    private void printReturnMessage(HttpServletResponse response, String value) {
        String msg = getServlet().returnMessageToString(new ReturnMessage(value));
        response.setContentLength(msg.getBytes().length);
        printString(response, msg);
    }
    
    private void askModel(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response) {
        manageModel(requestMap, request, response, true);
    }
    
    private void setModel(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response) {
        manageModel(requestMap, request, response, false);
    }
    
    private void printModels(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response) {
        ModelBean<EventType, PropsType> mb = getServlet().getModelBean(request);
        ModelInfo<EventType, PropsType> modelInfo = getModelInfo(requestMap, request);
        if (checkModelInfo(response, modelInfo)) {
            List<Model<EventType, PropsType>> models = modelInfo.getModels();
            Map<String, PropsType> msgmap = new HashMap<String, PropsType>();
            for (Model<EventType, PropsType> m : models) {
                PropsType props = m.safeGetProperties(request, requestMap);
                msgmap.put(mb.getModelName(m), props);
            }
            String msg = getServlet().modelMessageToString(new ModelMessage<PropsType>(msgmap));
            response.setContentLength(msg.getBytes().length);
            printString(response, msg);
        }
    }
    
    private boolean checkModelInfo(HttpServletResponse response, ModelInfo<EventType, PropsType> mi) {
        boolean ok = mi.getNotFindedModels().isEmpty() && !mi.getModels().isEmpty();
        if (!ok) printCloseMessage(response, mi);
        return ok;
    }
    
    private void printCloseMessage(HttpServletResponse response, ModelInfo<EventType, PropsType> mi) {
        List<String> nfl = mi.getNotFindedModels();
        if (nfl.isEmpty()) {
            if (mi.getModels().isEmpty()) {
                printCloseMessage(response, ControllerCloseMessage.REASON_NULL_MODEL_NAME);
            }
        }
        else {
            printCloseMessage(response, ControllerCloseMessage.REASON_WRONG_MODEL_NAME + " - " + nfl);
        }
    }
    
    private void registerListeners(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response) {
        manageListeners(requestMap, true, request, response);
    }
    
    private void unregisterListeners(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response) {
        manageListeners(requestMap, false, request, response);
    }
    
    private void manageListeners(ControllerServletRequestMap requestMap, boolean enable, HttpServletRequest request, HttpServletResponse response) {
        String id = requestMap.getListenerId();
        if (isListenerIdExists(id)) {
            ModelInfo<EventType, PropsType> modelInfo = getModelInfo(requestMap, request);
            if (checkModelInfo(response, modelInfo)) {
                List<Model<EventType, PropsType>> models = modelInfo.getModels();
                for (Model<EventType, PropsType> m : models) {
                    if (enable) m.addListener(id);
                    else m.removeListener(id);
                }
                printReturnMessage(response, Boolean.toString(true));
            }
        }
        else {
            printCloseMessage(response, ControllerCloseMessage.REASON_INVALID_LISTENER_ID);
        }
    }
    
    private void printCloseMessage(HttpServletResponse response, String reason) {
        String msg = getServlet().closeMessageToString(new ControllerCloseMessage(reason));
        response.setContentLength(msg.getBytes().length);
        printString(response, msg);
    }

    @Override
    public ControllerServletRequestMap createRequestMap(HttpServletRequest request) {
        return new ControllerServletRequestMap(request.getParameterMap());
    }
    
    private void printModelImage(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelInfo<EventType, PropsType> modelInfo = getModelInfo(requestMap, request);
        if (checkModelInfo(response, modelInfo)) {
            writeImage(response, modelInfo.getModels().get(0).safeGetImage(request, requestMap));
        }
    }
    
    private void setModelImage(ControllerServletRequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelInfo<EventType, PropsType> modelInfo = getModelInfo(requestMap, request);
        if (checkModelInfo(response, modelInfo)) {
            RenderedImage img = readImage(request);
            if (img != null) printReturnMessage(response, String.valueOf(modelInfo.getModels().get(0).safeSetImage(img, request, requestMap)));
            else printCloseMessage(response, ControllerCloseMessage.REASON_NO_IMAGE);
        }
    }
    
    private static void writeImage(HttpServletResponse response, RenderedImage bi) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bi == null ? EMPTY_IMG : bi, "png", bos);
        response.setContentType("image/png");
        response.setContentLength(bos.size());
        response.setHeader("Cache-Control", "private,no-cache,no-store");
        OutputStream os = response.getOutputStream();
        os.write(bos.toByteArray());
        os.close();
    }
    
    private RenderedImage readImage(HttpServletRequest request) throws Exception {
        if (ServletFileUpload.isMultipartContent(request)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(getConfigNumber(ControllerServlet.PARAM_MEM_SIZE, 0, 52428800, 1048576));
            String tmp = getServlet().getInitParameter(ControllerServlet.PARAM_TMP_DIR);
            if (tmp != null) factory.setRepository(new File(tmp));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(getConfigNumber(ControllerServlet.PARAM_MAX_SIZE, 0, 52428800, 5242880));
            List<FileItem> l = upload.parseRequest(request);
            for (FileItem i : l) {
                if (i.getFieldName().equals(ControllerServletRequestMap.KEY_IMG)) {
                    return ImageIO.read(i.getInputStream());
                }
            }
        }
        return null;
    }
    
    @Override
    public void printResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ControllerServletRequestMap requestMap = createRequestMap(request);
        String action = requestMap.getAction();
        try {
            if (action == null) printCloseMessage(response, ControllerCloseMessage.REASON_INVALID_PARAMETERS);
            else if (action.equals(ControllerServletRequestMap.ACTION_GET_MODEL)) printModels(requestMap, request, response);
                 else if (action.equals(ControllerServletRequestMap.ACTION_SET_MODEL)) setModel(requestMap, request, response);
                      else if (action.equals(ControllerServletRequestMap.ACTION_ASK_MODEL)) askModel(requestMap, request, response);
                           else if (action.equals(ControllerServletRequestMap.ACTION_REG_LISTENER)) registerListeners(requestMap, request, response);
                                else if (action.equals(ControllerServletRequestMap.ACTION_UNREG_LISTENER)) unregisterListeners(requestMap, request, response);
                                     else if (action.equals(ControllerServletRequestMap.ACTION_GET_IMAGE)) printModelImage(requestMap, request, response);
                                          else if (action.equals(ControllerServletRequestMap.ACTION_SET_IMAGE)) setModelImage(requestMap, request, response);
                                               else if (action.equals(ControllerServletRequestMap.ACTION_TEST)) printCloseMessage(response, ControllerCloseMessage.REASON_TEST);
                                                    else printCloseMessage(response, ControllerCloseMessage.REASON_INVALID_PARAMETERS);
        }
        catch (Exception ex) {
            printCloseMessage(response, ControllerCloseMessage.REASON_EXCEPTION);
            ex.printStackTrace();
        }
    }
    
}