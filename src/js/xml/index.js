(function() {

    var testListener = $.ModelChangeListener();
    var controller = $.Controller();

    function setText(lb, tf, text) {
        var s = text.length > 0 ? ' ' : '';
        var evtype = tf.getAutoInputEventType();
        lb.html(s + text);
        tf.setAutoInputSuccess();
        tf.getAutoInput().val(text);
        if(evtype == 'timer' || evtype == 'enter') tf.getAutoInput().focus();
    }
    
    function setModel(obj, model) {
        var value = obj.getAutoInput().val();
        if (value == 'Ádám') {
            obj.setAutoInputError('Ez a szöveg biztosan nem érvényes!');
        }
        else {
            obj.setAutoInputProgress();
            controller.setModel(model, {property : 'str', value : value}, function(ret){
                if (ret == 0) obj.setAutoInputSuccess();
                else if (ret != null) obj.setAutoInputError('Ez a szöveg nem érvényes!');
                     else obj.setAutoInputError('Szerver hiba!');
            });
        }
    }
    
    $(document).ready(function() {
        var lb = $('span#lb');
        var mylb = $('span#mylb');
        var tf = $('span#tf');
        var mytf = $('span#mytf');
        
        tf.autoInput({
            title : 'Megosztott üzenet',
            message : 'Nem megengedett: Ádám, Éva',
            handler : function() {
                setModel(tf, 'test_model');
                tf.getAutoInput().focus();
            }
        });
        
        mytf.autoInput({
            title : 'Saját üzenet',
            message : 'Ezt a szöveget csak te látod.<br />Ádám és Éva itt sincs megengedve.',
            handler : function() {
                setModel(mytf, 'my_test_model');
                mytf.getAutoInput().focus();
            }
        });

        var eventHandler = function(ev) {
            var model = ev.attr('name');
            var event = ev.find('event');
            var value = event.attr('value');
            if (value != null) {
                if (model == 'test_model') setText(lb, tf, value);
                if (model == 'my_test_model') setText(mylb, mytf, value);
            }
        };

        testListener.addModels({'test_model' : eventHandler, 'my_test_model' : eventHandler});

        controller.getModels(['test_model', 'my_test_model'], function(models){
            var v = models.test_model.find('property').attr('value');
            var mv = models.my_test_model.find('property').attr('value');
            setText(lb, tf, v);
            setText(mylb, mytf, mv);
        });
        
    });
    
})();
