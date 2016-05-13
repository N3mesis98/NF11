package logoparsing;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * Created by JeanV on 13/05/2016.
 */
public class Procedure {
    private List<String> params;
    private ParseTree instructions;

    public Procedure() {

    }

    public Procedure(Procedure p) {
        this.params = p.params;
        this.instructions = p.instructions;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public ParseTree getInstructions() {
        return instructions;
    }

    public void setInstructions(ParseTree instructions) {
        this.instructions = instructions;
    }
}