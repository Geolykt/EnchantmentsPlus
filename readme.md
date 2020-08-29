<b> Active development on this branch will stop soon, the last feature that will be added is going to be a cross compatible enchantment storage system with upstream and legacy versions. After that, this branch will enter the LTS phase at which only new entities and blocks will be added to the adapter. Bugs will be of course also fixed, as long as they are reasonably easy to fix. Development on the branch should stop completely by 2021</b>
<hr>
Got a question? Need help or want to discuss changes? Then feel free to DM me via discord: tristellar#9022. Issues however should be reported here so forkers can profit from them being public
<hr>
This fork uses NBT tags to store enchantments, which makes it somewhat uninterchangeable with the regular plugin
It also only intends to support 1.16.1 and newer versions when the time comes, older may work however with a bit of commit reverting. Using older minecraft versions is generally not recommended after the commits of the 14th July.

# NMSless-Zenchantments
## Description
Zenchantments is a custom enchantment plugin that adds 70+ new enchantments to the game, covering a multitude of different uses. These include target-tracing arrows, lumber axes, block-breaking lasers, and much more. These custom enchantments are obtained through the normal enchantment process and act like regular enchantments, capable of being combined and merged. The plugin requires no client-side mods or resource packs. A comprehensive configuration file enables fine-tuning of individual enchantments to tailor them to every server's gameplay. 
<br> This specific repository tries to create a more stable version of Zenchantments while also making it availiable for newer versions, the partent repo doesn't support. This fork does not bother to enlarge the list of enchantments it adds as of yet, but is purely bug-squashing driven, some Optimisation might also be performed whenever possible.

## Download
See [Releases](https://github.com/Geolykt/NMSless-Zenchantments/releases) for downloads

## Compile
Compile this project like every other project out there

## Compatibility
The current version of this plugin is fully compatible with Spigot version 1.16.1. Any versions under 1.16.1, will **not** work without tinkering, versions above, may, although with a few issues.

## Contribute
Anyone is free to contribute to this repository via pull requests or comments, however keep in mind that this repository uses 4 space indentation.

## Changes performed in this fork
<ul>
 <li>Major changes:
  <ul>
   <li>Removed NMS</li>
   <li>Using NBT tags to store enchantments other than invisble lore</li>
   <li>Using Datapack tags instead of hardcoded values for some stuff</li>
   <li>1.16.1 and upwards compatible</li>
   <li>Rewrote large portions of the compatibillity adapter</li>
  </ul>
 </li>
 <li>Minor changes:
  <ul>
   <li>Various minor performance boosts</li>
   <li>The fire and aborist enchantment no longer use hardcoded recepies</li>
   <li>Command blocks and console now can use more commands</li>
   <li>Allow to target other players and using target modifiers in the enchantment command</li>
   <li>Added informative enchantment use events - cannot be cancelled as of yet</li>
   <li>Recoded the giant stack of if-else statements in the spectral class</li>
   <li>The siphon enchantment can be nerfed; configurable in the patches.yml</li>
  </ul>
 </li>
 <li>Patches:
  <ul>
   <li>Patched a bug where blue ice blocks and soulsand blocks placed by the frozenstep and netherstep enchantemnts can be moved or removed; can be toggled</li>
   <li>Patched a bug where the fire enchantment would work even when the player shouldn't be able to destroy a block</li>
   <li>Patched a bug which would result in the abillity of changing blockstates in protected regions, for example via Towny or World guard; can be toggled</li>
   <li>Patched a bug which would spam the console when the Weight enchantment is used</li>
   <li>Patched a bug which would result in a unbreaking 1 enchanted item, see https://github.com/Zedly/Zenchantments/issues/40</li>
   <li>Patched a bug which would result in console spam when a player with maximum or near maximum health is healed by the siphon enchantment [Issue #8](https://github.com/Geolykt/NMSless-Zenchantments/issues/8)</li>
  </ul>
 </li>
 <li>Code changes (doesn't affect behaviour as much):
  <ul>
   <li>A lot of whitespace changes; this repo is 100% 4-space indented</li>
   <li>Removed unused variables and methods</li>
   <li>General reduction of compiler warnings</li>
   <li>Other QoL changes when it comes to building the project</li>
  </ul>
 </li>
</ul>
