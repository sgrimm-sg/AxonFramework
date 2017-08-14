package org.axonframework.serialization.upcasting;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

/**
 * Abstract implementation of an {@link Upcaster} that eases the common process of upcasting one intermediate
 * representation to several other representations by applying a simple flat mapping function to the input stream of
 * intermediate representations.
 * Additionally, it's a context aware implementation, which enables it to store and reuse context information from one
 * entry to another during upcasting.
 *
 * @param <T> the type of entry to be upcasted as {@code T}
 * @param <C> the type of context used as {@code C}
 *
 * @author Steven van Beelen
 */
public abstract class ContextAwareSingleEntryMultiUpcaster<T, C> implements Upcaster<T> {

    @Override
    public Stream<T> upcast(Stream<T> intermediateRepresentations) {
        C context = buildContext();

        return intermediateRepresentations.flatMap(entry -> {
            if (!canUpcast(entry, context)) {
                return Stream.of(entry);
            }
            return requireNonNull(doUpcast(entry, context));
        });
    }

    /**
     * Checks if this upcaster can upcast the given {@code intermediateRepresentation}.
     * If the upcaster cannot upcast the representation the {@link #doUpcast(Object, Object)} is not invoked.
     * The {@code context} can be used to store or retrieve entry specific information required to make the
     * {@code canUpcast(Object, Object)} check.
     *
     * @param intermediateRepresentation the intermediate object representation to upcast as {@code T}
     * @param context                    the context for this upcaster as {@code C}
     * @return {@code true} if the representation can be upcast, {@code false} otherwise
     */
    protected abstract boolean canUpcast(T intermediateRepresentation, C context);

    /**
     * Upcasts the given {@code intermediateRepresentation}. This method is only invoked if {@link #canUpcast(Object,
     * Object)} returned {@code true} for the given representation. The {@code context} can be used to store or retrieve
     * entry specific information required to perform the upcasting process.
     * <p>
     * Note that the returned representation should not be {@code null}.
     * To remove an intermediateRepresentation add a filter to the input stream.
     *
     * @param intermediateRepresentation the representation of the object to upcast as {@code T}
     * @param context                    the context for this upcaster as {@code C}
     * @return the upcasted representations as a {@code Stream} with generic type {@code T}
     */
    protected abstract Stream<T> doUpcast(T intermediateRepresentation, C context);

    /**
     * Builds a context of generic type {@code C} to be used when processing the stream of intermediate object
     * representations {@code T}.
     *
     * @return a context of generic type {@code C}
     */
    protected abstract C buildContext();

}
