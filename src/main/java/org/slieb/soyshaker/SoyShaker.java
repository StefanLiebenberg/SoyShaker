package org.slieb.soyshaker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.soytree.SoyFileSetNode;
import org.slieb.soyshaker.visitors.DelegateShaker;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.slieb.soyshaker.internal.ParseInvoker.invokeParseMethod;

@SuppressWarnings("WeakerAccess")
@Singleton
public class SoyShaker {

    private final Supplier<SoyFileSet.Builder> builderSupplier;

    @Inject
    public SoyShaker(Provider<SoyFileSet.Builder> builderProvider) {
        this((Supplier<SoyFileSet.Builder>) builderProvider::get);
    }

    public SoyShaker(Supplier<SoyFileSet.Builder> builderSupplier) {
        this.builderSupplier = builderSupplier;
    }

    public SoyShaker() {
        this((Supplier<SoyFileSet.Builder>) SoyFileSet::builder);
    }

    public SoyFileSet shake(final SoyFileSet soyFileSet,
                            final List<String> prioritisedPackages,
                            final SyntaxVersion syntaxVersion) {
        final Map<SourceLocation, String> contentMap = shakeToContentMap(soyFileSet, prioritisedPackages, syntaxVersion);
        final SoyFileSet.Builder builder = builderSupplier.get();
        for (final Map.Entry<SourceLocation, String> entry : contentMap.entrySet()) {
            builder.add(entry.getValue(), entry.getKey().getFilePath());
        }
        return builder.build();
    }

    public SoyFileSet shake(final SoyFileSet soyFileSet,
                            final List<String> prioritisedPackages) {
        return this.shake(soyFileSet, prioritisedPackages, SyntaxVersion.V2_0);
    }

    public Map<SourceLocation, String> shakeToContentMap(
            final SoyFileSet soyFileSet,
            final List<String> prioritisedPackages,
            final SyntaxVersion syntaxVersion) {
        final SoyFileSetNode soyFileSetNode = invokeParseMethod(soyFileSet, syntaxVersion).fileSet();
        new DelegateShaker(prioritisedPackages).exec(soyFileSetNode);
        return new SoyFileSetToContentMapVisitor().exec(soyFileSetNode);
    }
}
