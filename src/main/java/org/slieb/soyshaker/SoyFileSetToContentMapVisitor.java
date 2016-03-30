package org.slieb.soyshaker;

import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.basetree.NodeVisitor;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoyFileSetToContentMapVisitor implements NodeVisitor<SoyFileSetNode, Map<SourceLocation, String>> {

    private final Map<SourceLocation, String> contentMap;

    public SoyFileSetToContentMapVisitor() {contentMap = new ConcurrentHashMap<>();}

    private void visitSoyFileNode(final SoyFileNode node) {
        contentMap.put(node.getSourceLocation(), node.toSourceString());
    }

    @Override
    public Map<SourceLocation, String> exec(final SoyFileSetNode node) {
        node.getChildren().forEach(this::visitSoyFileNode);
        return contentMap;
    }
}
