# YAML エンチャント効果

既存の `plugins/EnchantTuner/enchants.yml` に加えて、`plugins/EnchantTuner/enchants/` にエンチャントごとの YAML ファイルを置けます。ファイル名 (拡張子を除く) が ID になります。ファイルのトップレベルには `type`、`key`、`max-level` などを記述します。各定義には `effects` を設定でき、エフェクトは `id` で Java 側の組み込み処理を選び、`args` で値を渡し、`triggers` で実行するイベントを指定します。

```yaml
my_fluid_enchant:
  type: custom
  key: enchanttuner:my_fluid_enchant
  enabled: true
  display-name: 水流の加護
  max-level: 3
  target:
    - sword
  pool:
    enabled: true
    weight: 3
  effects:
    - id: fluid_heal_attack
      triggers:
        - melee_attack
    - id: fluid_heal_kill
      args:
        duration-seconds: 8
        amplifier: "%level% - 1"
      triggers:
        - entity_kill
```

同じエンチャントに複数の効果とトリガーを指定できます。イベント Listener はイベントを `EffectDispatcher` に渡し、付与済みエンチャントに設定された該当トリガーの効果を呼び出します。`key` はエンチャント ID とレベルをアイテムの PDC に保存するキーです。既存の組み込みカスタムエンチャントと異なる新しい `key` を使う場合も、既知の効果 ID を組み合わせて定義できます。

ファイル形式の定義で抽選プールに含める場合は、`pool.enabled` と `pool.weight` を指定します。`pool` がない定義は登録されますが、ランダム抽選には入りません。`EnchantPool.yml` にも同じ ID がある場合は、ファイル側の `pool` 設定を優先します。

## 組み込み効果

| 効果 ID | トリガー | 引数 |
| --- | --- | --- |
| `delicate_unripe_block` | `block_break` | `cancel-unripe` (boolean、既定値 `true`) |
| `fluid_heal_attack` | `melee_attack` | なし |
| `fluid_heal_kill` | `entity_kill` | `duration-seconds` (秒、既定値 `5`)、`amplifier` (既定値 `%level% - 1`) |
| `durability_bonus` | 付与時 | `bonus-per-level` (既定値はアイテム種別に応じて Java 側で決定) |

`amplifier` は整数、または `%level%` を含む単純な加減算式を指定できます。トリガーとして現在サポートする値は `block_break`、`melee_attack`、`entity_kill` です。効果 ID は組み込みカタログに登録されているものを指定してください。新しい種類のゲーム処理を追加する場合は、対応する Java 効果実装と Registry 登録が必要です。

既存サーバーが `effects` を含まない設定を使っている場合、`delicate`、`fluid_heal`、`durability` には従来の組み込み効果を補います。`effects: []` を明示した場合はイベント効果を設定しません。
