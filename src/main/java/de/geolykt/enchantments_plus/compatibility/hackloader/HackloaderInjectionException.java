package de.geolykt.enchantments_plus.compatibility.hackloader;

/**
 * The {@link HackloaderInjectionException} is an exception that is thrown in the {@link Hackloader#injectHackloader()}
 * method and may arise due to numerous reasons - for example due to restricted reflections.
 *
 * Either way it means that Hackloader cannot be used.
 *
 * @author Geolykt
 * @since 4.1.10
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
