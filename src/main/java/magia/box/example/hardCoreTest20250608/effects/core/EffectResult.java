package magia.box.example.hardCoreTest20250608.effects.core;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * エフェクト実行結果を統一的に管理するクラス
 */
public class EffectResult {
    private final boolean success;
    private final String message;
    private final List<String> details;
    private final Duration duration;
    private final String effectId;
    
    private EffectResult(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.details = new ArrayList<>(builder.details);
        this.duration = builder.duration;
        this.effectId = builder.effectId;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public List<String> getDetails() {
        return new ArrayList<>(details);
    }
    
    public Duration getDuration() {
        return duration;
    }
    
    public String getEffectId() {
        return effectId;
    }
    
    /**
     * ビルダークラス
     */
    public static class Builder {
        private boolean success = true;
        private String message = "";
        private List<String> details = new ArrayList<>();
        private Duration duration = Duration.ZERO;
        private String effectId = "";
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder addDetail(String detail) {
            this.details.add(detail);
            return this;
        }
        
        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }
        
        public Builder effectId(String effectId) {
            this.effectId = effectId;
            return this;
        }
        
        public EffectResult build() {
            return new EffectResult(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 成功結果を簡単に作成
     */
    public static EffectResult success(String message) {
        return builder()
            .success(true)
            .message(message)
            .build();
    }
    
    /**
     * 失敗結果を簡単に作成
     */
    public static EffectResult failure(String message) {
        return builder()
            .success(false)
            .message(message)
            .build();
    }
    
    @Override
    public String toString() {
        return String.format("EffectResult{success=%s, message='%s', effectId='%s', duration=%s}", 
            success, message, effectId, duration);
    }
}