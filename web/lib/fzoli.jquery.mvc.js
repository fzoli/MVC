(function($){

    var /*Integer*/ controllerErrorCounter = 0;

    var /*Map*/ models = {};

    var /*Map*/ defaults = {
        /* Controller variables */
        askModelAction: 'ask_model',
        getModelAction: 'get_model',
        setModelAction: 'set_model',
        /* Controller and ModelChangeListener variables */
        //amp: '&amp;', /* not working */
        dataType: 'json',
        amp: '&',
        method : 'POST',
        controllerUrl: 'Controller',
        listenerUrl: 'ChangeListener',
        attrValue : 'value',
        nodeReturn : 'return',
        modelParam: 'model',
        actionParam: 'action',
        nodeModel : 'model',
        attrName : 'name',
        alertOn : false,
        maxControllerErrors : 10,
        serverError: function(xhr, text) {
            reloadPage();
        },
        /* ModelChangeListener variables */
        serverRestart : function() {
            reloadPage();
        },
        idParam: 'listener_id',
        regAction: 'reg_listener',
        unregAction: 'unreg_listener',
        nodeClosed : 'closed',
        attrNewId : 'new_id',
        attrListenerId : 'listener_id',
        attrInitTime : 'init_time',
        attrReason : 'reason',
        valNoListeners : 'no_listeners',
        autoStart : true,
        reloadOnListenError : false,
        maxErrors : 100,
        time: 50
    };

    function getMapSize(map) {
        if (map == null) return 0;
        var count = 0;
        for (var i in map) {
            count++;
        }
        return count;
    }

    function getReturn(response, options) {
        if (options.dataType == 'xml') return $(response).find(options.nodeReturn).attr(options.attrValue);
        return response.value;
    }

    function reloadPage() {
        location.reload();
    }

    function arrayToMap(array) {
        if (array == null) return {};
        var obj = {};
        for (var i in array) {
            var val = array[i];
            obj[val] = i;
        }
        return obj;
    }

    $.Controller = function(options) {
        
        var /*Map*/ options =  $.extend(defaults, options);
        
        function getModels(modelMap, func, onlyFirst) {
            if (func == null) return;
            if (modelMap == null) modelMap = {};
            if (getMapSize(modelMap) > 0) {
                $.ajax({
                    type: options.method,
                    dataType: options.dataType,
                    url: options.controllerUrl,
                    data: createGetModelsRequest(modelMap),
                    async: true,
                    success: function(response) {
                        controllerErrorCounter = 0;
                        handleResponse(response, func, onlyFirst);
                    },
                    error: function(xhr, text) {
                        controllerErrorCounter++;
                        if (controllerErrorCounter < options.maxControllerErrors) {
                            getModels(modelMap, func, onlyFirst);
                        }
                        else {
                            if (options.alertOn) alert('Too lot getModels error.');
                            func({});
                            options.serverError(xhr, text);
                        }
                    }
                });
            }
            else func({});
        }
        
        function handleResponse(response, func, onlyFirst) {
            var models = {};
            if(options.dataType == 'xml') {
                $(response).find(options.nodeModel).each(function() {
                    var name = $(this).attr(options.attrName);
                    models[name] = $(this);
                });
            }
            else {
                models = response.models;
            }
            if (onlyFirst) {
                for (var m in models) {
                    func(models[m]);
                    break;
                }
            }
            else {
                func(models);
            }
        }
        
        function handleSetAskModel(name, argMap, func, isAsk) {
            if (func == null || name == null) return;
            if (argMap == null) argMap = {};
            $.ajax({
                type: options.method,
                dataType: options.dataType,
                url: options.controllerUrl,
                data: createSetAskRequest(name, argMap, isAsk),
                async: true,
                success: function(response) {
                    controllerErrorCounter = 0;
                    var ret = Number(getReturn(response, options));
                    func(ret);
                },
                error: function(xhr, text) {
                    controllerErrorCounter++;
                    if (controllerErrorCounter < options.maxControllerErrors) {
                        handleSetAskModel(name, argMap, func, isAsk);
                    }
                    else {
                        if (options.alertOn) alert((isAsk ? 'ask' :'set') + 'Model error!');
                        func(null);
                        options.serverError(xhr, text);
                    }
                }
            });
        }
        
        function createSetAskRequest(name, argMap, isAsk) {
            var amp = options.amp;
            var action = isAsk ? options.askModelAction : options.setModelAction;
            action = options.actionParam + '=' + action;
            var model = options.modelParam + '=' + name;
            return action + amp + model + amp + mapToRequest(argMap);
        }
        
        function mapToRequest(argMap) {
            var s = '';
            for (var name in argMap) {
                s += name + '=' + argMap[name] + options.amp;
            }
            s = s.substr(0, s.length - options.amp.length);
            return s;
        }
        
        function createGetModelsRequest(modelMap) {
            var amp = options.amp;
            var mods = options.actionParam + '=' + options.getModelAction + amp;
            for (var model in modelMap) {
                mods += options.modelParam + '=' + model + amp;
            }
            mods = mods.substr(0, mods.length - amp.length);
            return mods;
        }
        
        this.getModel = function(name, func) {
            getModels(arrayToMap([name]), func, true);
        };
        
        this.getModels = function(nameArray, func) {
            getModels(arrayToMap(nameArray), func, false);
        };
        
        this.askModel = function(name, argMap, func) {
            handleSetAskModel(name, argMap, func, true);
        };
        
        this.getRegistratedModels = function(func) {
            getModels(models, func, false);
        };
        
        this.setModel = function(name, argMap, func) {
            handleSetAskModel(name, argMap, func, false);
        };
        
        return this;
        
    };

    $.ModelChangeListener = function(options) {

        var /*Map*/ options =  $.extend(defaults, options);

        var /*Boolean*/ listening = false, running = false, reinit = false, regging = false;
        var /*String*/ listenerId, initTime;
        var errorCounter = 0;
        
        var /*Timer*/ timer = $.timer(function(){
            if (listening) return;
            listening = true;
            $.ajax({
                type: options.method,
                url: options.listenerUrl,
                data: createListenerPostData(),
                async: true,
                success: function(response) {
                    errorCounter = 0;
                    var closed, textNewId;
                    if(options.dataType == 'xml') closed = $(response).find(options.nodeClosed);
                    else closed = response.messages;
                    if (closed != null) {
                        if(options.dataType == 'xml') textNewId = closed.attr(options.attrNewId);
                        else textNewId = closed[options.attrNewId];
                    }
                    if (textNewId != null) {
                        var time;
                        if (options.dataType == 'xml') time = closed.attr(options.attrInitTime);
                        else time = closed[options.attrInitTime];
                        if (initTime != null && initTime != time) options.serverRestart();
                        var isNewId = (textNewId == 'true');
                        if (isNewId) {
                            if (options.dataType == 'xml') listenerId = closed.attr(options.attrListenerId);
                            else listenerId = closed[options.attrListenerId];
                            initTime = time;
                            regAllModel();
                        }
                    }
                    var reason;
                    if (closed != null) {
                        if (options.dataType == 'xml') reason = closed.attr(options.attrReason);
                        else reason = closed[options.attrReason];
                    }
                    if (reason != null) {
                        if (reason == options.valNoListeners && !reinit) {
                            listenerId = null;
                            stopListen(true, false);
                            running = false;
                        }
                    }
                    procEvent(response);
                    if (!reinit) listening = false;
                },
                error: function(xhr, text) { //xhr.status is 0 if ESC pressed (interrupted ajax call) but if server is down xhr.status is 0 too :-(
                    errorCounter++;
                    if (errorCounter < options.maxErrors) {
                        listening = false;
                    }
                    else {
                        timer.stop();
                        running = false;
                        if (options.alertOn) alert('Too lot Change Listener error!');
                        options.serverError(xhr, text);
                        if (options.reloadOnListenError) reloadPage();
                    }
                }
            });
        }, options.time, false);

        function regAllModel(force) {
            regModels(models, force);
        }
        
        function unregAllModel(async, force) {
            unregModels(models, async, force);
        }

        function regModel(name, force) {
            regModels(arrayToMap([name]), force);
        }
        
        function unregModel(name, force) {
            unregModels(arrayToMap([name]), true, force);
        }

        function regModels(modelMap, force) {
            manageModels(true, modelMap, true, force);
        }

        function unregModels(modelMap, async, force) {
            manageModels(false, modelMap, async, force);
        }

        function manageModels(reg, modelMap, async, force) {
            if (force == null) force = false;
            if (regging && !force) return;
            regging = true;
            if (getMapSize(modelMap) <= 0) {
                return;
            }
            $.ajax({
                type: options.method,
                url: options.controllerUrl,
                data: createControllerPostDataFromMap(reg, modelMap),
                async: async,
                success: function(response) {
                    if (reinit) {
                        reinit = false;
                        listening = false;
                    }
                    controllerErrorCounter = 0;
                    if (options.alertOn) {
                        var ret = getReturn(response, options);
                        if (ret != 'ALL_OK') alert('There is wrong model name. Check it!');
                    }
                    if (!reg && running && models == modelMap) { //if all model removed
                        running = false;
                    }
                    regging = false;
                },
                error: function(xhr, text) {
                    controllerErrorCounter++;
                    if (controllerErrorCounter < options.maxControllerErrors) {
                        manageModels(reg,modelMap, async);
                    }
                    else {
                        if (options.alertOn) alert('Too lot controller reg listener error.');
                        options.serverError(xhr, text);
                    }
                    regging = false;
                }
            });
        }

        function createControllerPostDataFromMap(reg, modelMap) {
            var id = createListenerPostData();
            var amp = options.amp;
            if (id.length > 0) id += amp;
            var action = reg ? options.regAction : options.unregAction;
            action = options.actionParam + '=' + action;
            var mods = '';
            for(var m in modelMap) {
                mods += options.modelParam + '=' + m + amp;
            }
            mods = mods.substr(0, mods.length - amp.length);
            if (mods.length > 0) mods = amp + mods;
            return id + action + mods;
        }

        function createListenerPostData() {
            return listenerId == null ? '' : options.idParam + '=' + listenerId;
        }

        function procEvent(response) {
            if (options.dataType == 'xml') {
                $(response).find(options.nodeModel).each(function(){
                    var name = $(this).attr(options.attrName);
                    if (models[name] != null) models[name]($(this)); //call event handler function
                });
            }
            else {
                if (response.type != null && response.type == 'events')
                for (var name in response.events) {
                    if (models[name] != null) models[name](response.events);
                }
            }
        }

        function needStart() {
            return !running && options.autoStart;
        }

        function needUpdate() {
            return running && listenerId != null;
        }

        this.getListenerId = function() {
            return listenerId;
        };

        this.isListening = function() {
            return running;
        };

        this.addModels = function(modelMap) {
            if (modelMap == null) return;
            $.extend(models, modelMap);
            if (needStart() && !reinit) {
                reinit = true;
                startListen();
            }
            else if (needUpdate()) {
                regModels(modelMap);
            }
        };

        this.addModel = function(name, func) {
            if (name == null) return;
            models[name] = func;
            if (needStart() && !reinit) {
                reinit = true;
                startListen();
            }
            else if (needUpdate()) {
                regModel(name, true);
            }
        };

        this.removeModels = function(modelArray) {
            if (modelArray == null) return;
            for (var i in modelArray) {
                var name = modelArray[i];
                delete models[name];
            }
            if (needUpdate()) {
                unregModels(arrayToMap(modelArray), true, true);
            }
        };

        this.removeModel = function(name) {
            if (name == null) return;
            delete models[name];
            if (needUpdate()) {
                unregModel(name, true);
            }
        };

        function startListen() {
            running = true;
            timer.reset();
        }

        this.startListen = function() {
            startListen();
        };

        function stopListen(async, unreg) {
            if (!running) return;
            timer.stop();
            if (listenerId != null) unregAllModel(async);
            if (unreg) models = {};
        }

        this.stopListen = function(async, unreg){
            if (async == null) {
                async = true;
            }
            if (unreg == null) {
                unreg = false;
            }
            stopListen(async, unreg);
        };
        
        function flush(async) {
            stopListen(async, true);
        }
        
        this.flush = function(async) {
            if(async == null) async = true;
            flush();
        };

        this.setServerRestartFunction = function(func) {
            if (func == null) return;
            options.serverRestart = func;
        };

        $(window).unload(function() {
            flush(false);
        });

        return this;

    };

})(jQuery);