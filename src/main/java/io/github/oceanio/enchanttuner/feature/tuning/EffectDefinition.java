package io.github.oceanio.enchanttuner.feature.tuning;

import java.util.List;
import java.util.Map;

/** Immutable configuration for one effect attached to an enchant definition. */
public record EffectDefinition(String id, Map<String, Object> args, List<EffectTrigger> triggers) {
    public EffectDefinition {
        args = Map.copyOf(args);
        triggers = List.copyOf(triggers);
    }
}
