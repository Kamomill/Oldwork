package exercises.basics;

import static java.lang.System.*;
import static java.lang.Math.*;

/**
 * Basic use of classes
 */
public class Classes {

    public static void main(String[] args) {
        new Classes().program();
    }

    // Write solutions as classes directly after program()
    // Print out solutions in program, see below

    void program() {
        // Printing result from solutions. Expected outcome as comment

        Point p = new Point(1, 2, 3);
        out.println(p.distance(new Point(p)));  // 0.0
        out.println(new Point(0, 0, 0).distance(new Point(1, 0, 0)));  // 1.0
        Point[] pts = {new Point(0, 0, 0), new Point(0, 1, 0), new Point(1, 0, 0)};
        Triangle t = new Triangle(pts);
        out.println(t.tArea());   // 0.49999999999999983

    }

    // ---------- Write your classes below this line ----------------------------
    class Point {
        double x, y, z;

        Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        Point(Point a) {
            this(a.x, a.y, a.z);
        }

        // root (  (x2- x1)^2  + (y2- y1)^2 + (z2-z1)^2 )
        double distance(Point b) {
            return sqrt(    pow(b.x - this.x, 2) +
                            pow(b.y - this.y, 2) +
                            pow(b.z - this.z, 2)
            );
        }
    }
    class Triangle {
        Point a,b,c;
        Triangle( Point[] a){
            this.a = a[0];
            this.b = a[1];
            this.c = a[2];

        }

        double tArea(){
            double la,lb,lc,s,area;
            la = this.c.distance(this.b);
            lb = this.c.distance(this.a);
            lc = this.a.distance(this.b);
            s = (la+lb+lc)/2;
            area = sqrt( s*(s-la)*(s-lb)*(s-lc));

            return area;
        }
    }

}
