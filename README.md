# GoodMC

A Fabric Mod that I basically just made to make Minecraft the way I like it with some alright ideas here and there :)

## Shulker Recursion Enchant
### Allows for shulker boxes to hold shulker boxes (Depth of 1)
- Shulker box items inside a Shulker box can be right-clicked to open its inventory while your in the Other Shulker
- Spawns in Bastion Treasure Chests (90%)

>There is one pretty bad flaw so far, but I'm sure I'll fix it eventually<br>

> **This shouldn't affect people on single-player**<br>
When you right-click a shulker item inside a shulker, the inventory is not synchronized between players in multiplayer meaning some nasty duping is possible in multiplayer

![shulker image](https://i.imgur.com/5sXOlCs.png)

<br>

## Shulker Affinity Enchant
### Makes players immune to the levitation effect from shulker bullets
- Spawns in End City Treasure Chests (10%)

<br>

## Realistic Lanterns
### Lanterns now render as if you're holding them with your hand in First Person
Adds a Keybind, the default being `left_alt`, to find a lantern in your inventory and put it in your offhand

> When swinging your arm with the lantern it can look goofy at times because I couldn't get the swing animation to work with the way I've done it

![lantern](https://i.imgur.com/rWofykE.png)
## Open in Wiki
### A command to open minecraft related stuff in https://minecraft.fandom.com with your default browser
`/wiki`<br><br>
Example<br>
`/wiki grass block` will effectively launch your browser at https://minecraft.fandom.com/wiki/Grass_Block

<br>

## Visually Aging Mobs
### In Vanilla, mobs that turn into an adult version of themselves, visually, they just do it instantly, they just... instantaneously .. become an adult, like huh?
### With Visually Aging Mobs I've achieved this... but with a flaw
 Right now the client can never know the actual age of a mob because the server doesn't send that information, so right now the client just assumes the age from how long the entity has been loaded by the client 🤷<br><br>
I'm pretty sure eventually I can make the server tell the client the age but for now it is what it is.

![Growing](https://i.imgur.com/8M9USxc.gif)

<br>

## Angry Mobs
### Zombies / Skeletons that can pickup items now have a different texture
- Can be Customized with Resource Packs
    - Texture location is at
      - goodmc/textures/entity/zombie/angry_zombie.png
      - goodmc/textures/entity/skeleton/angry_skeleton.png
      
<br>

![Angry Zombie](https://i.imgur.com/JW7OYjn.png)

<br>

## Budding Amethyst
- Budding Amethyst will now drop when exploded by Creepers
- Budding Amethyst is now <b>pushable</b> by pistons
- Budding Amethyst is now slower to mine (Hardness of 20)
- Compasses freak out whenever you're above or below a geode

<br>

## Things I want to add...
> A Todo List

> Better Mob Aging 

> Make a library for Enchantable Blocks <br>
> This could also fix the fact that you can't use this mod with EnchantedShulkers

> Add integration with Reinforced Shulkers

