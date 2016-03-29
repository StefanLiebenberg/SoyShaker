package org.slieb.soyshaker.visitors;

import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.soytree.*;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

class FindDelegateKeysInSoyFile extends AbstractSoyNodeVisitor<Set<TemplateDelegateNode.DelTemplateKey>> {

    private final Stream.Builder<TemplateDelegateNode.DelTemplateKey> builder;

    FindDelegateKeysInSoyFile() {builder = Stream.builder();}

    @Override
    protected void visitTemplateDelegateNode(final TemplateDelegateNode node) {
        builder.add(node.getDelTemplateKey());
    }

    @Override
    protected void visitTemplateBasicNode(final TemplateBasicNode node) {

    }

    @Override
    protected void visitSoyFileNode(final SoyFileNode node) {
        visitChildren((ParentNode<TemplateNode>) node);
    }

    @Override
    public Set<TemplateDelegateNode.DelTemplateKey> exec(final SoyNode node) {
        visit(node);
        return builder.build().collect(toSet());
    }
}
