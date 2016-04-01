/*
 * Created on 12 sept. 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package logogui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Traceur {
    private static Traceur instance;
    private Group g;
    private double initx = 300, inity = 300;   // position initiale
    private double posx = initx, posy = inity; // position courante
    private int angle = 90;
    private double teta;
    private boolean drawing = true;
    private Color color = Color.BLACK;


    private void setTeta() {
        teta = Math.toRadians(angle);
    }

    private int toInt(double a) {
        return (int) Math.round(a);
    }


    //----- public members --------------------------

    public Traceur() {
        setTeta();
    }

    public void setGraphics(Group g) {
        this.g = g;
    }

    public void avance(double r) {
        double a = posx + r * Math.cos(teta) ;
        double b = posy - r * Math.sin(teta) ;
        if (drawing) {
            int x1 = toInt(posx);
            int y1 = toInt(posy);
            int x2 = toInt(a);
            int y2 = toInt(b);
            Line line = new Line(x1,y1,x2,y2);
            line.setStroke(color);
            g.getChildren().add(line);
        }
        posx = a;
        posy = b;
    }

    public void recule(double attValue) {
        this.avance(-attValue);
    }

    public void td(double r) {
        angle = (angle - toInt(r)) % 360;
        setTeta();
    }

    public void tg(double r) {
        angle = (angle + toInt(r)) % 360;
        setTeta();
    }

    public void lc() {
        drawing = false;
    }

    public void bc() {
        drawing = true;
    }

    public void ve() {
        g.getChildren().clear();
    }

    public void fpos(double x, double y) {
        posx = x;
        posy = y;
    }

    public void fcc(int colorId) {
        colorId %= 8;
        switch (colorId) {
        case 1:
            color = Color.RED;
            break;
        case 2:
            color = Color.GREEN;
            break;
        case 3:
            color = Color.YELLOW;
            break;
        case 4:
            color = Color.BLUE;
            break;
        case 5:
            color = Color.PURPLE;
            break;
        case 6:
            color = Color.CYAN;
            break;
        case 7:
            color = Color.WHITE;
            break;
        default:
            color = Color.BLACK;
            break;
        }
    }
}
