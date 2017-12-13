package me.theyinspire.pandora.replica;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:47 PM)
 */
public interface ReplicaRegistry {

    Set<Replica> getReplicaSet();

    default Set<Replica> getReplicaSet(String signature) {
        return getReplicaSet().stream()
                              .filter(replica -> !replica.getSignature().equals(signature))
                              .collect(Collectors.toSet());
    }

    default void init(String signature, String uri) {}

    default void destroy(String signature) {}

}
