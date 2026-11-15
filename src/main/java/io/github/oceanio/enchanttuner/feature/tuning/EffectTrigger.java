package io.github.oceanio.enchanttuner.feature.tuning;

/** Events that can activate a YAML configured enchant effect. */
public enum EffectTrigger {
    BLOCK_BREAK,
    MELEE_ATTACK,
    ENTITY_KILL;

    public static EffectTrigger fromConfig(String value) {
        return valueOf(value.trim().toUpperCase().replace('-', '_'));
    }
}
