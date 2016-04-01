package logoparsing;

import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.Group;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import logogui.Log;
import logogui.Traceur;
import logoparsing.LogoParser.AruleContext;
import logoparsing.LogoParser.AvContext;
import logoparsing.LogoParser.BcContext;
import logoparsing.LogoParser.FccContext;
import logoparsing.LogoParser.FposContext;
import logoparsing.LogoParser.HasardContext;
import logoparsing.LogoParser.IntContext;
import logoparsing.LogoParser.LcContext;
import logoparsing.LogoParser.Liste_instructionsContext;
import logoparsing.LogoParser.MultContext;
import logoparsing.LogoParser.ParentContext;
import logoparsing.LogoParser.ProgrammeContext;
import logoparsing.LogoParser.ReContext;
import logoparsing.LogoParser.SumContext;
import logoparsing.LogoParser.TdContext;
import logoparsing.LogoParser.TgContext;
import logoparsing.LogoParser.VeContext;
import logoparsing.LogoParser.RepeteContext;

public class LogoTreeVisitor extends LogoBaseVisitor<Integer> {

    @Override
    public Integer visitHasard(HasardContext ctx) {
        visitChildren(ctx);

        int max = getAttValue(ctx.exp());
        int hvalue = ThreadLocalRandom.current().nextInt(0, max +1);
        setAttValue(ctx, hvalue);
        Log.appendnl("visitHasard (value=" + hvalue + ")");
        return 0;
    }

    Traceur traceur;
    ParseTreeProperty<Integer> atts = new ParseTreeProperty<Integer>();

    public LogoTreeVisitor() {
        super();
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
    public Integer visitArule(AruleContext ctx) {
        visit(ctx.atom());
        setAttValue(ctx, getAttValue(ctx.atom()));
        Log.appendnl("visitAtom");
        return 0;
    }

    @Override
    public Integer visitMult(MultContext ctx) {
        visitChildren(ctx);
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
        visitChildren(ctx);
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
        visit(ctx.exp());
        setAttValue(ctx, getAttValue(ctx.exp()));
        Log.appendnl("visitParent");
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
        visitChildren(ctx);
        traceur.avance(getAttValue(ctx.exp()));
        Log.appendnl("visitAv");
        return 0;
    }

    @Override
    public Integer visitTd(TdContext ctx) {
        visitChildren(ctx);
        traceur.td(getAttValue(ctx.exp()));
        Log.appendnl("visitTd");
        return 0;
    }

    @Override
    public Integer visitTg(TgContext ctx) {
        visitChildren(ctx);
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
        visitChildren(ctx);
        traceur.recule(getAttValue(ctx.exp()));
        Log.appendnl("visitRe");
        return 0;
    }

    @Override
    public Integer visitFpos(FposContext ctx) {
        visitChildren(ctx);
        traceur.fpos(getAttValue(ctx.exp(0)), getAttValue(ctx.exp(1)));
        Log.appendnl("visitFpos");
        return 0;
    }

    @Override
    public Integer visitFcc(FccContext ctx) {
        visitChildren(ctx);
        traceur.fcc(getAttValue(ctx.exp()));
        Log.appendnl("visitFcc");
        return 0;
    }
    
    @Override
    public Integer visitRepete(RepeteContext ctx) {
        visit(ctx.exp());
        int n = getAttValue(ctx.exp());
        for (int i=0; i<n; i++) {
            visit(ctx.liste_instructions());
        }
        Log.appendnl("visitRepete");
        return 0;
    }
}
