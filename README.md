emergence is the creation of complex or chaotic systems from simple and orderly rules.

# structure

- intro
    + lascaux
- conways life
    + definition of emergence
    + life rules
    + show chaos
    + identify order
        * stable / oscillators
        * glider
        * breeders
    + some of these were _created_ but many of them were _discovered_
- flocking
    + cellular automata only have "alive" and "dead" states
        * a glider is not an individual, but a collection of neighboring cells
        * the consitutuents of a glider do not move, they produce the next generation in the next position
        * beautiful in its simplicity, but impractical and alien as a model of life
    + micro vs macro
        * flocking isn't a simulation of life, just a particular behavior
        * boids have more state -- position, bearing, velocity
        * rules designed to create behavior, not other way 'round
    + similarities
        * simple rules for determining next position based on current state and neighbors
        * completely deterministic -- no randomness
        * no memory -- each position can be determined from previous position alone
- critters
    + behaviors
        * not a complete lifecycle, but wanted to capture the essence of guinea pigs
        * exhaustive research
        * eating, cowering, companionship, pooping
        * ran out of time before i could implement squeaking
    + differences vs flocking
        * critters have time-related thresholds for behaviors
        * critters have destinations for some behaviors
        * less elegant rules -> less surprising results
- failures
    + agents/actors/neural networks
        * minsky's Society of Mind
        * trying to capture behavior in an elegant way that resembles how guinea pigs actually think
        * emergence is a property both internal to the critter, as simple instincts form complex behaviors, and in the interactions between critters
        * turns out this takes more than a couple evenings to solve





# lascaux caves

since the dawn of time, humanity has yearned to simulate a guinea pig village in a web browser. The cave paintings at lascaux are some of the earliest documented attempts to capture the essence of a creature in a representational model.

I'm going to fast forward a little bit.

# early AI

In order to simulate a guinea pig in a web browser, let alone a whole village of them, we need to have computers and a paradigm for representing intelligence. Perhaps not surprisingly, the two are strongly related.







wheek wheek wheek

critter agent
attractors (food, houses, each other) and repellers (the hand)
boids
emergent behavior

Massively multi-critter online rodeant petting game

Simple AI
- critters run from cursor
- Critters run to food
- Critters prefer to be near each other
- Critters prefer to be in houses
- critters prefer to eat food that other critters are already eating
- Critters make sounds when hungry/angry
- critters poop a lot (use 💩 emoji?)
- critters will investigate new/moved structures

Automata 
Multiple cursors
The sims/sim city/sim ant