# FireworkShow
A RedstoneChips plugin to create firework shows. Fireworks are random, you'll never get the same show twice

This chip requires one input and one interface block.

The sign parameters are as follows:

d{x} -- Radius (in blocks) around the interface block that the fireworks should be spawned

h{x} -- Height (in blocks) above the interface block to spawn the fireworks

t{x} -- Time (in seconds) for the fireworks show to go for

EXAMPLE:

firework
d{25}
h{10}
t{30}

Will do a firework show for 30 seconds, 10 blocks above the interface block, in a 25 block radius when the input block goes high
