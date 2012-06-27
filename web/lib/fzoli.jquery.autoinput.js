(function($){
    var settedValues = {};
    var originalVal = $.fn.val;
    
    $.fn.val = function(a) {
        if (a == null) {
            return originalVal.call(this);
        }
        else {
            var id = this.attr('id');
            var isAutoinput = id.indexOf('autoinput') == 0;
            if (isAutoinput) {
                settedValues[id] = a;
            }
            return originalVal.call(this, a);
        }
    };

    var counter = 0;
    var values = {};
    var inputs = {};
    var options = {};
    var timerValues = {};
    var timerOldValues = {};
    var lastEventType = {};

    function init(id) {
        var obj = inputs[id];
        var opt = options[id];
        if (opt.handler == null) return;
        obj.html(
            '<dl id="' + id + '" class="' + opt.rootclass + '">' +
                '<dt class="' + opt.titleclass + '">' + opt.title + '</dt>' +
                '<dd class="' + opt.inputclass + '"><input id="autoinput' + (counter - 1) + '" type="' + opt.type + '" /></dd>' +
                '<dd class="' + opt.msgclass + '">' + opt.message + '</dd>' +
                '<dd class="' + opt.errorclass + '"></dd>' +
            '</dl>');
        initInput(obj);
        initFormat(obj);
        initTitleTag(obj);
        initMessageTag(obj);
        hideErrorMessage(obj);
        initEventHandler(obj);
    }

    function getId(obj) {
        return $('dl', obj).attr('id');
    }

    function getOptions(obj) {
        return options[getId(obj)];
    }

    function getValue(obj) {
        return values[getId(obj)];
    }

    function setValue(obj, val) {
        values[getId(obj)] = val;
    }

    function getLastEventType(obj) {
        return lastEventType[getId(obj)];
    }

    function setLastEventType(obj, type) {
        lastEventType[getId(obj)] = type;
    }

    function callEventHandler(obj, timer, type) {
        timer.stop();
        var noTimer = type != 'timer';
        var val = getInput(obj).val();
        var id = getInput(obj).attr('id');
        var oldValue = getTimerOldValue(obj);
        var value = getTimerValue(obj);
        var last = getValue(obj);
        var lastSetVal = settedValues[id];
        var isChanged = true;
        if (lastSetVal != null) { //felüldefiniált val meg lett már hívva
            isChanged = lastSetVal != val;
            //if (lastSetVal == val) return; //nem történt módosulás
        }
        if (last == null) { //nem volt még küldés
            if(value == null) { //nem volt módosulás
                return;
            }
            else {
                if (oldValue == value) { //ha lenyomáskori és felengedéskori érték egyezik: nem volt módosulás vagy change/enter esemény történt
                    if (!noTimer) return; //ha nem change/enter esemény történt tuti nem volt módosulás
                }
            }
        }
        if (last == val) { //utolsó kiküldött érték ugyan az mint a mostani érték: nem volt módosulás
            return;
        }
        setLastEventType(obj, type);
        setValue(obj, val);
        getOptions(obj).handler(isChanged, type);
    }

    function setTimerOldValue(obj, val) {
        timerOldValues[getId(obj)] = val;
    }
    
    function setTimerValue(obj, val) {
        timerValues[getId(obj)] = val;
    }
    
    function getTimerOldValue(obj) {
        return timerOldValues[getId(obj)];
    }
    
    function getTimerValue(obj) {
        return timerValues[getId(obj)];
    }

    function initEventHandler(obj) {
        var input = getInput(obj);
        var opt = getOptions(obj);
        var timer = $.timer(function(){
            callEventHandler(obj, timer,  'timer');
        }, opt.time, false);
        //ALERT! jquery function name bug!
        input.keyup(function() { //keydown
            setTimerValue(obj,getInput(obj).val());
        });
        input.keydown(function(ev) { //keyup
            setTimerOldValue(obj,getInput(obj).val());
            if (ev.which == 13) callEventHandler(obj, timer, 'enter');
            else if (opt.time != 0) timer.reset();
        });
        input.change(function(){
            callEventHandler(obj, timer, 'change');
        });
    }

    function initInput(obj) {
        setIcon(obj, null);
        obj = getInput(obj);
        obj.css('background-position', 'right center');
        obj.css('background-repeat', 'no-repeat');
    }

    function initFormat(obj) {
        var opt = getOptions(obj);
        if (opt.format) {
            $('dd', obj).each(function(){
                $(this).css('margin', '0px');
            });
            //$('dd.' + opt.errorclass, obj).css('color', 'red');
        }
    }

    function initTitleTag(obj) {
        obj = getTitleTag(obj);
        if (obj.html() == '') obj.hide();
    }

    function initMessageTag(obj) {
        obj = getMessageTag(obj);
        if (obj.html() == '') obj.hide();
    }

    function setIcon(obj, icon) {
        var opt = getOptions(obj);
        obj = getInput(obj);
        var bg = opt.imgdir + '/';
        switch (icon) {
            case 'success':
                bg += 'success.png';
                break;
            case 'error':
                bg += 'error.png';
                break;
            case 'progress':
                bg += 'indicator.gif';
                break;
            default:
                bg += 'empty.png';
                break;
        }
        obj.attr('readonly', icon == 'progress');
        obj.css('background-image', 'url(' + bg + ')');
    }

    function setErrorMessage(obj, msg) {
        hideMessage(obj);
        obj = getErrorTag(obj);
        obj.html(msg);
        obj.show();
    }

    function hideErrorMessage(obj) {
        showMessage(obj);
        getErrorTag(obj).hide();
    }

    function showMessage(obj) {
        obj = getMessageTag(obj);
        if (obj.html() != '') obj.show();
    }

    function hideMessage(obj) {
        getMessageTag(obj).hide();
    }

    function getErrorTag(obj) {
        return $("dd." + getOptions(obj).errorclass, obj);
    }

    function getMessageTag(obj) {
        return $("dd." + getOptions(obj).msgclass, obj);
    }

    function getTitleTag(obj) {
        return $("dt." + getOptions(obj).titleclass, obj);
    }

    function getInputTag(obj) {
        return $("dd." + getOptions(obj).inputclass, obj);
    }

    function getInput(obj) {
        return $("input", getInputTag(obj));
    }

    $.fn.extend({
        getAutoInputEventType: function() {
            var v = getLastEventType($(this));
            setLastEventType($(this), '');
            if (v == null) return '';
            return v;
        },
        getAutoInput: function() {
            return getInput($(this));
        },
        setAutoInputError: function(msg) {
            var obj = $(this);
            setErrorMessage(obj, msg);
            setIcon(obj, 'error');
        },
        setAutoInputSuccess: function() {
            var obj = $(this);
            hideErrorMessage(obj);
            setIcon(obj, 'success');
        },
        setAutoInputProgress: function() {
            var obj = $(this);
            //hideErrorMessage(obj);
            setIcon(obj, 'progress');
        },
        autoInput: function(args, opts) {
            
            var defArgs = {
                type : 'text',
                message : '',
                title : '',
                handler : null
            };
            
            var defOpts = {
                id : 'autoroot',
                rootclass : 'autoroot',
                titleclass : 'autotitle',
                inputclass : 'autoinput',
                msgclass : 'automessage',
                errorclass : 'autoerror',
                imgdir : 'lib/images',
                time : 1000,
                format : true
            };
            
            return this.each(function() {
                var arg = $.extend(defArgs, args);
                var opt = $.extend(defOpts, opts);
                opt = $.extend(arg, opt);
                opt.id += ('' + counter);
                counter++;
                inputs[opt.id] = $(this);
                options[opt.id] = opt;
                init(opt.id);
            });
            
        }
    });
    
})(jQuery);