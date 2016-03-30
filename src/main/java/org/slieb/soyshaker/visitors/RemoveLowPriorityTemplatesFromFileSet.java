package org.slieb.soyshaker.visitors;

import com.google.common.base.Preconditions;
import com.google.template.soy.basetree.NodeVisitor;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateDelegateNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class RemoveLowPriorityTemplatesFromFileSet
        implements NodeVisitor<SoyFileSetNode, SoyFileSetNode> {

    private final Map<TemplateDelegateNode.DelTemplateKey, Integer> map;
    private final Function<String, Integer> priorityCalculator;
    private Integer currentFilePriority;

    public RemoveLowPriorityTemplatesFromFileSet(final Function<String, Integer> priorityCalculator,
                                                 final Map<TemplateDelegateNode.DelTemplateKey, Integer> map) {
        this.priorityCalculator = priorityCalculator;
        this.map = map;
    }

    protected void visitSoyFileNode(final SoyFileNode node) {
        currentFilePriority = priorityCalculator.apply(node.getDelPackageName());
        if (currentFilePriority > 0) {
            new ArrayList<>(node.getChildren())
                    .stream()
                    .filter(child -> child.getKind().equals(SoyNode.Kind.TEMPLATE_DELEGATE_NODE))
                    .map(TemplateDelegateNode.class::cast)
                    .forEach(this::visitTemplateDelegateNode);
        }
    }

    protected void visitTemplateDelegateNode(final TemplateDelegateNode node) {
        Preconditions.checkNotNull(currentFilePriority);
        final TemplateDelegateNode.DelTemplateKey delTemplateKey = node.getDelTemplateKey();
        if (currentFilePriority > 0 && map.containsKey(delTemplateKey) && map.get(delTemplateKey) > currentFilePriority) {
            node.getParent().removeChild(node);
        }
    }

    @Override
    public SoyFileSetNode exec(final SoyFileSetNode node) {
        node.getChildren().forEach(this::visitSoyFileNode);
        return node;
    }
}
