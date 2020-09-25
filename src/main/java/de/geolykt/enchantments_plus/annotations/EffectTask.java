package de.geolykt.enchantments_plus.annotations;

import java.lang.annotation.*;

import de.geolykt.enchantments_plus.TaskRunner;
import de.geolykt.enchantments_plus.enums.Frequency;

/**
 * Method annotation used by {@link TaskRunner} to control frequency of execution of scheduled
 * events. Annotations must only be on static methods.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EffectTask {
    Frequency value();
}
