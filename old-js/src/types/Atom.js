"use strict";

import EventStream from 'types/EventStream';

var sym = 0;

var Atom = {
    _data: new Map(),
    _listeners: new Map(),
    get (key){
        var stream;
        if (Atom._listeners.has(key)){
            stream = Atom._listeners.get(key);
        } else {
            stream = new EventStream();
            Atom._listeners.set(key, stream);
        }
        var value = Atom._data.get(key);
        return { stream: stream, value: value };
    },
    set (key, value){
        // console.log(key)
        // only sets if value has changed
        if (Atom._data.get(key) !== value){
            Atom._data.set(key, value);
            if (Atom._listeners.has(key)){
                Atom._listeners.get(key).put(value);
            }
        }
        return Atom;
    },
    gensym (){
        return sym++;
    }
};

export default Atom;
