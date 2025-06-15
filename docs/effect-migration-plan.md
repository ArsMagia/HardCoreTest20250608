# エフェクト個別ファイル化計画

## 🎯 完了済み (Phase 1)

### 個別ファイル化済みエフェクト
- ✅ **HandTremorEffect** (COMMON) - 手の震え
- ✅ **LegCrampEffect** (COMMON) - 足の痙攣  
- ✅ **SleepDeprivationEffect** (COMMON) - 睡眠不足
- ✅ **ItemWeightEffect** (COMMON) - アイテム重量化
- ✅ **HungerCurseEffect** (COMMON) - 空腹の呪い
- ✅ **DrowningFeelingEffect** (RARE) - 溺水感覚
- ✅ **RandomTeleportEffect** (UNCOMMON) - ランダムテレポート

### 技術的改善
- ✅ `@EffectRegistration`アノテーション追加
- ✅ 自動登録システム対応
- ✅ カテゴリ分類実装 (physical, mental, survival, spatial)
- ✅ 統一された実装パターン

## 📋 今後の移行対象 (Phase 2)

### SimpleUnluckyEffects.java から移行予定
- `ReverseControlsEffect` (UNCOMMON) - 操作反転
- `FakeDeathEffect` (COMMON) - 偽死効果
- `MemoryLossEffect` (RARE) - 記憶喪失
- `FireRainEffect` (UNCOMMON) - 炎の雨
- `ExpLeakEffect` (COMMON) - 経験値流出
- `WeaponCurseEffect` (RARE) - 武器呪い
- `ArmorVanishEffect` (RARE) - 防具消失

### MoreUnluckyEffects.java から移行予定
- `DarkphobiaEffect` (UNCOMMON) - 暗闇恐怖症
- `ConfusionMistEffect` (RARE) - 混乱の霧
- `ElectricShockEffect` (RARE) - 電撃ショック
- `FrozenStateEffect` (RARE) - 冷凍状態
- `BackwardWalkingEffect` (COMMON) - 逆方向歩行
- `ToolAddictionEffect` (COMMON) - 道具中毒
- `MetalAllergyEffect` (COMMON) - 金属アレルギー
- `GravitySicknessEffect` (COMMON) - 重力酔い
- `HyperacusisEffect` (COMMON) - 音響過敏症
- `NumberAmnesiaEffect` (COMMON) - 数字忘却症
- `SpellFailureEffect` (COMMON) - 呪文詠唱失敗

### FinalUnluckyEffects.java から移行予定
- `EvaporationEffect` (RARE) - 蒸発効果
- `PetrificationEffect` (RARE) - 石化の始まり
- `DoubleVisionEffect` (COMMON) - 重複視覚
- `ClockMalfunctionEffect` (COMMON) - 時計故障
- `ShadowBindingEffect` (RARE) - 影の束縛
- `OxygenDeprivationEffect` (COMMON) - 酸素欠乏
- `RadioInterferenceEffect` (COMMON) - 電波障害
- `MagneticAnomalyEffect` (RARE) - 磁力異常
- `PhantomPainEffect` (COMMON) - 幻影の痛み
- `ItemShuffleEffect` (RARE) - アイテムシャッフル
- `SoundSpamEffect` (RARE) - 音響スパム
- `FakeDamageEffect` (COMMON) - 偽ダメージ

## 🏗️ 推奨実装手順

### Phase 2: 段階的移行
1. **COMMON**エフェクトを優先的に個別ファイル化
2. **RARE**エフェクトの複雑な実装を改善
3. **QuickDebuff**系の統合と最適化

### Phase 3: 構造改善
1. レアリティ別ディレクトリ整理
   ```
   effects/unlucky/
   ├── common/
   ├── uncommon/
   ├── rare/
   └── individual/ (移行完了後削除)
   ```

2. 自動登録システムの完全活用
3. 複合ファイルの段階的削除

## 💡 メリット確認済み

### 開発効率
- ✅ 個別編集・デバッグの容易性
- ✅ IDE検索・ナビゲーション改善
- ✅ Git履歴管理の向上

### コード品質
- ✅ @EffectRegistrationによる統一性
- ✅ 自動登録システムの恩恵
- ✅ カテゴリ分類による整理

### 保守性
- ✅ ファイル間依存の削減
- ✅ 単体テスト作成の容易性
- ✅ 将来的な拡張性向上

## 🎯 最終目標

全エフェクトの個別ファイル化により、保守性とスケーラビリティを最大化し、今後のエフェクト追加・修正を効率化する。