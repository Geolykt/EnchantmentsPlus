/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2022 Geolykt and EnchantmentsPlus contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus.compatibility.hackloader;

/**
 * The {@link HackloaderInjectionException} is an exception that is thrown in the {@link Hackloader#injectHackloader()}
 * method and may arise due to numerous reasons - for example due to restricted reflections.
 *
 * Either way it means that Hackloader cannot be used.
 *
 * @author Geolykt
 * @since 4.1.0
 */
public class HackloaderInjectionException extends Exception {

    /**
     * serialVersionUID.
     *
     * @since 4.1.0
     */
    private static final long serialVersionUID = -6103274629602795105L;

    HackloaderInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    HackloaderInjectionException(String message) {
        super(message);
    }
}
