package org.slieb.soyshaker;

import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.soytree.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoyFileSetToContentMapVisitor extends AbstractSoyNodeVisitor<Map<SourceLocation, String>> {

    private final Map<SourceLocation, String> contentMap;

    public SoyFileSetToContentMapVisitor() {contentMap = new ConcurrentHashMap<>();}

    @Override
    protected void visitSoyFileNode(final SoyFileNode node) {
        contentMap.put(node.getSourceLocation(), node.toSourceString());
    }

    @Override
    protected void visitSoyFileSetNode(final SoyFileSetNode node) {
        visitChildren((ParentNode<SoyFileNode>) node);
    }

    @Override
    public Map<SourceLocation, String> exec(final SoyNode node) {
        visit(node);
        return contentMap;
    }
}
