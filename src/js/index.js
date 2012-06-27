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
    
    function setModel(obj, model, changed) {
        var value = obj.getAutoInput().val();
        if (value == 'Ádám') {
            obj.setAutoInputError('Ez a szöveg biztosan nem érvényes!');
        }
        else {
            if (changed) {
                obj.setAutoInputProgress();
                controller.setModel(model, {property : 'str', value : value}, function(ret){
                    if (ret == 0) obj.setAutoInputSuccess();
                    else if (ret != null) obj.setAutoInputError('Ez a szöveg nem érvényes!');
                        else obj.setAutoInputError('Szerver hiba!');
                });
            }
            else {
                obj.setAutoInputSuccess();
            }
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
            handler : function(changed) {
                setModel(tf, 'test_model', changed);
                //tf.getAutoInput().focus();
            }
        });
        
        mytf.autoInput({
            title : 'Saját üzenet',
            message : 'Ezt a szöveget csak te látod.<br />Ádám és Éva itt sincs megengedve.',
            handler : function(changed) {
                setModel(mytf, 'my_test_model', changed);
                //mytf.getAutoInput().focus();
            }
        });

        testListener.addModels({
            'test_model' : function(ev){
                setText(lb, tf, ev.test_model[0].str);
            },
            'my_test_model' : function(ev){
                setText(mylb, mytf, ev.my_test_model[0].str);
            }
        });

        controller.getModels(['test_model', 'my_test_model'], function(models){
            var v = models.test_model.str;
            var mv = models.my_test_model.str;
            setText(lb, tf, v);
            setText(mylb, mytf, mv);
        });
        
    });
    
})();