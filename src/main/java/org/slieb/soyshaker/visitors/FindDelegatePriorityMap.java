package org.slieb.soyshaker.visitors;

import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.soytree.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class FindDelegatePriorityMap extends AbstractSoyNodeVisitor<Map<TemplateDelegateNode.DelTemplateKey, Integer>> {

    private final Map<TemplateDelegateNode.DelTemplateKey, Integer> priorityMap;

    private final Function<String, Integer> priorityCalculator;

    public FindDelegatePriorityMap(final Function<String, Integer> priorityCalculator) {
        this.priorityCalculator = priorityCalculator;
        this.priorityMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void visitSoyFileNode(final SoyFileNode node) {
        final String delPackageName = node.getDelPackageName();
        final Integer priority = priorityCalculator.apply(delPackageName);
        final Set<TemplateDelegateNode.DelTemplateKey> keys = new FindDelegateKeysInSoyFile().exec(node);
        keys.stream()
            .filter(key -> !priorityMap.containsKey(key) || priorityMap.get(key) < priority)
            .forEach(key -> priorityMap.put(key, priority));
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
