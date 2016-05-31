import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import lang.*;

/**
 * @author Luke Senseney
 */
class TestCase
{
	static ArrayList<String> cases=new ArrayList<>();
	static ArrayList<String> standard=new ArrayList<>();
	static ArrayList<String> factored=new ArrayList<>();
	static ArrayList<HashSet<Solution>> solutions=new ArrayList<>();
	static ArrayList<String> casesRound=new ArrayList<>();
	static ArrayList<String> standardRound=new ArrayList<>();
	static String imag=Term.IMAG_UNIT;
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
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
				"3x(5+9(x-10)^2)",
				"a(bcw+bdw+cdw)-e(fgw+fhw+ghw)=0");
		Collections.addAll(standard,
				"z^2-5z+6=0",
				"5a^2-3a-27=0",
				"x^2-36=0",
				"5x^3-4x^2+x=0",
				"X^2=0",
				"-132=0",
				"x=0",
				"x^2-3x+10=0",
				"X^2+5=0",
				"X^2+x-132=0",
				"x^2-x-132=0",
				"6m+888888888=0",
				"x^5+32=0",
				"5x/2-4=0",
				"x^2+6x+45=0",
				"(x+3)^(1/2)+36=0",
				"5x+4=0",
				"512",
				"64",
				"2^(a^2-2a+1)",
				"2^((a-1)^a)",
				"27x^3-540x^2+2715x",
				"abcw+abdw+acdw-efgw-efhw-eghw=0");
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
				"[3x, x+\u221a(5)"+imag+"/3-10, x-\u221a(5)"+imag+"/3-10]",
				"[w, abc+abd+acd-efg-efh-egh]");
        ArrayList<String> simpSolutions = new ArrayList<>();
		Collections.addAll(simpSolutions,"z=3 or 2",
				"a=3/10-3(61)^(1/2)/10 or 3(61)^(1/2)/10+3/10",
				"x=6 or -6",
				"x=0 or -"+imag+"/5+2/5 or "+imag+"/5+2/5",
				"X=0",
				"",
				"x=0",
				"x=-(31)^(1/2)"+imag+"/2+3/2 or (31)^(1/2)"+imag+"/2+3/2",
                "X=-(5)^(1/2)"+imag+" or (5)^(1/2)"+imag,
				"X=(-x+132)^(1/2),x=-X^2+132"
				,"x=-11 or 12"
				,"m=-148148148"
				,"x=2(-1)^(1/5)",
				"x=8/5","x=-6"+imag+"-3 or 6"+imag+"-3"
				,"x=1293"
				,"x=-4/5"
				,"","","","","",
				"b=-acd(ac+ad)^-1+efg(ac+ad)^-1+efh(ac+ad)^-1+egh(ac+ad)^-1,h=-abc(-ef-eg)^-1-abd(-ef-eg)^-1-acd(-ef-eg)^-1+efg(-ef-eg)^-1" +
						",e=-abc(-fg-fh-gh)^-1-abd(-fg-fh-gh)^-1-acd(-fg-fh-gh)^-1,f=-abc(-eg-eh)^-1-abd(-eg-eh)^-1-acd(-eg-eh)^-1+egh(-eg-eh)^-1" +
						",g=-abc(-ef-eh)^-1-abd(-ef-eh)^-1-acd(-ef-eh)^-1+efh(-ef-eh)^-1,w=0," +
						"a=efg(bc+bd+cd)^-1+efh(bc+bd+cd)^-1+egh(bc+bd+cd)^-1,c=-abd(ab+ad)^-1+efg(ab+ad)^-1+efh(ab+ad)^-1+egh(ab+ad)^-1" +
						",d=-abc(ab+ac)^-1+efg(ab+ac)^-1+efh(ab+ac)^-1+egh(ab+ac)^-1");
        for(String s : simpSolutions){
            HashSet<Solution> adding = new HashSet<>();
            for(String current : s.split(",")){
                if(current.length() > 2){
                    Solution solved = new Solution(current.charAt(0));
                    solved.value = new HashSet<>();
                    for(String value : current.substring(2).split(" or ")){
						solved.value.add(new Expression(value));
                    }
                    adding.add(solved);
                }
            }
            solutions.add(adding);
        }
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
                HashSet<Solution> s;
                try {
                    s=test.solve();
                }catch(NotEquation e) {
                    s=new HashSet<>();
                }
                if(!s.equals(solutions.get(i))) {
                    s.iterator().next().equals(solutions.get(i).iterator().next());
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
