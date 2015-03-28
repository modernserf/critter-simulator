"use strict";

function EventStream (){
    this._listeners = [];
}

EventStream.put = function (message){ return function (stream){
    stream._listeners.forEach(l => l(message));
    return stream;
};};


EventStream.listen =  function (listener){ return function (stream){
    stream._listeners.push(listener);
    return stream;
};};

EventStream.prototype.put = function(message) {
    return EventStream.put(message)(this);
};

EventStream.prototype.listen = function(listener) {
    return EventStream.listen(listener)(this);
};

EventStream.prototype.off = function (listener) {
    this._listeners = this._listeners.filter(function (l){
        return l !== listener;
    });
    return this;
};

EventStream.prototype.close = function (){
    this._listeners = [];
    return this;
};

EventStream.prototype.reduce = function(fn) {
    var folded = new EventStream();
    this.listen(function (event){
        fn(folded, event);
    });
    return folded;
};

EventStream.prototype.map = function (fn){
    return this.reduce(function (coll, event){
        coll.put(fn(event));
    });
};

EventStream.prototype.filter = function (pred){
    return this.reduce(function (coll, event){
        if (pred(event)){ coll.put(event); }
    });
};

export default EventStream;
