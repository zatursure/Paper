import com.puppycrawl.tools.checkstyle.api.DetailNode;
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes;
import com.puppycrawl.tools.checkstyle.checks.javadoc.AbstractJavadocCheck;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks that parameter descriptions in Javadoc are aligned.
 */
public final class JavadocAlignParameterDescriptions extends AbstractJavadocCheck {

    @Override
    public int[] getDefaultJavadocTokens() {
        return new int[]{JavadocTokenTypes.JAVADOC};
    }

    @Override
    public void visitJavadocToken(final DetailNode detailNode) {
        final List<DetailNode> params = new ArrayList<>();
        int maxColumn = -1;
        for (final DetailNode child : detailNode.getChildren()) {
            final DetailNode paramLiteralNode = JavadocUtil.findFirstToken(child, JavadocTokenTypes.PARAM_LITERAL);
            if (child.getType() != JavadocTokenTypes.JAVADOC_TAG || paramLiteralNode == null) {
                continue;
            }
            final DetailNode paramDescription = JavadocUtil.getNextSibling(paramLiteralNode, JavadocTokenTypes.DESCRIPTION);
            maxColumn = Math.max(maxColumn, paramDescription.getColumnNumber());
            params.add(paramDescription);
        }

        for (final DetailNode param : params) {
            if (param.getColumnNumber() != maxColumn) {
                final DetailNode paramNameNode = getPreviousSibling(param, JavadocTokenTypes.PARAMETER_NAME);
                this.log(
                    param.getLineNumber(),
                    param.getColumnNumber() - 1,
                    "Param description for %s should start at column %d".formatted(paramNameNode.getText(), maxColumn)
                );
            }
        }
    }

    private static DetailNode getPreviousSibling(final DetailNode node, final int type) {
        DetailNode sibling = JavadocUtil.getPreviousSibling(node);
        while (sibling != null && sibling.getType() != type) {
            sibling = JavadocUtil.getPreviousSibling(sibling);
        }
        return sibling;
    }
}
