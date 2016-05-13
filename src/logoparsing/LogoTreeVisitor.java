package logoparsing;

import javafx.scene.Group;
import logogui.Log;
import logogui.Traceur;
import logoparsing.LogoParser.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LogoTreeVisitor extends LogoBaseVisitor<Integer> {
    private Traceur traceur;
    private ParseTreeProperty<Integer> atts = new ParseTreeProperty<>();
    private List<Integer> loopIndex = new ArrayList<>();
    private Map<String, Procedure> procedures = new HashMap<>();
    // we need to keep track of the procedure we are creating, when exploring its parameters node
    private Procedure currentProcedure = null;

    //private SymTable symTable = new SymTable();
    // stack of SymTable
    private Stack<SymTable> symTableStack = new Stack<>();

    public LogoTreeVisitor() {
        super();

        // push a new table of symbols
        symTableStack.push(new SymTable());
    }

    public void initialize(Group g) {
        traceur = new Traceur();
        traceur.setGraphics(g);
    }

    public void setAttValue(ParseTree node, int value) {
        atts.put(node, value);
    }

    public int getAttValue(ParseTree node) {
        return atts.get(node);
    }

    @Override
    public Integer visitDeclaration(DeclarationContext ctx) {
        String name = ctx.ID().getText();
        ParseTree instr = ctx.liste_instructions();

        currentProcedure = new Procedure();
        currentProcedure.setInstructions(instr);

        visit(ctx.liste_params());

        procedures.put(name, new Procedure(currentProcedure));

        currentProcedure = null; // reset
        Log.appendnl("visitDeclaration ("+name+")");
        return 0;
    }

    @Override
    public Integer visitListe_params(Liste_paramsContext ctx) {
        List<String> params = ctx.ID().stream().map(TerminalNode::getText).collect(Collectors.toList());

        currentProcedure.setParams(params);

        return 0;
    }

    @Override
    public Integer visitProcedureCall(ProcedureCallContext ctx) {
        // get the procedure
        String procedureName = ctx.ID().getText();
        Procedure toCall = procedures.get(procedureName);
        if (toCall == null)
            return 1;

        // check number of params
        List<Integer> parameterValues = new ArrayList<>();
        for (ExpContext expCtx : ctx.exp()) {
            if (visit(expCtx) != 0) {
                return 1;
            }
            parameterValues.add(getAttValue(expCtx));
        }
        if (parameterValues.size() != toCall.getParams().size()) {
            Log.appendnl("visitProcedureCall ("+procedureName+") : Error : invalid number of argument");
            return 1;
        }
        
        // copy current symTable
        SymTable symTable = new SymTable(symTableStack.peek());
        // add the parameters
        for (int i=0; i<parameterValues.size(); i++) {
            symTable.donne(toCall.getParams().get(i), parameterValues.get(i));
        }
         // and push
        symTableStack.push(symTable);

        // execute procedure
        visit(toCall.getInstructions());
        
        // remove symTable from the stack
        symTableStack.pop();
        
        Log.appendnl("visitProcedureCall ("+procedureName+")");
        return 0;
    }

    @Override
    public Integer visitHasard(HasardContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }

        int max = getAttValue(ctx.exp());
        int hvalue = ThreadLocalRandom.current().nextInt(0, max + 1);
        setAttValue(ctx, hvalue);
        Log.appendnl("visitHasard (value=" + hvalue + ")");
        return 0;
    }

    @Override
    public Integer visitDonne(DonneContext ctx) {
        if (visit(ctx.exp()) != 0) {
            return 1;
        }

        symTableStack.peek().donne(ctx.ID().getText(), getAttValue(ctx.exp()));

        Log.appendnl("visitDonne : " + ctx.ID().getText() + " <- " + getAttValue(ctx.exp()));
        return 0;
    }

    @Override
    public Integer visitId(IdContext ctx) {
        try {
            setAttValue(ctx, symTableStack.peek().valueOf(ctx.ID().getText()));
        } catch (RuntimeException re) {
            Log.appendnl(re.toString());
            return 1;
        }

        Log.appendnl("visitId");
        return 0;
    }

    @Override
    public Integer visitArule(AruleContext ctx) {
        if (visit(ctx.atom()) != 0) {
            return 1;
        }

        setAttValue(ctx, getAttValue(ctx.atom()));
        Log.appendnl("visitAtom");
        return 0;
    }

    @Override
    public Integer visitMult(MultContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        // node is either a multiplication or a division
        boolean isMult = ctx.getChild(1).getText().equals("*");

        // get children's values
        int lChild = getAttValue(ctx.exp(0));
        int rChild = getAttValue(ctx.exp(1));

        // compute requested arithmetic operation
        setAttValue(ctx, isMult ? lChild * rChild : lChild / rChild);

        Log.appendnl("visitMult");

        // return value 0 : "all is well that ends well"
        return 0;
    }

    @Override
    public Integer visitSum(SumContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        // node is either an addition or a subtraction
        boolean isAdd = ctx.getChild(1).getText().equals("+");

        // get children's values
        int lChild = getAttValue(ctx.exp(0));
        int rChild = getAttValue(ctx.exp(1));

        // compute requested arithmetic operation
        setAttValue(ctx, isAdd ? lChild + rChild : lChild - rChild);

        Log.appendnl("visitSum");

        // return value 0 : "all is well that ends well"
        return 0;
    }

    @Override
    public Integer visitParent(ParentContext ctx) {
        if (visit(ctx.exp()) != 0) {
            return 1;
        }
        setAttValue(ctx, getAttValue(ctx.exp()));
        Log.appendnl("visitParent");
        return 0;
    }

    @Override
    public Integer visitNeg(LogoParser.NegContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }

        // get children's values
        int child = getAttValue(ctx.exp());
        setAttValue(ctx, child == 0 ? 1 : 0);
        Log.appendnl("visitNeg");

        // return value 0 : "all is well that ends well"
        return 0;
    }

    @Override
    public Integer visitTest(LogoParser.TestContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        // node is either an addition or a subtraction
        String op = ctx.getChild(1).getText();

        // get children's values
        int lChild = getAttValue(ctx.exp(0));
        int rChild = getAttValue(ctx.exp(1));

        boolean result = false;

        switch (op) {
            case "==":
                result = lChild == rChild;
                break;
            case ">=":
                result = lChild >= rChild;
                break;
            case "<=":
                result = lChild <= rChild;
                break;
            case ">":
                result = lChild > rChild;
                break;
            case "<":
                result = lChild < rChild;
                break;
            case "!=":
                result = lChild != rChild;
                break;
        }

        // compute requested arithmetic operation
        setAttValue(ctx, result ? 1 : 0);

        Log.appendnl("visitTest");

        // return value 0 : "all is well that ends well"
        return 0;
    }

    @Override
    public Integer visitInt(IntContext ctx) {
        String intText = ctx.INT().getText();
        setAttValue(ctx, Integer.valueOf(intText));
        Log.appendnl("visitInt");
        return 0;
    }

    @Override
    public Integer visitAv(AvContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        traceur.avance(getAttValue(ctx.exp()));
        Log.appendnl("visitAv");
        return 0;
    }

    @Override
    public Integer visitTd(TdContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        traceur.td(getAttValue(ctx.exp()));
        Log.appendnl("visitTd");
        return 0;
    }

    @Override
    public Integer visitTg(TgContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        traceur.tg(getAttValue(ctx.exp()));
        Log.appendnl("visitTg");
        return 0;
    }

    @Override
    public Integer visitLc(LcContext ctx) {
        traceur.lc();
        Log.appendnl("visitLc");
        return 0;
    }

    @Override
    public Integer visitBc(BcContext ctx) {
        traceur.bc();
        Log.appendnl("visitBc");
        return 0;
    }

    @Override
    public Integer visitVe(VeContext ctx) {
        traceur.ve();
        Log.appendnl("visitVe");
        return 0;
    }

    @Override
    public Integer visitRe(ReContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        traceur.recule(getAttValue(ctx.exp()));
        Log.appendnl("visitRe");
        return 0;
    }

    @Override
    public Integer visitFpos(FposContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        traceur.fpos(getAttValue(ctx.exp(0)), getAttValue(ctx.exp(1)));
        Log.appendnl("visitFpos");
        return 0;
    }

    @Override
    public Integer visitFcc(FccContext ctx) {
        if (visitChildren(ctx) != 0) {
            return 1;
        }
        traceur.fcc(getAttValue(ctx.exp()));
        Log.appendnl("visitFcc");
        return 0;
    }

    @Override
    public Integer visitRepete(RepeteContext ctx) {
        if (visit(ctx.exp()) != 0) {
            return 1;
        }
        int n = getAttValue(ctx.exp());
        int index = loopIndex.size();
        loopIndex.add(1);
        for (int i = 0; i < n; i++) {
            if (visit(ctx.liste_instructions()) != 0) {
                return 1;
            }
            loopIndex.set(index, loopIndex.get(index) + 1);
        }
        loopIndex.remove(loopIndex.size() - 1);
        Log.appendnl("visitRepete");
        return 0;
    }

    @Override
    public Integer visitSi(SiContext ctx) {
        if (visit(ctx.exp()) != 0) {
            return 1;
        }

        if (getAttValue(ctx.exp()) != 0) {
            if (visit(ctx.liste_instructions(0)) != 0) {
                return 1;
            }
        } else if (ctx.liste_instructions().size() >= 2) {
            if (visit(ctx.liste_instructions(1)) != 0) {
                return 1;
            }
        }

        Log.appendnl("visitSi");
        return 0;
    }

    @Override
    public Integer visitTantque(TantqueContext ctx) {
        if (visit(ctx.exp()) != 0) {
            return 1;
        }

        while (getAttValue(ctx.exp()) != 0) {
            // TODO comprendre ce qu'il se passe avec le retour de visitChildren()
            if (visit(ctx.liste_instructions()) != 0) {
                return 1;
            }
            if (visit(ctx.exp()) != 0) {
                return 1;
            }
        }

        Log.appendnl("visitTantque");
        return 0;
    }

    @Override
    public Integer visitLoop(LoopContext ctx) {
        if (loopIndex.size() > 0) {
            setAttValue(ctx, loopIndex.get(loopIndex.size() - 1));
            Log.appendnl("visitLoop");
            return 0;
        } else {
            return 1;
        }
    }
}
