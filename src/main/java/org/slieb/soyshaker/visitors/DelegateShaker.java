package org.slieb.soyshaker.visitors;

import com.google.template.soy.basetree.NodeVisitor;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.TemplateDelegateNode;

import java.util.List;
import java.util.Map;

public class DelegateShaker implements NodeVisitor<SoyFileSetNode, Object> {

    private final List<String> prioritisedPackages;
    
    public DelegateShaker(final List<String> prioritisedPackages) {
        this.prioritisedPackages = prioritisedPackages;
    }

    @Override
    public Object exec(final SoyFileSetNode node) {
        final FindDelegatePriorityMap priorityMapVisitor = new FindDelegatePriorityMap(prioritisedPackages);
        final Map<TemplateDelegateNode.DelTemplateKey, Integer> priorityMap = priorityMapVisitor.exec(node);
        final RemoveLowPriorityTemplatesFromFileSet removeVisitor = new RemoveLowPriorityTemplatesFromFileSet(prioritisedPackages, priorityMap);
        return removeVisitor.exec(node);
    }

    static Integer calculatePriority(final List<String> prioritisedPackages,
                                     final String packageName) {

        if (packageName == null) {
            return 0;
        }

        if (prioritisedPackages == null || prioritisedPackages.isEmpty() || !prioritisedPackages.contains(packageName)) {
            return 1;
        }

        return 1 + (prioritisedPackages.size() - prioritisedPackages.indexOf(packageName));
    }
}

