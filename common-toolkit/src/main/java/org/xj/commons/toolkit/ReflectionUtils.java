package org.xj.commons.toolkit;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author xj
 * @date 2022/5/19 16:26
 */
public class ReflectionUtils {

    // ========================== 反射：类 =========================

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("反射生成对象异常，class:" + clazz.getName());
        }
    }

    /**
     * 返回一个类及父类中所有被某个注解标注的属性
     *
     * @param clazz           类
     * @param annotationClass 注解类型
     * @return clazz类及父类中所有被annotationClass注解标注的属性Field
     */
    public static <T extends Annotation> List<Field> getAnnotationField(Class clazz, Class<T> annotationClass) {
        List<Field> list = MyCollections.newArrayList();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                T annotation = field.getAnnotation(annotationClass);
                if (annotation != null) {
                    list.add(field);
                }
            }
        }
        return list;
    }

    /**
     * 返回一个属性上的某个注解的某个属性值
     *
     * @param field           类的一个属性
     * @param annotationClass 注解类型
     * @param annotationFunc  注解的方法
     * @return 返回field属性上annotationClass注解的annotationFunc属性值
     */
    public static <T extends Annotation, R> R getAnnotationValue(Field field, Class<T> annotationClass, Function<T, R> annotationFunc) {
        T annotation = field.getAnnotation(annotationClass);
        return annotationFunc.apply(annotation);
    }

    /**
     * 返回对象的某属性的值
     *
     * @param field  类的一个属性
     * @param object 类的一个对象
     * @return object的field的值
     */
    public static <T> Object getFieldValue(Field field, T object) {
        field.setAccessible(true);
        Object filedValue;
        try {
            filedValue = field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return filedValue;
    }

    /**
     * 拿到getter方法对应的属性名称
     *
     * @param field getter方法
     */
    public static <T> String getFieldName(Function<T, ?> field) {
        Class<?> clazz = field.getClass();
        try {
            Method method = clazz.getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(field);
            String getterName = lambda.getImplMethodName();
            //首字母大写的fieldName
            String fieldName = getterName.startsWith("get") ?
                    getterName.substring(3) : getterName.startsWith("is") ? getterName.substring(2) : getterName;
            //反射出实现类
            Class<?> implClass = Class.forName(lambda.getImplClass().replaceAll("/", "."));
            //实际上不知道field首字母不是大写（SubGraph实体未必是驼峰，存在例如“A”的字段）。
            //所以遍历，进行全小写比较，找到实际的field对象。
            for (Field declaredField : implClass.getDeclaredFields()) {
                if (fieldName.toLowerCase().equals(declaredField.getName().toLowerCase())) {
                    return declaredField.getName();
                }
            }
            throw new RuntimeException("current property is not exists");
        } catch (Exception e) {
            throw new RuntimeException("current property is not exists");
        }
    }

    /**
     * 返回自己和父类的所有 Declared Field
     *
     * @param clazz 反射的类
     */
    public static Set<Field> getFields(Class clazz) {
        if (Objects.isNull(clazz)) {
            throw new RuntimeException("class不能为空");
        }

        Set<Field> fieldSet = MyCollections.newLinkedHashSet();
        // Object是所有java类(除自己)的父类
        // 采用递归而非循环，是为了让父类的属性先进入有序集合。
        if (clazz.getSuperclass() != Object.class) {
            CollectionUtils.addAll(fieldSet, getFields(clazz.getSuperclass()));
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        CollectionUtils.addAll(fieldSet, declaredFields);
        return fieldSet;
    }

    public static Type getFirstActualTypeOfGenericInterfaces(Class clazz) {
        try {
            return ((ParameterizedTypeImpl) clazz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        } catch (Exception e) {
            return null;
        }
    }

    // =============================== 反射：切点 ================================

//    /**
//     * @param pjp spring aop的pjp.getSignature返回的是MethodSignature
//     * @return
//     */
//    public static Method getMethod(ProceedingJoinPoint pjp) {
//        if (!(pjp.getSignature() instanceof MethodSignature)) {
//            return null;
//        }
//        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
//        return methodSignature.getMethod();
//    }
//
//    public static Type getMethodReturnType(ProceedingJoinPoint pjp) {
//        Method method = getMethod(pjp);
//        return method == null ? null : method.getAnnotatedReturnType().getType();
//    }

    // ===================== 反射：函数式 =====================

    public static SerializedLambda getSerializedLambda(Function f) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = f.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(f);
        method.setAccessible(true);
        return serializedLambda;
    }

    public static String getImplClassName(SerializedLambda serializedLambda) {
        return serializedLambda.getImplClass();
    }

    public static String getImplMethodName(SerializedLambda serializedLambda) {
        return serializedLambda.getImplMethodName();
    }

}
