package logoparsing;

import org.antlr.v4.runtime.tree.ParseTree;
import logoparsing.LogoParser.*;

import java.util.List;

public class Function extends Procedure {
    private List<String> params;
    private ParseTree instructions;
    private ExpContext returnExp;

    public Function() {}

    public Function(Procedure p, ExpContext returnExp) {
        super(p);
        this.returnExp = returnExp;
    }

    public ExpContext getReturnExp() {
        return returnExp;
    }

    public void setReturnExp(ExpContext returnExp) {
        this.returnExp = returnExp;
    }
}
