public class Rational {
    private long numerator = 0;  //分子
    private long denominator = 1;  //分母
    //构造函数
    public Rational() {
        this(0,1);
    }
    public Rational(long numerator, long denominator) {
        long gcd = gcd(numerator,denominator);
        this.numerator= ((denominator>0)? 1 : -1)*numerator / gcd;
        this.denominator=Math.abs(denominator)/gcd;
    }
    //求最大公约数，便于简化有理数
    public static long gcd(long n,long d){
        long n1=Math.abs(n);
        long n2=Math.abs(d);
        int gcd=1;
        for(int k=1;k<=n1&&k<=n2;k++){
            if(n1%k==0&&n2%k==0){
                gcd=k;
            }
        }
        return gcd;
    }
    //生成器
    public long getNumerator(){
        return numerator;
    }
    public long getDenominator(){
        return denominator;
    }
    //实现加法
    public Rational add(Rational secondRational){
        long n=numerator*secondRational.getDenominator()+
                denominator*secondRational.getNumerator();
        long d=denominator*secondRational.getDenominator();
        return new Rational(n,d);
    }
    //实现减法
    public Rational subtract(Rational secondRational){
        long n=numerator*secondRational.getDenominator()-
                denominator*secondRational.getNumerator();
        long d=denominator*secondRational.getDenominator();
        return new Rational(n,d);
    }
    //实现乘法
    public Rational multiply(Rational secondRational){
        long n=numerator*secondRational.getNumerator();
        long d=denominator*secondRational.getDenominator();
        return new Rational(n,d);
    }
    //实现除法
    public Rational divide(Rational secondRational){
        long n=numerator*secondRational.getDenominator();
        long d=denominator*secondRational.numerator;
        return new Rational(n,d);
    }
    //重写toString类
    public String toString(){
        if(denominator==1){
            return numerator+"";
        }else{
            return numerator+"/"+denominator;
        }
    }
    public boolean equals(Object praml){
        if((this.subtract((Rational)(praml))).getNumerator()==0){
            return true;
        }else{
            return false;
        }
    }
    public int intValue(){
        return (int)doubleValue();
    }
    public float floatValue(){
        return (float)doubleValue();
    }
    public double doubleValue(){
        return numerator*1.0/denominator;
    }
    public long longValue(){
        return (long)doubleValue();
    }

}