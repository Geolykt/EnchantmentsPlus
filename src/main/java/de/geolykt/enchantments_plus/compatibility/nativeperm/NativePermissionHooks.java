package de.geolykt.enchantments_plus.compatibility.nativeperm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.enchantments_plus.enums.BaseEnchantments;

public final class NativePermissionHooks {

    private final List<NativePermissionHook> hooks;
    private final List<NativeLoggingHook> logHooks;

    /**
     * @deprecated The class is now also responsible for logging hooks.
     *
     * Constructor.
     *
     * @param hooks The permission hooks
     * @since 3.1.4
     */
    @Deprecated(forRemoval = true, since = "3.1.8")
    public NativePermissionHooks(List<NativePermissionHook> hooks) {
        this(hooks, new ArrayList<>());
    }

    /**
     * Constructor.
     * 
     * @param permHooks The permission hooks
     * @param logHooks The logging hooks
     * @since 3.1.6
     */
    public NativePermissionHooks(List<NativePermissionHook> permHooks, List<NativeLoggingHook> logHooks) {
        this.hooks = permHooks;
        this.logHooks = logHooks;
    }

    /**
     * This method queries the correct Permission interfaces, which are plugins. 
     * If the plugin is not loaded the method will ignore it gracefully. <br>
     *
     * @param source The player, from where the Query originates from.
     * @param target The Block which should be tested whether the player may break/alter.
     * @return True if the player may break/alter the block, false otherwise
     * @since 3.1.4
     */
    public boolean nativeBlockPermissionQueryingSystem (@NotNull Player source, @NotNull Block target) {
        for (NativePermissionHook hook : hooks) {
            if (!hook.request(source, target)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calls {@link NativeLoggingHook#logInteraction(BaseEnchantments, UUID, String, BlockState, Block)} to all registered
     * logging hooks.
     *
     * @param ench The enchantment that triggered the interaction
     * @param source The player UUID that caused this action
     * @param username The username (NOT the display name!) of the user causing the action
     * @param before The blockdata of the block that was modified before it was modified. May be null if not applicable (e. g. placing blocks)
     * @param blk The modified block
     * @since 3.1.6
     */
    public final void performLog(@NotNull BaseEnchantments ench, @NotNull UUID source, @NotNull String username, @Nullable BlockState before, @NotNull Block blk) {
        if (doLog()) {
            for (NativeLoggingHook hook : logHooks) {
                hook.logInteraction(ench, source, username, before, blk);
            }
        }
    }

    /**
     * Returns whether logging should be performed.
     * In the current implementation is will always return false if there are no registered hooks
     *
     * @return Whether logging is enabled
     * @since 3.1.6
     */
    public final boolean doLog() {
        return !logHooks.isEmpty();
    }

    /**
     * Adds a logging hook to the container.
     * This method will NOT call {@link NativeLoggingHook#onEnable(java.util.logging.Logger)}.
     *
     * @param hook The hook to add.
     * @since 3.1.6
     */
    public final void addLogger(NativeLoggingHook hook) {
        logHooks.add(hook);
    }
}
