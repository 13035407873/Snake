package cn.xuemcu.snake;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 朱红晨 on 2017/8/24.
 */

public class Snake {

    // Points of the snake
    public List<Point> snake;

    // Length of the snake (euclidean distance)
    private double snakelength=0;

    // size of the image (and of the 2 arrays below)
    private int width=0,height=0;

    // gradient value (modulus)
    private int[][] gradient;

    // gradient flow (modulus)
    private int[][] flow;

    // 3x3 neighborhood used to compute energies
    private double[][] e_uniformity = new double[3][3];
    private double[][] e_curvature  = new double[3][3];
    private double[][] e_flow       = new double[3][3];
    private double[][] e_inertia    = new double[3][3];

    // auto add/remove points to the snake
    // according to distance between points
    public boolean AUTOADAPT=true;
    public int AUTOADAPT_LOOP=10;
    public int AUTOADAPT_MINLEN=8;
    public int AUTOADAPT_MAXLEN=16;

    // maximum number of iterations (if no convergence)
    public int MAXITERATION = 1000;

    // GUI feedback
    public boolean SHOWANIMATION = true;
    public MainActivity SNAKEGUI = null;

    // coefficients for the 4 energy functions
    public double alpha=1.1, beta=1.2, gamma=1.5, delta=3.0;

    List<Point> snake_object = new ArrayList<Point>();


    // alpha = coefficient for uniformity (high => force equals distance between points)
    // beta  = coefficient for curvature  (high => force smooth curvature)
    // gamma  = coefficient for flow      (high => force gradient attraction)
    // delta  = coefficient for inertia  (high => get stuck to gradient)

    /**
     * Constructor
     *
     * @param width,height size of the image and of the 2 following arrays
     * @param gradient gradient (modulus)
     * @param flow gradient flow (modulus)
     * @param points initial points of the snake
     */
    public Snake(int width, int height, int[][] gradient, int[][] flow, Point...points) {
        this.snake = new ArrayList<Point>(Arrays.asList(points));
        this.gradient = gradient;
        this.flow = flow;
        this.width = width;
        this.height = height;
    }

    /**
     * main loop
     *
     * @return the number of iterations performed
     */
    public int loop() {
        int loop=0;

        while(step() && loop<MAXITERATION) {
            // auto adapt the number of points in the snake
            if (AUTOADAPT && (loop%AUTOADAPT_LOOP)==0) {
                removeOverlappingPoints(AUTOADAPT_MINLEN);
                addMissingPoints(AUTOADAPT_MAXLEN);
            }
            loop++;

            if (SHOWANIMATION && SNAKEGUI!=null)
                SNAKEGUI.display();
        }

        // rebuild using spline interpolation
        if (AUTOADAPT) rebuild(AUTOADAPT_MAXLEN);

        return loop;
    }

    /**
     * update the position of each point of the snake
     *
     * @return true if the snake has changed, otherwise false.
     */
    private boolean step() {
        boolean changed=false;
        Point p = new Point(0,0);

        // compute length of original snake (used by method: f_uniformity)
        this.snakelength = getsnakelength();

        // compute the new snake
        List<Point> newsnake = new ArrayList<Point>(snake.size());

        Point p1=new Point(297,192);
        snake_object.add(p1);
        Point p2=new Point(300,188);
        snake_object.add(p2);
        Point p3=new Point(303,183);
        snake_object.add(p3);
        Point p4=new Point(304,178);
        snake_object.add(p4);
        Point p5=new Point(305,173);
        snake_object.add(p5);
        Point p6=new Point(304,168);
        snake_object.add(p6);
        Point p7=new Point(302,163);
        snake_object.add(p7);
        Point p8=new Point(300,159);
        snake_object.add(p8);
        Point p9=new Point(298,154);
        snake_object.add(p9);
        Point p10=new Point(294,150);
        snake_object.add(p10);
        Point p11=new Point(290,146);
        snake_object.add(p11);
        Point p12=new Point(286,143);
        snake_object.add(p12);
        Point p13=new Point(281,141);
        snake_object.add(p13);
        Point p14=new Point(276,139);
        snake_object.add(p14);
        Point p15=new Point(271,137);
        snake_object.add(p15);
        Point p16=new Point(266,136);
        snake_object.add(p16);
        Point p17=new Point(255,132);
        snake_object.add(p17);
        Point p18=new Point(250,130);
        snake_object.add(p18);
        Point p19=new Point(245,129);
        snake_object.add(p19);
        Point p20=new Point(240,127);
        snake_object.add(p20);
        Point p21=new Point(235,125);
        snake_object.add(p21);
        Point p22=new Point(229,123);
        snake_object.add(p22);
        Point p23=new Point(224,121);
        snake_object.add(p23);
        Point p24=new Point(219,119);
        snake_object.add(p24);
        Point p25=new Point(214,116);
        snake_object.add(p25);
        Point p26=new Point(209,114);
        snake_object.add(p26);
        Point p27=new Point(204,113);
        snake_object.add(p27);
        Point p28=new Point(198,111);
        snake_object.add(p28);
        Point p29=new Point(193,110);
        snake_object.add(p29);
        Point p30=new Point(187,109);
        snake_object.add(p30);
        Point p31=new Point(182,107);
        snake_object.add(p31);
        Point p32=new Point(176,106);
        snake_object.add(p32);
        Point p33=new Point(171,105);
        snake_object.add(p33);
        Point p34=new Point(165,104);
        snake_object.add(p34);
        Point p35=new Point(160,103);
        snake_object.add(p35);
        Point p36=new Point(154,102);
        snake_object.add(p36);
        Point p37=new Point(149,102);
        snake_object.add(p37);
        Point p38=new Point(143,101);
        snake_object.add(p38);
        Point p39=new Point(138,100);
        snake_object.add(p39);
        Point p40=new Point(132,100);
        snake_object.add(p40);
        Point p41=new Point(126,99);
        snake_object.add(p41);
        Point p42=new Point(121,99);
        snake_object.add(p42);
        Point p43=new Point(116,98);
        snake_object.add(p43);
        Point p44=new Point(110,98);
        snake_object.add(p44);
        Point p45=new Point(105,99);
        snake_object.add(p45);
        Point p46=new Point(99,99);
        snake_object.add(p46);
        Point p47=new Point(94,99);
        snake_object.add(p47);
        Point p48=new Point(88,100);
        snake_object.add(p48);
        Point p49=new Point(83,102);
        snake_object.add(p49);
        Point p50=new Point(78,104);
        snake_object.add(p50);
        Point p51=new Point(72,106);
        snake_object.add(p51);
        Point p52=new Point(67,108);
        snake_object.add(p52);
        Point p53=new Point(62,110);
        snake_object.add(p53);
        Point p54=new Point(58,114);
        snake_object.add(p54);
        Point p55=new Point(53,117);
        snake_object.add(p55);
        Point p56=new Point(49,120);
        snake_object.add(p56);
        Point p57=new Point(45,123);
        snake_object.add(p57);
        Point p58=new Point(41,127);
        snake_object.add(p58);
        Point p59=new Point(37,131);
        snake_object.add(p59);
        Point p60=new Point(34,135);
        snake_object.add(p60);
        Point p61=new Point(32,139);
        snake_object.add(p61);
        Point p62=new Point(30,144);
        snake_object.add(p62);
        Point p63=new Point(29,149);
        snake_object.add(p63);
        Point p64=new Point(28,154);
        snake_object.add(p64);
        Point p65=new Point(27,159);
        snake_object.add(p65);
        Point p66=new Point(26,164);
        snake_object.add(p66);
        Point p67=new Point(27,169);
        snake_object.add(p67);
        Point p68=new Point(27,174);
        snake_object.add(p68);
        Point p69=new Point(28,179);
        snake_object.add(p69);
        Point p70=new Point(31,184);
        snake_object.add(p70);
        Point p71=new Point(35,187);
        snake_object.add(p71);
        Point p72=new Point(39,190);
        snake_object.add(p72);
        Point p73=new Point(44,193);
        snake_object.add(p73);
        Point p74=new Point(49,195);
        snake_object.add(p74);
        Point p75=new Point(55,196);
        snake_object.add(p75);
        Point p76=new Point(60,197);
        snake_object.add(p76);
        Point p77=new Point(66,198);
        snake_object.add(p77);
        Point p78=new Point(71,199);
        snake_object.add(p78);
        Point p79=new Point(77,200);
        snake_object.add(p79);
        Point p80=new Point(82,201);
        snake_object.add(p80);
        Point p81=new Point(88,202);
        snake_object.add(p81);
        Point p82=new Point(93,202);
        snake_object.add(p82);
        Point p83=new Point(98,203);
        snake_object.add(p83);
        Point p84=new Point(104,203);
        snake_object.add(p84);
        Point p85=new Point(110,204);
        snake_object.add(p85);
        Point p86=new Point(115,204);
        snake_object.add(p86);
        Point p87=new Point(121,204);
        snake_object.add(p87);
        Point p88=new Point(126,203);
        snake_object.add(p88);
        Point p89=new Point(132,203);
        snake_object.add(p89);
        Point p90=new Point(137,202);
        snake_object.add(p90);
        Point p91=new Point(143,201);
        snake_object.add(p91);
        Point p92=new Point(148,201);
        snake_object.add(p92);
        Point p93=new Point(154,200);
        snake_object.add(p93);
        Point p94=new Point(160,200);
        snake_object.add(p94);
        Point p95=new Point(165,199);
        snake_object.add(p95);
        Point p96=new Point(171,198);
        snake_object.add(p96);
        Point p97=new Point(176,198);
        snake_object.add(p97);
        Point p98=new Point(182,198);
        snake_object.add(p98);
        Point p99=new Point(187,198);
        snake_object.add(p99);
        Point p100=new Point(193,198);
        snake_object.add(p100);
        Point p101=new Point(198,198);
        snake_object.add(p101);
        Point p102=new Point(204,199);
        snake_object.add(p102);
        Point p103=new Point(210,199);
        snake_object.add(p103);
        Point p104=new Point(215,199);
        snake_object.add(p104);
        Point p105=new Point(220,199);
        snake_object.add(p105);
        Point p106=new Point(226,199);
        snake_object.add(p106);
        Point p107=new Point(232,200);
        snake_object.add(p107);
        Point p108=new Point(238,201);
        snake_object.add(p108);
        Point p109=new Point(243,201);
        snake_object.add(p109);
        Point p110=new Point(249,202);
        snake_object.add(p110);
        Point p111=new Point(254,202);
        snake_object.add(p111);
        Point p112=new Point(260,202);
        snake_object.add(p112);
        Point p113=new Point(265,202);
        snake_object.add(p113);
        Point p114=new Point(271,202);
        snake_object.add(p114);
        Point p115=new Point(276,201);
        snake_object.add(p115);
        Point p116=new Point(282,201);
        snake_object.add(p116);
        Point p117=new Point(287,199);
        snake_object.add(p117);
        Point p118=new Point(292,197);
        snake_object.add(p118);
        Point p119=new Point(295,194);
        snake_object.add(p119);
        Point p120=new Point(295,193);
        snake_object.add(p120);
        // for each point of the previous snake
        for(int i=0;i<snake.size();i++) {
            Point prev = snake.get((i+snake.size()-1)%snake.size());
            Point cur  = snake.get(i);
            Point next = snake.get((i+1)%snake.size());
            Point prev1 = snake_object.get((i+snake_object.size()-1)%snake_object.size());
            Point cur1  = snake_object.get(i);
            Point next1 = snake_object.get((i+1)%snake_object.size());
            // compute all energies
            for(int dy=-1;dy<=1;dy++) {
                for(int dx=-1;dx<=1;dx++) {
                    p.setLocation(cur.x+dx, cur.y+dy);
                    e_uniformity[1+dx][1+dy] = f_uniformity(prev,next,p);
                    e_curvature[1+dx][1+dy]  =f_curvature(prev,next,p)+3.75*(Math.sqrt(f_curvature(prev,p,next))-Math.sqrt(f_curvature(prev1,cur1,next1)))*(Math.sqrt(f_curvature(prev,p,next))-Math.sqrt(f_curvature(prev1,cur1,next1)));
                    e_flow[1+dx][1+dy]       = f_gflow(cur,p);
                    e_inertia[1+dx][1+dy]    = f_inertia(cur,p);
                }
            }

            // normalize energies
            normalize(e_uniformity);
            normalize(e_curvature);
            normalize(e_flow);
            normalize(e_inertia);

            // find the point with the minimum sum of energies
            double emin = Double.MAX_VALUE, e=0;
            int x=0,y=0;
            for(int dy=-1;dy<=1;dy++) {
                for(int dx=-1;dx<=1;dx++) {
                    e = 0;
                    e+= alpha * e_uniformity[1+dx][1+dy]; // internal energy
                    e+= beta  * e_curvature[1+dx][1+dy];  // internal energy
                    e+= gamma * e_flow[1+dx][1+dy];       // external energy
                    e+= delta * e_inertia[1+dx][1+dy];    // external energy

                    if (e<emin) { emin=e; x=cur.x+dx; y=cur.y+dy; }
                }
            }

            // boundary check
            if (x<1) x=1;
            if (x>=(this.width-1)) x=this.width-2;
            if (y<1) y=1;
            if (y>=(this.height-1)) y=this.height-2;

            // compute the returned value
            if (x!=cur.x || y!=cur.y) changed=true;

            // create the point in the new snake
            newsnake.add(new Point(x,y));
        }

        // new snake becomes current
        this.snake=newsnake;

        return changed;
    }

    // normalize energy matrix
    private void normalize(double[][] array3x3) {
        double sum=0;
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                sum+=Math.abs(array3x3[i][j]);

        if (sum==0) return;

        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                array3x3[i][j]/=sum;
    }

    private double getsnakelength() {
        // total length of snake
        double length=0;
        for(int i=0;i<snake.size();i++) {
            Point cur   = snake.get(i);
            Point next  = snake.get((i+1)%snake.size());
            length+=distance2D(cur, next);
        }
        return length;
    }

    private double distance2D(Point A, Point B) {
        int ux = A.x-B.x;
        int uy = A.y-B.y;
        double un = ux*ux+uy*uy;
        return Math.sqrt(un);
    }


    // ************************** ENERGY FUNCTIONS **************************

    private double f_uniformity(Point prev, Point next, Point p) {
        // length of previous segment
        double un = distance2D(prev, p);

        // mesure of uniformity
        double avg = snakelength/snake.size();
        double dun = Math.abs(un-avg);

        // elasticity energy
        return dun*dun;
    }

    private double f_curvature(Point prev, Point p, Point next) {
        int ux = p.x-prev.x;
        int uy = p.y-prev.y;
        double un = Math.sqrt(ux*ux+uy*uy);

        int vx = p.x-next.x;
        int vy = p.y-next.y;
        double vn = Math.sqrt(vx*vx+vy*vy);

        if (un==0 || vn==0) return 0;

        double cx = (vx+ux)/(un*vn);
        double cy = (vy+uy)/(un*vn);

        double cn = cx*cx+cy*cy;

        return cn;
    }

    private double f_gflow(Point cur, Point p) {
        // gradient flow
        int dcur = this.flow[cur.x][cur.y];
        int dp   = this.flow[p.x][p.y];
        double d = dp-dcur;
        return d;
    }

    private double f_inertia(Point cur, Point p) {
        double d = distance2D(cur, p);
        double g = this.gradient[cur.x][cur.y];
        double e = g*d;
        return e;
    }

    // ************************** AUTOADAPT **************************

    // rebuild the snake using cubic spline interpolation
    private void rebuild(int space) {

        // precompute length(i) = length of the snake from start to point #i
        double[] clength = new double[snake.size()+1];
        clength[0]=0;
        for(int i=0;i<snake.size();i++) {
            Point cur   = snake.get(i);
            Point next  = snake.get((i+1)%snake.size());
            clength[i+1]=clength[i]+distance2D(cur, next);
        }

        // compute number of points in the new snake
        double total = clength[snake.size()];
        int nmb = (int)(0.5+total/space);

        // build a new snake
        List<Point> newsnake = new ArrayList<Point>(snake.size());
        for(int i=0,j=0;j<nmb;j++) {
            // current length in the new snake
            double dist = (j*total)/nmb;

            // find corresponding interval of points in the original snake
            while(! (clength[i]<=dist && dist<clength[i+1])) i++;

            // get points (P-1,P,P+1,P+2) in the original snake
            Point prev  = snake.get((i+snake.size()-1)%snake.size());
            Point cur   = snake.get(i);
            Point next  = snake.get((i+1)%snake.size());
            Point next2 = snake.get((i+2)%snake.size());

            // do cubic spline interpolation
            double t =  (dist-clength[i])/(clength[i+1]-clength[i]);
            double t2 = t*t, t3=t2*t;
            double c0 =  1*t3;
            double c1 = -3*t3 +3*t2 +3*t + 1;
            double c2 =  3*t3 -6*t2 + 4;
            double c3 = -1*t3 +3*t2 -3*t + 1;
            double x = prev.x*c3 + cur.x*c2 + next.x* c1 + next2.x*c0;
            double y = prev.y*c3 + cur.y*c2 + next.y* c1 + next2.y*c0;
            Point newpoint = new Point( (int)(0.5+x/6), (int)(0.5+y/6) );

            // add computed point to the new snake
            newsnake.add(newpoint);
        }
        this.snake = newsnake;
    }


    private void removeOverlappingPoints(int minlen) {
        // for each point of the snake
        for(int i=0;i<snake.size();i++) {
            Point cur = snake.get(i);

            // check the other points (right half)
            for(int di=1+snake.size()/2;di>0;di--) {
                Point end  = snake.get((i+di)%snake.size());
                double dist = distance2D(cur,end);

                // if the two points are to close...
                if ( dist>minlen ) continue;

                // ... cut the "loop" part og the snake
                for(int k=0;k<di;k++) snake.remove( (i+1) %snake.size() );
                break;
            }
        }
    }

    private void addMissingPoints(int maxlen) {
        // for each point of the snake
        for(int i=0;i<snake.size();i++) {
            Point prev  = snake.get((i+snake.size()-1)%snake.size());
            Point cur   = snake.get(i);
            Point next  = snake.get((i+1)%snake.size());
            Point next2  = snake.get((i+2)%snake.size());

            // if the next point is to far then add a new point
            if ( distance2D(cur,next)>maxlen ) {

                // precomputed Uniform cubic B-spline for t=0.5
                double c0=0.125/6.0, c1=2.875/6.0, c2=2.875/6.0, c3=0.125/6.0;
                double x = prev.x*c3 + cur.x*c2 + next.x* c1 + next2.x*c0;
                double y = prev.y*c3 + cur.y*c2 + next.y* c1 + next2.y*c0;
                Point newpoint = new Point( (int)(0.5+x), (int)(0.5+y) );

                snake.add( i+1 , newpoint ); i--;
            }
        }
    }
}
