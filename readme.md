Craftbukkit support (if you can talk about one) will be ditched rather soon. The plugin may no longer run on these Servers in the coming releases. We intend on continuing support with Spigot, Paper and any other Server that implement the Bukkit API
<br>
Got a question? Need help or want to discuss changes? Then feel free to DM me via discord: tristellar#9022. Issues however should be reported here so forkers can profit from them being public, but It'S the best if at least someone knows exploits or bugs so they can be fixed.

It also only intends to support 1.16.3 and newer versions when the time comes.

# Enchantments+
## Description
Zenchantments is a custom enchantment plugin that adds 70+ new enchantments (it doesn't feel like much, to be honest) to the game, covering a multitude of different uses. These include target-tracing arrows, lumber axes, block-breaking lasers, and much more. These custom enchantments are obtained through the normal enchantment process and act like regular enchantments, capable of being combined and merged. The plugin requires no client-side mods or resource packs. A comprehensive configuration file enables fine-tuning of individual enchantments to tailor them to every server's gameplay. 
<br> While we currently don't add or remove any Enchantments, we will attempt to rebalance them and make them futureproof. In essence, we attempt to make this Fork as stable as possible to support Production-grade Servers.

## Permissions
<b>enchplus.enchant.get</b> - On player enchant event, allow player to have a chance at the enabled custom enchantments<br>
<b>enchplus.enchant.use</b> - Allow player to use the given custom enchants on an item<br>
<b>enchplus.command.reload</b> - Access to /ench reload, to reload the configuration (try not to use, may lead to memory leaks)<br>
<b>enchplus.command.give</b> - Gives an enchanted item, while similar to enchant, it also gives the underlying material<br>
<b>enchplus.command.list</b> - Lists all availiable enchantments<br>
<b>enchplus.command.info</b> - Returns the info of an enchantments<br>
<b>enchplus.command.onoff</b> - Abillity to turn on / turn off a certain enchantment<br>
<b>enchplus.command.enchant</b> - Enchants a item without giving the underlying material. Also allows to enchant other people's stuff, so be cautious.<br>
<b>enchplus.command.lasercol</b> - Enables the abillity to colo(u)r the laser of your item in hand. Purely cosmetic and a good way to get a few extra "donations".<br>

## Download
See [Releases](https://github.com/Geolykt/EnchantmentsPlus/releases) for downloads

## Compilation
Compile this project like every other project out there. Note that you need to have the latest bindings.

## Compatibility
The current version of this plugin is fully compatible with Spigot version 1.16.1, 1.16.2 and 1.16.3. Any versions under 1.16.1, will **not** work without tinkering, versions above, may, although with a few issues.

## Contribute
Anyone is free to contribute to this repository via pull requests, issues or comments, however keep in mind that this repository uses 4 space indentation.

## Changes performed in this fork compared to NMS-less Zenchantments
To view the changes compared to Zenchantments, add the Changelog of [NMS-Less Zenchantments](https://github.com/Geolykt/NMSless-Zenchantments#changes-performed-in-this-fork) onto it
<ul>
 <li>Major changes:
  <ul>
   <li>Most values in the compatibillity adapter can be changed via magic configuration files now</li>
   <li>The Spectral enchantment can now be levelled by default</li>
   <li>Uses the 1.16.3 Spigot API</li>
  </ul>
 </li>
 <li>Minor changes:
  <ul>
   <li>Fixed the Reveal enchantment, with it's own twist</li>
   <li>Added an enchantment gatherer denylist - only works with NBT right now</li>
   <li>The color of the laser can now be changed for purely costmetical reasons. Why? Don't ask</li>
   <li>Help and tab completion now handle permissions</li>
   <li>Mystery fish can now catch other water mobs such as cod, pufferfish and salmon and was buffed overall</li>
  </ul>
 </li>
 <li>Patches:
  <ul>
   <li>Fire enchantment no longer OOMs when attempting to smelt unsmeltable stuff</li>
   <li>Added toggleable shred cooldown when the laser enchantment is used</li>
   <li>Fixed potential bug with the vortex enchantment, the specifics are unknown</li>
   <li>Mitigated severe lag issues with the Spectral enchantment, also performed some optimisations. To get to the pervious way of how the enchantment worked, put the enchantment power to really high values</li>
   <li>Spectral now can use it's own Protection system nullifying the risk of McMMO/Jobs exploits</li>
   <li>Spectral performance increased noticeably.</li>
  </ul>
 </li>
 <li>Code changes (doesn't affect behaviour as much):
  <ul>
   <li>Refractored to it's own structure</li>
   <li>Fix compilation issues with later bindings</li>
   <li>Changed the way permissions are handled within the code</li>
  </ul>
 </li>
</ul>
