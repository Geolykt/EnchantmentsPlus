package de.geolykt.enchantments_plus.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target({ METHOD, CONSTRUCTOR })
/**
 * This is a simply informational annotation to signify that the Method or Constructor is safe to call in the Bukkit async state.<br>
 * AsyncSafe methods/constructors should not call any important Bukkit API directly. Please note that they should not be considered
 * Thread safe.
 */
public @interface AsyncSafe {}
