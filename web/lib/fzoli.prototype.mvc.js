var Személy = Class.create({
    
    initialize: function(név) {
        this.név = név;
    },

    beszél: function(üzenet) {
        return this.név + ': ' + üzenet;
    }

});

var Kalóz = Class.create(Személy, {

    beszél: function($super, üzenet) {
        return $super(üzenet) + ', jarr!';
    }

});