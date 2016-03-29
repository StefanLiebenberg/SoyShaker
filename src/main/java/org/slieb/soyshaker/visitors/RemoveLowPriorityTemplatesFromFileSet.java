package org.slieb.soyshaker.visitors;

import com.google.common.base.Preconditions;
import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.soytree.*;

import java.util.List;
import java.util.Map;

import static org.slieb.soyshaker.visitors.DelegateShaker.calculatePriority;

class RemoveLowPriorityTemplatesFromFileSet extends AbstractSoyNodeVisitor<Object> {

    private final List<String> prioritisedPackages;
    private final Map<TemplateDelegateNode.DelTemplateKey, Integer> map;
    private Integer currentFilePriority;

    RemoveLowPriorityTemplatesFromFileSet(final List<String> prioritisedPackages,
                                          final Map<TemplateDelegateNode.DelTemplateKey, Integer> map) {
        this.prioritisedPackages = prioritisedPackages;
        this.map = map;
    }

    @Override
    protected void visitSoyFileNode(final SoyFileNode node) {
        currentFilePriority = calculatePriority(prioritisedPackages, node.getDelPackageName());
        if (currentFilePriority > 0) {
            visitChildrenAllowingConcurrentModification((ParentNode<TemplateNode>) node);
        }
    }

    @Override
    protected void visitTemplateDelegateNode(final TemplateDelegateNode node) {
        Preconditions.checkNotNull(currentFilePriority);
        final TemplateDelegateNode.DelTemplateKey delTemplateKey = node.getDelTemplateKey();
        if (map.containsKey(delTemplateKey) && map.get(delTemplateKey) > currentFilePriority) {
            node.getParent().removeChild(node);
        }
    }

    @Override
    protected void visitTemplateBasicNode(final TemplateBasicNode node) {

    }

    @Override
    protected void visitSoyFileSetNode(final SoyFileSetNode node) {
        visitChildren((ParentNode<SoyFileNode>) node);
    }
}
