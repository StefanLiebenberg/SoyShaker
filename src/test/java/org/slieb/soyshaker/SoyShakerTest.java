package org.slieb.soyshaker;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.template.soy.SoyFileSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;

public class SoyShakerTest {

    public String render(List<String> packages) {
        final Supplier<SoyFileSet.Builder> builderSupplier = SoyFileSet::builder;
        SoyFileSet.Builder builder = builderSupplier.get();
        builder.add(getClass().getResource("/templates/base.soy"));
        builder.add(getClass().getResource("/templates/alpha.soy"));
        builder.add(getClass().getResource("/templates/beta.soy"));
        final SoyFileSet baseFileSet = builder.build();
        final SoyShaker shaker = new SoyShaker(builderSupplier);
        final SoyFileSet fileSet = shaker.shake(baseFileSet, packages);
        return fileSet.compileToTofu().newRenderer("templates.base.Foo")
                      .setActiveDelegatePackageNames(Sets.newHashSet(packages))
                      .render();
    }

    @Test
    public void testBaseTemplate() throws Exception {
        Assert.assertEquals("Base Template", render(Lists.newArrayList()));
    }

    @Test
    public void testAlphaTemplate() throws Exception {
        Assert.assertEquals("Alpha Template", render(Lists.newArrayList("alpha", "beta")));
    }

    @Test
    public void testBetaTemplate() throws Exception {
        Assert.assertEquals("Beta Template", render(Lists.newArrayList("beta", "alpha")));
    }
}