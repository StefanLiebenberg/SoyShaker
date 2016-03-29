package org.slieb.soyshaker.visitors;

import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.soytree.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.slieb.soyshaker.visitors.DelegateShaker.calculatePriority;

/**
 * Created by stefan on 3/29/16.
 */
class FindDelegatePriorityMap extends AbstractSoyNodeVisitor<Map<TemplateDelegateNode.DelTemplateKey, Integer>> {

    private final List<String> prioritisedPackages;
    private final Map<TemplateDelegateNode.DelTemplateKey, Integer> priorityMap;

    FindDelegatePriorityMap(final List<String> prioritisedPackages) {
        this.prioritisedPackages = prioritisedPackages;
        this.priorityMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void visitSoyFileNode(final SoyFileNode node) {
        final String delPackageName = node.getDelPackageName();
        final Integer priority = calculatePriority(prioritisedPackages, delPackageName);
        final Set<TemplateDelegateNode.DelTemplateKey> keys = new FindDelegateKeysInSoyFile().exec(node);
        for (final TemplateDelegateNode.DelTemplateKey key : keys) {
            if (!priorityMap.containsKey(key) || priorityMap.get(key) < priority) {
                priorityMap.put(key, priority);
            }
        }
    }

    @Override
    protected void visitSoyFileSetNode(final SoyFileSetNode node) {
        visitChildren((ParentNode<SoyFileNode>) node);
    }

    @Override
    public Map<TemplateDelegateNode.DelTemplateKey, Integer> exec(final SoyNode node) {
        visit(node);
        return priorityMap;
    }
}
