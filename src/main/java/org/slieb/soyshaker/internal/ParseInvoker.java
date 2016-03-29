package org.slieb.soyshaker.internal;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyFileSetParser;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.types.SoyTypeRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ParseInvoker {

    private ParseInvoker() {}

    private static final Method RESET_REPORTER_METHOD, PARSE_METHOD;
    private static final Field TYPE_REGISTRY_FIELD, SOY_FUNCTIONS_MAP_FIELD;

    static {
        try {
            RESET_REPORTER_METHOD = SoyFileSet.class.getDeclaredMethod("resetErrorReporter");
            RESET_REPORTER_METHOD.setAccessible(true);
            PARSE_METHOD = SoyFileSet.class.getDeclaredMethod("parse", SyntaxVersion.class, boolean.class, boolean.class, SoyTypeRegistry.class,
                                                              ImmutableMap.class);
            PARSE_METHOD.setAccessible(true);

            SOY_FUNCTIONS_MAP_FIELD = SoyFileSet.class.getDeclaredField("soyFunctionMap");
            SOY_FUNCTIONS_MAP_FIELD.setAccessible(true);
            TYPE_REGISTRY_FIELD = SoyFileSet.class.getDeclaredField("typeRegistry");
            TYPE_REGISTRY_FIELD.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static SoyFileSetParser.ParseResult invokeParseMethod(SoyFileSet fileSet,
                                                                 SyntaxVersion syntaxVersion) {
        try {
            RESET_REPORTER_METHOD.invoke(fileSet);
            final SoyTypeRegistry typeRegistry = SoyTypeRegistry.class.cast(TYPE_REGISTRY_FIELD.get(fileSet));
            final ImmutableMap soyFunctionMap = ImmutableMap.class.cast(SOY_FUNCTIONS_MAP_FIELD.get(fileSet));
            return SoyFileSetParser.ParseResult.class.cast(PARSE_METHOD.invoke(fileSet, syntaxVersion, true, true, typeRegistry, soyFunctionMap));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static SoyFileSetParser.ParseResult invokeParseMethod(SoyFileSet fileSet) {
        return invokeParseMethod(fileSet, SyntaxVersion.V2_0);
    }
}
