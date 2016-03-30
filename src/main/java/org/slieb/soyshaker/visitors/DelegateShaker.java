package org.slieb.soyshaker.visitors;

import com.google.template.soy.basetree.NodeVisitor;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.TemplateDelegateNode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DelegateShaker implements NodeVisitor<SoyFileSetNode, SoyFileSetNode> {

    private final List<String> prioritisedPackages;

    public DelegateShaker(final List<String> prioritisedPackages) {
        this.prioritisedPackages = prioritisedPackages;
    }

    @Override
    public SoyFileSetNode exec(final SoyFileSetNode node) {
        final Function<String, Integer> calculator = this::calculatePriority;
        final Map<TemplateDelegateNode.DelTemplateKey, Integer> priorityMap = new FindDelegatePriorityMap(calculator).exec(node);
        return new RemoveLowPriorityTemplatesFromFileSet(calculator, priorityMap).exec(node);
    }

    private Integer calculatePriority(String packageName) {

        if (packageName == null) {
            return 0;
        }

        if (prioritisedPackages == null || prioritisedPackages.isEmpty() || !prioritisedPackages.contains(packageName)) {
            return 1;
        }

        return 1 + (prioritisedPackages.size() - prioritisedPackages.indexOf(packageName));
    }
}

