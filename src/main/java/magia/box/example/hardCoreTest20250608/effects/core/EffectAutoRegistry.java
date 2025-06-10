package magia.box.example.hardCoreTest20250608.effects.core;

import magia.box.example.hardCoreTest20250608.effects.EffectRegistry;
import magia.box.example.hardCoreTest20250608.effects.LuckyEffect;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * エフェクト自動登録システム
 * アノテーション付きエフェクトクラスを自動検出・登録
 */
public class EffectAutoRegistry {
    
    private final JavaPlugin plugin;
    private final Logger logger;
    
    public EffectAutoRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }
    
    /**
     * 指定パッケージ内のエフェクトを自動スキャン・登録
     */
    public void scanAndRegister(EffectRegistry effectRegistry, String packageName) {
        logger.info("エフェクト自動登録を開始します: " + packageName);
        
        try {
            Set<Class<?>> annotatedClasses = findAnnotatedClasses(packageName);
            
            int successCount = 0;
            int skipCount = 0;
            int errorCount = 0;
            
            for (Class<?> clazz : annotatedClasses) {
                try {
                    EffectRegistration annotation = clazz.getAnnotation(EffectRegistration.class);
                    
                    // 無効なエフェクトはスキップ
                    if (!annotation.enabled()) {
                        logger.info("エフェクトが無効のためスキップ: " + clazz.getSimpleName());
                        skipCount++;
                        continue;
                    }
                    
                    // LuckyEffectインターフェースを実装しているかチェック
                    if (!LuckyEffect.class.isAssignableFrom(clazz)) {
                        logger.warning("LuckyEffectを実装していないため無視: " + clazz.getSimpleName());
                        errorCount++;
                        continue;
                    }
                    
                    // エフェクトインスタンスを作成
                    LuckyEffect effect = createEffectInstance(clazz);
                    if (effect != null) {
                        effectRegistry.registerEffect(annotation.id(), effect);
                        logger.info(String.format("エフェクト登録成功: %s [%s] (%s)", 
                            annotation.id(), annotation.rarity(), clazz.getSimpleName()));
                        successCount++;
                    } else {
                        errorCount++;
                    }
                    
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "エフェクト登録エラー: " + clazz.getSimpleName(), e);
                    errorCount++;
                }
            }
            
            logger.info(String.format("エフェクト自動登録完了: 成功=%d, スキップ=%d, エラー=%d", 
                successCount, skipCount, errorCount));
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "エフェクト自動登録システムエラー", e);
        }
    }
    
    /**
     * 指定パッケージ内でEffectRegistrationアノテーションが付いたクラスを手動スキャン
     */
    private Set<Class<?>> findAnnotatedClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        
        try {
            ClassLoader classLoader = plugin.getClass().getClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                
                if (resource.getProtocol().equals("file")) {
                    // ファイルシステムからスキャン（開発環境）
                    File directory = new File(resource.getFile());
                    classes.addAll(findClassesInDirectory(directory, packageName));
                } else if (resource.getProtocol().equals("jar")) {
                    // JARファイルからスキャン（本番環境）
                    classes.addAll(findClassesInJar(resource, packageName));
                }
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "クラススキャンエラー", e);
        }
        
        return classes;
    }
    
    /**
     * ディレクトリ内のクラスをスキャン
     */
    private Set<Class<?>> findClassesInDirectory(File directory, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(EffectRegistration.class)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // 無視（クラスが見つからない場合）
                }
            }
        }
        
        return classes;
    }
    
    /**
     * JARファイル内のクラスをスキャン
     */
    private Set<Class<?>> findClassesInJar(URL jarUrl, String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        
        try {
            String jarPath = jarUrl.getPath().substring(5, jarUrl.getPath().indexOf("!"));
            JarFile jar = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jar.entries();
            
            String packagePath = packageName.replace('.', '/');
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                
                if (name.startsWith(packagePath) && name.endsWith(".class")) {
                    String className = name.replace('/', '.').substring(0, name.length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(EffectRegistration.class)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        // 無視（クラスが見つからない場合）
                    }
                }
            }
            
            jar.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "JARスキャンエラー", e);
        }
        
        return classes;
    }
    
    /**
     * エフェクトインスタンスを作成
     */
    @SuppressWarnings("unchecked")
    private LuckyEffect createEffectInstance(Class<?> clazz) {
        try {
            // JavaPluginを引数とするコンストラクタを探す
            Constructor<?> constructor = clazz.getConstructor(JavaPlugin.class);
            return (LuckyEffect) constructor.newInstance(plugin);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "エフェクトインスタンス作成エラー: " + clazz.getSimpleName(), e);
            return null;
        }
    }
    
    /**
     * 登録されたエフェクトの統計情報を出力
     */
    public void printRegistrationStats(EffectRegistry effectRegistry) {
        logger.info("=== エフェクト登録統計 ===");
        logger.info("ラッキーエフェクト数: " + effectRegistry.getLuckyEffectsCount());
        logger.info("アンラッキーエフェクト数: " + effectRegistry.getUnluckyEffectsCount());
        logger.info("合計エフェクト数: " + effectRegistry.getTotalEffectsCount());
        logger.info("========================");
    }
}