"use strict";

var IngredientManager = {
    onSelect (id){
        this.setGlobal('selectedID',id);
        var ingredients = this.getGlobal('ingredients');
        // put selected ingredient at top (end) of list
        var nextIngredients = ingredients.filter(x => x.id !== id)
            .concat([ingredients.find(x => x.id === id)]);

        this.setGlobal('ingredients',nextIngredients);   
    },
    onHover (id){
        this.setGlobal('hoverID',id);
    },
    onCook (params){
        var {id, value} = params;
        var igs = this.getGlobal('ingredients');

        var nextIgs = igs.map(x => x.id === id ? 
            Object.assign(x,{ cook: value }) :
            x);
        
        this.setGlobal('ingredients', nextIgs);
    }
};

export default IngredientManager;
