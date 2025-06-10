package magia.box.example.hardCoreTest20250608.effects.core;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * エフェクト自動登録用アノテーション
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EffectRegistration {
    
    /**
     * エフェクトID（一意識別子）
     */
    String id();
    
    /**
     * エフェクトタイプ
     */
    EffectType type();
    
    /**
     * エフェクトレアリティ
     */
    EffectRarity rarity();
    
    /**
     * 有効かどうか（false の場合は登録されない）
     */
    boolean enabled() default true;
    
    /**
     * 説明文
     */
    String description() default "";
    
    /**
     * カテゴリ
     */
    String category() default "general";
}