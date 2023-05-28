# GoodMC

A Fabric Mod that I basically just made to make Minecraft the way I like it with some alright ideas here and there 😄

Adds a fair bit of random things, so here's an overview, with some simplistic 
descriptions, read on for more explanatory descriptions.

### Overview 🌍
> Shulker Box Recursion (shulker in shulker)

> Shulker Shell Affinity (shulker shell levitation immunity)
 
> Locator Compass (locates biomes and structures)

> Realistic Lanterns (shows a 3d lantern when you hold it, and your hand)

> Open in Wiki (client side commmand to open things on the wiki website)

> Angry Mobs (mobs than can pick up things have a different texture)

> View Bobbing Strength (customizable view bobbing strength)

# ▶️ <span style="color: #fff">Firstly</span> 
### In order to have healthy communications here's some things I want you to know

1. **⚠️BEWARE OF INEXPERIENCED MODDER! DO NOT FEED!⚠️**
    * I'm relatively new to modding, but I think I'm getting the hang of it. 
   I'm not exceptionally smart but at least I've made something that I kinda like 😄.
2. **Known mods that break (working on it)⁽ᵖʳᵒᵇᵃᵇˡʸ⁾**
   * EnhancedBlockEntities
   * Connectivity
   * EntityTextureFeatures
   * And most likely other mods targeting rendering entities / block entities
3. Most things are subject to change at my own volition.
4. **DO NOT FORGET** that I'm only doing this for fun, I'm not trying to make a production ready mod for the community, this is essentially for myself, but I will obviously try to fix / improve where there can be improvement (when I can), just don't expect royalty. Thanks.
<br>

## 🔄 Shulker Recursion Enchant 
### Allows for shulker boxes to hold shulker boxes (Depth of 1) ⁽ⁿᵒᵗ ᶜᵒⁿᶠⁱᵍᵘᵃᵇˡᵉ⁾
- Shulker box items inside a Shulker box can be right-clicked to open its inventory while your in the Other Shulker
- Spawns in Bastion Treasure Chests (90%)

![shulker image](https://i.imgur.com/5sXOlCs.png)

<br>

## 🛡 Shulker Affinity Enchant 
### Makes players immune to the levitation effect from shulker bullets
- Spawns in End City Treasure Chests (10%)

<br>

## 🕯 Realistic Lanterns
### Lanterns now render as if you're holding them with your hand in First Person
Adds a Keybind, the default being `left_alt` (can be toggled or held), to find a lantern in your inventory and put it in your offhand

> When swinging your arm with the lantern it can look goofy at times because I couldn't get the swing animation to work with the way I've done it

![lantern](https://i.imgur.com/rWofykE.png)
## 📖 Open in Wiki 
### A command to open minecraft related stuff in https://minecraft.fandom.com with your default browser
`/wiki`<br><br>
Example<br>
`/wiki grass block` will effectively launch your browser at https://minecraft.fandom.com/wiki/Grass_Block

<br>

## 👴🏻 <span style="color:light_gray">Visually Aging Mobs<span> 
### In Vanilla, mobs that turn into an adult version of themselves, visually, they just do it instantly, they just... instantaneously .. become an adult, like huh?
### With Visually Aging Mobs I've achieved this... but with a flaw
Right now everything works dandilly, but in vanilla when baby animals are rendered they use the
size of an adults head size, which is why they have a slightly big heads as babies, and because I'm scaling
every single part of their model, it also scales the babies heads bigger when it should probably be scaling
it smaller or just scaling only the body parts, so for now until I learn how to render only the body parts
without affecting the babies heads, the babies increase their brain size as they grow older ;)

![Growing](https://i.imgur.com/8M9USxc.gif)

<br>

## 💢 <span style="color: #ffcccc">Angry Mobs</span> 
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

## <span style="color: #b88be5;">Budding Amethyst</span>
- Budding Amethyst will now drop when exploded by Creepers
- Budding Amethyst is now <b>pushable</b> by pistons
- Budding Amethyst is now slower to mine (Hardness of 20)
- Compasses freak out whenever you're above or below a geode

## Locator Compass
### Compasses can now be used to locate structures and biomes
Using structure tattered map found in most structures (it will always spawn 1 per structure)
in a smithing table with a compass will randomly roll a structure that the compass will **try** to point towards.

Using biome tattered map found in most structures ( 10% chance of spawning in all loot tables)
in a smithing table with a compass will randomly roll a biome the compass will **try** to point towards.

They try to find the closest biome / structure within a configurable radius.

**Beware** kind of experimental still, shift clicking in the smithing 
table can produce unexpected results **SOMETIMES**. Should be mostly fine tho.

![Structure Locator](https://i.imgur.com/IPVb2tm.gif)

<br>

### Custom View Bobbing Strength 
Minecraft has either View Bobbing on or View Bobbing off, but what if
you like it but just want it to be weaker? Well that's what I added... View Bobbing Strength. <br><br>
**Found at**: <br>
- `Options` -> `Video Options` -> `View Bobbing Strength Slider` <br>
- `Mods` -> `GoodMC` -> `All` -> `View Bobbing Strength Slider` <br>
or I guess the mod's config file in the minecraft folder🤷‍♂️

![View Bobbing Strength](https://i.imgur.com/XCSobkg.png])
## Things I want to add...
If you want the todo list I use to track things, here it is: https://trello.com/b/JO99cFBz
> A Todo List (Working on it... rendering is hard)

> Better Mob Aging (Kinda done just need to fix baby animal head scaling)

> Make a library for Enchantable Blocks (KINDA DONE)<br>
> This could also fix the fact that you can't use this mod with EnchantedShulkers

> Add integration with Reinforced Shulkers (KINDA DONE)

