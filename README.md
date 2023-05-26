# GoodMC

A Fabric Mod that I basically just made to make Minecraft the way I like it with some alright ideas here and there :)

## Shulker Recursion Enchant
### Allows for shulker boxes to hold shulker boxes (Depth of 1)
- Shulker box items inside a Shulker box can be right-clicked to open its inventory while your in the Other Shulker
- Spawns in Bastion Treasure Chests (90%)

![shulker image](https://i.imgur.com/5sXOlCs.png)

<br>

## Shulker Affinity Enchant
### Makes players immune to the levitation effect from shulker bullets
- Spawns in End City Treasure Chests (10%)

<br>

## Realistic Lanterns
### Lanterns now render as if you're holding them with your hand in First Person
Adds a Keybind, the default being `left_alt` (can be toggled or held), to find a lantern in your inventory and put it in your offhand

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
 Right now everything works dandilly, but in vanilla when baby animals are rendered they use the 
 size of an adults head size, which is why they have a slightly big heads as babies, and because I'm scaling 
 every single part of their model, it also scales the babies heads bigger when it should probably be scaling 
 it smaller or just scaling only the body parts, so for now until I learn how to render only the body parts
 without affecting the babies heads, the babies increase their brain size as they grow older ;)

![Growing](https://i.imgur.com/8M9USxc.gif)

<br>

## Angry Mobs
### Zombies / Skeletons that can pickup items now have a different texture
I call them Angry Mobs cause the local difficulty determines whether mobs spawn with item picking abilities<br>
So higher difficulty = Tougher Mobs = Item picking abilities = Angry Mobs, so the tougher, the angrier ;)
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

## Locator Compass
### Compasses can now be used to locate structures and biomes
Using structure tattered map found in most structures (it will always spawn 1 per structure)
in a smithing table with a compass will randomly roll a structure the compass will be 
pointing to within a certain distance

Using biome tattered map found in most structures ( 10% chance of spawning in all loot tables)
in a smithing table with a compass will randomly roll a biome the compass will be pointing 
to within a certain distance

![Structure Locator](https://i.imgur.com/IPVb2tm.gif)

<br>

### Custom View Bobbing Strength
Minecraft has either View Bobbing on or View Bobbing off, but what if 
you like it to just be less strong, well that's what I added. View Bobbing Strength
![View Bobbing Strength](https://i.imgur.com/XCSobkg.png])
## Things I want to add...
> A Todo List (Working on it... rendering is hard)

> Better Mob Aging (Kinda done just need to fix baby animal head scaling)

> Make a library for Enchantable Blocks (KINDA DONE)<br> 
> This could also fix the fact that you can't use this mod with EnchantedShulkers

> Add integration with Reinforced Shulkers (KINDA DONE)

