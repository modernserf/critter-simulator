"use strict";

import React from 'react';
import Atom from 'types/Atom';

var eq  = function (a,b){
    if (!a || !b){ return false; }
    for (var key in a){
        if (a[key] !== b[key]){ return false; }
    }
    return true;
};

/* usage:

this.groupState is a struct of private IDs
var kpi = this.getGlobal(this.groupState.kpi);
this.setGlobal(this.groupState.kpi, 'cpm');

*/
var globalize = function (params){
    var keys = {};
    for (var key in params){
        keys[key] = Atom.gensym();
        Atom.set(keys[key], params[key]);
    }
    return keys;
};

var GlobalAtom = {
    // stores local to this component
    getInitialState () {
        this._stores = new Map();
        this._atomListeners = new Map();

        if (this.getInitialGroupState){
            this.groupState = globalize(this.getInitialGroupState());
        }

        return { __atom__: 0 };
    },
    componentWillUnmount (){
        this._atomListeners.forEach(function (v, key){
            v.stream.off(v.listener);
        });
        this._atomListeners.clear();
    },
    atomUpdate (){
        if (this.componentWillReceiveGlobalState){
            this.componentWillReceiveGlobalState();
        }

        if (this.isMounted()){
            this.setState({__atom__ : this.state.__atom__ + 1});
        }
    },
    updateStore (store, params){
        store(params).then(data => {
            this._stores.set(store,
                {loading: false, data: data, params: params});
            this.atomUpdate();
        }).catch(window.fail);
    },
    getStore (store, params){
        var res = this._stores.get(store);

        // first load
        if (!res){
            var initialParams = {loading: true, data: null, params: params};
            // set data as loading
            this._stores.set(store, initialParams);
            // finish loading when promise resolves
            this.updateStore(store, params);
            return initialParams;
        }

        // if params have changed, update and set loading
        if (!eq(params, res.params)){
            this.updateStore(store, params);
            return {loading: true, data: res.data, params: params };
        }

        // otherwise, return the data
        return res;
    },
    clearStore (store){
        this._stores.delete(store);
        this.atomUpdate();
    },
    getGroup (obj){
        var res = {};

        for (var key in obj){
            res[key] = this.getGlobal(obj[key]);
        }

        return res;
    },
    setStateFor (key){
        return (value) => {
            this.setState({[key]: value});
        };
    },
    getGlobal (key){
        var { value, stream } = Atom.get(key);
        var self = this;
        // console.log(key, value, Atom._data.get(key))
        // limit one instance of per component
        if (!this._atomListeners.has(key)){
            var listener = x => this.atomUpdate();
            stream.listen(listener);

            this._atomListeners.set(key, {
                stream: stream,
                listener: listener
            });

        }

        return value;
    },
    setGlobal (key, value){
        Atom.set(key, value);
    },
    gensym: Atom.gensym
};

export default GlobalAtom;
