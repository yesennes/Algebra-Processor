import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import lang.Expression;
import lang.NotEquation;
import lang.Solution;

/**
 * @author Luke Senseney
 */
class TestCase
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		final String imag=new String(Character.toChars(120050));
		ArrayList<String> cases=new ArrayList<String>();
		Collections.addAll(cases,"z^2-5z+6=0",
				"5a^2-3a+9=36",
				"x^2=36",
				"5x^3-4x^2+x-36=-36",
				"X^2=0",
				"(x*x)/x-x=132",
				"x=0",
				"x^2-x3+10=0",
				"X^2=-5",
				"X^2+x=132",
				"(x*x*x*x)/x^2-x=132",
				"888888888+6m=0",
				"x^5+32=0",
				"5/2x=4",
				"(x+3)^2+36=0",
				"(x+3)^(1/2)+36=0",
				"5x+7=3",
				"2^3^2",
				"(2^3)^2",
				"2^(a-1)^2",
				"2^(a-1)^a",
				"3x(5+9(x-10)^2)");
		ArrayList<String> standard=new ArrayList<String>();
		Collections.addAll(standard,"z^2-5z+6=0","5a^2-3a-27=0","x^2-36=0","5x^3-4x^2+x=0","X^2=0",
				"-132=0","x=0","x^2-3x+10=0","X^2+5=0","X^2+x-132=0","x^2-x-132=0",
				"6m+888888888=0","x^5+32=0","5x/2-4=0","x^2+6x+45=0","(x+3)^(1/2)+36=0","5x+4=0",
				"512","64","2^(a^2-2a+1)","2^((a-1)^a)","27x^3-540x^2+2715x");
		ArrayList<String> factored=new ArrayList<String>();
		Collections.addAll(factored,"[z-2, z-3]",
				"[a+3\u221a(61)/10-3/10, a-3/10-3\u221a(61)/10]",
				"[x+6, x-6]",
				"[x, x+"+imag+"/5-2/5, x-"+imag+"/5-2/5]",
				"[X^2]",
				"[-132]",
				"[x]",
				"[x+\u221a(31)"+imag+"/2-3/2, x-\u221a(31)"+imag+"/2-3/2]",
				"[X+\u221a(5)"+imag+", X-\u221a(5)"+imag+"]",
				"[X^2+x-132]",
				"[x+11, x-12]",
				"[6, m+148148148]",
				"[x^5+32]",
				"[1/2, 5x-8]",
				"[x+6"+imag+"+3, x-6"+imag+"+3]",
				"[(x+3)^(1/2)+36]",
				"[5x+4]",
				"[512]",
				"[64]",
				"[2^(a^2-2a+1)]",
				"[2^((a-1)^a)]",
				"[3x, x+\u221a(5)"+imag+"/3-10, x-\u221a(5)"+imag+"/3-10]");
		ArrayList<String> solutions=new ArrayList<String>();
		Collections.addAll(solutions,"z=3 or 2",
				"a=3/10-3\u221a(61)/10 or 3\u221a(61)/10+3/10",
				"x=6 or -6",
				"x=0 or -"+imag+"/5+2/5 or "+imag+"/5+2/5",
				"X=0",
				"",
				"x=0",
				"x=-\u221a(31)"+imag+"/2+3/2 or \u221a(31)"+imag+"/2+3/2"
				,"X=-\u221a(5)"+imag+" or \u221a(5)"+imag,
				"x=-X^2+132,X=(-x+132)^(1/2)"
				,"x=-11 or 12"
				,"m=-148148148"
				,"x=2\u2075\u221a(-1)",
				"x=8/5","x=-6"+imag+"-3 or 6"+imag+"-3"
				,"x=1293"
				,"x=-4/5"
				,"","","","","");
		for(int i=0;i<cases.size();i++) {
			try {
				Expression test=new Expression(cases.get(i));
				try {
					if(!test.toString().equals(standard.get(i))) {
						System.out.println(i+": "+cases.get(i)+" read as: "+test.toString().replace(imag,"i"));
						break;
					}
				}catch(Exception e) {
					System.out.println(i+": "+"On "+cases.get(i)+" toString() produced:");
					e.printStackTrace();
					break;
				}
				try {
					if(!test.factor().toString().equals(factored.get(i))) {
						System.out.println(i+": "+cases.get(i)+" factored as: "+test.factor().toString().replace(imag,"i")
                                +" but should have factored to "+factored.get(i));
						break;
					}
				}catch(Exception e) {
					System.out.println(i+": "+"On "+cases.get(i)+" factor() produced:");
					e.printStackTrace();
					break;
				}
                String s;
                try {
                    HashSet<Solution> sol=test.solve();
                    StringBuffer b=new StringBuffer();
                    for(Solution so:sol)
                        b.append(so).append(',');
                    if(b.length() > 1)
                        b.deleteCharAt(b.length() - 1);
                    s=b.toString();
                }catch(NotEquation e) {
                    s="";
                }
                if(!s.equals(solutions.get(i))) {
                    System.out.println(i+": "+cases.get(i)+" solved as "+s.toString().replace(imag,"i")+
                            " but should have solved as "+solutions.get(i).toString().replace(imag,"i"));
                    break;
                }
			}catch(Exception e) {
				System.out.println(i+": "+cases.get(i)+" Produced an error reading:");
				e.printStackTrace();
				break;
			}
		}
	}
}
