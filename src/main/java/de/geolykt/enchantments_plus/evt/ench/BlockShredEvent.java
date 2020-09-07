/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.geolykt.enchantments_plus.evt.ench;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockShredEvent extends BlockBreakEvent {

    public BlockShredEvent(Block theBlock, Player player) {
        super(theBlock, player);
    }

}
