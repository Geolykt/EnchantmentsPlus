package de.geolykt.enchantments_plus;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.bukkit.Bukkit;

import de.geolykt.enchantments_plus.annotations.EffectTask;
import de.geolykt.enchantments_plus.enums.Frequency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A runnable class that will execute all events of the specified frequency.
 *
 */
public class TaskRunner implements Runnable {

    private Set<Method> tasks;
    private Logger logger;

    /**
     * Initializes this EventRunner by collecting all methods with an
     * {@link EffectTask} annotation of the specified frequency.
     *
     * @param freq The frequency of annotation that we'll be running.
     */
    TaskRunner(Frequency freq) {
        this.logger = Bukkit.getLogger();

        tasks = new HashSet<>();

        new FastClasspathScanner(Enchantments_plus.class.getPackage().getName()).overrideClasspath(Storage.pluginPath)
                .matchClassesWithMethodAnnotation(
                        EffectTask.class,
                        (clazz, method) -> {
                            if (!Modifier.isStatic(method.getModifiers())) {
                                this.logger.warning(
                                        "EffectTask on non-static method '" + method.getName() + "' in class '"
                                        + clazz.getName() + "'");
                            }
                            if (method.getAnnotation(EffectTask.class).value() == freq) {
                                assert method instanceof Method : "Event annotation not valid on constructors";
                                tasks.add((Method) method);
                            }
                        }).scan();
    }

    /**
     * Runs all methods on subclasses of CustomEnchantment that are annotated
     * with {@link EffectTask} and have the same event frequency as this
     * EventRunner.
     *
     * @see Frequency
     */
    @Override
    public void run() {
        for (Method m : tasks) {
            try {
                m.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                this.logger.log(Level.SEVERE, "Could not invoke event '" + m.getName() + "' due to \n" + e.getCause(),
                        e);
                e.printStackTrace();
            }
        }
    }
}
